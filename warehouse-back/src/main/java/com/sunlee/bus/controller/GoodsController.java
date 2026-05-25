package com.sunlee.bus.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sunlee.bus.entity.*;
import com.sunlee.bus.service.*;
import com.sunlee.bus.vo.GoodsVo;
import com.sunlee.sys.common.AppFileUtils;
import com.sunlee.sys.common.Constast;
import com.sunlee.sys.common.DataGridView;
import com.sunlee.sys.common.ResultObj;
import com.sunlee.sys.common.WebUtils;
import com.sunlee.sys.entity.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * InnoDB free: 9216 kB; (`providerid`) REFER `warehouse/bus_provider`(`id`) 前端控制器
 * </p>
 *
 * @author sunlee
 * @since 2026-03-20
 */
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

    @Autowired
    private IOperationLogService operationLogService;

    /**
     * 查询商品
     * @param goodsVo
     * @return
     */
    @RequestMapping("loadAllGoods")
    public DataGridView loadAllGoods(GoodsVo goodsVo){
        IPage<Goods> page = new Page<Goods>(goodsVo.getPage(),goodsVo.getLimit());
        QueryWrapper<Goods> queryWrapper = new QueryWrapper<Goods>();
        queryWrapper.eq(goodsVo.getProviderid()!=null&&goodsVo.getProviderid()!=0,"providerid",goodsVo.getProviderid());
        queryWrapper.eq(goodsVo.getCategoryid()!=null&&goodsVo.getCategoryid()!=0,"categoryid",goodsVo.getCategoryid());
        queryWrapper.like(StringUtils.isNotBlank(goodsVo.getGoodsname()),"goodsname",goodsVo.getGoodsname());
        queryWrapper.like(StringUtils.isNotBlank(goodsVo.getProductcode()),"productcode",goodsVo.getProductcode());
        queryWrapper.like(StringUtils.isNotBlank(goodsVo.getPromitcode()),"promitcode",goodsVo.getPromitcode());
        queryWrapper.like(StringUtils.isNotBlank(goodsVo.getDescription()),"description",goodsVo.getDescription());
        queryWrapper.like(StringUtils.isNotBlank(goodsVo.getSize()),"size",goodsVo.getSize());

        queryWrapper.orderByDesc("id");
        goodsService.page(page,queryWrapper);
        List<Goods> records = page.getRecords();
        for (Goods goods : records) {
            Provider provider = providerService.getById(goods.getProviderid());
            if (null!=provider){
                goods.setProvidername(provider.getProvidername());
            }
            if (goods.getCategoryid() != null) {
                Category category = categoryService.getById(goods.getCategoryid());
                if (category != null) {
                    goods.setCategoryname(category.getCatename());
                }
            }
        }
        return new DataGridView(page.getTotal(),page.getRecords());
    }

    /**
     * 添加商品
     * @param goodsVo
     * @return
     */
    @RequestMapping("addGoods")
    public ResultObj addGoods(GoodsVo goodsVo){
        try {
            if (goodsVo.getGoodsimg()!=null&&goodsVo.getGoodsimg().endsWith("_temp")){
                String newName = AppFileUtils.renameFile(goodsVo.getGoodsimg());
                goodsVo.setGoodsimg(newName);
            }
            //库存由进货和销售决定，新增商品时库存默认为0
            goodsVo.setNumber(0);
            goodsVo.setAvailable(Constast.AVAILABLE_TRUE);
            goodsService.save(goodsVo);
            User user = (User) WebUtils.getSession().getAttribute("user");
            OperationLog log = new OperationLog();
            log.setType("添加");
            log.setModule("商品管理");
            log.setDescription("添加商品: " + goodsVo.getGoodsname());
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
     * 修改商品
     * @param goodsVo
     * @return
     */
    @RequestMapping("updateGoods")
    public ResultObj updateGoods(GoodsVo goodsVo){
        try {
            //商品图片不是默认图片且是临时文件
            if (goodsVo.getGoodsimg()!=null
                    && !goodsVo.getGoodsimg().equals(Constast.DEFAULT_IMG_GOODS)
                    && goodsVo.getGoodsimg().endsWith("_temp")){
                String newName = AppFileUtils.renameFile(goodsVo.getGoodsimg());
                goodsVo.setGoodsimg(newName);
                //删除原先的图片
                String oldPath = goodsService.getById(goodsVo.getId()).getGoodsimg();
                AppFileUtils.removeFileByPath(oldPath);
            }
            //库存由进货和销售决定，编辑商品时保留原库存值
            Goods oldGoods = goodsService.getById(goodsVo.getId());
            goodsVo.setNumber(oldGoods.getNumber());
            goodsService.updateById(goodsVo);
            User user = (User) WebUtils.getSession().getAttribute("user");
            OperationLog log = new OperationLog();
            log.setType("修改");
            log.setModule("商品管理");
            log.setDescription("修改商品: " + goodsVo.getGoodsname());
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
     * 商品上下架
     * @param id 商品id
     * @param available 状态 1上架 0下架
     * @return
     */
    @RequestMapping("updateGoodsAvailable")
    public ResultObj updateGoodsAvailable(Integer id,Integer available){
        try {
            Goods oldGoods = goodsService.getById(id);
            Goods goods = new Goods();
            goods.setId(id);
            goods.setAvailable(available);
            goodsService.updateById(goods);
            User user = (User) WebUtils.getSession().getAttribute("user");
            OperationLog log = new OperationLog();
            log.setType(available.equals(Constast.AVAILABLE_TRUE) ? "上架" : "下架");
            log.setModule("商品管理");
            log.setDescription((available.equals(Constast.AVAILABLE_TRUE) ? "上架" : "下架") + "商品: " + oldGoods.getGoodsname());
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
     * 删除商品
     * @param id 商品id
     * @return
     */
    @RequestMapping("deleteGoods")
    public ResultObj deleteGoods(Integer id,String goodsimg){
        try {
            Goods goods = goodsService.getById(id);
            //删除商品的图片
            AppFileUtils.removeFileByPath(goodsimg);
//            goodsService.removeById(id);
            goodsService.deleteGoodsById(id);
            User user = (User) WebUtils.getSession().getAttribute("user");
            OperationLog log = new OperationLog();
            log.setType("删除");
            log.setModule("商品管理");
            log.setDescription("删除商品: " + (goods != null ? goods.getGoodsname() : "ID=" + id));
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

