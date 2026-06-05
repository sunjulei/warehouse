package com.sunlee.bus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 盘点单
 */
@Data
@TableName("bus_stocktake")
public class Stocktake implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String stocktakeNo;

    /** 状态: 0进行中 1已完成 2已取消 */
    private Integer status;

    private String operator;

    private String remark;

    private Date createTime;

    private Date finishTime;

    @TableField(exist = false)
    private List<StocktakeItem> items;
}
