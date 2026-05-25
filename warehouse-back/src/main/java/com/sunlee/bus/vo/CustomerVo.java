package com.sunlee.bus.vo;

import com.sunlee.bus.entity.Customer;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: sunlee
 * @Date: 2026/03/20 9:30
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class CustomerVo extends Customer{

    /**
     * 分页参数，当前是第一页，每页10条数据
     */
    private Integer page=1;
    private Integer limit=10;

    /**
     * 批量删除客户，存放客户ID的数组
     */
    private Integer[] ids;

}
