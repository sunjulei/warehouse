package com.sunlee.bus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sunlee.bus.entity.Goods;
import com.sunlee.bus.entity.Provider;
import com.sunlee.bus.mapper.GoodsMapper;
import com.sunlee.bus.mapper.ProviderMapper;
import com.sunlee.bus.service.IProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * <p>
 * InnoDB free: 9216 kB 服务实现类
 * </p>
 *
 * @author sunlee
 * @since 2026-03-15
 */
@Service
@Transactional
public class ProviderServiceImpl extends ServiceImpl<ProviderMapper, Provider> implements IProviderService {

    @Autowired
    private GoodsMapper goodsMapper;

    /**
     * 根据供应商id删除供应商
     * @param id    供应商id
     */
    @Override
    public void deleteProviderById(Integer id) {
        //根据供应商id查询出商品id
        QueryWrapper<Goods> queryWrapper = new QueryWrapper<Goods>();
        queryWrapper.eq("providerid",id);
        List<Goods> goods = goodsMapper.selectList(queryWrapper);
        for (Goods good : goods) {
            //获取一个商品id
            Integer id1 = good.getId();
            //根据商品id删除商品销售信息
            goodsMapper.deleteSaleByGoodsId(id1);
            //根据商品id删除商品销售退货信息
            goodsMapper.deleteSaleBackByGoodsId(id1);
        }
        //根据供应商id删除商品退货信息
        this.getBaseMapper().deleteOutPortByProviderId(id);
        //根据供应商id删除商品进货信息
        this.getBaseMapper().deleteInportByProviderId(id);
        //根据供应商id删除商品
        this.getBaseMapper().deleteGoodsByProviderId(id);
        //删除供应商
        this.removeById(id);
    }
}
