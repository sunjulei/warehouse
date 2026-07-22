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
        if (retail == null) {
            throw new RuntimeException("零售单不存在");
        }
        // 2. 验证退货数量不能超过零售单剩余数量
        if (number > retail.getNumber()) {
            throw new RuntimeException("退货数量不能超过零售单剩余数量，当前剩余: " + retail.getNumber());
        }
        // 3. 根据商品ID查询商品信息
        Goods goods = goodsMapper.selectById(retail.getGoodsid());
        if (goods == null) {
            throw new RuntimeException("商品不存在: " + retail.getGoodsid());
        }
        // 4. 原子增加商品库存（零售退货 = 商品入库）
        goodsMapper.increaseStock(retail.getGoodsid(), number);
        // 5. 减少零售单数量
        retail.setNumber(retail.getNumber() - number);
        retailMapper.updateById(retail);
        // 6. 添加退货单信息
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
        if (retailback == null) {
            throw new RuntimeException("退货记录不存在: " + id);
        }
        // 2. 回滚商品库存（库存不足时拦截，防止负库存）
        Goods goods = goodsMapper.selectById(retailback.getGoodsid());
        if (goods != null && goodsMapper.decreaseStock(retailback.getGoodsid(), retailback.getNumber()) == 0) {
            throw new RuntimeException("商品【" + goods.getGoodsname() + "】库存不足，当前库存: " + goods.getNumber() + "，无法取消退货");
        }
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
