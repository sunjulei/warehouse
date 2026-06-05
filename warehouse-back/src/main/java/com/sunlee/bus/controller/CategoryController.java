package com.sunlee.bus.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sunlee.bus.entity.Category;
import com.sunlee.bus.service.ICategoryService;
import com.sunlee.bus.vo.CategoryVo;
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
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private ICategoryService categoryService;

    @RequestMapping("loadAllCategory")
    public DataGridView loadAllCategory(CategoryVo categoryVo) {
        IPage<Category> page = new Page<>(categoryVo.getPage(), categoryVo.getLimit());
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(categoryVo.getCatename()), "catename", categoryVo.getCatename());
        queryWrapper.orderByDesc("id");
        categoryService.page(page, queryWrapper);
        return new DataGridView(page.getTotal(), page.getRecords());
    }

    @OperationLog(type = "添加", module = "商品分类", description = "'添加分类: ' + #args[0].catename")
    @RequestMapping("addCategory")
    public ResultObj addCategory(CategoryVo categoryVo) {
        try {
            categoryVo.setAvailable((Integer) Constast.AVAILABLE_TRUE);
            categoryService.save(categoryVo);
            return ResultObj.ADD_SUCCESS;
        } catch (Exception e) {
            log.error("添加分类失败: {}", e.getMessage(), e);
            return ResultObj.ADD_ERROR;
        }
    }

    @OperationLog(type = "修改", module = "商品分类", description = "'修改分类: ' + #args[0].catename")
    @RequestMapping("updateCategory")
    public ResultObj updateCategory(CategoryVo categoryVo) {
        try {
            categoryService.updateById(categoryVo);
            return ResultObj.UPDATE_SUCCESS;
        } catch (Exception e) {
            log.error("修改分类失败: {}", e.getMessage(), e);
            return ResultObj.UPDATE_ERROR;
        }
    }

    @OperationLog(type = "删除", module = "商品分类", description = "'删除分类ID: ' + #args[0]")
    @RequestMapping("deleteCategory")
    public ResultObj deleteCategory(Integer id) {
        try {
            categoryService.removeById(id);
            return ResultObj.DELETE_SUCCESS;
        } catch (Exception e) {
            log.error("删除分类失败: {}", e.getMessage(), e);
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
