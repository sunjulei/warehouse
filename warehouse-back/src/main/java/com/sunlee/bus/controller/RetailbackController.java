package com.sunlee.bus.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sunlee.bus.entity.Goods;
import com.sunlee.bus.entity.Retailback;
import com.sunlee.bus.service.IGoodsService;
import com.sunlee.bus.service.IRetailbackService;
import com.sunlee.bus.vo.RetailbackVo;
import com.sunlee.sys.common.DataGridView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/retailback")
public class RetailbackController {

    @Autowired
    private IRetailbackService retailbackService;

    @Autowired
    private IGoodsService goodsService;

    /**
     * 历史零售退货记录查询（只读）。
     * 退货写操作已统一到 /retail/returnSingleGoods 与 /retail/returnOrder，
     * 退货流水见 bus_retail_log（零售退回记录页）。
     */
    @RequestMapping("loadAllRetailback")
    public DataGridView loadAllRetailback(RetailbackVo retailbackVo) {
        IPage<Retailback> page = new Page<>(retailbackVo.getPage(), retailbackVo.getLimit());
        QueryWrapper<Retailback> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(retailbackVo.getGoodsid() != null && retailbackVo.getGoodsid() != 0, "goodsid", retailbackVo.getGoodsid());
        queryWrapper.ge(retailbackVo.getStartTime() != null, "retailbacktime", retailbackVo.getStartTime());
        queryWrapper.le(retailbackVo.getEndTime() != null, "retailbacktime", retailbackVo.getEndTime());
        queryWrapper.orderByDesc("retailbacktime");
        retailbackService.page(page, queryWrapper);
        List<Retailback> records = page.getRecords();
        for (Retailback retailback : records) {
            Goods goods = goodsService.getById(retailback.getGoodsid());
            if (goods != null) {
                retailback.setGoodsname(goods.getGoodsname());
                retailback.setSize(goods.getSize());
            }
        }
        return new DataGridView(page.getTotal(), records);
    }
}
