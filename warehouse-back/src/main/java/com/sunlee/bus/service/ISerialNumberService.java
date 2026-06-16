package com.sunlee.bus.service;

import com.sunlee.bus.entity.SerialNumber;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sunlee.bus.vo.SerialNumberVo;
import com.sunlee.sys.common.DataGridView;
import com.sunlee.sys.common.ResultObj;

import java.util.List;

/**
 * 序列号服务接口
 *
 * @author sunlee
 * @since 2026-06-12
 */
public interface ISerialNumberService extends IService<SerialNumber> {

    /**
     * 分页查询序列号列表
     * @param vo 查询参数
     * @return 表格数据
     */
    DataGridView querySerialNumbers(SerialNumberVo vo);

    /**
     * 批量添加序列号
     * @param list 序列号列表
     */
    void batchAdd(List<SerialNumber> list);

    /**
     * 删除序列号
     * @param id 序列号ID
     */
    void deleteSerialNumber(Integer id);

    /**
     * 检查序列号是否已存在
     * @param serialNumber 序列号
     * @return 是否存在
     */
    boolean existsBySerialNumber(String serialNumber);

    /**
     * 批量入库 - 进货时录入序列号
     * @param goodsId 商品ID
     * @param serialNumbers 序列号列表
     * @param inportId 进货单ID
     */
    void batchInport(Integer goodsId, List<String> serialNumbers, Integer inportId);

    /**
     * 批量销售 - 销售时标记序列号
     * @param goodsId 商品ID
     * @param serialNumbers 序列号列表
     * @param salesId 销售单ID
     */
    void batchSale(Integer goodsId, List<String> serialNumbers, Integer salesId);

    /**
     * 批量退货 - 退货时回库
     * @param serialNumbers 序列号列表
     * @param directResaleable 是否直接可售
     */
    void batchReturn(List<String> serialNumbers, boolean directResaleable);

    /**
     * 获取商品可用序列号
     * @param goodsId 商品ID
     * @return 可用序列号列表
     */
    List<SerialNumber> getAvailableByGoodsId(Integer goodsId);

    /**
     * 校验序列号是否可用
     * @param serialNumber 序列号
     * @param goodsId 商品ID
     * @return 是否可用
     */
    boolean checkAvailable(String serialNumber, Integer goodsId);

    /**
     * 更新序列号
     * @param serialNumber 序列号实体
     * @return 操作结果
     */
    ResultObj updateSerialNumber(SerialNumber serialNumber);
}
