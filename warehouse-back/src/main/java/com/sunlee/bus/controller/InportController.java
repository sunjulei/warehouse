package com.sunlee.bus.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sunlee.bus.entity.Goods;
import com.sunlee.bus.entity.Inport;
import com.sunlee.bus.entity.Provider;
import com.sunlee.bus.service.IGoodsService;
import com.sunlee.bus.service.IInportService;
import com.sunlee.bus.service.IOperationLogService;
import com.sunlee.bus.service.IProviderService;
import com.sunlee.bus.vo.InportVo;
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
@RequestMapping("inport")
public class InportController {

    @Autowired
    private IInportService inportService;

    @Autowired
    private IProviderService providerService;

    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private IOperationLogService operationLogService;

    @RequestMapping("loadAllInport")
    public DataGridView loadAllInport(InportVo inportVo) {
        IPage<Inport> page = new Page<>(inportVo.getPage(), inportVo.getLimit());
        QueryWrapper<Inport> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(inportVo.getProviderid() != null && inportVo.getProviderid() != 0, "providerid", inportVo.getProviderid());
        queryWrapper.eq(inportVo.getGoodsid() != null && inportVo.getGoodsid() != 0, "goodsid", inportVo.getGoodsid());
        queryWrapper.ge(inportVo.getStartTime() != null, "inporttime", inportVo.getStartTime());
        queryWrapper.le(inportVo.getEndTime() != null, "inporttime", inportVo.getEndTime());
        queryWrapper.orderByDesc("inporttime");
        inportService.page(page, queryWrapper);
        List<Inport> records = page.getRecords();
        for (Inport inport : records) {
            Provider provider = providerService.getById(inport.getProviderid());
            if (provider != null) {
                inport.setProvidername(provider.getProvidername());
            }
            Goods goods = goodsService.getById(inport.getGoodsid());
            if (goods != null) {
                inport.setGoodsname(goods.getGoodsname());
                inport.setSize(goods.getSize());
            }
        }
        return new DataGridView(page.getTotal(), records);
    }

    @OperationLog(type = "添加", module = "商品进货", description = "'进货商品ID: ' + #args[0].goodsid + ', 数量: ' + #args[0].number")
    @RequestMapping("addInport")
    public ResultObj addInport(InportVo inportVo) {
        try {
            User user = (User) WebUtils.getSession().getAttribute("user");
            inportVo.setOperateperson(user.getName());
            inportVo.setInporttime(new Date());
            inportService.save(inportVo);
            return ResultObj.ADD_SUCCESS;
        } catch (Exception e) {
            log.error("添加进货失败: {}", e.getMessage(), e);
            return ResultObj.ADD_ERROR;
        }
    }

    @RequestMapping("batchAddInport")
    public ResultObj batchAddInport(@RequestBody List<Inport> list) {
        try {
            User user = (User) WebUtils.getSession().getAttribute("user");
            Date now = new Date();
            // 生成订单号：年月日时分秒 + 4位序号
            String dateStr = new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(now);
            String orderNo = "IP" + dateStr + inportService.getNextOrderSeq(dateStr);
            int totalNumber = 0;
            for (Inport inport : list) {
                inport.setOrderno(orderNo);
                inport.setOperateperson(user.getName());
                inport.setInporttime(now);
                totalNumber += inport.getNumber();
            }
            inportService.batchSave(list);
            // 手动记录操作日志
            com.sunlee.bus.entity.OperationLog logEntity = new com.sunlee.bus.entity.OperationLog();
            logEntity.setType("添加");
            logEntity.setModule("商品进货");
            logEntity.setDescription("批量进货, 订单号: " + orderNo + ", 共" + list.size() + "种商品, 总数量: " + totalNumber);
            logEntity.setOperateperson(user.getName());
            logEntity.setOperatetime(now);
            operationLogService.save(logEntity);
            return ResultObj.ADD_SUCCESS;
        } catch (Exception e) {
            log.error("批量进货失败: {}", e.getMessage(), e);
            e.printStackTrace();
            return new ResultObj(Constast.ERROR, "添加失败: " + e.getMessage());
        }
    }

    @OperationLog(type = "修改", module = "商品进货", description = "'修改进货记录ID: ' + #args[0].id")
    @RequestMapping("updateInport")
    public ResultObj updateInport(InportVo inportVo) {
        try {
            inportService.updateById(inportVo);
            return ResultObj.UPDATE_SUCCESS;
        } catch (Exception e) {
            log.error("修改进货记录失败: {}", e.getMessage(), e);
            return ResultObj.UPDATE_ERROR;
        }
    }

    @OperationLog(type = "删除", module = "商品进货", description = "'删除进货记录ID: ' + #args[0]")
    @RequestMapping("deleteInport")
    public ResultObj deleteInport(Integer id) {
        try {
            inportService.deleteInport(id);
            return ResultObj.DELETE_SUCCESS;
        } catch (Exception e) {
            log.error("删除进货记录失败: {}", e.getMessage(), e);
            return ResultObj.DELETE_ERROR;
        }
    }

    /**
     * 查询进货订单列表
     */
    @RequestMapping("loadAllOrders")
    public DataGridView loadAllOrders(InportVo inportVo) {
        return inportService.queryOrders(inportVo);
    }

    /**
     * 查询订单详情
     */
    @RequestMapping("loadOrderDetail")
    public DataGridView loadOrderDetail(String orderNo) {
        List<Inport> list = inportService.queryOrderDetail(orderNo);
        for (Inport inport : list) {
            Provider provider = providerService.getById(inport.getProviderid());
            if (provider != null) {
                inport.setProvidername(provider.getProvidername());
            }
            Goods goods = goodsService.getById(inport.getGoodsid());
            if (goods != null) {
                inport.setGoodsname(goods.getGoodsname());
                inport.setSize(goods.getSize());
            }
        }
        return new DataGridView((long) list.size(), list);
    }

    /**
     * 单商品退货
     */
    @OperationLog(type = "退货", module = "商品进货", description = "'单商品退货, 进货ID: ' + #args[0]")
    @RequestMapping("returnSingleGoods")
    public ResultObj returnSingleGoods(Integer inportId, Integer returnNumber) {
        try {
            inportService.returnSingleGoods(inportId, returnNumber);
            return new ResultObj(Constast.OK, "退货成功");
        } catch (Exception e) {
            log.error("单商品退货失败: {}", e.getMessage(), e);
            return new ResultObj(Constast.ERROR, "退货失败: " + e.getMessage());
        }
    }

    /**
     * 整单退货
     */
    @OperationLog(type = "退货", module = "商品进货", description = "'整单退货, 订单号: ' + #args[0]")
    @RequestMapping("returnOrder")
    public ResultObj returnOrder(String orderNo) {
        try {
            inportService.returnOrder(orderNo);
            return new ResultObj(Constast.OK, "退货成功");
        } catch (Exception e) {
            log.error("整单退货失败: {}", e.getMessage(), e);
            return new ResultObj(Constast.ERROR, "退货失败: " + e.getMessage());
        }
    }

    /**
     * 向订单添加商品
     */
    @OperationLog(type = "加货", module = "商品进货", description = "'向订单 ' + #args[0].orderno + ' 添加商品'")
    @RequestMapping("addToOrder")
    public ResultObj addToOrder(@RequestBody List<Inport> list) {
        try {
            User user = (User) WebUtils.getSession().getAttribute("user");
            Date now = new Date();
            for (Inport inport : list) {
                inport.setOperateperson(user.getName());
                inport.setInporttime(now);
            }
            inportService.addToOrder(list);
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
    public DataGridView loadReturnAddRecords(InportVo inportVo) {
        return inportService.queryReturnAddRecords(inportVo);
    }
}
