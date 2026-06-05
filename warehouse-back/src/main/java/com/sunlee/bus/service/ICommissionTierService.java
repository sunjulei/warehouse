package com.sunlee.bus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sunlee.bus.entity.CommissionTier;
import java.util.List;

public interface ICommissionTierService extends IService<CommissionTier> {
    List<CommissionTier> loadByRuleId(Integer ruleId);
}
