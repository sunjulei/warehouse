package com.sunlee.bus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sunlee.bus.entity.Goods;
import com.sunlee.bus.entity.SerialNumber;
import com.sunlee.bus.mapper.GoodsMapper;
import com.sunlee.bus.mapper.SerialNumberMapper;
import com.sunlee.bus.service.ISerialNumberService;
import com.sunlee.bus.vo.SerialNumberVo;
import com.sunlee.sys.common.DataGridView;
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
}
