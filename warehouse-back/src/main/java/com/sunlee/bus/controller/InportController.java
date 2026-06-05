package com.sunlee.bus.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sunlee.bus.entity.Goods;
import com.sunlee.bus.entity.Inport;
import com.sunlee.bus.entity.Provider;
import com.sunlee.bus.service.IGoodsService;
import com.sunlee.bus.service.IInportService;
import com.sunlee.bus.service.IProviderService;
import com.sunlee.bus.vo.InportVo;
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
@RequestMapping("inport")
public class InportController {

    @Autowired
    private IInportService inportService;

    @Autowired
    private IProviderService providerService;

    @Autowired
    private IGoodsService goodsService;

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
}
