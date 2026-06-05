package com.sunlee.bus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sunlee.bus.entity.Member;
import java.math.BigDecimal;
import java.util.List;

public interface IMemberService extends IService<Member> {

    /** 会员充值 */
    void recharge(Integer memberId, BigDecimal amount, String operator, String remark);

    /** 会员消费（从储值卡扣款） */
    void consume(Integer memberId, BigDecimal amount, String operator, String remark);

    /** 退款 */
    void refund(Integer memberId, BigDecimal amount, String operator, String remark);
}
