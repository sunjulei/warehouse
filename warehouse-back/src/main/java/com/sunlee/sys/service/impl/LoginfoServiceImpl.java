package com.sunlee.sys.service.impl;

import com.sunlee.sys.entity.Loginfo;
import com.sunlee.sys.mapper.LoginfoMapper;
import com.sunlee.sys.service.ILoginfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * InnoDB free: 9216 kB 服务实现类
 * </p>
 *
 * @author sunlee
 * @since 2026-02-01
 */
@Service
@Transactional
public class LoginfoServiceImpl extends ServiceImpl<LoginfoMapper, Loginfo> implements ILoginfoService {

}
