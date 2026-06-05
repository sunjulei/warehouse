package com.sunlee.bus.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sunlee.bus.entity.Goods;
import com.sunlee.bus.entity.Retail;
import com.sunlee.bus.service.IGoodsService;
import com.sunlee.bus.service.IRetailService;
import com.sunlee.bus.vo.RetailVo;
import com.sunlee.sys.annotation.OperationLog;
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
@RequestMapping("/retail")
public class RetailController {

    @Autowired
    private IRetailService retailService;

    @Autowired
    private IGoodsService goodsService;

    @RequestMapping("loadAllRetail")
    public DataGridView loadAllRetail(RetailVo retailVo) {
        IPage<Retail> page = new Page<>(retailVo.getPage(), retailVo.getLimit());
        QueryWrapper<Retail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(retailVo.getGoodsid() != null && retailVo.getGoodsid() != 0, "goodsid", retailVo.getGoodsid());
        queryWrapper.ge(retailVo.getStartTime() != null, "retailtime", retailVo.getStartTime());
        queryWrapper.le(retailVo.getEndTime() != null, "retailtime", retailVo.getEndTime());
        queryWrapper.orderByDesc("retailtime");
        retailService.page(page, queryWrapper);
        List<Retail> records = page.getRecords();
        for (Retail retail : records) {
            Goods goods = goodsService.getById(retail.getGoodsid());
            if (goods != null) {
                retail.setGoodsname(goods.getGoodsname());
                retail.setSize(goods.getSize());
            }
        }
        return new DataGridView(page.getTotal(), records);
    }

    @OperationLog(type = "添加", module = "散客零售", description = "'零售商品ID: ' + #args[0].goodsid + ', 数量: ' + #args[0].number")
    @RequestMapping("addRetail")
    public ResultObj addRetail(RetailVo retailVo) {
        try {
            User user = (User) WebUtils.getSession().getAttribute("user");
            retailVo.setOperateperson(user.getName());
            retailVo.setRetailtime(new Date());
            retailService.save(retailVo);
            return ResultObj.ADD_SUCCESS;
        } catch (Exception e) {
            log.error("添加零售失败: {}", e.getMessage(), e);
            return ResultObj.ADD_ERROR;
        }
    }

    @OperationLog(type = "添加", module = "散客零售", description = "'批量零售, 共' + #args[0].size() + '种商品'")
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
            return ResultObj.ADD_SUCCESS;
        } catch (Exception e) {
            log.error("批量零售失败: {}", e.getMessage(), e);
            return ResultObj.ADD_ERROR;
        }
    }

    @OperationLog(type = "修改", module = "散客零售", description = "'修改零售记录ID: ' + #args[0].id")
    @RequestMapping("updateRetail")
    public ResultObj updateRetail(RetailVo retailVo) {
        try {
            retailService.updateById(retailVo);
            return ResultObj.UPDATE_SUCCESS;
        } catch (Exception e) {
            log.error("修改零售记录失败: {}", e.getMessage(), e);
            return ResultObj.UPDATE_ERROR;
        }
    }

    @OperationLog(type = "删除", module = "散客零售", description = "'删除零售记录ID: ' + #args[0]")
    @RequestMapping("deleteRetail")
    public ResultObj deleteRetail(Integer id) {
        try {
            retailService.deleteRetail(id);
            return ResultObj.DELETE_SUCCESS;
        } catch (Exception e) {
            log.error("删除零售记录失败: {}", e.getMessage(), e);
            return ResultObj.DELETE_ERROR;
        }
    }
}
