package com.sunlee.bus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sunlee.bus.entity.CommissionTier;
import com.sunlee.bus.mapper.CommissionTierMapper;
import com.sunlee.bus.service.ICommissionTierService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CommissionTierServiceImpl extends ServiceImpl<CommissionTierMapper, CommissionTier> implements ICommissionTierService {

    @Override
    public List<CommissionTier> loadByRuleId(Integer ruleId) {
        QueryWrapper<CommissionTier> qw = new QueryWrapper<>();
        qw.eq("rule_id", ruleId);
        qw.orderByAsc("min_amount");
        return list(qw);
    }
}
