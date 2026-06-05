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
import com.sunlee.sys.annotation.OperationLog;
import com.sunlee.sys.common.DataGridView;
import com.sunlee.sys.common.ResultObj;
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

    @OperationLog(type = "添加", module = "销售退回", description = "'销售退货, 销售单ID: ' + #args[0] + ', 数量: ' + #args[1]")
    @RequestMapping("addSalesback")
    public ResultObj addSalesback(Integer id, Integer number, String remark) {
        try {
            salesbackService.addSalesback(id, number, remark);
            return ResultObj.BACKINPORT_SUCCESS;
        } catch (Exception e) {
            log.error("销售退货失败: {}", e.getMessage(), e);
            return ResultObj.BACKINPORT_ERROR;
        }
    }

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

    @OperationLog(type = "删除", module = "销售退回", description = "'取消销售退货ID: ' + #args[0]")
    @RequestMapping("cancelSalesback")
    public ResultObj cancelSalesback(Integer id) {
        try {
            salesbackService.cancelSalesback(id);
            return ResultObj.CANCEL_SUCCESS;
        } catch (Exception e) {
            log.error("取消销售退货失败: {}", e.getMessage(), e);
            return ResultObj.CANCEL_ERROR;
        }
    }
}
