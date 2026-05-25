package com.sunlee.bus.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sunlee.bus.entity.OperationLog;
import com.sunlee.bus.service.IOperationLogService;
import com.sunlee.bus.vo.OperationLogVo;
import com.sunlee.sys.common.DataGridView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/operationLog")
public class OperationLogController {

    @Autowired
    private IOperationLogService operationLogService;

    @RequestMapping("loadAllOperationLog")
    public DataGridView loadAllOperationLog(OperationLogVo operationLogVo) {
        IPage<OperationLog> page = new Page<>(operationLogVo.getPage(), operationLogVo.getLimit());
        QueryWrapper<OperationLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("operatetime");
        operationLogService.page(page, queryWrapper);
        return new DataGridView(page.getTotal(), page.getRecords());
    }
}
