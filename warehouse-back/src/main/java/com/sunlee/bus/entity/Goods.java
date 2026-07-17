package com.sunlee.bus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * InnoDB free: 9216 kB; (`providerid`) REFER `warehouse/bus_provider`(`id`)
 * </p>
 *
 * @author sunlee
 * @since 2026-03-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("bus_goods")
@ToString
public class Goods implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String goodsname;

    private String produceplace;

    private String size;

    private String goodspackage;

    private String productcode;

    private String promitcode;

    private String description;

    private Double price;

    private Integer number;

    private Integer dangernum;

    private String goodsimg;

    private Integer available;

    private String attribute;

    private Integer providerid;

    @TableField(exist = false)
    private String providername;

    private Integer categoryid;

    @TableField(exist = false)
    private String categoryname;

    /** 商品名称拼音 */
    private String pinyin;

    /** 商品名称拼音首字母简写 */
    private String abbreviation;

    /** 是否管理序列号 0=否 1=是 */
    private Integer isSerialManaged;

    /** 退货后是否直接回库 0=待检 1=直接可售 */
    private Integer returnResaleable;

    /** 销售量（非数据库字段） */
    @TableField(exist = false)
    private Integer salesCount;

}
