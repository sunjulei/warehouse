package com.sunlee.bus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sunlee.bus.entity.CommissionRule;

import java.util.List;

public interface ICommissionRuleService extends IService<CommissionRule> {
    List<CommissionRule> loadAllWithTiers();
}
