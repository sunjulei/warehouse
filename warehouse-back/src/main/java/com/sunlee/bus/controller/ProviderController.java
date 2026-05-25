package com.sunlee.bus.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sunlee.bus.entity.OperationLog;
import com.sunlee.bus.entity.Provider;
import com.sunlee.bus.service.IOperationLogService;
import com.sunlee.bus.service.IProviderService;
import com.sunlee.bus.vo.ProviderVo;
import com.sunlee.sys.common.Constast;
import com.sunlee.sys.common.DataGridView;
import com.sunlee.sys.common.ResultObj;
import com.sunlee.sys.common.WebUtils;
import com.sunlee.sys.entity.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
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
@RequestMapping("/provider")
public class ProviderController {

    @Autowired
    private IProviderService providerService;

    @Autowired
    private IOperationLogService operationLogService;

    /**
     * 查询所有的供应商
     * @param providerVo
     * @return
     */
    @RequestMapping("loadAllProvider")
    public DataGridView loadAllProvider(ProviderVo providerVo){
        //1.声明一个分页page对象
        IPage<Provider> page = new Page(providerVo.getPage(),providerVo.getLimit());
        //2.声明一个queryWrapper
        QueryWrapper<Provider> queryWrapper = new QueryWrapper();
        queryWrapper.like(StringUtils.isNotBlank(providerVo.getProvidername()),"providername",providerVo.getProvidername());
        queryWrapper.like(StringUtils.isNotBlank(providerVo.getConnectionperson()),"connectionperson",providerVo.getConnectionperson());
        queryWrapper.like(StringUtils.isNotBlank(providerVo.getPhone()),"phone",providerVo.getPhone());
        queryWrapper.orderByDesc("id");
        providerService.page(page,queryWrapper);
        return new DataGridView(page.getTotal(),page.getRecords());
    }

    /**
     * 添加一个供应商
     * @param providerVo
     * @return
     */
    @RequestMapping("addProvider")
    public ResultObj addProvider(ProviderVo providerVo){
        try {
            providerVo.setAvailable((Integer) Constast.AVAILABLE_TRUE);
            providerService.save(providerVo);
            User user = (User) WebUtils.getSession().getAttribute("user");
            OperationLog log = new OperationLog();
            log.setType("添加");
            log.setModule("供应商管理");
            log.setDescription("添加供应商: " + providerVo.getProvidername());
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
     * 修改一个供应商
     * @param providerVo
     * @return
     */
    @RequestMapping("updateProvider")
    public ResultObj updateProvider(ProviderVo providerVo){
        try {
            providerService.updateById(providerVo);
            User user = (User) WebUtils.getSession().getAttribute("user");
            OperationLog log = new OperationLog();
            log.setType("修改");
            log.setModule("供应商管理");
            log.setDescription("修改供应商: " + providerVo.getProvidername());
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
     * 删除一个供应商
     * @param id
     * @return
     */
    @RequestMapping("deleteProvider")
    public ResultObj deleteProvider(Integer id){
        try {
            Provider provider = providerService.getById(id);
            providerService.deleteProviderById(id);
            User user = (User) WebUtils.getSession().getAttribute("user");
            OperationLog log = new OperationLog();
            log.setType("删除");
            log.setModule("供应商管理");
            log.setDescription("删除供应商: " + (provider != null ? provider.getProvidername() : "ID=" + id));
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
     * 加载所有可用的供应商
     * @return
     */
    @RequestMapping("loadAllProviderForSelect")
    public DataGridView loadAllProviderForSelect(){
        QueryWrapper<Provider> queryWrapper = new QueryWrapper<Provider>();
        queryWrapper.eq("available", Constast.AVAILABLE_TRUE);
        List<Provider> list = providerService.list(queryWrapper);
        return new DataGridView(list);
    }


}

