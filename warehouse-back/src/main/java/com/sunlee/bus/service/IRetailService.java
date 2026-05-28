package com.sunlee.bus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sunlee.bus.entity.Retail;

import java.util.List;

public interface IRetailService extends IService<Retail> {
    void deleteRetail(Integer id);
    void batchSave(List<Retail> list);
}
