package com.sunlee.bus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 退加货操作日志
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("bus_sales_log")
public class SalesLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /** 订单号 */
    private String orderNo;

    /** 客户ID */
    private Integer customerId;

    /** 商品ID */
    private Integer goodsId;

    /** 类型: sale=销售, add=加货, return=退货 */
    private String type;

    /** 数量 */
    private Integer number;

    /** 单价 */
    private BigDecimal price;

    /** 操作人 */
    private String operatePerson;

    /** 操作时间 */
    private Date operateTime;

    /** 备注 */
    private String remark;

    /** 客户名称（非数据库字段） */
    @TableField(exist = false)
    private String customerName;

    /** 商品名称（非数据库字段） */
    @TableField(exist = false)
    private String goodsName;

    /** 商品规格（非数据库字段） */
    @TableField(exist = false)
    private String goodsSize;
}
