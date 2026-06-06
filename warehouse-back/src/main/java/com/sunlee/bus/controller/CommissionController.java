package com.sunlee.bus.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sunlee.bus.entity.*;
import com.sunlee.bus.service.*;
import com.sunlee.sys.annotation.OperationLog;
import com.sunlee.sys.common.DataGridView;
import com.sunlee.sys.common.ResultObj;
import com.sunlee.sys.common.WebUtils;
import com.sunlee.sys.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 店员提成管理
 */
@Slf4j
@RestController
@RequestMapping("/commission")
public class CommissionController {

    @Autowired
    private ICommissionRuleService commissionRuleService;

    @Autowired
    private ICommissionTierService commissionTierService;

    @Autowired
    private ICommissionRecordService commissionRecordService;

    @Autowired
    private ISalesService salesService;

    /**
     * 查询所有提成规则
     */
    @RequestMapping("loadAllRules")
    public DataGridView loadAllRules() {
        List<CommissionRule> rules = commissionRuleService.loadAllWithTiers();
        return new DataGridView((long) rules.size(), rules);
    }

    /**
     * 保存提成规则
     */
    @OperationLog(type = "添加", module = "提成管理", description = "'保存提成规则: ' + #args[0].name")
    @RequestMapping("saveRule")
    public ResultObj saveRule(CommissionRule rule) {
        try {
            if (rule.getId() != null) {
                commissionRuleService.updateById(rule);
            } else {
                commissionRuleService.save(rule);
            }
            return ResultObj.UPDATE_SUCCESS;
        } catch (Exception e) {
            log.error("保存提成规则失败: {}", e.getMessage(), e);
            return ResultObj.UPDATE_ERROR;
        }
    }

    /**
     * 删除提成规则
     */
    @OperationLog(type = "删除", module = "提成管理", description = "'删除提成规则ID: ' + #args[0]")
    @RequestMapping("deleteRule")
    public ResultObj deleteRule(Integer id) {
        try {
            // 先删除关联的阶梯明细
            QueryWrapper<CommissionTier> tqw = new QueryWrapper<>();
            tqw.eq("rule_id", id);
            commissionTierService.remove(tqw);
            commissionRuleService.removeById(id);
            return ResultObj.DELETE_SUCCESS;
        } catch (Exception e) {
            log.error("删除提成规则失败: {}", e.getMessage(), e);
            return ResultObj.DELETE_ERROR;
        }
    }

    /**
     * 保存阶梯提成明细
     */
    @RequestMapping("saveTiers")
    @SuppressWarnings("unchecked")
    public ResultObj saveTiers(@RequestBody Map<String, Object> body) {
        try {
            Integer ruleId = (Integer) body.get("ruleId");
            List<Map<String, Object>> tiersData = (List<Map<String, Object>>) body.get("tiers");

            // 先删除旧的
            QueryWrapper<CommissionTier> qw = new QueryWrapper<>();
            qw.eq("rule_id", ruleId);
            commissionTierService.remove(qw);

            // 保存新的
            if (tiersData != null) {
                for (Map<String, Object> t : tiersData) {
                    CommissionTier tier = new CommissionTier();
                    tier.setRuleId(ruleId);
                    tier.setMinAmount(new java.math.BigDecimal(String.valueOf(t.get("minAmount"))));
                    tier.setMaxAmount(t.get("maxAmount") != null ? new java.math.BigDecimal(String.valueOf(t.get("maxAmount"))) : null);
                    tier.setRate(new java.math.BigDecimal(String.valueOf(t.get("rate"))));
                    commissionTierService.save(tier);
                }
            }
            return ResultObj.UPDATE_SUCCESS;
        } catch (Exception e) {
            log.error("保存阶梯提成失败: {}", e.getMessage(), e);
            return ResultObj.UPDATE_ERROR;
        }
    }

    /**
     * 计算某月提成
     * @param yearMonth 格式: yyyy-MM
     * @param ruleId 使用的规则ID
     */
    @OperationLog(type = "计算", module = "提成管理", description = "'计算提成月份: ' + #args[0]")
    @RequestMapping("calculate")
    public ResultObj calculate(String yearMonth, Integer ruleId) {
        try {
            CommissionRule rule = commissionRuleService.getById(ruleId);
            if (rule == null) {
                return new ResultObj(-1, "提成规则不存在");
            }

            // 查询该月所有销售记录
            QueryWrapper<Sales> salesQW = new QueryWrapper<>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
            Date monthStart = sdf.parse(yearMonth);
            Calendar cal = Calendar.getInstance();
            cal.setTime(monthStart);
            cal.add(Calendar.MONTH, 1);
            Date monthEnd = cal.getTime();
            salesQW.ge("salestime", monthStart);
            salesQW.lt("salestime", monthEnd);
            List<Sales> salesList = salesService.list(salesQW);

            // 按店员分组
            Map<String, List<Sales>> grouped = new HashMap<>();
            for (Sales sale : salesList) {
                if (sale.getOperateperson() != null) {
                    grouped.computeIfAbsent(sale.getOperateperson(), k -> new ArrayList<>()).add(sale);
                }
            }

            List<CommissionRecord> records = new ArrayList<>();
            for (Map.Entry<String, List<Sales>> entry : grouped.entrySet()) {
                String operator = entry.getKey();
                List<Sales> opSales = entry.getValue();

                BigDecimal totalSales = opSales.stream()
                        .map(s -> s.getSaleprice() != null ?
                                s.getSaleprice().multiply(BigDecimal.valueOf(s.getNumber() != null ? s.getNumber() : 0)) :
                                BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                int totalOrders = opSales.size();

                // 计算提成
                BigDecimal rate;
                if ("fixed".equals(rule.getType())) {
                    rate = rule.getRate();
                } else {
                    // 阶梯计算
                    rate = calculateTieredRate(totalSales, rule.getId());
                }

                BigDecimal commissionAmount = totalSales.multiply(rate).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

                // 更新或创建记录
                QueryWrapper<CommissionRecord> crQW = new QueryWrapper<>();
                crQW.eq("operator", operator).eq("`year_month`", yearMonth);
                CommissionRecord existing = commissionRecordService.getOne(crQW);

                CommissionRecord record = existing != null ? existing : new CommissionRecord();
                record.setOperator(operator);
                record.setYearMonth(yearMonth);
                record.setTotalSales(totalSales.setScale(2, RoundingMode.HALF_UP));
                record.setTotalOrders(totalOrders);
                record.setCommissionRate(rate);
                record.setCommissionAmount(commissionAmount);
                record.setRuleId(ruleId);
                record.setStatus(0);

                if (existing != null) {
                    commissionRecordService.updateById(record);
                } else {
                    commissionRecordService.save(record);
                }
                records.add(record);
            }

            return new ResultObj(200, "提成计算完成，共 " + records.size() + " 名店员");
        } catch (Exception e) {
            log.error("计算提成失败: {}", e.getMessage(), e);
            return new ResultObj(-1, "计算提成失败: " + e.getMessage());
        }
    }

    /**
     * 查询某月提成记录
     */
    @RequestMapping("loadRecords")
    public DataGridView loadRecords(String yearMonth) {
        List<CommissionRecord> records = commissionRecordService.loadByMonth(yearMonth);
        return new DataGridView((long) records.size(), records);
    }

    /**
     * 确认/发放提成
     */
    @OperationLog(type = "确认", module = "提成管理", description = "'确认提成ID: ' + #args[0]")
    @RequestMapping("confirmRecord")
    public ResultObj confirmRecord(Integer id, Integer status) {
        try {
            CommissionRecord record = commissionRecordService.getById(id);
            if (record == null) return new ResultObj(-1, "记录不存在");
            record.setStatus(status);
            commissionRecordService.updateById(record);
            return ResultObj.UPDATE_SUCCESS;
        } catch (Exception e) {
            log.error("确认提成失败: {}", e.getMessage(), e);
            return ResultObj.UPDATE_ERROR;
        }
    }

    /**
     * 查询当前登录用户的提成记录
     */
    @RequestMapping("loadMyCommission")
    public DataGridView loadMyCommission(String yearMonth) {
        try {
            User user = (User) WebUtils.getSession().getAttribute("user");
            if (user == null) {
                return new DataGridView(0L, new ArrayList<>());
            }
            String operator = user.getName();

            QueryWrapper<CommissionRecord> qw = new QueryWrapper<>();
            qw.eq("operator", operator);
            if (yearMonth != null && !yearMonth.isEmpty()) {
                qw.eq("`year_month`", yearMonth);
            }
            qw.orderByDesc("`year_month`");
            List<CommissionRecord> records = commissionRecordService.list(qw);
            return new DataGridView((long) records.size(), records);
        } catch (Exception e) {
            log.error("查询个人提成失败: {}", e.getMessage(), e);
            return new DataGridView(0L, new ArrayList<>());
        }
    }

    private BigDecimal calculateTieredRate(BigDecimal totalSales, Integer ruleId) {
        List<CommissionTier> tiers = commissionTierService.loadByRuleId(ruleId);
        for (CommissionTier tier : tiers) {
            boolean aboveMin = totalSales.compareTo(tier.getMinAmount()) >= 0;
            boolean belowMax = tier.getMaxAmount() == null || totalSales.compareTo(tier.getMaxAmount()) < 0;
            if (aboveMin && belowMax) {
                return tier.getRate();
            }
        }
        return BigDecimal.ZERO;
    }
}
