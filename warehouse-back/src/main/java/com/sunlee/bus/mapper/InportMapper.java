package com.sunlee.bus.mapper;

import com.sunlee.bus.entity.Inport;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * InnoDB free: 9216 kB; (`providerid`) REFER `warehouse/bus_provider`(`id`); (`goo Mapper 接口
 * </p>
 *
 * @author sunlee
 * @since 2026-04-01
 */
public interface InportMapper extends BaseMapper<Inport> {

    List<Map<String, Object>> queryInportAnalysis(@Param("type") String type, @Param("startDate") String startDate, @Param("endDate") String endDate);

    List<Map<String, Object>> queryInportGoodsAnalysis(@Param("type") String type, @Param("startDate") String startDate, @Param("endDate") String endDate);

    List<Map<String, Object>> queryInportCostAnalysis(@Param("type") String type, @Param("startDate") String startDate, @Param("endDate") String endDate);

}
