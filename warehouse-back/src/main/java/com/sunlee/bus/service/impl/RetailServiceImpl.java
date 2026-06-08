package com.sunlee.bus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sunlee.bus.entity.Goods;
import com.sunlee.bus.entity.Retail;
import com.sunlee.bus.entity.RetailLog;
import com.sunlee.bus.entity.Retailback;
import com.sunlee.bus.mapper.GoodsMapper;
import com.sunlee.bus.mapper.RetailLogMapper;
import com.sunlee.bus.mapper.RetailMapper;
import com.sunlee.bus.mapper.RetailbackMapper;
import com.sunlee.bus.service.IRetailService;
import com.sunlee.bus.vo.RetailVo;
import com.sunlee.sys.common.DataGridView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RetailServiceImpl extends ServiceImpl<RetailMapper, Retail> implements IRetailService {

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private RetailbackMapper retailbackMapper;

    @Autowired
    private RetailLogMapper retailLogMapper;

    @Override
    public boolean save(Retail entity) {
        Goods goods = goodsMapper.selectById(entity.getGoodsid());
        goods.setNumber(goods.getNumber() - entity.getNumber());
        goodsMapper.updateById(goods);
        return super.save(entity);
    }

    @Override
    public boolean updateById(Retail entity) {
        Retail retail = baseMapper.selectById(entity.getId());
        Goods goods = goodsMapper.selectById(entity.getGoodsid());
        goods.setNumber(goods.getNumber() + retail.getNumber() - entity.getNumber());
        goodsMapper.updateById(goods);
        return super.updateById(entity);
    }

    @Override
    public void deleteRetail(Integer id) {
        Retail retail = baseMapper.selectById(id);
        // 级联软删除该零售单关联的所有退货记录
        Retailback retailbackUpdate = new Retailback();
        retailbackUpdate.setIsdelete(1);
        QueryWrapper<Retailback> wrapper = new QueryWrapper<>();
        wrapper.eq("retailid", id);
        retailbackMapper.update(retailbackUpdate, wrapper);
        // 回滚商品库存
        Goods goods = goodsMapper.selectById(retail.getGoodsid());
        goods.setNumber(goods.getNumber() + retail.getNumber());
        goodsMapper.updateById(goods);
        // 软删除零售单
        baseMapper.deleteById(id);
    }

    @Override
    public void batchSave(List<Retail> list) {
        for (Retail entity : list) {
            Goods goods = goodsMapper.selectById(entity.getGoodsid());
            if (goods != null) {
                goods.setNumber(goods.getNumber() - entity.getNumber());
                goodsMapper.updateById(goods);
            }
        }
        saveBatch(list);

        // 记录操作日志
        for (Retail entity : list) {
            RetailLog log = new RetailLog();
            log.setOrderNo(entity.getOrderno());
            log.setGoodsId(entity.getGoodsid());
            log.setType("retail");
            log.setNumber(entity.getNumber());
            log.setPrice(entity.getRetailprice());
            log.setPaytype(entity.getPaytype());
            log.setOperatePerson(entity.getOperateperson());
            log.setOperateTime(entity.getRetailtime());
            log.setRemark(entity.getRemark());
            retailLogMapper.insert(log);
        }
    }

    @Override
    public DataGridView queryOrders(RetailVo retailVo) {
        // 查询所有零售记录
        QueryWrapper<Retail> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNotNull("orderno");
        queryWrapper.eq(retailVo.getPaytype() != null && !retailVo.getPaytype().isEmpty(), "paytype", retailVo.getPaytype());
        queryWrapper.ge(retailVo.getStartTime() != null, "retailtime", retailVo.getStartTime());
        queryWrapper.le(retailVo.getEndTime() != null, "retailtime", retailVo.getEndTime());
        queryWrapper.orderByDesc("retailtime");

        List<Retail> allRetail = baseMapper.selectList(queryWrapper);

        // 按订单号分组
        Map<String, List<Retail>> orderMap = new HashMap<>();
        for (Retail retail : allRetail) {
            orderMap.computeIfAbsent(retail.getOrderno(), k -> new ArrayList<>()).add(retail);
        }

        // 构建订单列表
        List<Map<String, Object>> orderList = new ArrayList<>();
        for (Map.Entry<String, List<Retail>> entry : orderMap.entrySet()) {
            List<Retail> items = entry.getValue();
            if (items.isEmpty()) continue;

            Retail first = items.get(0);
            Map<String, Object> order = new HashMap<>();
            order.put("orderNo", entry.getKey());
            order.put("retailtime", first.getRetailtime());
            order.put("operateperson", first.getOperateperson());
            order.put("paytype", first.getPaytype());
            order.put("remark", first.getRemark());

            // 计算总数量和总金额（只计算未退完的）
            int totalNumber = 0;
            double totalAmount = 0;
            for (Retail item : items) {
                if (item.getOrderStatus() == null || item.getOrderStatus() == 0) {
                    totalNumber += item.getNumber();
                    totalAmount += item.getRetailprice() * item.getNumber();
                }
            }
            order.put("totalNumber", totalNumber);
            order.put("totalAmount", totalAmount);
            order.put("itemCount", items.size());

            // 订单状态：0=正常, 1=已退完
            boolean isReturnedAll = items.stream().allMatch(r -> r.getOrderStatus() != null && r.getOrderStatus() == 1);
            order.put("orderStatus", isReturnedAll ? 1 : 0);

            orderList.add(order);
        }

        // 分页处理
        int page = retailVo.getPage() != null ? retailVo.getPage() : 1;
        int limit = retailVo.getLimit() != null ? retailVo.getLimit() : 10;
        int fromIndex = (page - 1) * limit;
        int toIndex = Math.min(fromIndex + limit, orderList.size());

        List<Map<String, Object>> pageData = fromIndex < orderList.size()
                ? orderList.subList(fromIndex, toIndex)
                : new ArrayList<>();

        return new DataGridView((long) orderList.size(), pageData);
    }

    @Override
    public List<Retail> queryOrderDetail(String orderNo) {
        QueryWrapper<Retail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("orderno", orderNo);
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public void returnSingleGoods(Integer retailId, Integer returnNumber) {
        Retail retail = baseMapper.selectById(retailId);
        if (retail == null) {
            throw new RuntimeException("零售记录不存在");
        }

        if (retail.getOrderStatus() != null && retail.getOrderStatus() == 1) {
            throw new RuntimeException("该订单已退完，无法操作");
        }

        int returnQty = (returnNumber != null && returnNumber > 0) ? returnNumber : retail.getNumber();
        if (returnQty > retail.getNumber()) {
            throw new RuntimeException("退货数量不能超过零售数量");
        }

        // 回滚商品库存
        Goods goods = goodsMapper.selectById(retail.getGoodsid());
        if (goods != null) {
            goods.setNumber(goods.getNumber() + returnQty);
            goodsMapper.updateById(goods);
        }

        // 记录退货日志
        RetailLog log = new RetailLog();
        log.setOrderNo(retail.getOrderno());
        log.setGoodsId(retail.getGoodsid());
        log.setType("return");
        log.setNumber(returnQty);
        log.setPrice(retail.getRetailprice());
        log.setPaytype(retail.getPaytype());
        log.setOperatePerson(retail.getOperateperson());
        log.setOperateTime(new Date());
        log.setRemark(returnQty >= retail.getNumber() ? "单商品退货（全部）" : "单商品退货（部分）");
        retailLogMapper.insert(log);

        if (returnQty >= retail.getNumber()) {
            retail.setNumber(0);
            retail.setOrderStatus(1);
            baseMapper.updateById(retail);
        } else {
            retail.setNumber(retail.getNumber() - returnQty);
            baseMapper.updateById(retail);
        }

        checkOrderReturnComplete(retail.getOrderno());
    }

    @Override
    public void returnOrder(String orderNo) {
        QueryWrapper<Retail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("orderno", orderNo);
        queryWrapper.eq("order_status", 0);
        List<Retail> list = baseMapper.selectList(queryWrapper);

        for (Retail retail : list) {
            Goods goods = goodsMapper.selectById(retail.getGoodsid());
            if (goods != null) {
                goods.setNumber(goods.getNumber() + retail.getNumber());
                goodsMapper.updateById(goods);
            }

            RetailLog log = new RetailLog();
            log.setOrderNo(retail.getOrderno());
            log.setGoodsId(retail.getGoodsid());
            log.setType("return");
            log.setNumber(retail.getNumber());
            log.setPrice(retail.getRetailprice());
            log.setPaytype(retail.getPaytype());
            log.setOperatePerson(retail.getOperateperson());
            log.setOperateTime(new Date());
            log.setRemark("整单退货");
            retailLogMapper.insert(log);

            retail.setNumber(0);
            retail.setOrderStatus(1);
            baseMapper.updateById(retail);
        }
    }

    private void checkOrderReturnComplete(String orderNo) {
        QueryWrapper<Retail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("orderno", orderNo);
        queryWrapper.eq("order_status", 0);
        queryWrapper.gt("number", 0);
        long count = baseMapper.selectCount(queryWrapper);

        if (count == 0) {
            Retail update = new Retail();
            update.setOrderStatus(1);
            QueryWrapper<Retail> updateWrapper = new QueryWrapper<>();
            updateWrapper.eq("orderno", orderNo);
            baseMapper.update(update, updateWrapper);
        }
    }

    @Override
    public void addToOrder(List<Retail> list) {
        if (list == null || list.isEmpty()) {
            throw new RuntimeException("商品列表不能为空");
        }

        String orderNo = list.get(0).getOrderno();

        for (Retail retail : list) {
            retail.setOrderno(orderNo);

            Goods goods = goodsMapper.selectById(retail.getGoodsid());
            if (goods == null) {
                throw new RuntimeException("商品不存在: " + retail.getGoodsid());
            }
            if (goods.getNumber() < retail.getNumber()) {
                throw new RuntimeException("商品【" + goods.getGoodsname() + "】库存不足，当前库存: " + goods.getNumber());
            }
            goods.setNumber(goods.getNumber() - retail.getNumber());
            goodsMapper.updateById(goods);
        }

        saveBatch(list);

        // 记录操作日志
        for (Retail entity : list) {
            RetailLog log = new RetailLog();
            log.setOrderNo(entity.getOrderno());
            log.setGoodsId(entity.getGoodsid());
            log.setType("add");
            log.setNumber(entity.getNumber());
            log.setPrice(entity.getRetailprice());
            log.setPaytype(entity.getPaytype());
            log.setOperatePerson(entity.getOperateperson());
            log.setOperateTime(entity.getRetailtime());
            log.setRemark(entity.getRemark());
            retailLogMapper.insert(log);
        }
    }

    @Override
    public DataGridView queryReturnAddRecords(RetailVo retailVo) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        QueryWrapper<RetailLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(retailVo.getOrderNo() != null && !retailVo.getOrderNo().isEmpty(), "order_no", retailVo.getOrderNo());
        queryWrapper.eq(retailVo.getRecordType() != null && !retailVo.getRecordType().isEmpty(), "type", retailVo.getRecordType());
        queryWrapper.ge(retailVo.getStartTime() != null, "operate_time", retailVo.getStartTime());
        queryWrapper.le(retailVo.getEndTime() != null, "operate_time", retailVo.getEndTime());
        queryWrapper.orderByDesc("operate_time");

        List<RetailLog> logList = retailLogMapper.selectList(queryWrapper);

        List<Map<String, Object>> recordList = new ArrayList<>();
        for (RetailLog log : logList) {
            Map<String, Object> record = new HashMap<>();
            record.put("id", log.getId());
            record.put("orderNo", log.getOrderNo());

            String type = log.getType();
            if ("retail".equals(type)) {
                record.put("type", "零售");
                record.put("typeTag", "primary");
            } else if ("add".equals(type)) {
                record.put("type", "加货");
                record.put("typeTag", "success");
            } else if ("return".equals(type)) {
                record.put("type", "退货");
                record.put("typeTag", "danger");
            }

            Goods goods = goodsMapper.selectById(log.getGoodsId());
            record.put("goodsName", goods != null ? goods.getGoodsname() : "");
            record.put("goodsSize", goods != null ? goods.getSize() : "");

            record.put("number", log.getNumber());
            record.put("price", log.getPrice());
            record.put("totalAmount", log.getPrice() * log.getNumber());
            record.put("paytype", log.getPaytype());
            record.put("operatePerson", log.getOperatePerson());
            record.put("operateTime", log.getOperateTime() != null ? sdf.format(log.getOperateTime()) : "");
            record.put("remark", log.getRemark());

            recordList.add(record);
        }

        // 分页处理
        int page = retailVo.getPage() != null ? retailVo.getPage() : 1;
        int limit = retailVo.getLimit() != null ? retailVo.getLimit() : 10;
        int fromIndex = (page - 1) * limit;
        int toIndex = Math.min(fromIndex + limit, recordList.size());

        List<Map<String, Object>> pageData = fromIndex < recordList.size()
                ? recordList.subList(fromIndex, toIndex)
                : new ArrayList<>();

        return new DataGridView((long) recordList.size(), pageData);
    }
}
