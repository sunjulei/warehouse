package com.sunlee.bus.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sunlee.bus.entity.Customer;
import com.sunlee.bus.entity.Goods;
import com.sunlee.bus.entity.Sales;
import com.sunlee.bus.service.ICustomerService;
import com.sunlee.bus.service.IGoodsService;
import com.sunlee.bus.service.IOperationLogService;
import com.sunlee.bus.service.ISalesService;
import com.sunlee.bus.vo.SalesVo;
import com.sunlee.sys.annotation.OperationLog;
import com.sunlee.sys.common.Constast;
import com.sunlee.sys.common.DataGridView;
import com.sunlee.sys.common.ResultObj;
import com.sunlee.sys.common.WebUtils;
import com.sunlee.sys.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
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

    @Autowired
    private IOperationLogService operationLogService;

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
            return ResultObj.error("添加失败: " + e.getMessage());
        }
    }

    @OperationLog(type = "添加", module = "商品销售", description = "''")
    @RequestMapping("batchAddSales")
    public ResultObj batchAddSales(@RequestBody List<Sales> list) {
        try {
            User user = (User) WebUtils.getSession().getAttribute("user");
            Date now = new Date();
            // 生成订单号：UUID 前缀（高并发安全）
            String orderNo = "SO" + java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
            int totalNumber = 0;
            for (Sales sales : list) {
                sales.setOrderno(orderNo);
                sales.setOperateperson(user.getName());
                sales.setSalestime(now);
                totalNumber += sales.getNumber();
            }
            salesService.batchSave(list);
            // 手动记录操作日志
            com.sunlee.bus.entity.OperationLog logEntity = new com.sunlee.bus.entity.OperationLog();
            logEntity.setType("添加");
            logEntity.setModule("商品销售");
            logEntity.setDescription("批量销售, 订单号: " + orderNo + ", 共" + list.size() + "种商品, 总数量: " + totalNumber);
            logEntity.setOperateperson(user.getName());
            logEntity.setOperatetime(now);
            operationLogService.save(logEntity);
            return ResultObj.ADD_SUCCESS;
        } catch (Exception e) {
            log.error("批量销售失败: {}", e.getMessage(), e);
            e.printStackTrace();
            return new ResultObj(Constast.ERROR, "添加失败: " + e.getMessage());
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
            return ResultObj.error("修改失败: " + e.getMessage());
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
            return ResultObj.error("删除失败: " + e.getMessage());
        }
    }

    /**
     * 查询订单列表（按订单号分组）
     */
    @RequestMapping("loadAllOrders")
    public DataGridView loadAllOrders(SalesVo salesVo) {
        return salesService.queryOrders(salesVo);
    }

    /**
     * 查询订单详情
     */
    @RequestMapping("loadOrderDetail")
    public DataGridView loadOrderDetail(String orderNo) {
        List<Sales> list = salesService.queryOrderDetail(orderNo);
        for (Sales sales : list) {
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
        return new DataGridView((long) list.size(), list);
    }

    /**
     * 单商品退货
     */
    @OperationLog(type = "退货", module = "商品销售", description = "'单商品退货, 销售ID: ' + #args[0]")
    @RequestMapping("returnSingleGoods")
    public ResultObj returnSingleGoods(Integer salesId, Integer returnNumber) {
        try {
            salesService.returnSingleGoods(salesId, returnNumber);
            return new ResultObj(Constast.OK, "退货成功");
        } catch (Exception e) {
            log.error("单商品退货失败: {}", e.getMessage(), e);
            return new ResultObj(Constast.ERROR, "退货失败: " + e.getMessage());
        }
    }

    /**
     * 整单退货
     */
    @OperationLog(type = "退货", module = "商品销售", description = "'整单退货, 订单号: ' + #args[0]")
    @RequestMapping("returnOrder")
    public ResultObj returnOrder(String orderNo) {
        try {
            salesService.returnOrder(orderNo);
            return new ResultObj(Constast.OK, "退货成功");
        } catch (Exception e) {
            log.error("整单退货失败: {}", e.getMessage(), e);
            return new ResultObj(Constast.ERROR, "退货失败: " + e.getMessage());
        }
    }

    /**
     * 向订单添加商品
     */
    @OperationLog(type = "加货", module = "商品销售", description = "'向订单 ' + #args[0].orderno + ' 添加商品'")
    @RequestMapping("addToOrder")
    public ResultObj addToOrder(@RequestBody List<Sales> list) {
        try {
            User user = (User) WebUtils.getSession().getAttribute("user");
            Date now = new Date();
            for (Sales sales : list) {
                sales.setOperateperson(user.getName());
                sales.setSalestime(now);
            }
            salesService.addToOrder(list);
            return new ResultObj(Constast.OK, "加货成功");
        } catch (Exception e) {
            log.error("加货失败: {}", e.getMessage(), e);
            return new ResultObj(Constast.ERROR, "加货失败: " + e.getMessage());
        }
    }

    /**
     * 查询退加货记录
     */
    @RequestMapping("loadReturnAddRecords")
    public DataGridView loadReturnAddRecords(SalesVo salesVo) {
        return salesService.queryReturnAddRecords(salesVo);
    }
}
