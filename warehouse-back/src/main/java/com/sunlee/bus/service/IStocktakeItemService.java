package com.sunlee.bus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sunlee.bus.entity.StocktakeItem;
import java.util.List;

public interface IStocktakeItemService extends IService<StocktakeItem> {
    List<StocktakeItem> loadByStocktakeId(Integer stocktakeId);
}
