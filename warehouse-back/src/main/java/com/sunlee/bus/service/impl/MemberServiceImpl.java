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

        // 原子更新余额与累计充值，防止并发充值丢失更新
        if (baseMapper.addRecharge(memberId, amount) == 0) {
            throw new RuntimeException("充值失败，请重试");
        }

        // 重新读取更新后的数据（用于等级计算与流水余额），等级只升不降，且只回写等级字段，避免覆盖并发更新
        Member updated = getById(memberId);
        Integer newLevel = computeLevel(updated.getTotalRecharge());
        if (newLevel > updated.getLevel()) {
            Member levelUpdate = new Member();
            levelUpdate.setId(memberId);
            levelUpdate.setLevel(newLevel);
            updateById(levelUpdate);
        }

        saveRecord(memberId, "充值", amount, updated.getBalance(), operator, remark);
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

        // 原子扣减余额（余额不足时不更新），同时累加消费额与积分（1元=1积分）
        if (baseMapper.deductBalance(memberId, amount, amount.intValue()) == 0) {
            throw new RuntimeException("余额不足，当前余额: " + member.getBalance());
        }

        Member updated = getById(memberId);
        saveRecord(memberId, "消费", amount, updated.getBalance(), operator, remark);
    }

    @Override
    public void refund(Integer memberId, BigDecimal amount, String operator, String remark) {
        Member member = getById(memberId);
        if (member == null) {
            throw new RuntimeException("会员不存在");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("退款金额必须大于0");
        }

        // 原子增加余额，防止并发退款丢失更新
        if (baseMapper.addBalance(memberId, amount) == 0) {
            throw new RuntimeException("退款失败，请重试");
        }

        Member updated = getById(memberId);
        saveRecord(memberId, "退款", amount, updated.getBalance(), operator, remark);
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

    /**
     * 根据累计充值计算会员等级: 1普通 2银卡 3金卡 4钻石
     */
    private Integer computeLevel(BigDecimal total) {
        if (total.compareTo(new BigDecimal("5000")) >= 0) {
            return 4; // 钻石
        } else if (total.compareTo(new BigDecimal("2000")) >= 0) {
            return 3; // 金卡
        } else if (total.compareTo(new BigDecimal("500")) >= 0) {
            return 2; // 银卡
        }
        return 1; // 普通
    }

    /**
     * 生成会员卡号: VIP + 日期 + 4位序号
     */
    public static String generateMemberNo(int seq) {
        return "VIP" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + String.format("%04d", seq);
    }
}
