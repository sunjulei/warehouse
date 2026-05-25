package com.sunlee.bus.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sunlee.bus.entity.Goods;
import com.sunlee.bus.entity.OperationLog;
import com.sunlee.bus.entity.Outport;
import com.sunlee.bus.entity.Provider;
import com.sunlee.bus.service.IGoodsService;
import com.sunlee.bus.service.IOperationLogService;
import com.sunlee.bus.service.IOutportService;
import com.sunlee.bus.service.IProviderService;
import com.sunlee.bus.vo.OutportVo;
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
 * InnoDB free: 9216 kB 前端控制器
 * </p>
 *
 * @author sunlee
 * @since 2026-04-10
 */
@RestController
@RequestMapping("/outport")
public class OutportController {

    @Autowired
    private IOutportService outportService;

    @Autowired
    private IProviderService providerService;

    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private IOperationLogService operationLogService;

    /**
     * 添加退货信息
     * @param id    进货单ID
     * @param number    退货数量
     * @param remark    备注
     * @return
     */
    @RequestMapping("addOutport")
    public ResultObj addOutport(Integer id,Integer number,String remark){
        try {
            outportService.addOutport(id,number,remark);
            User user = (User) WebUtils.getSession().getAttribute("user");
            OperationLog opLog = new OperationLog();
            opLog.setType("添加");
            opLog.setModule("退回供应商");
            opLog.setDescription("进货退货, 进货单ID=" + id + ", 数量=" + number);
            opLog.setOperateperson(user != null ? user.getName() : "未知用户");
            opLog.setOperatetime(new Date());
            operationLogService.save(opLog);
            return ResultObj.BACKINPORT_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.BACKINPORT_ERROR;
        }
    }

    /**
     * 删除退货信息（仅删除记录，不回滚库存）
     * @param id
     * @return
     */
    @RequestMapping("deleteOutport")
    public ResultObj deleteOutport(Integer id){
        try {
            Outport outport = outportService.getById(id);
            outportService.removeById(id);
            User user = (User) WebUtils.getSession().getAttribute("user");
            OperationLog opLog = new OperationLog();
            opLog.setType("删除");
            opLog.setModule("退回供应商");
            opLog.setDescription("删除退货记录 ID=" + id);
            opLog.setOperateperson(user != null ? user.getName() : "未知用户");
            opLog.setOperatetime(new Date());
            operationLogService.save(opLog);
            return ResultObj.DELETE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.DELETE_ERROR;
        }
    }

    /**
     * 取消退货（删除记录并回滚库存）
     * @param id
     * @return
     */
    @RequestMapping("cancelOutport")
    public ResultObj cancelOutport(Integer id){
        try {
            outportService.cancelOutport(id);
            User user = (User) WebUtils.getSession().getAttribute("user");
            OperationLog opLog = new OperationLog();
            opLog.setType("删除");
            opLog.setModule("退回供应商");
            opLog.setDescription("取消退货 ID=" + id);
            opLog.setOperateperson(user != null ? user.getName() : "未知用户");
            opLog.setOperatetime(new Date());
            operationLogService.save(opLog);
            return ResultObj.CANCEL_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.CANCEL_ERROR;
        }
    }

    /**t
     * 查询商品退货
     * @param outportVo
     * @return
     */
    @RequestMapping("loadAllOutport")
    public DataGridView loadAllOuport(OutportVo outportVo){
        IPage<Outport> page = new Page<Outport>(outportVo.getPage(),outportVo.getLimit());
        QueryWrapper<Outport> queryWrapper = new QueryWrapper<Outport>();
        //对供应商进行查询
        queryWrapper.eq(outportVo.getProviderid()!=null&&outportVo.getProviderid()!=0,"providerid",outportVo.getProviderid());
        //对商品进行查询
        queryWrapper.eq(outportVo.getGoodsid()!=null&&outportVo.getGoodsid()!=0,"goodsid",outportVo.getGoodsid());
        //对时间进行查询要求大于开始时间小于结束时间
        queryWrapper.ge(outportVo.getStartTime()!=null,"outputtime",outportVo.getStartTime());
        queryWrapper.le(outportVo.getEndTime()!=null,"outputtime",outportVo.getEndTime());
        //通过进货时间对商品进行排序
        queryWrapper.orderByDesc("outputtime");
        IPage<Outport> page1 = outportService.page(page, queryWrapper);
        List<Outport> records = page1.getRecords();
        for (Outport ouport : records) {
            Provider provider = providerService.getById(ouport.getProviderid());
            if (provider!=null){
                //设置供应商姓名
                ouport.setProvidername(provider.getProvidername());
            }
            Goods goods = goodsService.getById(ouport.getGoodsid());
            if (goods!=null){
                //设置商品名称
                ouport.setGoodsname(goods.getGoodsname());
                //设置商品规格
                ouport.setSize(goods.getSize());
            }
        }
        return new DataGridView(page1.getTotal(),page1.getRecords());
    }

}

