package com.sunlee.bus.mapper;

import com.sunlee.bus.entity.SerialNumber;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * 序列号 Mapper 接口
 *
 * @author sunlee
 * @since 2026-06-12
 */
public interface SerialNumberMapper extends BaseMapper<SerialNumber> {

    /**
     * 原子售出：仅当序列号处于在库(0)状态时流转为已售(1)，防止并发销售同一序列号
     * @return 受影响行数，0表示序列号不可用
     */
    int markSoldIfInStock(@Param("serialNumber") String serialNumber, @Param("goodsid") Integer goodsid);

    /**
     * 原子退货：仅当序列号处于已售(1)状态时流转为目标状态，防止并发重复退货
     * @return 受影响行数，0表示序列号不属于已售状态
     */
    int markReturnedIfSold(@Param("serialNumber") String serialNumber, @Param("newStatus") Integer newStatus);

}
