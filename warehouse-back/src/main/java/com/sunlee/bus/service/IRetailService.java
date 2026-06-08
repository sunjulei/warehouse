package com.sunlee.bus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sunlee.bus.entity.Retail;
import com.sunlee.bus.vo.RetailVo;
import com.sunlee.sys.common.DataGridView;

import java.util.List;

public interface IRetailService extends IService<Retail> {
    void deleteRetail(Integer id);
    void batchSave(List<Retail> list);

    /**
     * 查询零售订单列表（按订单号分组）
     */
    DataGridView queryOrders(RetailVo retailVo);

    /**
     * 查询订单详情
     */
    List<Retail> queryOrderDetail(String orderNo);

    /**
     * 单商品退货
     */
    void returnSingleGoods(Integer retailId, Integer returnNumber);

    /**
     * 整单退货
     */
    void returnOrder(String orderNo);

    /**
     * 向订单添加商品
     */
    void addToOrder(List<Retail> list);

    /**
     * 查询退加货记录
     */
    DataGridView queryReturnAddRecords(RetailVo retailVo);
}
