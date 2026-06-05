package com.sunlee.bus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sunlee.bus.entity.Stocktake;

public interface IStocktakeService extends IService<Stocktake> {

    /**
     * 创建盘点单（自动加载当前库存）
     */
    Stocktake createStocktake(String operator, String remark);

    /**
     * 提交盘点结果（更新库存）
     */
    void submitStocktake(Integer stocktakeId);

    /**
     * 取消盘点单
     */
    void cancelStocktake(Integer stocktakeId);
}
