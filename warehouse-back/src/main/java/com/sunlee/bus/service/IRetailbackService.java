package com.sunlee.bus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sunlee.bus.entity.Retailback;

public interface IRetailbackService extends IService<Retailback> {
    void addRetailback(Integer id, Integer number, String remark);
    void cancelRetailback(Integer id);
}
