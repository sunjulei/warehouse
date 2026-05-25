package com.sunlee.bus.controller;

import com.sunlee.bus.mapper.InportMapper;
import com.sunlee.bus.mapper.SalesMapper;
import com.sunlee.sys.common.DataGridView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * 报表管理
 */
@RestController
@RequestMapping("/report")
public class ReportController {

    @Autowired
    private InportMapper inportMapper;

    @Autowired
    private SalesMapper salesMapper;

    private static final String[] HOURS = {"0时","1时","2时","3时","4时","5时","6时","7时","8时","9时","10时","11时","12时","13时","14时","15时","16时","17时","18时","19时","20时","21时","22时","23时"};
    private static final String[] WEEK_DAYS = {"周日","周一","周二","周三","周四","周五","周六"};
    private static final String[] MONTHS = {"1月","2月","3月","4月","5月","6月","7月","8月","9月","10月","11月","12月"};

    @RequestMapping("inportAnalysis")
    public Map<String, Object> inportAnalysis(String type, String startDate, String endDate) {
        List<Map<String, Object>> data = inportMapper.queryInportAnalysis(type, startDate, endDate);
        return buildResult(type, data, startDate, endDate);
    }

    @RequestMapping("salesAnalysis")
    public Map<String, Object> salesAnalysis(String type, String startDate, String endDate) {
        List<Map<String, Object>> data = salesMapper.querySalesAnalysis(type, startDate, endDate);
        return buildResult(type, data, startDate, endDate);
    }

    @RequestMapping("inportGoodsAnalysis")
    public DataGridView inportGoodsAnalysis(String type, String startDate, String endDate) {
        List<Map<String, Object>> data = inportMapper.queryInportGoodsAnalysis(type, startDate, endDate);
        return new DataGridView(data);
    }

    @RequestMapping("salesGoodsAnalysis")
    public DataGridView salesGoodsAnalysis(String type, String startDate, String endDate) {
        List<Map<String, Object>> data = salesMapper.querySalesGoodsAnalysis(type, startDate, endDate);
        return new DataGridView(data);
    }

    @RequestMapping("profitAnalysis")
    public Map<String, Object> profitAnalysis(String type, String startDate, String endDate) {
        Map<String, Object> inportResult = buildResult(type, inportMapper.queryInportCostAnalysis(type, startDate, endDate), startDate, endDate);
        Map<String, Object> salesResult = buildResult(type, salesMapper.querySalesRevenueAnalysis(type, startDate, endDate), startDate, endDate);

        List<String> labels = (List<String>) inportResult.get("labels");
        List<Double> inportValues = (List<Double>) inportResult.get("values");
        List<Double> salesValues = (List<Double>) salesResult.get("values");
        List<Double> profitValues = new ArrayList<>();
        for (int i = 0; i < labels.size(); i++) {
            double s = i < salesValues.size() ? salesValues.get(i) : 0;
            double c = i < inportValues.size() ? inportValues.get(i) : 0;
            profitValues.add(s - c);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("labels", labels);
        result.put("inportValues", inportValues);
        result.put("salesValues", salesValues);
        result.put("profitValues", profitValues);
        return result;
    }

    private Map<String, Object> buildResult(String type, List<Map<String, Object>> data, String startDate, String endDate) {
        Map<String, Object> result = new HashMap<>();
        List<String> labels = new ArrayList<>();
        List<Double> values = new ArrayList<>();

        // 自定义日期范围走单独逻辑
        if ("custom".equals(type)) {
            Map<String, Double> strDataMap = new LinkedHashMap<>();
            for (Map<String, Object> row : data) {
                String label = String.valueOf(row.get("label"));
                double total = ((Number) row.get("total")).doubleValue();
                strDataMap.put(label, total);
            }
            try {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                java.text.SimpleDateFormat keyFmt = new java.text.SimpleDateFormat("yyyyMMdd");
                java.util.Date start = sdf.parse(startDate);
                java.util.Date end = sdf.parse(endDate);
                Calendar c = Calendar.getInstance();
                c.setTime(start);
                Calendar endCal = Calendar.getInstance();
                endCal.setTime(end);
                while (!c.after(endCal)) {
                    labels.add((c.get(Calendar.MONTH) + 1) + "月" + c.get(Calendar.DAY_OF_MONTH) + "日");
                    values.add(strDataMap.getOrDefault(keyFmt.format(c.getTime()), 0.0));
                    c.add(Calendar.DAY_OF_MONTH, 1);
                }
            } catch (Exception e) {
                // ignore parse errors
            }
            result.put("labels", labels);
            result.put("values", values);
            return result;
        }

        // 将查询结果转为 Map<label, total>
        Map<Integer, Double> dataMap = new LinkedHashMap<>();
        for (Map<String, Object> row : data) {
            int label = ((Number) row.get("label")).intValue();
            double total = ((Number) row.get("total")).doubleValue();
            dataMap.put(label, total);
        }

        // 根据类型填充完整的标签和数据
        switch (type) {
            case "today":
            case "yesterday":
                for (int i = 0; i < 24; i++) {
                    labels.add(HOURS[i]);
                    values.add(dataMap.getOrDefault(i, 0.0));
                }
                break;
            case "week":
                for (int i = 0; i < 7; i++) {
                    labels.add(WEEK_DAYS[i]);
                    values.add(dataMap.getOrDefault(i, 0.0));
                }
                break;
            case "month":
                Calendar cal = Calendar.getInstance();
                int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                for (int i = 1; i <= daysInMonth; i++) {
                    labels.add(i + "日");
                    values.add(dataMap.getOrDefault(i, 0.0));
                }
                break;
            case "quarter":
                Calendar qCal = Calendar.getInstance();
                int currentQuarter = (qCal.get(Calendar.MONTH)) / 3;
                int startMonth = currentQuarter * 3 + 1;
                for (int i = startMonth; i < startMonth + 3; i++) {
                    labels.add(MONTHS[i - 1]);
                    values.add(dataMap.getOrDefault(i, 0.0));
                }
                break;
            case "year":
                for (int i = 1; i <= 12; i++) {
                    labels.add(MONTHS[i - 1]);
                    values.add(dataMap.getOrDefault(i, 0.0));
                }
                break;
        }

        result.put("labels", labels);
        result.put("values", values);
        return result;
    }
}
