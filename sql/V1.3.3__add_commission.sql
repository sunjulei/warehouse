-- 店员提成规则表
CREATE TABLE `bus_commission_rule` (
    `id`          int NOT NULL AUTO_INCREMENT,
    `name`        varchar(100) NOT NULL COMMENT '规则名称',
    `type`        varchar(20) NOT NULL COMMENT '提成类型: fixed(固定比例)/tiered(阶梯)',
    `rate`        decimal(5,2) DEFAULT NULL COMMENT '固定提成比例(%)',
    `status`      int DEFAULT 1 COMMENT '状态: 0禁用 1启用',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `remark`      varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='提成规则';

-- 阶梯提成明细
CREATE TABLE `bus_commission_tier` (
    `id`          int NOT NULL AUTO_INCREMENT,
    `rule_id`     int NOT NULL COMMENT '规则ID',
    `min_amount`  decimal(10,2) NOT NULL COMMENT '销售额下限',
    `max_amount`  decimal(10,2) DEFAULT NULL COMMENT '销售额上限（NULL=无上限）',
    `rate`        decimal(5,2) NOT NULL COMMENT '提成比例(%)',
    PRIMARY KEY (`id`),
    KEY `fk_tier_rule` (`rule_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='阶梯提成';

-- 店员提成记录（月度汇总）
CREATE TABLE `bus_commission_record` (
    `id`            int NOT NULL AUTO_INCREMENT,
    `operator`      varchar(50) NOT NULL COMMENT '店员姓名',
    `year_month`    varchar(10) NOT NULL COMMENT '月份 (yyyy-MM)',
    `total_sales`   decimal(12,2) DEFAULT 0 COMMENT '月销售额',
    `total_orders`  int DEFAULT 0 COMMENT '月订单数',
    `commission_rate` decimal(5,2) DEFAULT 0 COMMENT '适用提成比例(%)',
    `commission_amount` decimal(10,2) DEFAULT 0 COMMENT '提成金额',
    `rule_id`       int DEFAULT NULL COMMENT '适用规则ID',
    `status`        int DEFAULT 0 COMMENT '状态: 0待确认 1已确认 2已发放',
    `create_time`   datetime DEFAULT CURRENT_TIMESTAMP,
    `remark`        varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_operator_month` (`operator`, `year_month`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='店员提成记录';

-- 默认提成规则
INSERT INTO `bus_commission_rule` (`name`, `type`, `rate`, `status`, `remark`) VALUES
('默认固定5%', 'fixed', 5.00, 1, '默认固定提成比例5%');

-- 默认阶梯提成规则
INSERT INTO `bus_commission_rule` (`name`, `type`, `rate`, `status`, `remark`) VALUES
('阶梯提成', 'tiered', NULL, 0, '按销售额阶梯计算提成');
SET @rule_id = LAST_INSERT_ID();
INSERT INTO `bus_commission_tier` (`rule_id`, `min_amount`, `max_amount`, `rate`) VALUES
(@rule_id, 0, 10000, 3.00),
(@rule_id, 10000, 30000, 5.00),
(@rule_id, 30000, 50000, 7.00),
(@rule_id, 50000, NULL, 10.00);
