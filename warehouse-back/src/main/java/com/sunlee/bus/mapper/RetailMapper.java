package com.sunlee.bus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sunlee.bus.entity.Retail;
import com.sunlee.bus.vo.RetailVo;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface RetailMapper extends BaseMapper<Retail> {

    /**
     * 零售订单分组分页查询（按订单号分组，数据库分页）
     */
    IPage<Map<String, Object>> selectOrdersPage(Page<Map<String, Object>> page, @Param("vo") RetailVo vo);

    /**
     * 零售退加货记录分页查询（联查商品，数据库分页）
     */
    IPage<Map<String, Object>> selectReturnAddRecordsPage(Page<Map<String, Object>> page, @Param("vo") RetailVo vo);

    /**
     * 原子扣减零售单剩余可退数量（退货用）
     * @param id 零售单ID
     * @param number 扣减数量
     * @return 受影响行数，0表示剩余不足或已退完（并发安全）
     */
    int decreaseRemaining(@Param("id") Integer id, @Param("number") Integer number);
}
