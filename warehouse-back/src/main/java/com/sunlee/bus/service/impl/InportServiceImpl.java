package com.sunlee.bus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sunlee.bus.entity.Goods;
import com.sunlee.bus.entity.Inport;
import com.sunlee.bus.entity.Outport;
import com.sunlee.bus.mapper.GoodsMapper;
import com.sunlee.bus.mapper.InportMapper;
import com.sunlee.bus.mapper.OutportMapper;
import com.sunlee.bus.service.IInportService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


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

    /**
     * 保存商品进货
     * @param entity
     * @return
     */
    @Override
    public boolean save(Inport entity) {
        //根据商品ID查询商品
        Goods goods = goodsMapper.selectById(entity.getGoodsid());
        goods.setNumber(goods.getNumber()+entity.getNumber());
        goodsMapper.updateById(goods);
        //保存进货信息
        return super.save(entity);
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
        //根据商品ID查询商品信息
        Goods goods = goodsMapper.selectById(entity.getGoodsid());
        //库存算法  当前库存-进货单修改之前的数量+修改之后的数量
        goods.setNumber(goods.getNumber()-inport.getNumber()+entity.getNumber());
        goodsMapper.updateById(goods);
        //更新进货单
        return super.updateById(entity);
    }

    @Override
    public void deleteInport(Integer id) {
        Inport inport = baseMapper.selectById(id);
        // 级联软删除该进货单关联的所有退货记录
        Outport outportUpdate = new Outport();
        outportUpdate.setIsdelete(1);
        QueryWrapper<Outport> outportWrapper = new QueryWrapper<>();
        outportWrapper.eq("inportid", id);
        outportMapper.update(outportUpdate, outportWrapper);
        // 回滚商品库存
        Goods goods = goodsMapper.selectById(inport.getGoodsid());
        goods.setNumber(goods.getNumber() - inport.getNumber());
        goodsMapper.updateById(goods);
        // 软删除进货单
        baseMapper.deleteById(id);
    }

}
