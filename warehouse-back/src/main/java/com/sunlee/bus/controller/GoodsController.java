package com.sunlee.bus.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sunlee.bus.entity.*;
import com.sunlee.bus.service.*;
import com.sunlee.bus.vo.GoodsVo;
import com.sunlee.sys.annotation.OperationLog;
import com.sunlee.sys.common.AppFileUtils;
import com.sunlee.sys.common.Constast;
import com.sunlee.sys.common.DataGridView;
import com.sunlee.sys.common.ResultObj;
import com.sunlee.sys.common.WebUtils;
import com.sunlee.sys.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * InnoDB free: 9216 kB; (`providerid`) REFER `warehouse/bus_provider`(`id`) 前端控制器
 * </p>
 *
 * @author sunlee
 * @since 2026-03-20
 */
@Slf4j
@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private IProviderService providerService;

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private IInportService inportService;

    @Autowired
    private ISalesService salesService;

    @Autowired
    private IOutportService outportService;

    @Autowired
    private ISalesbackService salesbackService;

    /**
     * 查询商品（批量查询供应商和分类，避免 N+1）
     */
    @RequestMapping("loadAllGoods")
    public DataGridView loadAllGoods(GoodsVo goodsVo){
        IPage<Goods> page = new Page<>(goodsVo.getPage(), goodsVo.getLimit());
        QueryWrapper<Goods> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(goodsVo.getProviderid()!=null && goodsVo.getProviderid()!=0, "providerid", goodsVo.getProviderid());
        queryWrapper.eq(goodsVo.getCategoryid()!=null && goodsVo.getCategoryid()!=0, "categoryid", goodsVo.getCategoryid());
        queryWrapper.like(StringUtils.isNotBlank(goodsVo.getGoodsname()), "goodsname", goodsVo.getGoodsname());
        queryWrapper.like(StringUtils.isNotBlank(goodsVo.getProductcode()), "productcode", goodsVo.getProductcode());
        queryWrapper.like(StringUtils.isNotBlank(goodsVo.getPromitcode()), "promitcode", goodsVo.getPromitcode());
        queryWrapper.like(StringUtils.isNotBlank(goodsVo.getDescription()), "description", goodsVo.getDescription());
        queryWrapper.like(StringUtils.isNotBlank(goodsVo.getSize()), "size", goodsVo.getSize());
        queryWrapper.like(StringUtils.isNotBlank(goodsVo.getAttribute()), "attribute", goodsVo.getAttribute());
        queryWrapper.orderByDesc("id");
        goodsService.page(page, queryWrapper);

        // 批量查询供应商和分类，避免 N+1
        List<Goods> records = page.getRecords();
        Set<Integer> providerIds = records.stream().map(Goods::getProviderid).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<Integer> categoryIds = records.stream().map(Goods::getCategoryid).filter(Objects::nonNull).collect(Collectors.toSet());

        Map<Integer, String> providerMap = new HashMap<>();
        if (!providerIds.isEmpty()) {
            providerService.listByIds(providerIds).forEach(p -> providerMap.put(p.getId(), p.getProvidername()));
        }
        Map<Integer, String> categoryMap = new HashMap<>();
        if (!categoryIds.isEmpty()) {
            categoryService.listByIds(categoryIds).forEach(c -> categoryMap.put(c.getId(), c.getCatename()));
        }

        for (Goods goods : records) {
            goods.setProvidername(providerMap.get(goods.getProviderid()));
            goods.setCategoryname(categoryMap.get(goods.getCategoryid()));
        }
        return new DataGridView(page.getTotal(), records);
    }

    /**
     * 添加商品
     */
    @OperationLog(type = "添加", module = "商品管理", description = "#args[0].goodsname != null ? '添加商品: ' + #args[0].goodsname : '添加商品'")
    @RequestMapping("addGoods")
    public ResultObj addGoods(GoodsVo goodsVo){
        try {
            if (goodsVo.getGoodsimg()!=null && goodsVo.getGoodsimg().endsWith("_temp")){
                String newName = AppFileUtils.renameFile(goodsVo.getGoodsimg());
                goodsVo.setGoodsimg(newName);
            }
            goodsVo.setNumber(0);
            goodsVo.setAvailable(Constast.AVAILABLE_TRUE);
            goodsService.save(goodsVo);
            return ResultObj.ADD_SUCCESS;
        } catch (Exception e) {
            log.error("添加商品失败: {}", e.getMessage(), e);
            return ResultObj.ADD_ERROR;
        }
    }

    /**
     * 修改商品
     */
    @OperationLog(type = "修改", module = "商品管理", description = "#args[0].goodsname != null ? '修改商品: ' + #args[0].goodsname : '修改商品'")
    @RequestMapping("updateGoods")
    public ResultObj updateGoods(GoodsVo goodsVo){
        try {
            if (goodsVo.getGoodsimg()!=null
                    && !goodsVo.getGoodsimg().equals(Constast.DEFAULT_IMG_GOODS)
                    && goodsVo.getGoodsimg().endsWith("_temp")){
                String newName = AppFileUtils.renameFile(goodsVo.getGoodsimg());
                goodsVo.setGoodsimg(newName);
                String oldPath = goodsService.getById(goodsVo.getId()).getGoodsimg();
                AppFileUtils.removeFileByPath(oldPath);
            }
            Goods oldGoods = goodsService.getById(goodsVo.getId());
            goodsVo.setNumber(oldGoods.getNumber());
            goodsService.updateById(goodsVo);
            return ResultObj.UPDATE_SUCCESS;
        } catch (Exception e) {
            log.error("修改商品失败: {}", e.getMessage(), e);
            return ResultObj.UPDATE_ERROR;
        }
    }

    /**
     * 商品上下架
     */
    @OperationLog(type = "#args[1] == 1 ? '上架' : '下架'", module = "商品管理", description = "#args[1] == 1 ? '上架商品ID: ' + #args[0] : '下架商品ID: ' + #args[0]")
    @RequestMapping("updateGoodsAvailable")
    public ResultObj updateGoodsAvailable(Integer id, Integer available){
        try {
            Goods goods = new Goods();
            goods.setId(id);
            goods.setAvailable(available);
            goodsService.updateById(goods);
            return ResultObj.UPDATE_SUCCESS;
        } catch (Exception e) {
            log.error("商品上下架失败: {}", e.getMessage(), e);
            return ResultObj.UPDATE_ERROR;
        }
    }

    /**
     * 删除商品
     */
    @OperationLog(type = "删除", module = "商品管理", description = "'删除商品ID: ' + #args[0]")
    @RequestMapping("deleteGoods")
    public ResultObj deleteGoods(Integer id, String goodsimg){
        try {
            AppFileUtils.removeFileByPath(goodsimg);
            goodsService.deleteGoodsById(id);
            return ResultObj.DELETE_SUCCESS;
        } catch (Exception e) {
            log.error("删除商品失败: {}", e.getMessage(), e);
            return ResultObj.DELETE_ERROR;
        }
    }

    /**
     * 加载所有可用的商品
     * @return
     */
    @RequestMapping("loadAllGoodsForSelect")
    public DataGridView loadAllGoodsForSelect(){
        QueryWrapper<Goods> queryWrapper = new QueryWrapper<Goods>();
        queryWrapper.eq("available",Constast.AVAILABLE_TRUE);
        List<Goods> list = goodsService.list(queryWrapper);
        for (Goods goods : list) {
            Provider provider = providerService.getById(goods.getProviderid());
            if (null!=provider){
                goods.setProvidername(provider.getProvidername());
            }
        }
        return new DataGridView(list);
    }

    /**
     * 根据供应商ID查询商品信息
     * @param providerid    供应商ID
     * @return
     */
    @RequestMapping("loadGoodsByProviderId")
    public DataGridView loadGoodsByProviderId(Integer providerid,Integer allStatus){
        QueryWrapper<Goods> queryWrapper = new QueryWrapper<Goods>();
        if (!Constast.AVAILABLE_TRUE.equals(allStatus)){
            queryWrapper.eq("available",Constast.AVAILABLE_TRUE);
        }
        queryWrapper.eq(providerid!=null,"providerid",providerid);
        List<Goods> list = goodsService.list(queryWrapper);
        for (Goods goods : list) {
            Provider provider = providerService.getById(goods.getProviderid());
            if (null!=provider){
                goods.setProvidername(provider.getProvidername());
            }
        }
        return new DataGridView(list);
    }

    @RequestMapping("loadAllWarningGoods")
    public DataGridView loadAllWarningGoods(){
        List<Goods> goods = goodsService.loadAllWarning();
        for (Goods g : goods) {
            Provider provider = providerService.getById(g.getProviderid());
            if (provider != null) {
                g.setProvidername(provider.getProvidername());
            }
        }
        return new DataGridView((long) goods.size(),goods);
    }

    /**
     * 加载首页统计数据
     */
    @RequestMapping("loadDashboardStats")
    public Map<String, Object> loadDashboardStats(){
        Map<String, Object> result = new HashMap<>();
        // 商品总数
        long goodsTotal = goodsService.count();
        result.put("goodsTotal", goodsTotal);
        // 今日入库商品数量
        LocalDate today = LocalDate.now();
        Date dayStart = java.sql.Timestamp.valueOf(today.atStartOfDay());
        Date dayEnd = java.sql.Timestamp.valueOf(today.plusDays(1).atStartOfDay());
        QueryWrapper<Inport> inportQuery = new QueryWrapper<>();
        inportQuery.select("IFNULL(SUM(number), 0) as number");
        inportQuery.ge("inporttime", dayStart);
        inportQuery.lt("inporttime", dayEnd);
        Map<String, Object> inportResult = inportService.getMap(inportQuery);
        result.put("todayInport", inportResult != null ? inportResult.get("number") : 0);
        // 今日销售商品数量
        QueryWrapper<Sales> salesQuery = new QueryWrapper<>();
        salesQuery.select("IFNULL(SUM(number), 0) as number");
        salesQuery.ge("salestime", dayStart);
        salesQuery.lt("salestime", dayEnd);
        Map<String, Object> salesResult = salesService.getMap(salesQuery);
        result.put("todaySales", salesResult != null ? salesResult.get("number") : 0);
        return result;
    }

    /**
     * 加载最近操作记录（进货、销售、进货退货、销售退货）
     */
    @RequestMapping("loadRecentOperations")
    public DataGridView loadRecentOperations(){
        List<Map<String, Object>> allOps = new ArrayList<>();

        // 进货
        QueryWrapper<Inport> inportQW = new QueryWrapper<>();
        inportQW.orderByDesc("inporttime");
        inportQW.last("LIMIT 10");
        List<Inport> inports = inportService.list(inportQW);
        for (Inport item : inports) {
            Map<String, Object> op = new HashMap<>();
            op.put("type", "进货");
            op.put("typeTag", "success");
            Goods goods = goodsService.getById(item.getGoodsid());
            op.put("goodsname", goods != null ? goods.getGoodsname() : "未知商品");
            op.put("number", item.getNumber());
            op.put("time", item.getInporttime());
            op.put("operateperson", item.getOperateperson());
            allOps.add(op);
        }

        // 销售
        QueryWrapper<Sales> salesQW = new QueryWrapper<>();
        salesQW.orderByDesc("salestime");
        salesQW.last("LIMIT 10");
        List<Sales> salesList = salesService.list(salesQW);
        for (Sales item : salesList) {
            Map<String, Object> op = new HashMap<>();
            op.put("type", "销售");
            op.put("typeTag", "primary");
            Goods goods = goodsService.getById(item.getGoodsid());
            op.put("goodsname", goods != null ? goods.getGoodsname() : "未知商品");
            op.put("number", item.getNumber());
            op.put("time", item.getSalestime());
            op.put("operateperson", item.getOperateperson());
            allOps.add(op);
        }

        // 进货退货
        QueryWrapper<Outport> outportQW = new QueryWrapper<>();
        outportQW.orderByDesc("outputtime");
        outportQW.last("LIMIT 10");
        List<Outport> outports = outportService.list(outportQW);
        for (Outport item : outports) {
            Map<String, Object> op = new HashMap<>();
            op.put("type", "进货退货");
            op.put("typeTag", "warning");
            Goods goods = goodsService.getById(item.getGoodsid());
            op.put("goodsname", goods != null ? goods.getGoodsname() : "未知商品");
            op.put("number", item.getNumber());
            op.put("time", item.getOutputtime());
            op.put("operateperson", item.getOperateperson());
            allOps.add(op);
        }

        // 销售退货
        QueryWrapper<Salesback> salesbackQW = new QueryWrapper<>();
        salesbackQW.orderByDesc("salesbacktime");
        salesbackQW.last("LIMIT 10");
        List<Salesback> salesbacks = salesbackService.list(salesbackQW);
        for (Salesback item : salesbacks) {
            Map<String, Object> op = new HashMap<>();
            op.put("type", "销售退货");
            op.put("typeTag", "danger");
            Goods goods = goodsService.getById(item.getGoodsid());
            op.put("goodsname", goods != null ? goods.getGoodsname() : "未知商品");
            op.put("number", item.getNumber());
            op.put("time", item.getSalesbacktime());
            op.put("operateperson", item.getOperateperson());
            allOps.add(op);
        }

        // 按时间倒序排序，取前10条
        allOps.sort((a, b) -> {
            Date timeA = (Date) a.get("time");
            Date timeB = (Date) b.get("time");
            if (timeA == null && timeB == null) return 0;
            if (timeA == null) return 1;
            if (timeB == null) return -1;
            return timeB.compareTo(timeA);
        });
        if (allOps.size() > 10) {
            allOps = allOps.subList(0, 10);
        }

        return new DataGridView((long) allOps.size(), allOps);
    }

}

