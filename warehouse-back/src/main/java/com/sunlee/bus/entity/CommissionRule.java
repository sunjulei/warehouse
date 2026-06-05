package com.sunlee.bus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 提成规则
 */
@Data
@TableName("bus_commission_rule")
public class CommissionRule implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String name;

    /** 提成类型: fixed(固定比例)/tiered(阶梯) */
    private String type;

    /** 固定提成比例(%) */
    private BigDecimal rate;

    private Integer status;

    private Date createTime;

    private String remark;

    @TableField(exist = false)
    private List<CommissionTier> tiers;
}
