package com.sunlee.bus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sunlee.bus.entity.Goods;
import com.sunlee.bus.entity.Sales;
import com.sunlee.bus.entity.Salesback;
import com.sunlee.bus.mapper.GoodsMapper;
import com.sunlee.bus.mapper.SalesMapper;
import com.sunlee.bus.mapper.SalesbackMapper;
import com.sunlee.bus.service.ISalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
        QueryWrapper<Salesback> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("salesid", id);
        List<Integer> salesbackIds = salesbackMapper.selectList(queryWrapper)
                .stream().map(Salesback::getId).collect(Collectors.toList());
        if (!salesbackIds.isEmpty()) {
            salesbackMapper.deleteByIds(salesbackIds);
        }
        // 回滚商品库存
        Goods goods = goodsMapper.selectById(sales.getGoodsid());
        goods.setNumber(goods.getNumber() + sales.getNumber());
        goodsMapper.updateById(goods);
        // 软删除销售单
        baseMapper.deleteById(id);
    }

}
