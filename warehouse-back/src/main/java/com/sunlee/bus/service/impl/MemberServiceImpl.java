package com.sunlee.bus.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sunlee.bus.entity.Member;
import com.sunlee.bus.entity.MemberRecord;
import com.sunlee.bus.mapper.MemberMapper;
import com.sunlee.bus.service.IMemberRecordService;
import com.sunlee.bus.service.IMemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Service
@Transactional
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member> implements IMemberService {

    @Autowired
    private IMemberRecordService memberRecordService;

    @Override
    public void recharge(Integer memberId, BigDecimal amount, String operator, String remark) {
        Member member = getById(memberId);
        if (member == null || member.getStatus() != 1) {
            throw new RuntimeException("会员不存在或已禁用");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("充值金额必须大于0");
        }

        member.setBalance(member.getBalance().add(amount));
        member.setTotalRecharge(member.getTotalRecharge().add(amount));
        // 自动升级等级
        updateMemberLevel(member);
        updateById(member);

        saveRecord(memberId, "充值", amount, member.getBalance(), operator, remark);
    }

    @Override
    public void consume(Integer memberId, BigDecimal amount, String operator, String remark) {
        Member member = getById(memberId);
        if (member == null || member.getStatus() != 1) {
            throw new RuntimeException("会员不存在或已禁用");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("消费金额必须大于0");
        }
        if (member.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("余额不足，当前余额: " + member.getBalance());
        }

        member.setBalance(member.getBalance().subtract(amount));
        member.setTotalConsume(member.getTotalConsume().add(amount));
        // 消费积分: 1元=1积分
        member.setPoints(member.getPoints() + amount.intValue());
        updateById(member);

        saveRecord(memberId, "消费", amount, member.getBalance(), operator, remark);
    }

    @Override
    public void refund(Integer memberId, BigDecimal amount, String operator, String remark) {
        Member member = getById(memberId);
        if (member == null) {
            throw new RuntimeException("会员不存在");
        }

        member.setBalance(member.getBalance().add(amount));
        updateById(member);

        saveRecord(memberId, "退款", amount, member.getBalance(), operator, remark);
    }

    private void saveRecord(Integer memberId, String type, BigDecimal amount, BigDecimal balanceAfter, String operator, String remark) {
        MemberRecord record = new MemberRecord();
        record.setMemberId(memberId);
        record.setType(type);
        record.setAmount(amount);
        record.setBalanceAfter(balanceAfter);
        record.setOperator(operator);
        record.setCreateTime(new Date());
        record.setRemark(remark);
        memberRecordService.save(record);
    }

    private void updateMemberLevel(Member member) {
        BigDecimal total = member.getTotalRecharge();
        if (total.compareTo(new BigDecimal("5000")) >= 0) {
            member.setLevel(4); // 钻石
        } else if (total.compareTo(new BigDecimal("2000")) >= 0) {
            member.setLevel(3); // 金卡
        } else if (total.compareTo(new BigDecimal("500")) >= 0) {
            member.setLevel(2); // 银卡
        }
    }

    /**
     * 生成会员卡号: VIP + 日期 + 4位序号
     */
    public static String generateMemberNo(int seq) {
        return "VIP" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + String.format("%04d", seq);
    }
}
