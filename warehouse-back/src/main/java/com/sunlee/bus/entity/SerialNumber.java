package com.sunlee.bus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 序列号实体
 *
 * @author sunlee
 * @since 2026-06-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("bus_serial_number")
public class SerialNumber implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 序列号
     */
    private String serialNumber;

    /**
     * 商品ID
     */
    private Integer goodsid;

    /**
     * 入库单ID
     */
    private Integer inportid;

    /**
     * 状态：0=在库, 1=已售, 2=已退
     */
    private Integer status;

    /**
     * 入库时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date instockTime;

    /**
     * 出库时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date outstockTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 逻辑删除：0=正常, 1=已删除
     */
    @TableLogic
    private Integer isdelete;

    /**
     * 商品名称（非数据库字段）
     */
    @TableField(exist = false)
    private String goodsname;

    /**
     * 商品规格（非数据库字段）
     */
    @TableField(exist = false)
    private String size;
}
