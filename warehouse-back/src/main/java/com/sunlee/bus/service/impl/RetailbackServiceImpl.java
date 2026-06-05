package com.sunlee.bus.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sunlee.bus.entity.Goods;
import com.sunlee.bus.entity.Retail;
import com.sunlee.bus.entity.Retailback;
import com.sunlee.bus.mapper.GoodsMapper;
import com.sunlee.bus.mapper.RetailMapper;
import com.sunlee.bus.mapper.RetailbackMapper;
import com.sunlee.bus.service.IRetailbackService;
import com.sunlee.sys.common.WebUtils;
import com.sunlee.sys.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
public class RetailbackServiceImpl extends ServiceImpl<RetailbackMapper, Retailback> implements IRetailbackService {

    @Autowired
    private RetailMapper retailMapper;

    @Autowired
    private GoodsMapper goodsMapper;

    @Override
    public void addRetailback(Integer id, Integer number, String remark) {
        // 1. 通过零售单ID查询零售单信息
        Retail retail = retailMapper.selectById(id);
        // 2. 根据商品ID查询商品信息
        Goods goods = goodsMapper.selectById(retail.getGoodsid());
        // 3. 增加商品库存
        goods.setNumber(goods.getNumber() + number);
        goodsMapper.updateById(goods);
        // 4. 减少零售单数量
        retail.setNumber(retail.getNumber() - number);
        retailMapper.updateById(retail);
        // 5. 添加退货单信息
        Retailback retailback = new Retailback();
        retailback.setGoodsid(retail.getGoodsid());
        retailback.setNumber(number);
        User user = (User) WebUtils.getSession().getAttribute("user");
        retailback.setOperateperson(user.getName());
        retailback.setRetailbackprice(retail.getRetailprice());
        retailback.setPaytype(retail.getPaytype());
        retailback.setRetailbacktime(new Date());
        retailback.setRemark(remark);
        retailback.setRetailid(retail.getId());
        getBaseMapper().insert(retailback);
    }

    @Override
    public void cancelRetailback(Integer id) {
        // 1. 查询退货单
        Retailback retailback = getBaseMapper().selectById(id);
        // 2. 回滚商品库存
        Goods goods = goodsMapper.selectById(retailback.getGoodsid());
        goods.setNumber(goods.getNumber() - retailback.getNumber());
        goodsMapper.updateById(goods);
        // 3. 回滚零售单数量
        Retail retail = retailMapper.selectById(retailback.getRetailid());
        if (retail != null) {
            retail.setNumber(retail.getNumber() + retailback.getNumber());
            retailMapper.updateById(retail);
        }
        // 4. 删除退货单
        getBaseMapper().deleteById(id);
    }
}
