package com.sunlee.bus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 会员充值/消费记录
 */
@Data
@TableName("bus_member_record")
public class MemberRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer memberId;

    /** 类型: 充值/消费/退款 */
    private String type;

    private BigDecimal amount;

    private BigDecimal balanceAfter;

    private String operator;

    private Date createTime;

    private String remark;
}
