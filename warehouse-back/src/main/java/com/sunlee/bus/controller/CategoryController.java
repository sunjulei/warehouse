package com.sunlee.bus.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sunlee.bus.entity.Category;
import com.sunlee.bus.entity.OperationLog;
import com.sunlee.bus.service.ICategoryService;
import com.sunlee.bus.service.IOperationLogService;
import com.sunlee.bus.vo.CategoryVo;
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

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private IOperationLogService operationLogService;

    @RequestMapping("loadAllCategory")
    public DataGridView loadAllCategory(CategoryVo categoryVo) {
        IPage<Category> page = new Page<>(categoryVo.getPage(), categoryVo.getLimit());
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(categoryVo.getCatename()), "catename", categoryVo.getCatename());
        queryWrapper.orderByDesc("id");
        categoryService.page(page, queryWrapper);
        return new DataGridView(page.getTotal(), page.getRecords());
    }

    @RequestMapping("addCategory")
    public ResultObj addCategory(CategoryVo categoryVo) {
        try {
            categoryVo.setAvailable((Integer) Constast.AVAILABLE_TRUE);
            categoryService.save(categoryVo);
            User user = (User) WebUtils.getSession().getAttribute("user");
            OperationLog log = new OperationLog();
            log.setType("添加");
            log.setModule("商品分类");
            log.setDescription("添加分类: " + categoryVo.getCatename());
            log.setOperateperson(user != null ? user.getName() : "未知用户");
            log.setOperatetime(new Date());
            operationLogService.save(log);
            return ResultObj.ADD_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.ADD_ERROR;
        }
    }

    @RequestMapping("updateCategory")
    public ResultObj updateCategory(CategoryVo categoryVo) {
        try {
            categoryService.updateById(categoryVo);
            User user = (User) WebUtils.getSession().getAttribute("user");
            OperationLog log = new OperationLog();
            log.setType("修改");
            log.setModule("商品分类");
            log.setDescription("修改分类: " + categoryVo.getCatename());
            log.setOperateperson(user != null ? user.getName() : "未知用户");
            log.setOperatetime(new Date());
            operationLogService.save(log);
            return ResultObj.UPDATE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.UPDATE_ERROR;
        }
    }

    @RequestMapping("deleteCategory")
    public ResultObj deleteCategory(Integer id) {
        try {
            Category category = categoryService.getById(id);
            categoryService.removeById(id);
            User user = (User) WebUtils.getSession().getAttribute("user");
            OperationLog log = new OperationLog();
            log.setType("删除");
            log.setModule("商品分类");
            log.setDescription("删除分类: " + (category != null ? category.getCatename() : "ID=" + id));
            log.setOperateperson(user != null ? user.getName() : "未知用户");
            log.setOperatetime(new Date());
            operationLogService.save(log);
            return ResultObj.DELETE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.DELETE_ERROR;
        }
    }

    @RequestMapping("loadAllCategoryForSelect")
    public DataGridView loadAllCategoryForSelect() {
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("available", Constast.AVAILABLE_TRUE);
        List<Category> list = categoryService.list(queryWrapper);
        return new DataGridView(list);
    }
}
