package com.sunlee.bus.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sunlee.bus.entity.Provider;
import com.sunlee.bus.service.IProviderService;
import com.sunlee.bus.vo.ProviderVo;
import com.sunlee.sys.annotation.OperationLog;
import com.sunlee.sys.common.Constast;
import com.sunlee.sys.common.DataGridView;
import com.sunlee.sys.common.ResultObj;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/provider")
public class ProviderController {

    @Autowired
    private IProviderService providerService;

    @RequestMapping("loadAllProvider")
    public DataGridView loadAllProvider(ProviderVo providerVo) {
        IPage<Provider> page = new Page<>(providerVo.getPage(), providerVo.getLimit());
        QueryWrapper<Provider> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(providerVo.getProvidername()), "providername", providerVo.getProvidername());
        queryWrapper.like(StringUtils.isNotBlank(providerVo.getConnectionperson()), "connectionperson", providerVo.getConnectionperson());
        queryWrapper.like(StringUtils.isNotBlank(providerVo.getPhone()), "phone", providerVo.getPhone());
        queryWrapper.orderByDesc("id");
        providerService.page(page, queryWrapper);
        return new DataGridView(page.getTotal(), page.getRecords());
    }

    @OperationLog(type = "添加", module = "供应商管理", description = "'添加供应商: ' + #args[0].providername")
    @RequestMapping("addProvider")
    public ResultObj addProvider(ProviderVo providerVo) {
        try {
            providerVo.setAvailable((Integer) Constast.AVAILABLE_TRUE);
            providerService.save(providerVo);
            return ResultObj.ADD_SUCCESS;
        } catch (Exception e) {
            log.error("添加供应商失败: {}", e.getMessage(), e);
            return ResultObj.ADD_ERROR;
        }
    }

    @OperationLog(type = "修改", module = "供应商管理", description = "'修改供应商: ' + #args[0].providername")
    @RequestMapping("updateProvider")
    public ResultObj updateProvider(ProviderVo providerVo) {
        try {
            providerService.updateById(providerVo);
            return ResultObj.UPDATE_SUCCESS;
        } catch (Exception e) {
            log.error("修改供应商失败: {}", e.getMessage(), e);
            return ResultObj.UPDATE_ERROR;
        }
    }

    @OperationLog(type = "删除", module = "供应商管理", description = "'删除供应商ID: ' + #args[0]")
    @RequestMapping("deleteProvider")
    public ResultObj deleteProvider(Integer id) {
        try {
            providerService.deleteProviderById(id);
            return ResultObj.DELETE_SUCCESS;
        } catch (Exception e) {
            log.error("删除供应商失败: {}", e.getMessage(), e);
            return ResultObj.DELETE_ERROR;
        }
    }

    @RequestMapping("loadAllProviderForSelect")
    public DataGridView loadAllProviderForSelect() {
        QueryWrapper<Provider> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("available", Constast.AVAILABLE_TRUE);
        List<Provider> list = providerService.list(queryWrapper);
        return new DataGridView(list);
    }
}
