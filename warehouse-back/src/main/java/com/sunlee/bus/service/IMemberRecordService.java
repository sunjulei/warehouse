package com.sunlee.bus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sunlee.bus.entity.MemberRecord;
import java.util.List;

public interface IMemberRecordService extends IService<MemberRecord> {
    List<MemberRecord> loadByMemberId(Integer memberId);
}
