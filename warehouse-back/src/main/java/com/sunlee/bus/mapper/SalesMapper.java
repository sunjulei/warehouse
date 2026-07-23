package com.sunlee.bus.mapper;

import com.sunlee.bus.entity.Sales;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sunlee.bus.vo.SalesVo;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * InnoDB free: 9216 kB Mapper 接口
 * </p>
 *
 * @author sunlee
 * @since 2026-04-20
 */
public interface SalesMapper extends BaseMapper<Sales> {

    List<Map<String, Object>> querySalesAnalysis(@Param("type") String type, @Param("startDate") String startDate, @Param("endDate") String endDate);

    List<Map<String, Object>> querySalesGoodsAnalysis(@Param("type") String type, @Param("startDate") String startDate, @Param("endDate") String endDate);

    List<Map<String, Object>> querySalesRevenueAnalysis(@Param("type") String type, @Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 销售订单分组分页查询（按订单号分组，数据库分页）
     */
    IPage<Map<String, Object>> selectOrdersPage(Page<Map<String, Object>> page, @Param("vo") SalesVo vo);

    /**
     * 销售退加货记录分页查询（联查客户与商品，数据库分页）
     */
    IPage<Map<String, Object>> selectReturnAddRecordsPage(Page<Map<String, Object>> page, @Param("vo") SalesVo vo);

    /**
     * 原子扣减销售单剩余可退数量（退货用）
     * @param id 销售单ID
     * @param number 扣减数量
     * @return 受影响行数，0表示剩余不足或已退完（并发安全）
     */
    int decreaseRemaining(@Param("id") Integer id, @Param("number") Integer number);

}
