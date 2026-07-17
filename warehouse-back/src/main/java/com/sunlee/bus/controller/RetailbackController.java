package com.sunlee.bus.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sunlee.bus.entity.Goods;
import com.sunlee.bus.entity.Retailback;
import com.sunlee.bus.service.IGoodsService;
import com.sunlee.bus.service.IRetailbackService;
import com.sunlee.bus.vo.RetailbackVo;
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
@RequestMapping("/retailback")
public class RetailbackController {

    @Autowired
    private IRetailbackService retailbackService;

    @Autowired
    private IGoodsService goodsService;

    @OperationLog(type = "添加", module = "零售退回", description = "'零售退货, 零售单ID: ' + #args[0] + ', 数量: ' + #args[1]")
    @RequestMapping("addRetailback")
    public ResultObj addRetailback(Integer id, Integer number, String remark) {
        try {
            retailbackService.addRetailback(id, number, remark);
            return ResultObj.BACKINPORT_SUCCESS;
        } catch (RuntimeException e) {
            log.warn("零售退货失败: {}", e.getMessage());
            return new ResultObj(-1, e.getMessage());
        } catch (Exception e) {
            log.error("零售退货失败: {}", e.getMessage(), e);
            return ResultObj.error("退货失败: " + e.getMessage());
        }
    }

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

    @OperationLog(type = "删除", module = "零售退回", description = "'取消零售退货ID: ' + #args[0]")
    @RequestMapping("cancelRetailback")
    public ResultObj cancelRetailback(Integer id) {
        try {
            retailbackService.cancelRetailback(id);
            return ResultObj.CANCEL_SUCCESS;
        } catch (Exception e) {
            log.error("取消零售退货失败: {}", e.getMessage(), e);
            return ResultObj.error("取消失败: " + e.getMessage());
        }
    }
}
