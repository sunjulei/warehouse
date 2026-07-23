package com.sunlee.bus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sunlee.bus.entity.Member;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

@Mapper
public interface MemberMapper extends BaseMapper<Member> {

    /**
     * 原子充值：余额与累计充值同时增加
     * @return 受影响行数
     */
    int addRecharge(@Param("id") Integer id, @Param("amount") BigDecimal amount);

    /**
     * 原子消费：余额不足时不更新（返回0），同时累加消费额与积分
     * @return 受影响行数，0表示余额不足
     */
    int deductBalance(@Param("id") Integer id, @Param("amount") BigDecimal amount, @Param("points") Integer points);

    /**
     * 原子退款：余额增加
     * @return 受影响行数
     */
    int addBalance(@Param("id") Integer id, @Param("amount") BigDecimal amount);
}
