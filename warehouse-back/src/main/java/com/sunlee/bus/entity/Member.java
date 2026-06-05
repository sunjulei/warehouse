package com.sunlee.bus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 会员
 */
@Data
@TableName("bus_member")
public class Member implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String memberNo;

    private String name;

    private String phone;

    private String gender;

    private BigDecimal balance;

    private BigDecimal totalRecharge;

    private BigDecimal totalConsume;

    private Integer points;

    /** 会员等级: 1普通 2银卡 3金卡 4钻石 */
    private Integer level;

    /** 状态: 0禁用 1正常 */
    private Integer status;

    private Date createTime;

    private String remark;
}
