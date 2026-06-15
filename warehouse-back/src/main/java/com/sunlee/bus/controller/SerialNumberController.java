package com.sunlee.bus.controller;

import com.sunlee.bus.entity.SerialNumber;
import com.sunlee.bus.service.ISerialNumberService;
import com.sunlee.bus.vo.SerialNumberVo;
import com.sunlee.sys.common.Constast;
import com.sunlee.sys.common.DataGridView;
import com.sunlee.sys.common.ResultObj;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 * 序列号控制器
 *
 * @author sunlee
 * @since 2026-06-12
 */
@Slf4j
@RestController
@RequestMapping("serialNumber")
public class SerialNumberController {

    @Autowired
    private ISerialNumberService serialNumberService;

    /**
     * 查询序列号列表
     */
    @RequestMapping("loadAllSerialNumber")
    public DataGridView loadAllSerialNumber(SerialNumberVo vo) {
        return serialNumberService.querySerialNumbers(vo);
    }

    /**
     * 添加单个序列号
     */
    @RequestMapping("addSerialNumber")
    public ResultObj addSerialNumber(SerialNumberVo vo) {
        try {
            if (vo.getSerialNumber() == null || vo.getSerialNumber().isEmpty()) {
                return ResultObj.error("序列号不能为空");
            }
            if (vo.getGoodsid() == null) {
                return ResultObj.error("商品不能为空");
            }
            if (serialNumberService.existsBySerialNumber(vo.getSerialNumber())) {
                return ResultObj.error("序列号已存在");
            }
            vo.setInstockTime(new Date());
            vo.setStatus(0);
            serialNumberService.save(vo);
            return ResultObj.ADD_SUCCESS;
        } catch (Exception e) {
            log.error("添加序列号失败: {}", e.getMessage(), e);
            return ResultObj.error("添加失败: " + e.getMessage());
        }
    }

    /**
     * 批量添加序列号
     */
    @RequestMapping("batchAddSerialNumber")
    public ResultObj batchAddSerialNumber(@RequestBody List<SerialNumber> list) {
        try {
            Date now = new Date();
            for (SerialNumber sn : list) {
                sn.setInstockTime(now);
                sn.setStatus(0);
            }
            serialNumberService.batchAdd(list);
            return new ResultObj(Constast.OK, "批量添加成功，共" + list.size() + "条");
        } catch (Exception e) {
            log.error("批量添加序列号失败: {}", e.getMessage(), e);
            return new ResultObj(Constast.ERROR, "添加失败: " + e.getMessage());
        }
    }

    /**
     * 删除序列号
     */
    @RequestMapping("deleteSerialNumber")
    public ResultObj deleteSerialNumber(Integer id) {
        try {
            serialNumberService.deleteSerialNumber(id);
            return ResultObj.DELETE_SUCCESS;
        } catch (Exception e) {
            log.error("删除序列号失败: {}", e.getMessage(), e);
            return ResultObj.error("删除失败: " + e.getMessage());
        }
    }

    /**
     * 修改序列号
     */
    @RequestMapping("updateSerialNumber")
    public ResultObj updateSerialNumber(SerialNumberVo vo) {
        try {
            serialNumberService.updateById(vo);
            return ResultObj.UPDATE_SUCCESS;
        } catch (Exception e) {
            log.error("修改序列号失败: {}", e.getMessage(), e);
            return ResultObj.error("修改失败: " + e.getMessage());
        }
    }
}
