-- 远程库结构补齐增量 DDL（2026-07-18）
-- 使 175.178.96.252/warehouse 与当前代码实体定义一致

-- 1. bus_goods 补充序列号管理相关列（序列号功能新增，实体 Goods 已包含）
ALTER TABLE `bus_goods`
  ADD COLUMN `is_serial_managed` tinyint NOT NULL DEFAULT 0 COMMENT '是否管理序列号 0=否 1=是',
  ADD COLUMN `return_resaleable` tinyint NOT NULL DEFAULT 1 COMMENT '退货后是否直接回库 0=待检 1=直接可售';

-- 2. bus_retail 补充订单分组列（与 warehouse.sql 权威定义一致）
ALTER TABLE `bus_retail`
  ADD COLUMN `orderno` varchar(50) DEFAULT NULL COMMENT '订单号' AFTER `id`,
  ADD COLUMN `order_status` int DEFAULT '0' COMMENT '订单状态: 0=正常, 1=已退完' AFTER `orderno`;

-- 3. 零售操作日志表（与 warehouse.sql 权威定义一致）
CREATE TABLE `bus_retail_log` (
  `id` int NOT NULL AUTO_INCREMENT,
  `order_no` varchar(50) DEFAULT NULL COMMENT '订单号',
  `goods_id` int DEFAULT NULL COMMENT '商品ID',
  `type` varchar(20) DEFAULT NULL COMMENT '类型: retail=零售, add=加货, return=退货',
  `number` int DEFAULT NULL COMMENT '数量',
  `price` double DEFAULT NULL COMMENT '单价',
  `paytype` varchar(20) DEFAULT NULL COMMENT '支付方式',
  `operate_person` varchar(50) DEFAULT NULL COMMENT '操作人',
  `operate_time` datetime DEFAULT NULL COMMENT '操作时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='零售操作日志';

-- 4. 序列号主表（与 warehouse.sql 权威定义一致）
CREATE TABLE `bus_serial_number` (
  `id` int NOT NULL AUTO_INCREMENT,
  `serial_number` varchar(50) NOT NULL COMMENT '序列号',
  `goodsid` int NOT NULL COMMENT '商品ID',
  `inportid` int DEFAULT NULL COMMENT '入库单ID',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0=在库, 1=已售, 2=已退',
  `instock_time` datetime DEFAULT NULL COMMENT '入库时间',
  `outstock_time` datetime DEFAULT NULL COMMENT '出库时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `isdelete` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=正常, 1=已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_serial_number` (`serial_number`),
  KEY `idx_goodsid` (`goodsid`),
  KEY `idx_inportid` (`inportid`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='序列号表';

-- 5. 序列号操作日志表（按 SerialNumberLog 实体字段推导，warehouse.sql 中缺失）
CREATE TABLE `bus_serial_number_log` (
  `id` int NOT NULL AUTO_INCREMENT,
  `serial_number` varchar(50) NOT NULL COMMENT '序列号',
  `goodsid` int NOT NULL COMMENT '商品ID',
  `action` varchar(20) NOT NULL COMMENT '操作类型',
  `from_status` tinyint DEFAULT NULL COMMENT '变更前状态',
  `to_status` tinyint NOT NULL COMMENT '变更后状态',
  `order_no` varchar(50) DEFAULT NULL COMMENT '关联订单号',
  `operate_person` varchar(50) NOT NULL COMMENT '操作人',
  `operate_time` datetime DEFAULT NULL COMMENT '操作时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_serial_number` (`serial_number`),
  KEY `idx_goodsid` (`goodsid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='序列号操作日志表';
