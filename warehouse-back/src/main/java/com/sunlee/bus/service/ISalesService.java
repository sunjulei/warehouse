package com.sunlee.bus.service;

import com.sunlee.bus.entity.Sales;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * InnoDB free: 9216 kB 服务类
 * </p>
 *
 * @author sunlee
 * @since 2026-04-20
 */
public interface ISalesService extends IService<Sales> {

    /**
     * 删除销售记录并级联删除相关退货记录
     * @param id 销售单ID
     */
    void deleteSales(Integer id);
}
