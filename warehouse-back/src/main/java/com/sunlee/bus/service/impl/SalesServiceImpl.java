package com.sunlee.bus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sunlee.bus.entity.Customer;
import com.sunlee.bus.entity.Goods;
import com.sunlee.bus.entity.Sales;
import com.sunlee.bus.entity.Salesback;
import com.sunlee.bus.entity.SalesLog;
import com.sunlee.bus.mapper.CustomerMapper;
import com.sunlee.bus.mapper.GoodsMapper;
import com.sunlee.bus.mapper.SalesLogMapper;
import com.sunlee.bus.mapper.SalesMapper;
import com.sunlee.bus.mapper.SalesbackMapper;
import com.sunlee.bus.service.ISalesService;
import com.sunlee.bus.vo.SalesVo;
import com.sunlee.sys.common.DataGridView;

import java.text.SimpleDateFormat;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * <p>
 * InnoDB free: 9216 kB 服务实现类
 * </p>
 *
 * @author sunlee
 * @since 2026-04-20
 */
@Service
@Transactional
public class SalesServiceImpl extends ServiceImpl<SalesMapper, Sales> implements ISalesService {

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private SalesbackMapper salesbackMapper;

    @Autowired
    private SalesLogMapper salesLogMapper;

    /**
     * 添加商品销售
     * @param entity    商品销售实体类
     * @return
     */
    @Override
    public boolean save(Sales entity) {
        Goods goods = goodsMapper.selectById(entity.getGoodsid());
        goods.setNumber(goods.getNumber()-entity.getNumber());
        //更新商品的库存信息
        goodsMapper.updateById(goods);
        return super.save(entity);
    }

    /**
     * 批量保存销售记录并更新库存
     * @param list 销售记录列表
     */
    @Override
    public void batchSave(List<Sales> list) {
        for (Sales entity : list) {
            Goods goods = goodsMapper.selectById(entity.getGoodsid());
            if (goods != null) {
                goods.setNumber(goods.getNumber() - entity.getNumber());
                goodsMapper.updateById(goods);
            }
        }
        saveBatch(list);

        // 记录操作日志
        for (Sales entity : list) {
            SalesLog log = new SalesLog();
            log.setOrderNo(entity.getOrderno());
            log.setCustomerId(entity.getCustomerid());
            log.setGoodsId(entity.getGoodsid());
            log.setType("sale");
            log.setNumber(entity.getNumber());
            log.setPrice(entity.getSaleprice());
            log.setOperatePerson(entity.getOperateperson());
            log.setOperateTime(entity.getSalestime());
            log.setRemark(entity.getRemark());
            salesLogMapper.insert(log);
        }
    }

    /**
     * 更新商品销售
     * @param entity    商品销售实体类
     * @return
     */
    @Override
    public boolean updateById(Sales entity) {
        //根据销售单ID查询销售单信息
        Sales sales = baseMapper.selectById(entity.getId());
        Goods goods = goodsMapper.selectById(entity.getGoodsid());
        //仓库商品数量=原库存-销售单修改之前的数量+修改之后的数量
        goods.setNumber(goods.getNumber()+sales.getNumber()-entity.getNumber());
        //更新商品
        goodsMapper.updateById(goods);
        return super.updateById(entity);
    }

    @Override
    public void deleteSales(Integer id) {
        Sales sales = baseMapper.selectById(id);
        // 级联软删除该销售单关联的所有退货记录
        Salesback salesbackUpdate = new Salesback();
        salesbackUpdate.setIsdelete(1);
        QueryWrapper<Salesback> salesbackWrapper = new QueryWrapper<>();
        salesbackWrapper.eq("salesid", id);
        salesbackMapper.update(salesbackUpdate, salesbackWrapper);
        // 回滚商品库存
        Goods goods = goodsMapper.selectById(sales.getGoodsid());
        goods.setNumber(goods.getNumber() + sales.getNumber());
        goodsMapper.updateById(goods);
        // 软删除销售单
        baseMapper.deleteById(id);
    }

    @Override
    public DataGridView queryOrders(SalesVo salesVo) {
        // 查询所有销售记录（包括已退完的）
        QueryWrapper<Sales> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNotNull("orderno");
        queryWrapper.eq(salesVo.getCustomerid() != null && salesVo.getCustomerid() != 0, "customerid", salesVo.getCustomerid());
        queryWrapper.ge(salesVo.getStartTime() != null, "salestime", salesVo.getStartTime());
        queryWrapper.le(salesVo.getEndTime() != null, "salestime", salesVo.getEndTime());
        queryWrapper.orderByDesc("salestime");

        List<Sales> allSales = baseMapper.selectList(queryWrapper);

        // 按订单号分组
        Map<String, List<Sales>> orderMap = new HashMap<>();
        for (Sales sales : allSales) {
            orderMap.computeIfAbsent(sales.getOrderno(), k -> new ArrayList<>()).add(sales);
        }

        // 构建订单列表
        List<Map<String, Object>> orderList = new ArrayList<>();
        // 按订单时间倒序排列
        List<Map.Entry<String, List<Sales>>> sortedEntries = new ArrayList<>(orderMap.entrySet());
        sortedEntries.sort((a, b) -> {
            Date timeA = a.getValue().get(0).getSalestime();
            Date timeB = b.getValue().get(0).getSalestime();
            if (timeA == null && timeB == null) return 0;
            if (timeA == null) return 1;
            if (timeB == null) return -1;
            return timeB.compareTo(timeA);
        });
        for (Map.Entry<String, List<Sales>> entry : sortedEntries) {
            List<Sales> items = entry.getValue();
            if (items.isEmpty()) continue;

            Sales first = items.get(0);
            Map<String, Object> order = new HashMap<>();
            order.put("orderNo", entry.getKey());
            order.put("customerId", first.getCustomerid());

            Customer customer = customerMapper.selectById(first.getCustomerid());
            order.put("customerName", customer != null ? customer.getCustomername() : "");

            order.put("salestime", first.getSalestime());
            order.put("operateperson", first.getOperateperson());
            order.put("remark", first.getRemark());

            // 计算总数量和总金额（只计算未退完的）
            int totalNumber = 0;
            double totalAmount = 0;
            int validItemCount = 0;
            for (Sales item : items) {
                if (item.getOrderStatus() == null || item.getOrderStatus() == 0) {
                    totalNumber += item.getNumber();
                    totalAmount += item.getSaleprice().doubleValue() * item.getNumber();
                    validItemCount++;
                }
            }
            order.put("totalNumber", totalNumber);
            order.put("totalAmount", totalAmount);
            order.put("itemCount", items.size());

            // 订单状态：0=正常, 1=已退完
            boolean isReturnedAll = items.stream().allMatch(s -> s.getOrderStatus() != null && s.getOrderStatus() == 1);
            order.put("orderStatus", isReturnedAll ? 1 : 0);

            orderList.add(order);
        }

        // 分页处理
        int page = salesVo.getPage() != null ? salesVo.getPage() : 1;
        int limit = salesVo.getLimit() != null ? salesVo.getLimit() : 10;
        int fromIndex = (page - 1) * limit;
        int toIndex = Math.min(fromIndex + limit, orderList.size());

        List<Map<String, Object>> pageData = fromIndex < orderList.size()
                ? orderList.subList(fromIndex, toIndex)
                : new ArrayList<>();

        return new DataGridView((long) orderList.size(), pageData);
    }

    @Override
    public List<Sales> queryOrderDetail(String orderNo) {
        QueryWrapper<Sales> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("orderno", orderNo);
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public void returnSingleGoods(Integer salesId, Integer returnNumber) {
        Sales sales = baseMapper.selectById(salesId);
        if (sales == null) {
            throw new RuntimeException("销售记录不存在");
        }

        // 检查订单状态
        if (sales.getOrderStatus() != null && sales.getOrderStatus() == 1) {
            throw new RuntimeException("该订单已退完，无法操作");
        }

        // 确定退货数量
        int returnQty = (returnNumber != null && returnNumber > 0) ? returnNumber : sales.getNumber();
        if (returnQty > sales.getNumber()) {
            throw new RuntimeException("退货数量不能超过销售数量");
        }

        // 回滚商品库存
        Goods goods = goodsMapper.selectById(sales.getGoodsid());
        if (goods != null) {
            goods.setNumber(goods.getNumber() + returnQty);
            goodsMapper.updateById(goods);
        }

        // 记录退货日志
        SalesLog log = new SalesLog();
        log.setOrderNo(sales.getOrderno());
        log.setCustomerId(sales.getCustomerid());
        log.setGoodsId(sales.getGoodsid());
        log.setType("return");
        log.setNumber(returnQty);
        log.setPrice(sales.getSaleprice());
        log.setOperatePerson(sales.getOperateperson());
        log.setOperateTime(new Date());
        log.setRemark(returnQty >= sales.getNumber() ? "单商品退货（全部）" : "单商品退货（部分）");
        salesLogMapper.insert(log);

        // 如果退全部，则标记该商品为已退完；否则更新数量
        if (returnQty >= sales.getNumber()) {
            // 标记该条记录为已退完（数量设为0）
            sales.setNumber(0);
            sales.setOrderStatus(1);
            baseMapper.updateById(sales);
        } else {
            sales.setNumber(sales.getNumber() - returnQty);
            baseMapper.updateById(sales);
        }

        // 检查订单是否所有商品都退完了
        checkOrderReturnComplete(sales.getOrderno());
    }

    @Override
    public void returnOrder(String orderNo) {
        QueryWrapper<Sales> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("orderno", orderNo);
        queryWrapper.eq("order_status", 0); // 只查询未退完的记录
        List<Sales> list = baseMapper.selectList(queryWrapper);

        for (Sales sales : list) {
            // 回滚商品库存
            Goods goods = goodsMapper.selectById(sales.getGoodsid());
            if (goods != null) {
                goods.setNumber(goods.getNumber() + sales.getNumber());
                goodsMapper.updateById(goods);
            }

            // 记录退货日志
            SalesLog log = new SalesLog();
            log.setOrderNo(sales.getOrderno());
            log.setCustomerId(sales.getCustomerid());
            log.setGoodsId(sales.getGoodsid());
            log.setType("return");
            log.setNumber(sales.getNumber());
            log.setPrice(sales.getSaleprice());
            log.setOperatePerson(sales.getOperateperson());
            log.setOperateTime(new Date());
            log.setRemark("整单退货");
            salesLogMapper.insert(log);

            // 标记为已退完
            sales.setNumber(0);
            sales.setOrderStatus(1);
            baseMapper.updateById(sales);
        }
    }

    /**
     * 检查订单是否所有商品都退完了
     */
    private void checkOrderReturnComplete(String orderNo) {
        QueryWrapper<Sales> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("orderno", orderNo);
        queryWrapper.eq("order_status", 0);
        queryWrapper.gt("number", 0);
        long count = baseMapper.selectCount(queryWrapper);

        // 如果没有未退完的商品，标记整个订单为已退完
        if (count == 0) {
            Sales update = new Sales();
            update.setOrderStatus(1);
            QueryWrapper<Sales> updateWrapper = new QueryWrapper<>();
            updateWrapper.eq("orderno", orderNo);
            baseMapper.update(update, updateWrapper);
        }
    }

    @Override
    public void addToOrder(List<Sales> list) {
        if (list == null || list.isEmpty()) {
            throw new RuntimeException("商品列表不能为空");
        }

        // 获取订单号和客户ID
        String orderNo = list.get(0).getOrderno();
        Integer customerId = list.get(0).getCustomerid();

        for (Sales sales : list) {
            // 设置订单信息
            sales.setOrderno(orderNo);
            sales.setCustomerid(customerId);

            // 扣减库存
            Goods goods = goodsMapper.selectById(sales.getGoodsid());
            if (goods == null) {
                throw new RuntimeException("商品不存在: " + sales.getGoodsid());
            }
            if (goods.getNumber() < sales.getNumber()) {
                throw new RuntimeException("商品【" + goods.getGoodsname() + "】库存不足，当前库存: " + goods.getNumber());
            }
            goods.setNumber(goods.getNumber() - sales.getNumber());
            goodsMapper.updateById(goods);
        }

        // 批量保存
        saveBatch(list);

        // 记录操作日志
        for (Sales entity : list) {
            SalesLog log = new SalesLog();
            log.setOrderNo(entity.getOrderno());
            log.setCustomerId(entity.getCustomerid());
            log.setGoodsId(entity.getGoodsid());
            log.setType("add");
            log.setNumber(entity.getNumber());
            log.setPrice(entity.getSaleprice());
            log.setOperatePerson(entity.getOperateperson());
            log.setOperateTime(entity.getSalestime());
            log.setRemark(entity.getRemark());
            salesLogMapper.insert(log);
        }
    }

    @Override
    public DataGridView queryReturnAddRecords(SalesVo salesVo) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 从日志表查询
        QueryWrapper<SalesLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(salesVo.getCustomerid() != null && salesVo.getCustomerid() != 0, "customer_id", salesVo.getCustomerid());
        queryWrapper.eq(salesVo.getOrderNo() != null && !salesVo.getOrderNo().isEmpty(), "order_no", salesVo.getOrderNo());
        queryWrapper.eq(salesVo.getRecordType() != null && !salesVo.getRecordType().isEmpty(), "type", salesVo.getRecordType());
        queryWrapper.ge(salesVo.getStartTime() != null, "operate_time", salesVo.getStartTime());
        queryWrapper.le(salesVo.getEndTime() != null, "operate_time", salesVo.getEndTime());
        queryWrapper.orderByDesc("operate_time");

        List<SalesLog> logList = salesLogMapper.selectList(queryWrapper);

        // 构建返回数据
        List<Map<String, Object>> recordList = new ArrayList<>();
        for (SalesLog log : logList) {
            Map<String, Object> record = new HashMap<>();
            record.put("id", log.getId());
            record.put("orderNo", log.getOrderNo());

            // 设置类型和标签
            String type = log.getType();
            if ("sale".equals(type)) {
                record.put("type", "销售");
                record.put("typeTag", "primary");
            } else if ("add".equals(type)) {
                record.put("type", "加货");
                record.put("typeTag", "success");
            } else if ("return".equals(type)) {
                record.put("type", "退货");
                record.put("typeTag", "danger");
            }

            Customer customer = customerMapper.selectById(log.getCustomerId());
            record.put("customerName", customer != null ? customer.getCustomername() : "");

            Goods goods = goodsMapper.selectById(log.getGoodsId());
            record.put("goodsName", goods != null ? goods.getGoodsname() : "");
            record.put("goodsSize", goods != null ? goods.getSize() : "");

            record.put("number", log.getNumber());
            record.put("price", log.getPrice());
            record.put("totalAmount", log.getPrice().doubleValue() * log.getNumber());
            record.put("operatePerson", log.getOperatePerson());
            record.put("operateTime", log.getOperateTime() != null ? sdf.format(log.getOperateTime()) : "");
            record.put("remark", log.getRemark());

            recordList.add(record);
        }

        // 分页处理
        int page = salesVo.getPage() != null ? salesVo.getPage() : 1;
        int limit = salesVo.getLimit() != null ? salesVo.getLimit() : 10;
        int fromIndex = (page - 1) * limit;
        int toIndex = Math.min(fromIndex + limit, recordList.size());

        List<Map<String, Object>> pageData = fromIndex < recordList.size()
                ? recordList.subList(fromIndex, toIndex)
                : new ArrayList<>();

        return new DataGridView((long) recordList.size(), pageData);
    }

    @Override
    public Map<Integer, Integer> getSalesCountByGoodsId() {
        Map<Integer, Integer> result = new HashMap<>();

        // 查询所有有效销售记录（未退完的）
        QueryWrapper<Sales> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_status", 0);
        queryWrapper.gt("number", 0);
        List<Sales> salesList = baseMapper.selectList(queryWrapper);

        // 按商品ID统计销售量
        for (Sales sales : salesList) {
            result.merge(sales.getGoodsid(), sales.getNumber(), Integer::sum);
        }

        return result;
    }

}
