package com.sunlee.bus.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sunlee.bus.entity.Goods;
import com.sunlee.bus.entity.Inport;
import com.sunlee.bus.entity.OperationLog;
import com.sunlee.bus.entity.Provider;
import com.sunlee.bus.service.IGoodsService;
import com.sunlee.bus.service.IInportService;
import com.sunlee.bus.service.IOperationLogService;
import com.sunlee.bus.service.IProviderService;
import com.sunlee.bus.vo.InportVo;
import com.sunlee.sys.common.DataGridView;
import com.sunlee.sys.common.ResultObj;
import com.sunlee.sys.common.WebUtils;
import com.sunlee.sys.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * InnoDB free: 9216 kB; (`providerid`) REFER `warehouse/bus_provider`(`id`); (`goo 前端控制器
 * </p>
 *
 * @author sunlee
 * @since 2026-04-01
 */
@RestController
@RequestMapping("inport")
public class InportController {

    @Autowired
    private IInportService inportService;

    @Autowired
    private IProviderService providerService;

    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private IOperationLogService operationLogService;

    /**
     * 查询商品进货
     * @param inportVo
     * @return
     */
    @RequestMapping("loadAllInport")
    public DataGridView loadAllInport(InportVo inportVo){
        IPage<Inport> page = new Page<Inport>(inportVo.getPage(),inportVo.getLimit());
        QueryWrapper<Inport> queryWrapper = new QueryWrapper<Inport>();
        //对供应商进行查询
        queryWrapper.eq(inportVo.getProviderid()!=null&&inportVo.getProviderid()!=0,"providerid",inportVo.getProviderid());
        //对商品进行查询
        queryWrapper.eq(inportVo.getGoodsid()!=null&&inportVo.getGoodsid()!=0,"goodsid",inportVo.getGoodsid());
        //对时间进行查询要求大于开始时间小于结束时间
        queryWrapper.ge(inportVo.getStartTime()!=null,"inporttime",inportVo.getStartTime());
        queryWrapper.le(inportVo.getEndTime()!=null,"inporttime",inportVo.getEndTime());
        //通过进货时间对商品进行排序
        queryWrapper.orderByDesc("inporttime");
        IPage<Inport> page1 = inportService.page(page, queryWrapper);
        List<Inport> records = page1.getRecords();
        for (Inport inport : records) {
            Provider provider = providerService.getById(inport.getProviderid());
            if (provider!=null){
                //设置供应商姓名
                inport.setProvidername(provider.getProvidername());
            }
            Goods goods = goodsService.getById(inport.getGoodsid());
            if (goods!=null){
                //设置商品名称
                inport.setGoodsname(goods.getGoodsname());
                //设置商品规格
                inport.setSize(goods.getSize());
            }
        }
        return new DataGridView(page1.getTotal(),page1.getRecords());
    }


    /**
     * 添加进货商品
     * @param inportVo
     * @return
     */
    @RequestMapping("addInport")
    public ResultObj addInport(InportVo inportVo){
        try {
            //获得当前系统用户
            User user = (User) WebUtils.getSession().getAttribute("user");
            //设置操作人
            inportVo.setOperateperson(user.getName());
            //设置进货时间
            inportVo.setInporttime(new Date());
            inportService.save(inportVo);
            Goods goods = goodsService.getById(inportVo.getGoodsid());
            OperationLog opLog = new OperationLog();
            opLog.setType("添加");
            opLog.setModule("商品进货");
            opLog.setDescription("进货商品: " + (goods != null ? goods.getGoodsname() : "ID=" + inportVo.getGoodsid()) + ", 数量: " + inportVo.getNumber());
            opLog.setOperateperson(user.getName());
            opLog.setOperatetime(new Date());
            operationLogService.save(opLog);
            return ResultObj.ADD_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.ADD_ERROR;
        }
    }

    /**
     * 更新进货商品
     * @param inportVo
     * @return
     */
    @RequestMapping("updateInport")
    public ResultObj updateInport(InportVo inportVo){
        try {
            inportService.updateById(inportVo);
            User user = (User) WebUtils.getSession().getAttribute("user");
            OperationLog opLog = new OperationLog();
            opLog.setType("修改");
            opLog.setModule("商品进货");
            opLog.setDescription("修改进货记录 ID=" + inportVo.getId());
            opLog.setOperateperson(user != null ? user.getName() : "未知用户");
            opLog.setOperatetime(new Date());
            operationLogService.save(opLog);
            return ResultObj.UPDATE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.UPDATE_ERROR;
        }

    }

    /**
     * 删除进货记录（级联删除相关退货记录）
     * @param id
     * @return
     */
    @RequestMapping("deleteInport")
    public ResultObj deleteInport(Integer id){
        try {
            inportService.deleteInport(id);
            User user = (User) WebUtils.getSession().getAttribute("user");
            OperationLog opLog = new OperationLog();
            opLog.setType("删除");
            opLog.setModule("商品进货");
            opLog.setDescription("删除进货记录 ID=" + id);
            opLog.setOperateperson(user != null ? user.getName() : "未知用户");
            opLog.setOperatetime(new Date());
            operationLogService.save(opLog);
            return ResultObj.DELETE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.DELETE_ERROR;
        }
    }

}

