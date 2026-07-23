package com.sunlee.bus.mapper;

import com.sunlee.bus.entity.Inport;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sunlee.bus.vo.InportVo;
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

    /**
     * 进货订单分组分页查询（按订单号分组，联查供应商，数据库分页）
     */
    IPage<Map<String, Object>> selectOrdersPage(Page<Map<String, Object>> page, @Param("vo") InportVo vo);

    /**
     * 进货退加货记录分页查询（联查供应商与商品，数据库分页）
     */
    IPage<Map<String, Object>> selectReturnAddRecordsPage(Page<Map<String, Object>> page, @Param("vo") InportVo vo);

    /**
     * 原子扣减进货单剩余可退数量（退货用）
     * @param id 进货单ID
     * @param number 扣减数量
     * @return 受影响行数，0表示剩余不足或已退完（并发安全）
     */
    int decreaseRemaining(@Param("id") Integer id, @Param("number") Integer number);

}
