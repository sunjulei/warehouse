package com.sunlee.bus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sunlee.bus.entity.StocktakeItem;
import com.sunlee.bus.mapper.StocktakeItemMapper;
import com.sunlee.bus.service.IStocktakeItemService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class StocktakeItemServiceImpl extends ServiceImpl<StocktakeItemMapper, StocktakeItem> implements IStocktakeItemService {

    @Override
    public List<StocktakeItem> loadByStocktakeId(Integer stocktakeId) {
        QueryWrapper<StocktakeItem> qw = new QueryWrapper<>();
        qw.eq("stocktake_id", stocktakeId);
        return list(qw);
    }
}
