package com.sunlee.bus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 序列号操作日志实体
 *
 * @author sunlee
 * @since 2026-06-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("bus_serial_number_log")
public class SerialNumberLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /** 序列号 */
    private String serialNumber;

    /** 商品ID */
    private Integer goodsid;

    /** 操作类型: inport/sale/return/stocktake */
    private String action;

    /** 变更前状态 */
    private Integer fromStatus;

    /** 变更后状态 */
    private Integer toStatus;

    /** 关联订单号 */
    private String orderNo;

    /** 操作人 */
    private String operatePerson;

    /** 操作时间 */
    private Date operateTime;

    /** 备注 */
    private String remark;
}
