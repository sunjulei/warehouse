package com.sunlee.bus.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sunlee.bus.entity.Customer;
import com.sunlee.bus.entity.Goods;
import com.sunlee.bus.entity.OperationLog;
import com.sunlee.bus.entity.Salesback;
import com.sunlee.bus.service.ICustomerService;
import com.sunlee.bus.service.IGoodsService;
import com.sunlee.bus.service.IOperationLogService;
import com.sunlee.bus.service.ISalesbackService;
import com.sunlee.bus.vo.SalesbackVo;
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
 * @since 2026-05-01
 */
@RestController
@RequestMapping("/salesback")
public class SalesbackController {

    @Autowired
    private ISalesbackService salesbackService;

    @Autowired
    private ICustomerService customerService;

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
    @RequestMapping("addSalesback")
    public ResultObj addSalesback(Integer id,Integer number,String remark){
        try {
            salesbackService.addSalesback(id,number,remark);
            User user = (User) WebUtils.getSession().getAttribute("user");
            OperationLog opLog = new OperationLog();
            opLog.setType("添加");
            opLog.setModule("销售退回");
            opLog.setDescription("销售退货, 销售单ID=" + id + ", 数量=" + number);
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
     * 查询商品销售退货
     * @param salesbackVo
     * @return
     */
    @RequestMapping("loadAllSalesback")
    public DataGridView loadAllSalesback(SalesbackVo salesbackVo){
        IPage<Salesback> page = new Page<Salesback>(salesbackVo.getPage(),salesbackVo.getLimit());
        QueryWrapper<Salesback> queryWrapper = new QueryWrapper<Salesback>();
        //对客户进行查询
        queryWrapper.eq(salesbackVo.getCustomerid()!=null&&salesbackVo.getCustomerid()!=0,"customerid",salesbackVo.getCustomerid());
        //对商品进行查询
        queryWrapper.eq(salesbackVo.getGoodsid()!=null&&salesbackVo.getGoodsid()!=0,"goodsid",salesbackVo.getGoodsid());
        //对时间进行查询要求大于开始时间小于结束时间
        queryWrapper.ge(salesbackVo.getStartTime()!=null,"salesbacktime",salesbackVo.getStartTime());
        queryWrapper.le(salesbackVo.getEndTime()!=null,"salesbacktime",salesbackVo.getEndTime());
        //通过商品退货时间对商品进行排序
        queryWrapper.orderByDesc("salesbacktime");
        salesbackService.page(page, queryWrapper);
        List<Salesback> records = page.getRecords();
        for (Salesback salesback : records) {
            System.out.println("============================");
            Customer customer = customerService.getById(salesback.getCustomerid());
            if (customer!=null){
                //设置客户姓名
                salesback.setCustomername(customer.getCustomername());
            }
            Goods goods = goodsService.getById(salesback.getGoodsid());
            if (goods!=null){
                //设置商品名称
                salesback.setGoodsname(goods.getGoodsname());
                //设置商品规格
                salesback.setSize(goods.getSize());
            }
        }
        return new DataGridView(page.getTotal(),page.getRecords());
    }

    /**
     * 取消销售退货（删除记录并回滚库存）
     * @param id
     * @return
     */
    @RequestMapping("cancelSalesback")
    public ResultObj cancelSalesback(Integer id){
        try {
            salesbackService.cancelSalesback(id);
            User user = (User) WebUtils.getSession().getAttribute("user");
            OperationLog opLog = new OperationLog();
            opLog.setType("删除");
            opLog.setModule("销售退回");
            opLog.setDescription("取消销售退货 ID=" + id);
            opLog.setOperateperson(user != null ? user.getName() : "未知用户");
            opLog.setOperatetime(new Date());
            operationLogService.save(opLog);
            return ResultObj.CANCEL_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.CANCEL_ERROR;
        }
    }
    
}

