package com.sunlee.bus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sunlee.bus.entity.CommissionRecord;
import java.util.List;

public interface ICommissionRecordService extends IService<CommissionRecord> {
    List<CommissionRecord> loadByMonth(String yearMonth);
}
