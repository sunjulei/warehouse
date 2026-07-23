package com.sunlee.bus.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sunlee.bus.entity.Retailback;
import com.sunlee.bus.mapper.RetailbackMapper;
import com.sunlee.bus.service.IRetailbackService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RetailbackServiceImpl extends ServiceImpl<RetailbackMapper, Retailback> implements IRetailbackService {
}
