package com.sunlee.bus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sunlee.bus.entity.Goods;
import com.sunlee.bus.entity.Sales;
import com.sunlee.bus.entity.Salesback;
import com.sunlee.bus.entity.SalesLog;
import com.sunlee.bus.mapper.GoodsMapper;
import com.sunlee.bus.mapper.SalesLogMapper;
import com.sunlee.bus.mapper.SalesMapper;
import com.sunlee.bus.mapper.SalesbackMapper;
import com.sunlee.bus.service.ISalesService;
import com.sunlee.bus.vo.SalesVo;
import com.sunlee.sys.common.DataGridView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        if (goods == null) {
            throw new RuntimeException("商品不存在: " + entity.getGoodsid());
        }
        // 原子扣减库存，库存不足时扣减失败并回滚事务
        if (goodsMapper.decreaseStock(entity.getGoodsid(), entity.getNumber()) == 0) {
            throw new RuntimeException("商品【" + goods.getGoodsname() + "】库存不足，当前库存: " + goods.getNumber());
        }
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
            if (goods == null) {
                throw new RuntimeException("商品不存在: " + entity.getGoodsid());
            }
            // 原子扣减库存，任一商品库存不足则整批回滚
            if (goodsMapper.decreaseStock(entity.getGoodsid(), entity.getNumber()) == 0) {
                throw new RuntimeException("商品【" + goods.getGoodsname() + "】库存不足，当前库存: " + goods.getNumber());
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
        if (sales == null) {
            throw new RuntimeException("销售记录不存在: " + entity.getId());
        }
        // 禁止编辑时更换商品：库存按商品维度差量调整，换商品会导致新旧商品库存都错账
        if (!sales.getGoodsid().equals(entity.getGoodsid())) {
            throw new RuntimeException("编辑单据不允许更换商品，请删除原单后重新开单");
        }
        // 已退完的记录禁止编辑，避免静默复活已退完订单导致账实不符
        if (sales.getOrderStatus() != null && sales.getOrderStatus() == 1) {
            throw new RuntimeException("该记录已退完，禁止编辑");
        }
        Goods goods = goodsMapper.selectById(entity.getGoodsid());
        if (goods == null) {
            throw new RuntimeException("商品不存在: " + entity.getGoodsid());
        }
        if (entity.getNumber() == null) {
            throw new RuntimeException("销售数量不能为空");
        }
        // 校验修改后的数量不能为负数
        if (entity.getNumber() < 0) {
            throw new RuntimeException("销售数量不能为负数");
        }
        //按差量调整库存：新数量比原数量多则原子扣减，少则回补
        int delta = entity.getNumber() - sales.getNumber();
        if (delta > 0) {
            if (goodsMapper.decreaseStock(entity.getGoodsid(), delta) == 0) {
                throw new RuntimeException("商品【" + goods.getGoodsname() + "】库存不足，当前库存: " + goods.getNumber());
            }
        } else if (delta < 0) {
            goodsMapper.increaseStock(entity.getGoodsid(), -delta);
        }
        return super.updateById(entity);
    }

    @Override
    public void deleteSales(Integer id) {
        Sales sales = baseMapper.selectById(id);
        if (sales == null) {
            throw new RuntimeException("销售记录不存在: " + id);
        }
        // 级联软删除该销售单关联的所有退货记录
        Salesback salesbackUpdate = new Salesback();
        salesbackUpdate.setIsdelete(1);
        QueryWrapper<Salesback> salesbackWrapper = new QueryWrapper<>();
        salesbackWrapper.eq("salesid", id);
        salesbackMapper.update(salesbackUpdate, salesbackWrapper);
        // 回滚商品库存
        goodsMapper.increaseStock(sales.getGoodsid(), sales.getNumber());
        // 软删除销售单
        baseMapper.deleteById(id);
    }

    @Override
    public DataGridView queryOrders(SalesVo salesVo) {
        // 按订单号分组，SQL 联表 + 数据库分页（避免全表加载与 N+1 查询）
        Page<Map<String, Object>> page = new Page<>(
                salesVo.getPage() != null ? salesVo.getPage() : 1,
                salesVo.getLimit() != null ? salesVo.getLimit() : 10);
        IPage<Map<String, Object>> result = baseMapper.selectOrdersPage(page, salesVo);
        return new DataGridView(result.getTotal(), result.getRecords());
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

        // 原子扣减剩余可退数量（并发退货时防止超退导致库存与单据不一致）
        if (baseMapper.decreaseRemaining(salesId, returnQty) == 0) {
            throw new RuntimeException("退货失败：剩余可退数量不足或该记录已退完");
        }

        // 回滚商品库存
        goodsMapper.increaseStock(sales.getGoodsid(), returnQty);

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

        // 如果退全部，则标记该记录为已退完（数量已被原子扣减为0）
        if (returnQty >= sales.getNumber()) {
            Sales finish = new Sales();
            finish.setId(salesId);
            finish.setOrderStatus(1);
            baseMapper.updateById(finish);
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
            if (sales.getNumber() == null || sales.getNumber() <= 0) {
                continue;
            }
            // 原子扣减剩余数量：并发整单退货时已被其他事务退掉的记录跳过，防止重复回库
            if (baseMapper.decreaseRemaining(sales.getId(), sales.getNumber()) == 0) {
                continue;
            }
            // 回滚商品库存
            goodsMapper.increaseStock(sales.getGoodsid(), sales.getNumber());

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

            // 标记为已退完（数量已被原子扣减为0）
            Sales finish = new Sales();
            finish.setId(sales.getId());
            finish.setOrderStatus(1);
            baseMapper.updateById(finish);
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

            // 原子扣减库存，库存不足则整批回滚
            Goods goods = goodsMapper.selectById(sales.getGoodsid());
            if (goods == null) {
                throw new RuntimeException("商品不存在: " + sales.getGoodsid());
            }
            if (goodsMapper.decreaseStock(sales.getGoodsid(), sales.getNumber()) == 0) {
                throw new RuntimeException("商品【" + goods.getGoodsname() + "】库存不足，当前库存: " + goods.getNumber());
            }
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
        // SQL 联表 + 数据库分页（避免全表加载与 N+1 查询）
        Page<Map<String, Object>> page = new Page<>(
                salesVo.getPage() != null ? salesVo.getPage() : 1,
                salesVo.getLimit() != null ? salesVo.getLimit() : 10);
        IPage<Map<String, Object>> result = baseMapper.selectReturnAddRecordsPage(page, salesVo);

        // 设置类型中文标签
        for (Map<String, Object> record : result.getRecords()) {
            String type = (String) record.get("type");
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
        }

        return new DataGridView(result.getTotal(), result.getRecords());
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
