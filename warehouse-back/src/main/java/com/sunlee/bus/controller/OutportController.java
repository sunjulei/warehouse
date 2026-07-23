package com.sunlee.bus.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sunlee.bus.entity.Goods;
import com.sunlee.bus.entity.Outport;
import com.sunlee.bus.entity.Provider;
import com.sunlee.bus.service.IGoodsService;
import com.sunlee.bus.service.IOutportService;
import com.sunlee.bus.service.IProviderService;
import com.sunlee.bus.vo.OutportVo;
import com.sunlee.sys.common.DataGridView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/outport")
public class OutportController {

    @Autowired
    private IOutportService outportService;

    @Autowired
    private IProviderService providerService;

    @Autowired
    private IGoodsService goodsService;

    /**
     * 历史进货退货记录查询（只读）。
     * 退货写操作已统一到 /inport/returnSingleGoods 与 /inport/returnOrder，
     * 退货流水见 bus_inport_log（退加货记录页）。
     */
    @RequestMapping("loadAllOutport")
    public DataGridView loadAllOutport(OutportVo outportVo) {
        IPage<Outport> page = new Page<>(outportVo.getPage(), outportVo.getLimit());
        QueryWrapper<Outport> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(outportVo.getProviderid() != null && outportVo.getProviderid() != 0, "providerid", outportVo.getProviderid());
        queryWrapper.eq(outportVo.getGoodsid() != null && outportVo.getGoodsid() != 0, "goodsid", outportVo.getGoodsid());
        queryWrapper.ge(outportVo.getStartTime() != null, "outputtime", outportVo.getStartTime());
        queryWrapper.le(outportVo.getEndTime() != null, "outputtime", outportVo.getEndTime());
        queryWrapper.orderByDesc("outputtime");
        outportService.page(page, queryWrapper);
        List<Outport> records = page.getRecords();
        for (Outport outport : records) {
            Provider provider = providerService.getById(outport.getProviderid());
            if (provider != null) {
                outport.setProvidername(provider.getProvidername());
            }
            Goods goods = goodsService.getById(outport.getGoodsid());
            if (goods != null) {
                outport.setGoodsname(goods.getGoodsname());
                outport.setSize(goods.getSize());
            }
        }
        return new DataGridView(page.getTotal(), records);
    }
}
