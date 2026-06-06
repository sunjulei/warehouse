package com.sunlee.bus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 会员等级规则
 */
@Data
@TableName("bus_member_level_rule")
public class MemberLevelRule implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String levelName;

    private Integer levelValue;

    private BigDecimal minRecharge;

    private BigDecimal minConsume;

    /** 达标条件关系: 1满足其一 2同时满足 */
    private Integer conditionType;

    private BigDecimal discountRate;

    private BigDecimal pointsRate;

    private String remark;

    private Integer sortOrder;

    private Date createTime;
}
