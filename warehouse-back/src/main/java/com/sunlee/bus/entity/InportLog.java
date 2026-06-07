package com.sunlee.bus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 进货操作日志实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("bus_inport_log")
public class InportLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 供应商ID
     */
    private Integer providerId;

    /**
     * 商品ID
     */
    private Integer goodsId;

    /**
     * 操作类型：inport=进货, return=退货
     */
    private String type;

    /**
     * 数量
     */
    private Integer number;

    /**
     * 进货价
     */
    private BigDecimal price;

    /**
     * 操作人
     */
    private String operatePerson;

    /**
     * 操作时间
     */
    private Date operateTime;

    /**
     * 备注
     */
    private String remark;
}
