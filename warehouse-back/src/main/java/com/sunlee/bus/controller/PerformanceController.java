package com.sunlee.bus.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sunlee.bus.entity.Sales;
import com.sunlee.bus.service.ISalesService;
import com.sunlee.sys.common.DataGridView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 业绩排名
 */
@Slf4j
@RestController
@RequestMapping("/performance")
public class PerformanceController {

    @Autowired
    private ISalesService salesService;

    /**
     * 销售员业绩排名
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param period    时间维度: today/week/month/quarter/year
     */
    @RequestMapping("salesRanking")
    public DataGridView salesRanking(
            @DateTimeFormat(pattern = "yyyy-MM-dd") Date startTime,
            @DateTimeFormat(pattern = "yyyy-MM-dd") Date endTime,
            String period) {

        Date[] range = resolveDateRange(period, startTime, endTime);
        QueryWrapper<Sales> qw = new QueryWrapper<>();
        qw.ge(range[0] != null, "salestime", range[0]);
        qw.le(range[1] != null, "salestime", range[1]);
        List<Sales> salesList = salesService.list(qw);

        // 按操作人分组统计
        Map<String, List<Sales>> grouped = salesList.stream()
                .filter(s -> s.getOperateperson() != null)
                .collect(Collectors.groupingBy(Sales::getOperateperson));

        List<Map<String, Object>> ranking = new ArrayList<>();
        for (Map.Entry<String, List<Sales>> entry : grouped.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("operator", entry.getKey());
            List<Sales> list = entry.getValue();
            int totalOrders = list.size();
            int totalQuantity = list.stream().mapToInt(s -> s.getNumber() != null ? s.getNumber() : 0).sum();
            BigDecimal totalAmount = list.stream()
                    .map(s -> s.getSaleprice() != null ? s.getSaleprice().multiply(BigDecimal.valueOf(s.getNumber() != null ? s.getNumber() : 0)) : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            item.put("totalOrders", totalOrders);
            item.put("totalQuantity", totalQuantity);
            item.put("totalAmount", totalAmount.setScale(2, RoundingMode.HALF_UP));
            ranking.add(item);
        }

        // 按总金额降序
        ranking.sort((a, b) -> ((BigDecimal) b.get("totalAmount")).compareTo((BigDecimal) a.get("totalAmount")));

        // 添加排名
        for (int i = 0; i < ranking.size(); i++) {
            ranking.get(i).put("rank", i + 1);
        }

        return new DataGridView((long) ranking.size(), ranking);
    }

    /**
     * 商品销量排名
     */
    @RequestMapping("goodsRanking")
    public DataGridView goodsRanking(
            @DateTimeFormat(pattern = "yyyy-MM-dd") Date startTime,
            @DateTimeFormat(pattern = "yyyy-MM-dd") Date endTime,
            String period) {

        Date[] range = resolveDateRange(period, startTime, endTime);
        QueryWrapper<Sales> qw = new QueryWrapper<>();
        qw.ge(range[0] != null, "salestime", range[0]);
        qw.le(range[1] != null, "salestime", range[1]);
        List<Sales> salesList = salesService.list(qw);

        // 按商品分组
        Map<Integer, List<Sales>> grouped = salesList.stream()
                .filter(s -> s.getGoodsid() != null)
                .collect(Collectors.groupingBy(Sales::getGoodsid));

        List<Map<String, Object>> ranking = new ArrayList<>();
        for (Map.Entry<Integer, List<Sales>> entry : grouped.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("goodsid", entry.getKey());
            List<Sales> list = entry.getValue();
            // 取商品名
            String goodsName = list.stream().map(Sales::getGoodsname).filter(Objects::nonNull).findFirst().orElse("商品#" + entry.getKey());
            item.put("goodsname", goodsName);
            int totalQuantity = list.stream().mapToInt(s -> s.getNumber() != null ? s.getNumber() : 0).sum();
            BigDecimal totalAmount = list.stream()
                    .map(s -> s.getSaleprice() != null ? s.getSaleprice().multiply(BigDecimal.valueOf(s.getNumber() != null ? s.getNumber() : 0)) : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            item.put("totalQuantity", totalQuantity);
            item.put("totalAmount", totalAmount.setScale(2, RoundingMode.HALF_UP));
            ranking.add(item);
        }

        ranking.sort((a, b) -> ((Integer) b.get("totalQuantity")).compareTo((Integer) a.get("totalQuantity")));

        for (int i = 0; i < ranking.size(); i++) {
            ranking.get(i).put("rank", i + 1);
        }

        return new DataGridView((long) ranking.size(), ranking);
    }

    private Date[] resolveDateRange(String period, Date startTime, Date endTime) {
        if (startTime != null && endTime != null) {
            return new Date[]{startTime, endTime};
        }
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        Date start = null;
        if (period != null) {
            switch (period) {
                case "today":
                    cal.set(Calendar.HOUR_OF_DAY, 0);
                    cal.set(Calendar.MINUTE, 0);
                    cal.set(Calendar.SECOND, 0);
                    start = cal.getTime();
                    break;
                case "week":
                    cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
                    cal.set(Calendar.HOUR_OF_DAY, 0);
                    cal.set(Calendar.MINUTE, 0);
                    cal.set(Calendar.SECOND, 0);
                    start = cal.getTime();
                    break;
                case "month":
                    cal.set(Calendar.DAY_OF_MONTH, 1);
                    cal.set(Calendar.HOUR_OF_DAY, 0);
                    cal.set(Calendar.MINUTE, 0);
                    cal.set(Calendar.SECOND, 0);
                    start = cal.getTime();
                    break;
                case "quarter":
                    int month = cal.get(Calendar.MONTH);
                    cal.set(Calendar.MONTH, month / 3 * 3);
                    cal.set(Calendar.DAY_OF_MONTH, 1);
                    cal.set(Calendar.HOUR_OF_DAY, 0);
                    cal.set(Calendar.MINUTE, 0);
                    cal.set(Calendar.SECOND, 0);
                    start = cal.getTime();
                    break;
                case "year":
                    cal.set(Calendar.DAY_OF_YEAR, 1);
                    cal.set(Calendar.HOUR_OF_DAY, 0);
                    cal.set(Calendar.MINUTE, 0);
                    cal.set(Calendar.SECOND, 0);
                    start = cal.getTime();
                    break;
            }
        }
        return new Date[]{start, now};
    }
}
