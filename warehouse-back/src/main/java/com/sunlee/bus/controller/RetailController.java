package com.sunlee.bus.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sunlee.bus.entity.Goods;
import com.sunlee.bus.entity.OperationLog;
import com.sunlee.bus.entity.Retail;
import com.sunlee.bus.service.IGoodsService;
import com.sunlee.bus.service.IOperationLogService;
import com.sunlee.bus.service.IRetailService;
import com.sunlee.bus.vo.RetailVo;
import com.sunlee.sys.common.DataGridView;
import com.sunlee.sys.common.ResultObj;
import com.sunlee.sys.common.WebUtils;
import com.sunlee.sys.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.RequestBody;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/retail")
public class RetailController {

    @Autowired
    private IRetailService retailService;

    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private IOperationLogService operationLogService;

    @RequestMapping("loadAllRetail")
    public DataGridView loadAllRetail(RetailVo retailVo) {
        IPage<Retail> page = new Page<>(retailVo.getPage(), retailVo.getLimit());
        QueryWrapper<Retail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(retailVo.getGoodsid() != null && retailVo.getGoodsid() != 0, "goodsid", retailVo.getGoodsid());
        queryWrapper.ge(retailVo.getStartTime() != null, "retailtime", retailVo.getStartTime());
        queryWrapper.le(retailVo.getEndTime() != null, "retailtime", retailVo.getEndTime());
        queryWrapper.orderByDesc("retailtime");
        IPage<Retail> page1 = retailService.page(page, queryWrapper);
        List<Retail> records = page1.getRecords();
        for (Retail retail : records) {
            Goods goods = goodsService.getById(retail.getGoodsid());
            if (goods != null) {
                retail.setGoodsname(goods.getGoodsname());
                retail.setSize(goods.getSize());
            }
        }
        return new DataGridView(page1.getTotal(), page1.getRecords());
    }

    @RequestMapping("addRetail")
    public ResultObj addRetail(RetailVo retailVo) {
        try {
            User user = (User) WebUtils.getSession().getAttribute("user");
            retailVo.setOperateperson(user.getName());
            retailVo.setRetailtime(new Date());
            retailService.save(retailVo);
            Goods goods = goodsService.getById(retailVo.getGoodsid());
            OperationLog opLog = new OperationLog();
            opLog.setType("添加");
            opLog.setModule("散客零售");
            opLog.setDescription("零售商品: " + (goods != null ? goods.getGoodsname() : "ID=" + retailVo.getGoodsid()) + ", 数量: " + retailVo.getNumber());
            opLog.setOperateperson(user.getName());
            opLog.setOperatetime(new Date());
            operationLogService.save(opLog);
            return ResultObj.ADD_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.ADD_ERROR;
        }
    }

    @RequestMapping("batchAddRetail")
    public ResultObj batchAddRetail(@RequestBody List<Retail> list) {
        try {
            User user = (User) WebUtils.getSession().getAttribute("user");
            Date now = new Date();
            for (Retail retail : list) {
                retail.setOperateperson(user.getName());
                retail.setRetailtime(now);
            }
            retailService.batchSave(list);
            OperationLog opLog = new OperationLog();
            opLog.setType("添加");
            opLog.setModule("散客零售");
            opLog.setDescription("批量零售, 共" + list.size() + "种商品");
            opLog.setOperateperson(user.getName());
            opLog.setOperatetime(now);
            operationLogService.save(opLog);
            return ResultObj.ADD_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.ADD_ERROR;
        }
    }

    @RequestMapping("updateRetail")
    public ResultObj updateRetail(RetailVo retailVo) {
        try {
            retailService.updateById(retailVo);
            User user = (User) WebUtils.getSession().getAttribute("user");
            OperationLog opLog = new OperationLog();
            opLog.setType("修改");
            opLog.setModule("散客零售");
            opLog.setDescription("修改零售记录 ID=" + retailVo.getId());
            opLog.setOperateperson(user != null ? user.getName() : "未知用户");
            opLog.setOperatetime(new Date());
            operationLogService.save(opLog);
            return ResultObj.UPDATE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.UPDATE_ERROR;
        }
    }

    @RequestMapping("deleteRetail")
    public ResultObj deleteRetail(Integer id) {
        try {
            retailService.deleteRetail(id);
            User user = (User) WebUtils.getSession().getAttribute("user");
            OperationLog opLog = new OperationLog();
            opLog.setType("删除");
            opLog.setModule("散客零售");
            opLog.setDescription("删除零售记录 ID=" + id);
            opLog.setOperateperson(user != null ? user.getName() : "未知用户");
            opLog.setOperatetime(new Date());
            operationLogService.save(opLog);
            return ResultObj.DELETE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.DELETE_ERROR;
        }
    }

}
