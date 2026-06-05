package com.sunlee.bus.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sunlee.bus.entity.Customer;
import com.sunlee.bus.entity.Goods;
import com.sunlee.bus.entity.Sales;
import com.sunlee.bus.service.ICustomerService;
import com.sunlee.bus.service.IGoodsService;
import com.sunlee.bus.service.ISalesService;
import com.sunlee.bus.vo.SalesVo;
import com.sunlee.sys.annotation.OperationLog;
import com.sunlee.sys.common.DataGridView;
import com.sunlee.sys.common.ResultObj;
import com.sunlee.sys.common.WebUtils;
import com.sunlee.sys.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/sales")
public class SalesController {

    @Autowired
    private ISalesService salesService;

    @Autowired
    private ICustomerService customerService;

    @Autowired
    private IGoodsService goodsService;

    @RequestMapping("loadAllSales")
    public DataGridView loadAllSales(SalesVo salesVo) {
        IPage<Sales> page = new Page<>(salesVo.getPage(), salesVo.getLimit());
        QueryWrapper<Sales> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(salesVo.getCustomerid() != null && salesVo.getCustomerid() != 0, "customerid", salesVo.getCustomerid());
        queryWrapper.eq(salesVo.getGoodsid() != null && salesVo.getGoodsid() != 0, "goodsid", salesVo.getGoodsid());
        queryWrapper.ge(salesVo.getStartTime() != null, "salestime", salesVo.getStartTime());
        queryWrapper.le(salesVo.getEndTime() != null, "salestime", salesVo.getEndTime());
        salesService.page(page, queryWrapper);
        List<Sales> records = page.getRecords();
        for (Sales sales : records) {
            Customer customer = customerService.getById(sales.getCustomerid());
            if (customer != null) {
                sales.setCustomername(customer.getCustomername());
            }
            Goods goods = goodsService.getById(sales.getGoodsid());
            if (goods != null) {
                sales.setGoodsname(goods.getGoodsname());
                sales.setSize(goods.getSize());
            }
        }
        return new DataGridView(page.getTotal(), records);
    }

    @OperationLog(type = "添加", module = "商品销售", description = "'销售商品ID: ' + #args[0].goodsid + ', 数量: ' + #args[0].number")
    @RequestMapping("addSales")
    public ResultObj addSales(SalesVo salesVo) {
        try {
            User user = (User) WebUtils.getSession().getAttribute("user");
            salesVo.setOperateperson(user.getName());
            salesVo.setSalestime(new Date());
            salesService.save(salesVo);
            return ResultObj.ADD_SUCCESS;
        } catch (Exception e) {
            log.error("添加销售失败: {}", e.getMessage(), e);
            return ResultObj.ADD_ERROR;
        }
    }

    @OperationLog(type = "修改", module = "商品销售", description = "'修改销售记录ID: ' + #args[0].id")
    @RequestMapping("updateSales")
    public ResultObj updateSales(SalesVo salesVo) {
        try {
            salesService.updateById(salesVo);
            return ResultObj.UPDATE_SUCCESS;
        } catch (Exception e) {
            log.error("修改销售记录失败: {}", e.getMessage(), e);
            return ResultObj.UPDATE_ERROR;
        }
    }

    @OperationLog(type = "删除", module = "商品销售", description = "'删除销售记录ID: ' + #args[0]")
    @RequestMapping("deleteSales")
    public ResultObj deleteSales(Integer id) {
        try {
            salesService.deleteSales(id);
            return ResultObj.DELETE_SUCCESS;
        } catch (Exception e) {
            log.error("删除销售记录失败: {}", e.getMessage(), e);
            return ResultObj.DELETE_ERROR;
        }
    }
}
