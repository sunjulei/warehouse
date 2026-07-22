package com.sunlee.bus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

import java.util.Date;
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
        if (goods == null) {
            throw new RuntimeException("商品不存在: " + entity.getGoodsid());
        }
        // 原子扣减库存，库存不足时扣减失败并回滚事务
        if (goodsMapper.decreaseStock(entity.getGoodsid(), entity.getNumber()) == 0) {
            throw new RuntimeException("商品【" + goods.getGoodsname() + "】库存不足，当前库存: " + goods.getNumber());
        }
        return super.save(entity);
    }

    @Override
    public boolean updateById(Retail entity) {
        Retail retail = baseMapper.selectById(entity.getId());
        if (retail == null) {
            throw new RuntimeException("零售记录不存在: " + entity.getId());
        }
        Goods goods = goodsMapper.selectById(entity.getGoodsid());
        if (goods == null) {
            throw new RuntimeException("商品不存在: " + entity.getGoodsid());
        }
        if (entity.getNumber() == null) {
            throw new RuntimeException("零售数量不能为空");
        }
        if (entity.getNumber() < 0) {
            throw new RuntimeException("零售数量不能为负数");
        }
        //按差量调整库存：新数量比原数量多则原子扣减，少则回补
        int delta = entity.getNumber() - retail.getNumber();
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
    public void deleteRetail(Integer id) {
        Retail retail = baseMapper.selectById(id);
        if (retail == null) {
            throw new RuntimeException("零售记录不存在: " + id);
        }
        // 级联软删除该零售单关联的所有退货记录
        Retailback retailbackUpdate = new Retailback();
        retailbackUpdate.setIsdelete(1);
        QueryWrapper<Retailback> wrapper = new QueryWrapper<>();
        wrapper.eq("retailid", id);
        retailbackMapper.update(retailbackUpdate, wrapper);
        // 回滚商品库存
        goodsMapper.increaseStock(retail.getGoodsid(), retail.getNumber());
        // 软删除零售单
        baseMapper.deleteById(id);
    }

    @Override
    public void batchSave(List<Retail> list) {
        for (Retail entity : list) {
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
        // 按订单号分组，SQL + 数据库分页（避免全表加载与内存分页，保证按时间倒序稳定输出）
        Page<Map<String, Object>> page = new Page<>(
                retailVo.getPage() != null ? retailVo.getPage() : 1,
                retailVo.getLimit() != null ? retailVo.getLimit() : 10);
        IPage<Map<String, Object>> result = baseMapper.selectOrdersPage(page, retailVo);
        return new DataGridView(result.getTotal(), result.getRecords());
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
        goodsMapper.increaseStock(retail.getGoodsid(), returnQty);

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
            // 回滚商品库存
            goodsMapper.increaseStock(retail.getGoodsid(), retail.getNumber());

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

            // 原子扣减库存，库存不足则整批回滚
            Goods goods = goodsMapper.selectById(retail.getGoodsid());
            if (goods == null) {
                throw new RuntimeException("商品不存在: " + retail.getGoodsid());
            }
            if (goodsMapper.decreaseStock(retail.getGoodsid(), retail.getNumber()) == 0) {
                throw new RuntimeException("商品【" + goods.getGoodsname() + "】库存不足，当前库存: " + goods.getNumber());
            }
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
        // SQL 联表 + 数据库分页（避免全表加载与 N+1 查询）
        Page<Map<String, Object>> page = new Page<>(
                retailVo.getPage() != null ? retailVo.getPage() : 1,
                retailVo.getLimit() != null ? retailVo.getLimit() : 10);
        IPage<Map<String, Object>> result = baseMapper.selectReturnAddRecordsPage(page, retailVo);

        // 设置类型中文标签
        for (Map<String, Object> record : result.getRecords()) {
            String type = (String) record.get("type");
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
        }

        return new DataGridView(result.getTotal(), result.getRecords());
    }
}
