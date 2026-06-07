package com.sunlee.bus.vo;

import com.sunlee.bus.entity.Sales;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Author: sunlee
 * @Date: 2026/04/15 10:29
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SalesVo extends Sales {

    private Integer page = 1;

    private Integer limit = 10;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endTime;

    /** 订单号筛选 */
    private String orderNo;

    /** 记录类型筛选: sale, add, return */
    private String recordType;

}
