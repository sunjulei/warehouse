package com.sunlee.bus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("bus_retailback")
public class Retailback implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer goodsid;

    private Integer retailid;

    private String paytype;

    private Date retailbacktime;

    private Double retailbackprice;

    private String operateperson;

    private Integer number;

    private String remark;

    @TableLogic
    private Integer isdelete;

    @TableField(exist = false)
    private String goodsname;

    @TableField(exist = false)
    private String size;

}
