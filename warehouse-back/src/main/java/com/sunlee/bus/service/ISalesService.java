package com.sunlee.bus.service;

import com.sunlee.bus.entity.Sales;
import com.sunlee.bus.vo.SalesVo;
import com.sunlee.sys.common.DataGridView;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * InnoDB free: 9216 kB 服务类
 * </p>
 *
 * @author sunlee
 * @since 2026-04-20
 */
public interface ISalesService extends IService<Sales> {

    /**
     * 批量保存销售记录并更新库存
     * @param list 销售记录列表
     */
    void batchSave(List<Sales> list);

    /**
     * 删除销售记录并级联删除相关退货记录
     * @param id 销售单ID
     */
    void deleteSales(Integer id);

    /**
     * 查询订单列表（按订单号分组）
     * @param salesVo 查询参数
     * @return 订单列表
     */
    DataGridView queryOrders(SalesVo salesVo);

    /**
     * 查询订单详情
     * @param orderNo 订单号
     * @return 订单商品列表
     */
    List<Sales> queryOrderDetail(String orderNo);

    /**
     * 单商品退货
     * @param salesId 销售记录ID
     * @param returnNumber 退货数量，null则退全部
     */
    void returnSingleGoods(Integer salesId, Integer returnNumber);

    /**
     * 整单退货
     * @param orderNo 订单号
     */
    void returnOrder(String orderNo);

    /**
     * 向订单添加商品
     * @param list 商品列表（包含订单号）
     */
    void addToOrder(List<Sales> list);

    /**
     * 查询退加货记录
     * @param salesVo 查询参数
     * @return 记录列表
     */
    DataGridView queryReturnAddRecords(SalesVo salesVo);

    /**
     * 获取每个商品的销售量
     * @return 商品ID -> 销售量 的映射
     */
    Map<Integer, Integer> getSalesCountByGoodsId();
}
