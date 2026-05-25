package com.sunlee.bus.service;

import com.sunlee.bus.entity.Inport;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * InnoDB free: 9216 kB; (`providerid`) REFER `warehouse/bus_provider`(`id`); (`goo 服务类
 * </p>
 *
 * @author sunlee
 * @since 2026-04-01
 */
public interface IInportService extends IService<Inport> {

    /**
     * 删除进货记录并级联删除相关退货记录
     * @param id 进货单ID
     */
    void deleteInport(Integer id);
}
