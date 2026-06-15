package com.sunlee.bus.service;

import com.sunlee.bus.entity.SerialNumber;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sunlee.bus.vo.SerialNumberVo;
import com.sunlee.sys.common.DataGridView;

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
}
