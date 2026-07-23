package com.sunlee.bus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sunlee.bus.entity.Goods;
import com.sunlee.bus.entity.Inport;
import com.sunlee.bus.entity.InportLog;
import com.sunlee.bus.entity.Outport;
import com.sunlee.bus.mapper.GoodsMapper;
import com.sunlee.bus.mapper.InportLogMapper;
import com.sunlee.bus.mapper.InportMapper;
import com.sunlee.bus.mapper.OutportMapper;
import com.sunlee.bus.service.IInportService;
import com.sunlee.bus.vo.InportVo;
import com.sunlee.sys.common.DataGridView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
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
        // 原子增加库存
        goodsMapper.increaseStock(entity.getGoodsid(), entity.getNumber());
        return super.save(entity);
    }

    /**
     * 批量保存进货记录并更新库存
     * @param list 进货记录列表
     */
    @Override
    public void batchSave(List<Inport> list) {
        for (Inport entity : list) {
            Goods goods = goodsMapper.selectById(entity.getGoodsid());
            if (goods == null) {
                throw new RuntimeException("商品不存在: " + entity.getGoodsid());
            }
            // 原子增加库存
            goodsMapper.increaseStock(entity.getGoodsid(), entity.getNumber());
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
        // 禁止编辑时更换商品：库存按商品维度差量调整，换商品会导致新旧商品库存都错账
        if (!inport.getGoodsid().equals(entity.getGoodsid())) {
            throw new RuntimeException("编辑单据不允许更换商品，请删除原单后重新开单");
        }
        // 已退完的记录禁止编辑，避免静默复活已退完订单导致账实不符
        if (inport.getOrderStatus() != null && inport.getOrderStatus() == 1) {
            throw new RuntimeException("该记录已退完，禁止编辑");
        }
        //根据商品ID查询商品信息
        Goods goods = goodsMapper.selectById(entity.getGoodsid());
        if (goods == null) {
            throw new RuntimeException("商品不存在: " + entity.getGoodsid());
        }
        if (entity.getNumber() == null) {
            throw new RuntimeException("进货数量不能为空");
        }
        // 校验修改后的数量不能为负数
        if (entity.getNumber() < 0) {
            throw new RuntimeException("进货数量不能为负数");
        }
        //按差量调整库存：新数量比原数量多则增加，少则原子扣减（库存不足时拦截，防止扣成负数）
        int delta = entity.getNumber() - inport.getNumber();
        if (delta > 0) {
            goodsMapper.increaseStock(entity.getGoodsid(), delta);
        } else if (delta < 0) {
            if (goodsMapper.decreaseStock(entity.getGoodsid(), -delta) == 0) {
                throw new RuntimeException("商品【" + goods.getGoodsname() + "】库存不足，当前库存: " + goods.getNumber() + "，无法减少进货数量");
            }
        }
        //更新进货单
        return super.updateById(entity);
    }

    @Override
    public void deleteInport(Integer id) {
        Inport inport = baseMapper.selectById(id);
        if (inport == null) {
            throw new RuntimeException("进货记录不存在: " + id);
        }
        // 级联软删除该进货单关联的所有退货记录
        Outport outportUpdate = new Outport();
        outportUpdate.setIsdelete(1);
        QueryWrapper<Outport> outportWrapper = new QueryWrapper<>();
        outportWrapper.eq("inportid", id);
        outportMapper.update(outportUpdate, outportWrapper);
        // 回滚商品库存（库存不足时拦截，防止扣成负数）
        Goods goods = goodsMapper.selectById(inport.getGoodsid());
        if (goods != null && goodsMapper.decreaseStock(inport.getGoodsid(), inport.getNumber()) == 0) {
            throw new RuntimeException("商品【" + goods.getGoodsname() + "】库存不足，当前库存: " + goods.getNumber() + "，无法删除该进货单");
        }
        // 软删除进货单
        baseMapper.deleteById(id);
    }

    @Override
    public DataGridView queryOrders(InportVo inportVo) {
        // 按订单号分组，SQL 联表 + 数据库分页（避免全表加载与 N+1 查询）
        Page<Map<String, Object>> page = new Page<>(
                inportVo.getPage() != null ? inportVo.getPage() : 1,
                inportVo.getLimit() != null ? inportVo.getLimit() : 10);
        IPage<Map<String, Object>> result = baseMapper.selectOrdersPage(page, inportVo);
        return new DataGridView(result.getTotal(), result.getRecords());
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

        // 原子扣减剩余可退数量（并发退货时防止超退导致库存与单据不一致）
        if (baseMapper.decreaseRemaining(inportId, returnQty) == 0) {
            throw new RuntimeException("退货失败：剩余可退数量不足或该记录已退完");
        }

        // 回滚商品库存：进货退货需扣减库存（库存不足时拦截，防止扣成负数）
        Goods goods = goodsMapper.selectById(inport.getGoodsid());
        if (goods != null && goodsMapper.decreaseStock(inport.getGoodsid(), returnQty) == 0) {
            throw new RuntimeException("商品【" + goods.getGoodsname() + "】库存不足，当前库存: " + goods.getNumber() + "，无法退货");
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

        // 如果退全部，则标记该记录为已退完（数量已被原子扣减为0）
        if (returnQty >= inport.getNumber()) {
            Inport finish = new Inport();
            finish.setId(inportId);
            finish.setOrderStatus(1);
            baseMapper.updateById(finish);
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
            if (inport.getNumber() == null || inport.getNumber() <= 0) {
                continue;
            }
            // 原子扣减剩余数量：并发整单退货时已被其他事务退掉的记录跳过，防止重复扣库存
            if (baseMapper.decreaseRemaining(inport.getId(), inport.getNumber()) == 0) {
                continue;
            }
            // 回滚商品库存：进货退货需扣减库存（库存不足时拦截，防止扣成负数）
            Goods goods = goodsMapper.selectById(inport.getGoodsid());
            if (goods != null && goodsMapper.decreaseStock(inport.getGoodsid(), inport.getNumber()) == 0) {
                throw new RuntimeException("商品【" + goods.getGoodsname() + "】库存不足，当前库存: " + goods.getNumber() + "，无法退货");
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

            // 标记为已退完（数量已被原子扣减为0）
            Inport finish = new Inport();
            finish.setId(inport.getId());
            finish.setOrderStatus(1);
            baseMapper.updateById(finish);
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

            // 原子增加库存
            Goods goods = goodsMapper.selectById(inport.getGoodsid());
            if (goods == null) {
                throw new RuntimeException("商品不存在: " + inport.getGoodsid());
            }
            goodsMapper.increaseStock(inport.getGoodsid(), inport.getNumber());
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
        // SQL 联表 + 数据库分页（避免全表加载与 N+1 查询）
        Page<Map<String, Object>> page = new Page<>(
                inportVo.getPage() != null ? inportVo.getPage() : 1,
                inportVo.getLimit() != null ? inportVo.getLimit() : 10);
        IPage<Map<String, Object>> result = baseMapper.selectReturnAddRecordsPage(page, inportVo);

        // 设置类型中文标签
        for (Map<String, Object> record : result.getRecords()) {
            String type = (String) record.get("type");
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
        }

        return new DataGridView(result.getTotal(), result.getRecords());
    }

    @Override
    public String getNextOrderSeq(String dateStr) {
        // 使用 UUID 后缀替代计数器，避免高并发下重复
        String uuid = java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return uuid;
    }
}
