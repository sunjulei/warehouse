package com.sunlee.bus.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sunlee.bus.entity.Customer;
import com.sunlee.bus.entity.OperationLog;
import com.sunlee.bus.service.ICustomerService;
import com.sunlee.bus.service.IOperationLogService;
import com.sunlee.bus.vo.CustomerVo;
import com.sunlee.sys.common.Constast;
import com.sunlee.sys.common.DataGridView;
import com.sunlee.sys.common.ResultObj;
import com.sunlee.sys.common.WebUtils;
import com.sunlee.sys.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * InnoDB free: 9216 kB 前端控制器
 * </p>
 *
 * @author sunlee
 * @since 2026-03-15
 */
@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private ICustomerService customerService;

    @Autowired
    private IOperationLogService operationLogService;

    /**
     * 查询所有的客户
     * @param customerVo
     * @return
     */
    @RequestMapping("loadAllCustomer")
    public DataGridView loadAllCustomer(CustomerVo customerVo){
        //1.声明一个分页page对象
        IPage<Customer> page = new Page<Customer>(customerVo.getPage(),customerVo.getLimit());
        //2.声明一个queryWrapper
        QueryWrapper<Customer> queryWrapper = new QueryWrapper<Customer>();
        queryWrapper.like(StringUtils.isNotBlank(customerVo.getCustomername()),"customername",customerVo.getCustomername());
        queryWrapper.like(StringUtils.isNotBlank(customerVo.getConnectionperson()),"connectionperson",customerVo.getConnectionperson());
        queryWrapper.like(StringUtils.isNotBlank(customerVo.getPhone()),"phone",customerVo.getPhone());
        queryWrapper.orderByDesc("id");
        customerService.page(page,queryWrapper);
        return new DataGridView(page.getTotal(),page.getRecords());
    }

    /**
     * 添加一个客户
     * @param customerVo
     * @return
     */
    @RequestMapping("addCustomer")
    public ResultObj addCustomer(CustomerVo customerVo){
        try {
            customerService.save(customerVo);
            User user = (User) WebUtils.getSession().getAttribute("user");
            OperationLog log = new OperationLog();
            log.setType("添加");
            log.setModule("客户管理");
            log.setDescription("添加客户: " + customerVo.getCustomername());
            log.setOperateperson(user != null ? user.getName() : "未知用户");
            log.setOperatetime(new Date());
            operationLogService.save(log);
            return ResultObj.ADD_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.ADD_ERROR;
        }
    }

    /**
     * 修改一个客户
     * @param customerVo
     * @return
     */
    @RequestMapping("updateCustomer")
    public ResultObj updateCustomer(CustomerVo customerVo){
        try {
            customerService.updateById(customerVo);
            User user = (User) WebUtils.getSession().getAttribute("user");
            OperationLog log = new OperationLog();
            log.setType("修改");
            log.setModule("客户管理");
            log.setDescription("修改客户: " + customerVo.getCustomername());
            log.setOperateperson(user != null ? user.getName() : "未知用户");
            log.setOperatetime(new Date());
            operationLogService.save(log);
            return ResultObj.UPDATE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.UPDATE_ERROR;
        }
    }

    /**
     * 删除一个客户
     * @param id 客户的ID
     * @return
     */
    @Operation(summary = "删除一个客户", description = "删除一个客户")
    @Parameters({@Parameter(name = "id", description = "客户ID", required = true)})
    @RequestMapping(value = "deleteCustomer",method = RequestMethod.DELETE)
    public ResultObj deleteCustomer(Integer id){
        try {
            Customer customer = customerService.getById(id);
            customerService.deleteCustomerById(id);
            User user = (User) WebUtils.getSession().getAttribute("user");
            OperationLog log = new OperationLog();
            log.setType("删除");
            log.setModule("客户管理");
            log.setDescription("删除客户: " + (customer != null ? customer.getCustomername() : "ID=" + id));
            log.setOperateperson(user != null ? user.getName() : "未知用户");
            log.setOperatetime(new Date());
            operationLogService.save(log);
            return ResultObj.DELETE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.DELETE_ERROR;
        }
    }


    /**
     * 加载所有客户的下拉列表
     * @return
     */
    @RequestMapping("loadAllCustomerForSelect")
    public DataGridView loadAllCustomerForSelect(){
        QueryWrapper<Customer> queryWrapper = new QueryWrapper<Customer>();
        queryWrapper.eq("available", Constast.AVAILABLE_TRUE);
        List<Customer> list = customerService.list(queryWrapper);
        return new DataGridView(list);
    }

}

