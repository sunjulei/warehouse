package com.sunlee.bus.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sunlee.bus.entity.MemberLevelRule;
import com.sunlee.bus.mapper.MemberLevelRuleMapper;
import com.sunlee.bus.service.IMemberLevelRuleService;
import org.springframework.stereotype.Service;

/**
 * 会员等级规则 Service 实现
 */
@Service
public class MemberLevelRuleServiceImpl extends ServiceImpl<MemberLevelRuleMapper, MemberLevelRule> implements IMemberLevelRuleService {
}
