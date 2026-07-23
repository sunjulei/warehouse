package com.sunlee.bus.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sunlee.bus.entity.Customer;
import com.sunlee.bus.entity.Goods;
import com.sunlee.bus.entity.Salesback;
import com.sunlee.bus.service.ICustomerService;
import com.sunlee.bus.service.IGoodsService;
import com.sunlee.bus.service.ISalesbackService;
import com.sunlee.bus.vo.SalesbackVo;
import com.sunlee.sys.common.DataGridView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/salesback")
public class SalesbackController {

    @Autowired
    private ISalesbackService salesbackService;

    @Autowired
    private ICustomerService customerService;

    @Autowired
    private IGoodsService goodsService;

    /**
     * 历史销售退货记录查询（只读）。
     * 退货写操作已统一到 /sales/returnSingleGoods 与 /sales/returnOrder，
     * 退货流水见 bus_sales_log（退加货记录页）。
     */
    @RequestMapping("loadAllSalesback")
    public DataGridView loadAllSalesback(SalesbackVo salesbackVo) {
        IPage<Salesback> page = new Page<>(salesbackVo.getPage(), salesbackVo.getLimit());
        QueryWrapper<Salesback> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(salesbackVo.getCustomerid() != null && salesbackVo.getCustomerid() != 0, "customerid", salesbackVo.getCustomerid());
        queryWrapper.eq(salesbackVo.getGoodsid() != null && salesbackVo.getGoodsid() != 0, "goodsid", salesbackVo.getGoodsid());
        queryWrapper.ge(salesbackVo.getStartTime() != null, "salesbacktime", salesbackVo.getStartTime());
        queryWrapper.le(salesbackVo.getEndTime() != null, "salesbacktime", salesbackVo.getEndTime());
        queryWrapper.orderByDesc("salesbacktime");
        salesbackService.page(page, queryWrapper);
        List<Salesback> records = page.getRecords();
        for (Salesback salesback : records) {
            Customer customer = customerService.getById(salesback.getCustomerid());
            if (customer != null) {
                salesback.setCustomername(customer.getCustomername());
            }
            Goods goods = goodsService.getById(salesback.getGoodsid());
            if (goods != null) {
                salesback.setGoodsname(goods.getGoodsname());
                salesback.setSize(goods.getSize());
            }
        }
        return new DataGridView(page.getTotal(), records);
    }
}
