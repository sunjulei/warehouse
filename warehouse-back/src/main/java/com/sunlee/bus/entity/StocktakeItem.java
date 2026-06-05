package com.sunlee.bus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;

/**
 * 盘点明细
 */
@Data
@TableName("bus_stocktake_item")
public class StocktakeItem implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer stocktakeId;

    private Integer goodsid;

    @TableField(exist = false)
    private String goodsname;

    private Integer systemNum;

    private Integer actualNum;

    private Integer diffNum;

    private String remark;
}
