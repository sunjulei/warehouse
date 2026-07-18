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
}
