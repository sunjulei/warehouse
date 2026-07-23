package com.sunlee.bus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sunlee.bus.entity.Goods;
import com.sunlee.bus.entity.SerialNumber;
import com.sunlee.bus.entity.SerialNumberLog;
import com.sunlee.bus.mapper.GoodsMapper;
import com.sunlee.bus.mapper.SerialNumberLogMapper;
import com.sunlee.bus.mapper.SerialNumberMapper;
import com.sunlee.bus.service.ISerialNumberService;
import com.sunlee.bus.vo.SerialNumberVo;
import com.sunlee.sys.common.Constast;
import com.sunlee.sys.common.DataGridView;
import com.sunlee.sys.common.ResultObj;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 序列号服务实现
 *
 * @author sunlee
 * @since 2026-06-12
 */
@Service
@Transactional
public class SerialNumberServiceImpl extends ServiceImpl<SerialNumberMapper, SerialNumber> implements ISerialNumberService {

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private SerialNumberLogMapper serialNumberLogMapper;

    /**
     * 记录序列号操作日志
     */
    private void saveLog(String serialNumber, Integer goodsId, String action,
                         Integer fromStatus, Integer toStatus, String orderNo, String remark) {
        SerialNumberLog log = new SerialNumberLog();
        log.setSerialNumber(serialNumber);
        log.setGoodsid(goodsId);
        log.setAction(action);
        log.setFromStatus(fromStatus);
        log.setToStatus(toStatus);
        log.setOrderNo(orderNo);
        log.setOperateTime(new Date());
        log.setRemark(remark);
        serialNumberLogMapper.insert(log);
    }

    @Override
    public DataGridView querySerialNumbers(SerialNumberVo vo) {
        QueryWrapper<SerialNumber> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(vo.getGoodsid() != null && vo.getGoodsid() != 0, "goodsid", vo.getGoodsid());
        queryWrapper.eq(vo.getStatus() != null, "status", vo.getStatus());
        queryWrapper.like(vo.getSerialNumber() != null && !vo.getSerialNumber().isEmpty(), "serial_number", vo.getSerialNumber());
        queryWrapper.ge(vo.getStartTime() != null, "instock_time", vo.getStartTime());
        queryWrapper.le(vo.getEndTime() != null, "instock_time", vo.getEndTime());
        queryWrapper.orderByDesc("instock_time");

        // 分页
        com.baomidou.mybatisplus.core.metadata.IPage<SerialNumber> page =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(vo.getPage(), vo.getLimit());
        baseMapper.selectPage(page, queryWrapper);

        // 填充商品名称
        List<SerialNumber> records = page.getRecords();
        for (SerialNumber sn : records) {
            Goods goods = goodsMapper.selectById(sn.getGoodsid());
            if (goods != null) {
                sn.setGoodsname(goods.getGoodsname());
                sn.setSize(goods.getSize());
            }
        }

        return new DataGridView(page.getTotal(), records);
    }

    @Override
    public void batchAdd(List<SerialNumber> list) {
        if (list == null || list.isEmpty()) {
            throw new RuntimeException("序列号列表不能为空");
        }
        // 检查重复
        for (SerialNumber sn : list) {
            if (existsBySerialNumber(sn.getSerialNumber())) {
                throw new RuntimeException("序列号已存在: " + sn.getSerialNumber());
            }
            // 检查商品是否存在
            Goods goods = goodsMapper.selectById(sn.getGoodsid());
            if (goods == null) {
                throw new RuntimeException("商品不存在: " + sn.getGoodsid());
            }
        }
        saveBatch(list);
    }

    @Override
    public void deleteSerialNumber(Integer id) {
        baseMapper.deleteById(id);
    }

    @Override
    public boolean existsBySerialNumber(String serialNumber) {
        QueryWrapper<SerialNumber> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("serial_number", serialNumber);
        return baseMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public void batchInport(Integer goodsId, List<String> serialNumbers, Integer inportId) {
        if (serialNumbers == null || serialNumbers.isEmpty()) {
            return;
        }
        Goods goods = goodsMapper.selectById(goodsId);
        if (goods == null) {
            throw new RuntimeException("商品不存在: " + goodsId);
        }
        List<SerialNumber> list = new ArrayList<>();
        for (String sn : serialNumbers) {
            if (existsBySerialNumber(sn)) {
                throw new RuntimeException("序列号已存在: " + sn);
            }
            SerialNumber serialNumber = new SerialNumber();
            serialNumber.setSerialNumber(sn);
            serialNumber.setGoodsid(goodsId);
            serialNumber.setInportid(inportId);
            serialNumber.setStatus(0); // 在库
            serialNumber.setInstockTime(new Date());
            list.add(serialNumber);

            // 记录入库日志
            saveLog(sn, goodsId, "inport", null, 0, null, "进货入库");
        }
        saveBatch(list);
    }

    @Override
    public void batchSale(Integer goodsId, List<String> serialNumbers, Integer salesId) {
        if (serialNumbers == null || serialNumbers.isEmpty()) {
            return;
        }
        for (String sn : serialNumbers) {
            // 原子状态流转：仅当序列号处于在库状态时才售出，防止并发销售同一序列号
            if (baseMapper.markSoldIfInStock(sn, goodsId) == 0) {
                throw new RuntimeException("序列号不可用: " + sn);
            }
            // 记录销售日志
            saveLog(sn, goodsId, "sale", 0, 1, null, "销售出库");
        }
    }

    @Override
    public void batchReturn(List<String> serialNumbers, boolean directResaleable) {
        if (serialNumbers == null || serialNumbers.isEmpty()) {
            return;
        }
        for (String sn : serialNumbers) {
            int newStatus = directResaleable ? 0 : 2;
            // 原子状态流转：仅当序列号处于已售状态时才可退货，防止并发重复退货
            if (baseMapper.markReturnedIfSold(sn, newStatus) == 0) {
                throw new RuntimeException("序列号不属于已售状态: " + sn);
            }
            // 查询商品ID用于日志（同事务内读取，可见刚更新的行）
            QueryWrapper<SerialNumber> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("serial_number", sn);
            SerialNumber serialNumber = getOne(queryWrapper);

            // 记录退货日志
            saveLog(sn, serialNumber != null ? serialNumber.getGoodsid() : null, "return", 1, newStatus, null,
                    directResaleable ? "退货直接回库" : "退货待检");
        }
    }

    @Override
    public List<SerialNumber> getAvailableByGoodsId(Integer goodsId) {
        QueryWrapper<SerialNumber> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("goodsid", goodsId);
        queryWrapper.eq("status", 0); // 在库可用
        queryWrapper.orderByDesc("instock_time");
        return list(queryWrapper);
    }

    @Override
    public boolean checkAvailable(String serialNumber, Integer goodsId) {
        QueryWrapper<SerialNumber> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("serial_number", serialNumber);
        queryWrapper.eq("goodsid", goodsId);
        queryWrapper.eq("status", 0);
        return count(queryWrapper) > 0;
    }

    @Override
    public ResultObj updateSerialNumber(SerialNumber serialNumber) {
        try {
            updateById(serialNumber);
            return ResultObj.UPDATE_SUCCESS;
        } catch (Exception e) {
            return ResultObj.error("更新失败: " + e.getMessage());
        }
    }
}
