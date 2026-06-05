package com.sunlee.bus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sunlee.bus.entity.Goods;
import com.sunlee.bus.entity.Stocktake;
import com.sunlee.bus.entity.StocktakeItem;
import com.sunlee.bus.mapper.StocktakeMapper;
import com.sunlee.bus.service.IGoodsService;
import com.sunlee.bus.service.IStocktakeItemService;
import com.sunlee.bus.service.IStocktakeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@Transactional
public class StocktakeServiceImpl extends ServiceImpl<StocktakeMapper, Stocktake> implements IStocktakeService {

    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private IStocktakeItemService stocktakeItemService;

    @Override
    public Stocktake createStocktake(String operator, String remark) {
        // 生成盘点单号: ST + 日期 + 序号
        String no = "ST" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        Stocktake stocktake = new Stocktake();
        stocktake.setStocktakeNo(no);
        stocktake.setStatus(0);
        stocktake.setOperator(operator);
        stocktake.setRemark(remark);
        stocktake.setCreateTime(new Date());
        save(stocktake);

        // 自动加载所有商品当前库存作为盘点明细
        List<Goods> allGoods = goodsService.list();
        for (Goods goods : allGoods) {
            StocktakeItem item = new StocktakeItem();
            item.setStocktakeId(stocktake.getId());
            item.setGoodsid(goods.getId());
            item.setGoodsname(goods.getGoodsname());
            item.setSystemNum(goods.getNumber() != null ? goods.getNumber() : 0);
            item.setActualNum(null); // 待填写
            item.setDiffNum(null);
            stocktakeItemService.save(item);
        }
        stocktake.setItems(stocktakeItemService.loadByStocktakeId(stocktake.getId()));
        return stocktake;
    }

    @Override
    public void submitStocktake(Integer stocktakeId) {
        Stocktake stocktake = getById(stocktakeId);
        if (stocktake == null || stocktake.getStatus() != 0) {
            throw new RuntimeException("盘点单不存在或状态异常");
        }

        List<StocktakeItem> items = stocktakeItemService.loadByStocktakeId(stocktakeId);
        for (StocktakeItem item : items) {
            if (item.getActualNum() == null) {
                throw new RuntimeException("商品 [" + item.getGoodsname() + "] 尚未填写实际盘点数量");
            }
            // 计算差异
            item.setDiffNum(item.getActualNum() - item.getSystemNum());
            stocktakeItemService.updateById(item);

            // 更新商品库存为实际盘点数量
            if (!item.getActualNum().equals(item.getSystemNum())) {
                Goods goods = new Goods();
                goods.setId(item.getGoodsid());
                goods.setNumber(item.getActualNum());
                goodsService.updateById(goods);
            }
        }

        stocktake.setStatus(1);
        stocktake.setFinishTime(new Date());
        updateById(stocktake);
    }

    @Override
    public void cancelStocktake(Integer stocktakeId) {
        Stocktake stocktake = getById(stocktakeId);
        if (stocktake == null || stocktake.getStatus() != 0) {
            throw new RuntimeException("盘点单不存在或状态异常");
        }
        stocktake.setStatus(2);
        updateById(stocktake);
    }
}
