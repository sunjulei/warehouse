package com.sunlee.bus.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sunlee.bus.entity.Goods;
import com.sunlee.bus.entity.Sales;
import com.sunlee.bus.entity.Salesback;
import com.sunlee.bus.mapper.GoodsMapper;
import com.sunlee.bus.mapper.SalesMapper;
import com.sunlee.bus.mapper.SalesbackMapper;
import com.sunlee.bus.service.ISalesbackService;
import com.sunlee.sys.common.WebUtils;
import com.sunlee.sys.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * <p>
 * InnoDB free: 9216 kB 服务实现类
 * </p>
 *
 * @author sunlee
 * @since 2026-05-01
 */
@Service
@Transactional
public class SalesbackServiceImpl extends ServiceImpl<SalesbackMapper, Salesback> implements ISalesbackService {

    @Autowired
    private SalesMapper salesMapper;

    @Autowired
    private GoodsMapper goodsMapper;

    /**
     * @param id    销售单ID
     * @param number    退货数量
     * @param remark    备注
     */
    @Override
    public void addSalesback(Integer id, Integer number, String remark) {
        // 校验退货数量
        if (number == null || number <= 0) {
            throw new RuntimeException("退货数量必须大于0");
        }
        //1.通过销售单ID查询出销售单信息
        Sales sales = salesMapper.selectById(id);
        if (sales == null) {
            throw new RuntimeException("销售记录不存在: " + id);
        }
        // 校验退货数量不能超过销售数量
        if (number > sales.getNumber()) {
            throw new RuntimeException("退货数量不能超过销售数量，当前销售数量: " + sales.getNumber());
        }
        //2.根据商品ID查询商品信息
        Goods goods = goodsMapper.selectById(sales.getGoodsid());
        if (goods == null) {
            throw new RuntimeException("商品不存在: " + sales.getGoodsid());
        }
        //3.原子增加商品库存（销售退货 = 商品入库）
        goodsMapper.increaseStock(sales.getGoodsid(), number);

        //修改销售的数量
        sales.setNumber(sales.getNumber()-number);
        salesMapper.updateById(sales);

        //5.添加退货单信息
        Salesback salesback = new Salesback();
        salesback.setGoodsid(sales.getGoodsid());

        salesback.setNumber(number);
        User user = (User) WebUtils.getSession().getAttribute("user");
        salesback.setOperateperson(user.getName());


        salesback.setSalebackprice(sales.getSaleprice() != null ? sales.getSaleprice().doubleValue() : null);
        salesback.setPaytype(sales.getPaytype());

        salesback.setSalesbacktime(new Date());
        salesback.setRemark(remark);


        salesback.setCustomerid(sales.getCustomerid());
        salesback.setSalesid(sales.getId());

        getBaseMapper().insert(salesback);
    }

    @Override
    public void cancelSalesback(Integer id) {
        // 1. 查询退货单
        Salesback salesback = getBaseMapper().selectById(id);
        if (salesback == null) {
            throw new RuntimeException("退货记录不存在: " + id);
        }
        // 2. 回滚商品库存：销售退货取消 = 商品数量要减回去（库存不足时拦截，防止负库存）
        Goods goods = goodsMapper.selectById(salesback.getGoodsid());
        if (goods != null && goodsMapper.decreaseStock(salesback.getGoodsid(), salesback.getNumber()) == 0) {
            throw new RuntimeException("商品【" + goods.getGoodsname() + "】库存不足，当前库存: " + goods.getNumber() + "，无法取消退货");
        }
        // 3. 回滚销售单数量
        Sales sales = salesMapper.selectById(salesback.getSalesid());
        if (sales != null) {
            sales.setNumber(sales.getNumber() + salesback.getNumber());
            salesMapper.updateById(sales);
        }
        // 4. 删除退货单
        getBaseMapper().deleteById(id);
    }
}
