package com.sunlee.bus.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sunlee.bus.entity.Goods;
import com.sunlee.bus.entity.OperationLog;
import com.sunlee.bus.entity.Retailback;
import com.sunlee.bus.service.IGoodsService;
import com.sunlee.bus.service.IOperationLogService;
import com.sunlee.bus.service.IRetailbackService;
import com.sunlee.bus.vo.RetailbackVo;
import com.sunlee.sys.common.DataGridView;
import com.sunlee.sys.common.ResultObj;
import com.sunlee.sys.common.WebUtils;
import com.sunlee.sys.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/retailback")
public class RetailbackController {

    @Autowired
    private IRetailbackService retailbackService;

    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private IOperationLogService operationLogService;

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
        return new DataGridView(page.getTotal(), page.getRecords());
    }

    @RequestMapping("addRetailback")
    public ResultObj addRetailback(Integer id, Integer number, String remark) {
        try {
            retailbackService.addRetailback(id, number, remark);
            User user = (User) WebUtils.getSession().getAttribute("user");
            OperationLog opLog = new OperationLog();
            opLog.setType("添加");
            opLog.setModule("零售退回");
            opLog.setDescription("零售退货, 零售单ID=" + id + ", 数量=" + number);
            opLog.setOperateperson(user != null ? user.getName() : "未知用户");
            opLog.setOperatetime(new Date());
            operationLogService.save(opLog);
            return ResultObj.BACKINPORT_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.BACKINPORT_ERROR;
        }
    }

    @RequestMapping("cancelRetailback")
    public ResultObj cancelRetailback(Integer id) {
        try {
            retailbackService.cancelRetailback(id);
            User user = (User) WebUtils.getSession().getAttribute("user");
            OperationLog opLog = new OperationLog();
            opLog.setType("删除");
            opLog.setModule("零售退回");
            opLog.setDescription("取消零售退货 ID=" + id);
            opLog.setOperateperson(user != null ? user.getName() : "未知用户");
            opLog.setOperatetime(new Date());
            operationLogService.save(opLog);
            return ResultObj.CANCEL_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.CANCEL_ERROR;
        }
    }

}
