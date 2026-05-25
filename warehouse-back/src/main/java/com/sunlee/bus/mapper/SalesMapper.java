package com.sunlee.bus.mapper;

import com.sunlee.bus.entity.Sales;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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

}
