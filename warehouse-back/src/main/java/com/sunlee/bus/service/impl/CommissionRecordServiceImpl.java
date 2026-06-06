package com.sunlee.bus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sunlee.bus.entity.CommissionRecord;
import com.sunlee.bus.mapper.CommissionRecordMapper;
import com.sunlee.bus.service.ICommissionRecordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CommissionRecordServiceImpl extends ServiceImpl<CommissionRecordMapper, CommissionRecord> implements ICommissionRecordService {

    @Override
    public List<CommissionRecord> loadByMonth(String yearMonth) {
        QueryWrapper<CommissionRecord> qw = new QueryWrapper<>();
        qw.eq("`year_month`", yearMonth);
        qw.orderByDesc("`commission_amount`");
        return list(qw);
    }
}
