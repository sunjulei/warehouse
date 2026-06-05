package com.sunlee.bus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sunlee.bus.entity.CommissionRule;
import com.sunlee.bus.entity.CommissionTier;
import com.sunlee.bus.mapper.CommissionRuleMapper;
import com.sunlee.bus.service.ICommissionRuleService;
import com.sunlee.bus.service.ICommissionTierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CommissionRuleServiceImpl extends ServiceImpl<CommissionRuleMapper, CommissionRule> implements ICommissionRuleService {

    @Autowired
    private ICommissionTierService commissionTierService;

    @Override
    public List<CommissionRule> loadAllWithTiers() {
        List<CommissionRule> rules = list();
        for (CommissionRule rule : rules) {
            rule.setTiers(commissionTierService.loadByRuleId(rule.getId()));
        }
        return rules;
    }
}
