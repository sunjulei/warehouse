package com.sunlee.bus.service;

import com.sunlee.bus.entity.Inport;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sunlee.bus.vo.InportVo;
import com.sunlee.sys.common.DataGridView;

import java.util.List;

/**
 * <p>
 * InnoDB free: 9216 kB; (`providerid`) REFER `warehouse/bus_provider`(`id`); (`goo 服务类
 * </p>
 *
 * @author sunlee
 * @since 2026-04-01
 */
public interface IInportService extends IService<Inport> {

    /**
     * 批量保存进货记录并更新库存
     * @param list 进货记录列表
     */
    void batchSave(List<Inport> list);

    /**
     * 删除进货记录并级联删除相关退货记录
     * @param id 进货单ID
     */
    void deleteInport(Integer id);

    /**
     * 查询进货订单列表（按订单号分组）
     * @param inportVo 查询参数
     * @return 订单列表
     */
    DataGridView queryOrders(InportVo inportVo);

    /**
     * 查询订单详情
     * @param orderNo 订单号
     * @return 订单商品列表
     */
    List<Inport> queryOrderDetail(String orderNo);

    /**
     * 单商品退货
     * @param inportId 进货记录ID
     * @param returnNumber 退货数量，null则退全部
     */
    void returnSingleGoods(Integer inportId, Integer returnNumber);

    /**
     * 整单退货
     * @param orderNo 订单号
     */
    void returnOrder(String orderNo);

    /**
     * 向订单添加商品
     * @param list 商品列表（包含订单号）
     */
    void addToOrder(List<Inport> list);

    /**
     * 查询退加货记录
     * @param inportVo 查询参数
     * @return 记录列表
     */
    DataGridView queryReturnAddRecords(InportVo inportVo);

    /**
     * 获取当天的下一个订单序号
     * @param dateStr 日期字符串（yyyyMMddHHmmss）
     * @return 4位序号字符串
     */
    String getNextOrderSeq(String dateStr);
}
