package com.sunlee.bus.service;

import com.sunlee.bus.entity.Salesback;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * InnoDB free: 9216 kB 服务类
 * </p>
 *
 * @author sunlee
 * @since 2026-05-01
 */
public interface ISalesbackService extends IService<Salesback> {

    /**
     * 对商品销售进行退货处理
     * @param id    销售单ID
     * @param number    退货数量
     * @param remark    备注
     */
    void addSalesback(Integer id, Integer number, String remark);

    /**
     * 取消销售退货（回滚库存）
     * @param id 退货单ID
     */
    void cancelSalesback(Integer id);
}
