package com.sunlee.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.sunlee.sys.entity.Dept;
import com.sunlee.sys.mapper.DeptMapper;
import com.sunlee.sys.service.IDeptService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

/**
 * <p>
 * InnoDB free: 9216 kB 服务实现类
 * </p>
 *
 * @author sunlee
 * @since 2026-02-20
 */
@Service
@Transactional
public class DeptServiceImpl extends ServiceImpl<DeptMapper, Dept> implements IDeptService {

    @Override
    public Dept getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    public boolean update(Dept entity, Wrapper<Dept> updateWrapper){
        return super.update(entity,updateWrapper);
    }

    @Override
    public boolean updateById(Dept entity){
        return super.updateById(entity);
    }

    @Override
    public boolean removeById(Serializable id){
        return super.removeById(id);
    }

    @Override
    public boolean save(Dept entity) {
        return super.save(entity);
    }


}
