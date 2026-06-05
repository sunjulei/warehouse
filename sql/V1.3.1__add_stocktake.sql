-- 盘点单主表
CREATE TABLE `bus_stocktake` (
    `id`          int NOT NULL AUTO_INCREMENT,
    `stocktake_no` varchar(50) NOT NULL COMMENT '盘点单号',
    `status`      int DEFAULT 0 COMMENT '状态: 0进行中 1已完成 2已取消',
    `operator`    varchar(50) DEFAULT NULL COMMENT '盘点人',
    `remark`      varchar(500) DEFAULT NULL COMMENT '备注',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `finish_time` datetime DEFAULT NULL COMMENT '完成时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_stocktake_no` (`stocktake_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='盘点单';

-- 盘点明细表
CREATE TABLE `bus_stocktake_item` (
    `id`           int NOT NULL AUTO_INCREMENT,
    `stocktake_id` int NOT NULL COMMENT '盘点单ID',
    `goodsid`      int NOT NULL COMMENT '商品ID',
    `goodsname`    varchar(255) DEFAULT NULL COMMENT '商品名称（冗余）',
    `system_num`   int DEFAULT 0 COMMENT '系统库存',
    `actual_num`   int DEFAULT NULL COMMENT '实际盘点数量',
    `diff_num`     int DEFAULT NULL COMMENT '差异数量（实际-系统）',
    `remark`       varchar(255) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    KEY `fk_stocktake_item_stocktake` (`stocktake_id`),
    KEY `fk_stocktake_item_goods` (`goodsid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='盘点明细';
