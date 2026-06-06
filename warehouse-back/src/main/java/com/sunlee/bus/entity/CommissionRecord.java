package com.sunlee.bus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 店员提成记录
 */
@Data
@TableName("bus_commission_record")
public class CommissionRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String operator;

    /** 月份 (yyyy-MM) */
    @TableField("`year_month`")
    private String yearMonth;

    private BigDecimal totalSales;

    private Integer totalOrders;

    private BigDecimal commissionRate;

    private BigDecimal commissionAmount;

    private Integer ruleId;

    /** 状态: 0待确认 1已确认 2已发放 */
    private Integer status;

    private Date createTime;

    private String remark;
}
