-- 会员表
CREATE TABLE `bus_member` (
    `id`           int NOT NULL AUTO_INCREMENT,
    `member_no`    varchar(50) NOT NULL COMMENT '会员卡号',
    `name`         varchar(50) NOT NULL COMMENT '姓名',
    `phone`        varchar(20) DEFAULT NULL COMMENT '手机号',
    `gender`       varchar(10) DEFAULT NULL COMMENT '性别',
    `balance`      decimal(10,2) DEFAULT 0.00 COMMENT '储值卡余额',
    `total_recharge` decimal(10,2) DEFAULT 0.00 COMMENT '累计充值',
    `total_consume` decimal(10,2) DEFAULT 0.00 COMMENT '累计消费',
    `points`       int DEFAULT 0 COMMENT '积分',
    `level`        int DEFAULT 1 COMMENT '会员等级: 1普通 2银卡 3金卡 4钻石',
    `status`       int DEFAULT 1 COMMENT '状态: 0禁用 1正常',
    `create_time`  datetime DEFAULT CURRENT_TIMESTAMP,
    `remark`       varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_member_no` (`member_no`),
    KEY `idx_member_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='会员';

-- 会员充值/消费记录
CREATE TABLE `bus_member_record` (
    `id`          int NOT NULL AUTO_INCREMENT,
    `member_id`   int NOT NULL COMMENT '会员ID',
    `type`        varchar(20) NOT NULL COMMENT '类型: 充值/消费/退款',
    `amount`      decimal(10,2) NOT NULL COMMENT '金额',
    `balance_after` decimal(10,2) DEFAULT NULL COMMENT '操作后余额',
    `operator`    varchar(50) DEFAULT NULL COMMENT '操作人',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `remark`      varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `fk_member_record_member` (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='会员充值消费记录';
