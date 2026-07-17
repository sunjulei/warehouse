package com.sunlee.bus.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sunlee.bus.entity.Customer;
import com.sunlee.bus.service.ICustomerService;
import com.sunlee.bus.vo.CustomerVo;
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
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private ICustomerService customerService;

    @RequestMapping("loadAllCustomer")
    public DataGridView loadAllCustomer(CustomerVo customerVo) {
        IPage<Customer> page = new Page<>(customerVo.getPage(), customerVo.getLimit());
        QueryWrapper<Customer> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(customerVo.getCustomername()), "customername", customerVo.getCustomername());
        queryWrapper.like(StringUtils.isNotBlank(customerVo.getConnectionperson()), "connectionperson", customerVo.getConnectionperson());
        queryWrapper.like(StringUtils.isNotBlank(customerVo.getPhone()), "phone", customerVo.getPhone());
        queryWrapper.orderByDesc("id");
        customerService.page(page, queryWrapper);
        return new DataGridView(page.getTotal(), page.getRecords());
    }

    @OperationLog(type = "添加", module = "客户管理", description = "'添加客户: ' + #args[0].customername")
    @RequestMapping("addCustomer")
    public ResultObj addCustomer(CustomerVo customerVo) {
        try {
            customerService.save(customerVo);
            return ResultObj.ADD_SUCCESS;
        } catch (Exception e) {
            log.error("添加客户失败: {}", e.getMessage(), e);
            return ResultObj.error("添加失败: " + e.getMessage());
        }
    }

    @OperationLog(type = "修改", module = "客户管理", description = "'修改客户: ' + #args[0].customername")
    @RequestMapping("updateCustomer")
    public ResultObj updateCustomer(CustomerVo customerVo) {
        try {
            customerService.updateById(customerVo);
            return ResultObj.UPDATE_SUCCESS;
        } catch (Exception e) {
            log.error("修改客户失败: {}", e.getMessage(), e);
            return ResultObj.error("修改失败: " + e.getMessage());
        }
    }

    @OperationLog(type = "删除", module = "客户管理", description = "'删除客户ID: ' + #args[0]")
    @RequestMapping(value = "deleteCustomer")
    public ResultObj deleteCustomer(Integer id) {
        try {
            customerService.deleteCustomerById(id);
            return ResultObj.DELETE_SUCCESS;
        } catch (Exception e) {
            log.error("删除客户失败: {}", e.getMessage(), e);
            return ResultObj.error("删除失败: " + e.getMessage());
        }
    }

    @RequestMapping("loadAllCustomerForSelect")
    public DataGridView loadAllCustomerForSelect() {
        QueryWrapper<Customer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("available", Constast.AVAILABLE_TRUE);
        List<Customer> list = customerService.list(queryWrapper);
        return new DataGridView(list);
    }
}
