package com.sunlee.bus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sunlee.bus.entity.Goods;
import com.sunlee.bus.entity.Inport;
import com.sunlee.bus.entity.InportLog;
import com.sunlee.bus.entity.Outport;
import com.sunlee.bus.entity.Provider;
import com.sunlee.bus.mapper.GoodsMapper;
import com.sunlee.bus.mapper.InportLogMapper;
import com.sunlee.bus.mapper.InportMapper;
import com.sunlee.bus.mapper.OutportMapper;
import com.sunlee.bus.mapper.ProviderMapper;
import com.sunlee.bus.service.IInportService;
import com.sunlee.bus.vo.InportVo;
import com.sunlee.sys.common.DataGridView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * <p>
 * InnoDB free: 9216 kB; (`providerid`) REFER `warehouse/bus_provider`(`id`); (`goo 服务实现类
 * </p>
 *
 * @author sunlee
 * @since 2026-04-01
 */
@Service
@Transactional
public class InportServiceImpl extends ServiceImpl<InportMapper, Inport> implements IInportService {

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private OutportMapper outportMapper;

    @Autowired
    private ProviderMapper providerMapper;

    @Autowired
    private InportLogMapper inportLogMapper;

    /**
     * 保存商品进货
     * @param entity
     * @return
     */
    @Override
    public boolean save(Inport entity) {
        //根据商品ID查询商品
        Goods goods = goodsMapper.selectById(entity.getGoodsid());
        if (goods == null) {
            throw new RuntimeException("商品不存在: " + entity.getGoodsid());
        }
        // 先保存进货信息，再更新库存（确保事务一致性）
        boolean result = super.save(entity);
        goods.setNumber(goods.getNumber()+entity.getNumber());
        goodsMapper.updateById(goods);
        return result;
    }

    /**
     * 批量保存进货记录并更新库存
     * @param list 进货记录列表
     */
    @Override
    public void batchSave(List<Inport> list) {
        for (Inport entity : list) {
            Goods goods = goodsMapper.selectById(entity.getGoodsid());
            if (goods != null) {
                goods.setNumber(goods.getNumber() + entity.getNumber());
                goodsMapper.updateById(goods);
            }
        }
        saveBatch(list);

        // 记录操作日志
        for (Inport entity : list) {
            InportLog log = new InportLog();
            log.setOrderNo(entity.getOrderno());
            log.setProviderId(entity.getProviderid());
            log.setGoodsId(entity.getGoodsid());
            log.setType("inport");
            log.setNumber(entity.getNumber());
            log.setPrice(java.math.BigDecimal.valueOf(entity.getInportprice()));
            log.setOperatePerson(entity.getOperateperson());
            log.setOperateTime(entity.getInporttime());
            log.setRemark(entity.getRemark());
            inportLogMapper.insert(log);
        }
    }

    /**
     * 更新商品进货
     * @param entity
     * @return
     */
    @Override
    public boolean updateById(Inport entity) {
        //根据进货ID查询进货信息
        Inport inport = baseMapper.selectById(entity.getId());
        if (inport == null) {
            throw new RuntimeException("进货记录不存在: " + entity.getId());
        }
        //根据商品ID查询商品信息
        Goods goods = goodsMapper.selectById(entity.getGoodsid());
        if (goods == null) {
            throw new RuntimeException("商品不存在: " + entity.getGoodsid());
        }
        // 校验修改后的数量不能为负数
        if (entity.getNumber() != null && entity.getNumber() < 0) {
            throw new RuntimeException("进货数量不能为负数");
        }
        //库存算法  当前库存-进货单修改之前的数量+修改之后的数量
        goods.setNumber(goods.getNumber()-inport.getNumber()+entity.getNumber());
        goodsMapper.updateById(goods);
        //更新进货单
        return super.updateById(entity);
    }

    @Override
    public void deleteInport(Integer id) {
        Inport inport = baseMapper.selectById(id);
        // 级联软删除该进货单关联的所有退货记录
        Outport outportUpdate = new Outport();
        outportUpdate.setIsdelete(1);
        QueryWrapper<Outport> outportWrapper = new QueryWrapper<>();
        outportWrapper.eq("inportid", id);
        outportMapper.update(outportUpdate, outportWrapper);
        // 回滚商品库存
        Goods goods = goodsMapper.selectById(inport.getGoodsid());
        goods.setNumber(goods.getNumber() - inport.getNumber());
        goodsMapper.updateById(goods);
        // 软删除进货单
        baseMapper.deleteById(id);
    }

    @Override
    public DataGridView queryOrders(InportVo inportVo) {
        // 查询所有进货记录
        QueryWrapper<Inport> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNotNull("orderno");
        queryWrapper.eq(inportVo.getProviderid() != null && inportVo.getProviderid() != 0, "providerid", inportVo.getProviderid());
        queryWrapper.ge(inportVo.getStartTime() != null, "inporttime", inportVo.getStartTime());
        queryWrapper.le(inportVo.getEndTime() != null, "inporttime", inportVo.getEndTime());
        queryWrapper.orderByDesc("inporttime");

        List<Inport> allInports = baseMapper.selectList(queryWrapper);

        // 按订单号分组
        Map<String, List<Inport>> orderMap = new HashMap<>();
        for (Inport inport : allInports) {
            orderMap.computeIfAbsent(inport.getOrderno(), k -> new ArrayList<>()).add(inport);
        }

        // 构建订单列表
        List<Map<String, Object>> orderList = new ArrayList<>();
        for (Map.Entry<String, List<Inport>> entry : orderMap.entrySet()) {
            List<Inport> items = entry.getValue();
            if (items.isEmpty()) continue;

            Inport first = items.get(0);
            Map<String, Object> order = new HashMap<>();
            order.put("orderNo", entry.getKey());
            order.put("providerId", first.getProviderid());

            Provider provider = providerMapper.selectById(first.getProviderid());
            order.put("providerName", provider != null ? provider.getProvidername() : "");

            order.put("inporttime", first.getInporttime());
            order.put("operateperson", first.getOperateperson());
            order.put("remark", first.getRemark());

            // 计算总数量和总金额
            int totalNumber = 0;
            double totalAmount = 0;
            for (Inport item : items) {
                if (item.getOrderStatus() == null || item.getOrderStatus() == 0) {
                    totalNumber += item.getNumber();
                    totalAmount += item.getInportprice() * item.getNumber();
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

        // 按时间倒序排序
        orderList.sort((a, b) -> {
            Date timeA = (Date) a.get("inporttime");
            Date timeB = (Date) b.get("inporttime");
            if (timeA == null && timeB == null) return 0;
            if (timeA == null) return 1;
            if (timeB == null) return -1;
            return timeB.compareTo(timeA);
        });

        // 分页处理
        int page = inportVo.getPage() != null ? inportVo.getPage() : 1;
        int limit = inportVo.getLimit() != null ? inportVo.getLimit() : 10;
        int fromIndex = (page - 1) * limit;
        int toIndex = Math.min(fromIndex + limit, orderList.size());

        List<Map<String, Object>> pageData = fromIndex < orderList.size()
                ? orderList.subList(fromIndex, toIndex)
                : new ArrayList<>();

        return new DataGridView((long) orderList.size(), pageData);
    }

    @Override
    public List<Inport> queryOrderDetail(String orderNo) {
        QueryWrapper<Inport> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("orderno", orderNo);
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public void returnSingleGoods(Integer inportId, Integer returnNumber) {
        Inport inport = baseMapper.selectById(inportId);
        if (inport == null) {
            throw new RuntimeException("进货记录不存在");
        }

        // 检查订单状态
        if (inport.getOrderStatus() != null && inport.getOrderStatus() == 1) {
            throw new RuntimeException("该订单已退完，无法操作");
        }

        // 确定退货数量
        int returnQty = (returnNumber != null && returnNumber > 0) ? returnNumber : inport.getNumber();
        if (returnQty > inport.getNumber()) {
            throw new RuntimeException("退货数量不能超过进货数量");
        }

        // 回滚商品库存
        Goods goods = goodsMapper.selectById(inport.getGoodsid());
        if (goods != null) {
            goods.setNumber(goods.getNumber() - returnQty);
            goodsMapper.updateById(goods);
        }

        // 记录退货日志
        InportLog log = new InportLog();
        log.setOrderNo(inport.getOrderno());
        log.setProviderId(inport.getProviderid());
        log.setGoodsId(inport.getGoodsid());
        log.setType("return");
        log.setNumber(returnQty);
        log.setPrice(java.math.BigDecimal.valueOf(inport.getInportprice()));
        log.setOperatePerson(inport.getOperateperson());
        log.setOperateTime(new Date());
        log.setRemark(returnQty >= inport.getNumber() ? "单商品退货（全部）" : "单商品退货（部分）");
        inportLogMapper.insert(log);

        // 如果退全部，则标记该商品为已退完；否则更新数量
        if (returnQty >= inport.getNumber()) {
            inport.setNumber(0);
            inport.setOrderStatus(1);
            baseMapper.updateById(inport);
        } else {
            inport.setNumber(inport.getNumber() - returnQty);
            baseMapper.updateById(inport);
        }

        // 检查订单是否所有商品都退完了
        checkOrderReturnComplete(inport.getOrderno());
    }

    @Override
    public void returnOrder(String orderNo) {
        QueryWrapper<Inport> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("orderno", orderNo);
        queryWrapper.eq("order_status", 0);
        List<Inport> list = baseMapper.selectList(queryWrapper);

        for (Inport inport : list) {
            // 回滚商品库存
            Goods goods = goodsMapper.selectById(inport.getGoodsid());
            if (goods != null) {
                goods.setNumber(goods.getNumber() - inport.getNumber());
                goodsMapper.updateById(goods);
            }

            // 记录退货日志
            InportLog log = new InportLog();
            log.setOrderNo(inport.getOrderno());
            log.setProviderId(inport.getProviderid());
            log.setGoodsId(inport.getGoodsid());
            log.setType("return");
            log.setNumber(inport.getNumber());
            log.setPrice(java.math.BigDecimal.valueOf(inport.getInportprice()));
            log.setOperatePerson(inport.getOperateperson());
            log.setOperateTime(new Date());
            log.setRemark("整单退货");
            inportLogMapper.insert(log);

            // 标记为已退完
            inport.setNumber(0);
            inport.setOrderStatus(1);
            baseMapper.updateById(inport);
        }
    }

    /**
     * 检查订单是否所有商品都退完了
     */
    private void checkOrderReturnComplete(String orderNo) {
        QueryWrapper<Inport> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("orderno", orderNo);
        queryWrapper.eq("order_status", 0);
        queryWrapper.gt("number", 0);
        long count = baseMapper.selectCount(queryWrapper);

        if (count == 0) {
            Inport update = new Inport();
            update.setOrderStatus(1);
            QueryWrapper<Inport> updateWrapper = new QueryWrapper<>();
            updateWrapper.eq("orderno", orderNo);
            baseMapper.update(update, updateWrapper);
        }
    }

    @Override
    public void addToOrder(List<Inport> list) {
        if (list == null || list.isEmpty()) {
            throw new RuntimeException("商品列表不能为空");
        }

        String orderNo = list.get(0).getOrderno();
        Integer providerId = list.get(0).getProviderid();

        for (Inport inport : list) {
            inport.setOrderno(orderNo);
            inport.setProviderid(providerId);

            // 增加库存
            Goods goods = goodsMapper.selectById(inport.getGoodsid());
            if (goods == null) {
                throw new RuntimeException("商品不存在: " + inport.getGoodsid());
            }
            goods.setNumber(goods.getNumber() + inport.getNumber());
            goodsMapper.updateById(goods);
        }

        saveBatch(list);

        // 记录操作日志
        for (Inport entity : list) {
            InportLog log = new InportLog();
            log.setOrderNo(entity.getOrderno());
            log.setProviderId(entity.getProviderid());
            log.setGoodsId(entity.getGoodsid());
            log.setType("add");
            log.setNumber(entity.getNumber());
            log.setPrice(java.math.BigDecimal.valueOf(entity.getInportprice()));
            log.setOperatePerson(entity.getOperateperson());
            log.setOperateTime(entity.getInporttime());
            log.setRemark(entity.getRemark());
            inportLogMapper.insert(log);
        }
    }

    @Override
    public DataGridView queryReturnAddRecords(InportVo inportVo) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        QueryWrapper<InportLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(inportVo.getProviderid() != null && inportVo.getProviderid() != 0, "provider_id", inportVo.getProviderid());
        queryWrapper.eq(inportVo.getOrderNo() != null && !inportVo.getOrderNo().isEmpty(), "order_no", inportVo.getOrderNo());
        queryWrapper.eq(inportVo.getRecordType() != null && !inportVo.getRecordType().isEmpty(), "type", inportVo.getRecordType());
        queryWrapper.ge(inportVo.getStartTime() != null, "operate_time", inportVo.getStartTime());
        queryWrapper.le(inportVo.getEndTime() != null, "operate_time", inportVo.getEndTime());
        queryWrapper.orderByDesc("operate_time");

        List<InportLog> logList = inportLogMapper.selectList(queryWrapper);

        List<Map<String, Object>> recordList = new ArrayList<>();
        for (InportLog log : logList) {
            Map<String, Object> record = new HashMap<>();
            record.put("id", log.getId());
            record.put("orderNo", log.getOrderNo());

            String type = log.getType();
            if ("inport".equals(type)) {
                record.put("type", "进货");
                record.put("typeTag", "primary");
            } else if ("add".equals(type)) {
                record.put("type", "加货");
                record.put("typeTag", "success");
            } else if ("return".equals(type)) {
                record.put("type", "退货");
                record.put("typeTag", "danger");
            }

            Provider provider = providerMapper.selectById(log.getProviderId());
            record.put("providerName", provider != null ? provider.getProvidername() : "");

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
        int page = inportVo.getPage() != null ? inportVo.getPage() : 1;
        int limit = inportVo.getLimit() != null ? inportVo.getLimit() : 10;
        int fromIndex = (page - 1) * limit;
        int toIndex = Math.min(fromIndex + limit, recordList.size());

        List<Map<String, Object>> pageData = fromIndex < recordList.size()
                ? recordList.subList(fromIndex, toIndex)
                : new ArrayList<>();

        return new DataGridView((long) recordList.size(), pageData);
    }

    @Override
    public String getNextOrderSeq(String dateStr) {
        // 使用 UUID 后缀替代计数器，避免高并发下重复
        String uuid = java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return uuid;
    }
}
