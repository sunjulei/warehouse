package com.sunlee.bus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sunlee.bus.entity.Goods;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * InnoDB free: 9216 kB; (`providerid`) REFER `warehouse/bus_provider`(`id`) Mapper 接口
 * </p>
 *
 * @author sunlee
 * @since 2026-03-20
 */
public interface GoodsMapper extends BaseMapper<Goods> {

    /**
     * 根据商品id删除商品销售信息
     * @param id1
     */
    void deleteSaleByGoodsId(@Param("goodsid") Integer id1);

    /**
     * 根据商品id删除商品销售退货信息
     * @param id1
     */
    void deleteSaleBackByGoodsId(@Param("goodsid") Integer id1);

    /**
     * 根据商品id删除商品进货信息
     * @param id
     */
    void deleteInportByGoodsId(@Param("goodsid") Integer id);


    /**
     * 根据商品id删除商品退货信息
     * @param id
     */
    void deleteOutportByGoodsId(@Param("goodsid") Integer id);

    /**
     * 根据客户id删除商品销售
     * @param id    客户id
     */
    void deleteSaleByCustomerId(Integer id);

    /**
     * 根据客户id删除商品销售退货信息
     * @param id    客户id
     */
    void deleteSaleBackByCustomerId(Integer id);

    /**
     * 加载所有库存预警商品
     */
    List<Goods> loadAllWarning();

    /**
     * 原子扣减库存（库存不足时返回0，不会扣成负数）
     * @param goodsid 商品id
     * @param number 扣减数量
     * @return 受影响行数，0表示库存不足
     */
    int decreaseStock(@Param("goodsid") Integer goodsid, @Param("number") Integer number);

    /**
     * 原子增加库存
     * @param goodsid 商品id
     * @param number 增加数量
     * @return 受影响行数
     */
    int increaseStock(@Param("goodsid") Integer goodsid, @Param("number") Integer number);
}
