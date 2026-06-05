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
        //1.通过销售单ID查询出销售单信息
        Sales sales = salesMapper.selectById(id);
        //2.根据商品ID查询商品信息
        Goods goods = goodsMapper.selectById(sales.getGoodsid());
        //3.修改商品的数量     商品的数量-退货的数量
        goods.setNumber(goods.getNumber()+number);

        //修改进货的数量
        sales.setNumber(sales.getNumber()-number);
        salesMapper.updateById(sales);

        //4.进行修改
        goodsMapper.updateById(goods);

        //5.添加退货单信息
        Salesback salesback = new Salesback();
        salesback.setGoodsid(sales.getGoodsid());

        salesback.setNumber(number);
        User user = (User) WebUtils.getSession().getAttribute("user");
        salesback.setOperateperson(user.getName());


        salesback.setSalebackprice(sales.getSaleprice());
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
        // 2. 回滚商品库存：销售退货取消 = 商品数量要减回去
        Goods goods = goodsMapper.selectById(salesback.getGoodsid());
        goods.setNumber(goods.getNumber() - salesback.getNumber());
        goodsMapper.updateById(goods);
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
