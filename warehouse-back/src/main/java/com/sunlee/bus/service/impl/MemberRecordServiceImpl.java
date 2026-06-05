package com.sunlee.bus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sunlee.bus.entity.MemberRecord;
import com.sunlee.bus.mapper.MemberRecordMapper;
import com.sunlee.bus.service.IMemberRecordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MemberRecordServiceImpl extends ServiceImpl<MemberRecordMapper, MemberRecord> implements IMemberRecordService {

    @Override
    public List<MemberRecord> loadByMemberId(Integer memberId) {
        QueryWrapper<MemberRecord> qw = new QueryWrapper<>();
        qw.eq("member_id", memberId);
        qw.orderByDesc("create_time");
        return list(qw);
    }
}
