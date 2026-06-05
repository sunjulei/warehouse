package com.sunlee.bus.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sunlee.bus.entity.Member;
import com.sunlee.bus.entity.MemberRecord;
import com.sunlee.bus.service.IMemberRecordService;
import com.sunlee.bus.service.IMemberService;
import com.sunlee.bus.service.impl.MemberServiceImpl;
import com.sunlee.bus.vo.MemberVo;
import com.sunlee.sys.annotation.OperationLog;
import com.sunlee.sys.common.DataGridView;
import com.sunlee.sys.common.ResultObj;
import com.sunlee.sys.common.WebUtils;
import com.sunlee.sys.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

/**
 * 会员管理
 */
@Slf4j
@RestController
@RequestMapping("/member")
public class MemberController {

    @Autowired
    private IMemberService memberService;

    @Autowired
    private IMemberRecordService memberRecordService;

    @Autowired
    private MemberServiceImpl memberServiceImpl;

    /**
     * 查询会员列表
     */
    @RequestMapping("loadAllMember")
    public DataGridView loadAllMember(MemberVo memberVo) {
        IPage<Member> page = new Page<>(memberVo.getPage(), memberVo.getLimit());
        QueryWrapper<Member> qw = new QueryWrapper<>();
        qw.like(StringUtils.isNotBlank(memberVo.getName()), "name", memberVo.getName());
        qw.like(StringUtils.isNotBlank(memberVo.getPhone()), "phone", memberVo.getPhone());
        qw.like(StringUtils.isNotBlank(memberVo.getMemberNo()), "member_no", memberVo.getMemberNo());
        qw.eq(memberVo.getStatus() != null, "status", memberVo.getStatus());
        qw.orderByDesc("create_time");
        memberService.page(page, qw);
        return new DataGridView(page.getTotal(), page.getRecords());
    }

    /**
     * 根据手机号或卡号查找会员（散客零售用）
     */
    @RequestMapping("findMember")
    public DataGridView findMember(String keyword) {
        QueryWrapper<Member> qw = new QueryWrapper<>();
        if (StringUtils.isNotBlank(keyword)) {
            qw.and(w -> w.like("phone", keyword).or().like("member_no", keyword).or().like("name", keyword));
        }
        qw.eq("status", 1);
        List<Member> list = memberService.list(qw);
        return new DataGridView((long) list.size(), list);
    }

    /**
     * 添加会员
     */
    @OperationLog(type = "添加", module = "会员管理", description = "'添加会员: ' + #args[0].name")
    @RequestMapping("addMember")
    public ResultObj addMember(MemberVo memberVo) {
        try {
            int count = (int) memberService.count();
            memberVo.setMemberNo(MemberServiceImpl.generateMemberNo(count + 1));
            memberVo.setBalance(BigDecimal.ZERO);
            memberVo.setTotalRecharge(BigDecimal.ZERO);
            memberVo.setTotalConsume(BigDecimal.ZERO);
            memberVo.setPoints(0);
            memberVo.setLevel(1);
            memberVo.setStatus(1);
            memberService.save(memberVo);
            return new ResultObj(200, "会员添加成功，卡号: " + memberVo.getMemberNo());
        } catch (Exception e) {
            log.error("添加会员失败: {}", e.getMessage(), e);
            return ResultObj.ADD_ERROR;
        }
    }

    /**
     * 修改会员
     */
    @OperationLog(type = "修改", module = "会员管理", description = "'修改会员: ' + #args[0].name")
    @RequestMapping("updateMember")
    public ResultObj updateMember(MemberVo memberVo) {
        try {
            memberService.updateById(memberVo);
            return ResultObj.UPDATE_SUCCESS;
        } catch (Exception e) {
            log.error("修改会员失败: {}", e.getMessage(), e);
            return ResultObj.UPDATE_ERROR;
        }
    }

    /**
     * 会员充值
     */
    @OperationLog(type = "充值", module = "会员管理", description = "'会员充值ID: ' + #args[0] + ', 金额: ' + #args[1]")
    @RequestMapping("recharge")
    public ResultObj recharge(Integer memberId, BigDecimal amount) {
        try {
            User user = (User) WebUtils.getSession().getAttribute("user");
            String operator = user != null ? user.getName() : "未知用户";
            memberService.recharge(memberId, amount, operator, "储值卡充值");
            return new ResultObj(200, "充值成功");
        } catch (Exception e) {
            log.error("会员充值失败: {}", e.getMessage(), e);
            return new ResultObj(-1, e.getMessage());
        }
    }

    /**
     * 会员消费
     */
    @OperationLog(type = "消费", module = "会员管理", description = "'会员消费ID: ' + #args[0] + ', 金额: ' + #args[1]")
    @RequestMapping("consume")
    public ResultObj consume(Integer memberId, BigDecimal amount) {
        try {
            User user = (User) WebUtils.getSession().getAttribute("user");
            String operator = user != null ? user.getName() : "未知用户";
            memberService.consume(memberId, amount, operator, "储值卡消费");
            return new ResultObj(200, "消费成功");
        } catch (Exception e) {
            log.error("会员消费失败: {}", e.getMessage(), e);
            return new ResultObj(-1, e.getMessage());
        }
    }

    /**
     * 加载会员充值/消费记录
     */
    @RequestMapping("loadMemberRecords")
    public DataGridView loadMemberRecords(Integer memberId) {
        List<MemberRecord> records = memberRecordService.loadByMemberId(memberId);
        return new DataGridView((long) records.size(), records);
    }

    /**
     * 删除会员
     */
    @OperationLog(type = "删除", module = "会员管理", description = "'删除会员ID: ' + #args[0]")
    @RequestMapping("deleteMember")
    public ResultObj deleteMember(Integer id) {
        try {
            memberService.removeById(id);
            return ResultObj.DELETE_SUCCESS;
        } catch (Exception e) {
            log.error("删除会员失败: {}", e.getMessage(), e);
            return ResultObj.DELETE_ERROR;
        }
    }
}
