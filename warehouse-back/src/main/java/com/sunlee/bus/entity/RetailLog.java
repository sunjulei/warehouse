package com.sunlee.bus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 零售操作日志
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("bus_retail_log")
public class RetailLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /** 订单号 */
    private String orderNo;

    /** 商品ID */
    private Integer goodsId;

    /** 类型: retail=零售, add=加货, return=退货 */
    private String type;

    /** 数量 */
    private Integer number;

    /** 单价 */
    private Double price;

    /** 支付方式 */
    private String paytype;

    /** 操作人 */
    private String operatePerson;

    /** 操作时间 */
    private Date operateTime;

    /** 备注 */
    private String remark;

    /** 商品名称（非数据库字段） */
    @TableField(exist = false)
    private String goodsName;

    /** 商品规格（非数据库字段） */
    @TableField(exist = false)
    private String goodsSize;
}
