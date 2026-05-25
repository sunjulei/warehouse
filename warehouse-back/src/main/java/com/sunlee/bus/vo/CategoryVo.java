package com.sunlee.bus.vo;

import com.sunlee.bus.entity.Category;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class CategoryVo extends Category {

    private Integer page = 1;
    private Integer limit = 10;

    private Integer[] ids;
}
