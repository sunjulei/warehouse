package com.sunlee.bus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sunlee.bus.entity.Goods;
import com.sunlee.bus.entity.Retail;
import com.sunlee.bus.entity.Retailback;
import com.sunlee.bus.mapper.GoodsMapper;
import com.sunlee.bus.mapper.RetailMapper;
import com.sunlee.bus.mapper.RetailbackMapper;
import com.sunlee.bus.service.IRetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RetailServiceImpl extends ServiceImpl<RetailMapper, Retail> implements IRetailService {

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private RetailbackMapper retailbackMapper;

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
            goods.setNumber(goods.getNumber() - entity.getNumber());
            goodsMapper.updateById(goods);
            super.save(entity);
        }
    }

}
