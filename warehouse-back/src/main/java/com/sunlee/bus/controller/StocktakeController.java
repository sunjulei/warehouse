package com.sunlee.bus.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sunlee.bus.entity.Stocktake;
import com.sunlee.bus.entity.StocktakeItem;
import com.sunlee.bus.service.IGoodsService;
import com.sunlee.bus.service.IStocktakeItemService;
import com.sunlee.bus.service.IStocktakeService;
import com.sunlee.bus.vo.StocktakeVo;
import com.sunlee.sys.annotation.OperationLog;
import com.sunlee.sys.common.DataGridView;
import com.sunlee.sys.common.ResultObj;
import com.sunlee.sys.common.WebUtils;
import com.sunlee.sys.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sunlee.bus.entity.Goods;
import com.sunlee.bus.entity.Provider;
import com.sunlee.bus.service.IProviderService;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 盘点管理
 */
@Slf4j
@RestController
@RequestMapping("/stocktake")
public class StocktakeController {

    @Autowired
    private IStocktakeService stocktakeService;

    @Autowired
    private IStocktakeItemService stocktakeItemService;

    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private IProviderService providerService;

    /**
     * 查询盘点单列表
     */
    @RequestMapping("loadAllStocktake")
    public DataGridView loadAllStocktake(StocktakeVo stocktakeVo) {
        IPage<Stocktake> page = new Page<>(stocktakeVo.getPage(), stocktakeVo.getLimit());
        QueryWrapper<Stocktake> qw = new QueryWrapper<>();
        qw.eq(stocktakeVo.getStatus() != null, "status", stocktakeVo.getStatus());
        qw.ge(stocktakeVo.getStartTime() != null, "create_time", stocktakeVo.getStartTime());
        qw.le(stocktakeVo.getEndTime() != null, "create_time", stocktakeVo.getEndTime());
        qw.orderByDesc("create_time");
        stocktakeService.page(page, qw);
        return new DataGridView(page.getTotal(), page.getRecords());
    }

    /**
     * 创建盘点单
     */
    @OperationLog(type = "添加", module = "盘点管理", description = "'创建盘点单'")
    @RequestMapping("createStocktake")
    public ResultObj createStocktake(String remark) {
        try {
            User user = (User) WebUtils.getSession().getAttribute("user");
            String operator = user != null ? user.getName() : "未知用户";
            Stocktake stocktake = stocktakeService.createStocktake(operator, remark);
            return new ResultObj(200, "盘点单创建成功: " + stocktake.getStocktakeNo());
        } catch (Exception e) {
            log.error("创建盘点单失败: {}", e.getMessage(), e);
            return new ResultObj(-1, "创建盘点单失败: " + e.getMessage());
        }
    }

    /**
     * 加载盘点明细
     */
    @RequestMapping("loadStocktakeItems")
    public DataGridView loadStocktakeItems(Integer stocktakeId) {
        List<StocktakeItem> items = stocktakeItemService.loadByStocktakeId(stocktakeId);
        Set<Integer> goodsIds = items.stream().map(StocktakeItem::getGoodsid).collect(Collectors.toSet());
        if (!goodsIds.isEmpty()) {
            List<Goods> goodsList = goodsService.listByIds(goodsIds);
            Map<Integer, String> goodsMap = goodsList.stream().collect(Collectors.toMap(Goods::getId, Goods::getGoodsname, (a, b) -> a));
            Map<Integer, Integer> goodsProviderMap = goodsList.stream().filter(g -> g.getProviderid() != null).collect(Collectors.toMap(Goods::getId, Goods::getProviderid, (a, b) -> a));
            Set<Integer> providerIds = goodsProviderMap.values().stream().collect(Collectors.toSet());
            Map<Integer, String> providerNameMap = new java.util.HashMap<>();
            if (!providerIds.isEmpty()) {
                providerNameMap = providerService.listByIds(providerIds).stream().collect(Collectors.toMap(Provider::getId, Provider::getProvidername, (a, b) -> a));
            }
            for (StocktakeItem item : items) {
                item.setGoodsname(goodsMap.get(item.getGoodsid()));
                Integer pid = goodsProviderMap.get(item.getGoodsid());
                if (pid != null) {
                    item.setProvidername(providerNameMap.get(pid));
                }
            }
        }
        return new DataGridView((long) items.size(), items);
    }

    /**
     * 保存盘点明细（填写实际数量）
     */
    @RequestMapping("saveStocktakeItems")
    public ResultObj saveStocktakeItems(@RequestBody List<StocktakeItem> items) {
        try {
            for (StocktakeItem item : items) {
                if (item.getActualNum() != null) {
                    item.setDiffNum(item.getActualNum() - item.getSystemNum());
                }
                stocktakeItemService.updateById(item);
            }
            return ResultObj.UPDATE_SUCCESS;
        } catch (Exception e) {
            log.error("保存盘点明细失败: {}", e.getMessage(), e);
            return ResultObj.UPDATE_ERROR;
        }
    }

    /**
     * 提交盘点单（更新库存）
     */
    @OperationLog(type = "提交", module = "盘点管理", description = "'提交盘点单ID: ' + #args[0]")
    @RequestMapping("submitStocktake")
    public ResultObj submitStocktake(Integer id) {
        try {
            stocktakeService.submitStocktake(id);
            return new ResultObj(200, "盘点单已提交，库存已更新");
        } catch (Exception e) {
            log.error("提交盘点单失败: {}", e.getMessage(), e);
            return new ResultObj(-1, e.getMessage());
        }
    }

    /**
     * 取消盘点单
     */
    @OperationLog(type = "取消", module = "盘点管理", description = "'取消盘点单ID: ' + #args[0]")
    @RequestMapping("cancelStocktake")
    public ResultObj cancelStocktake(Integer id) {
        try {
            stocktakeService.cancelStocktake(id);
            return ResultObj.CANCEL_SUCCESS;
        } catch (Exception e) {
            log.error("取消盘点单失败: {}", e.getMessage(), e);
            return new ResultObj(-1, e.getMessage());
        }
    }
}
