-- 2026-05-27 为 bus_goods 表新增 attribute（附加属性）字段
ALTER TABLE `bus_goods` ADD COLUMN `attribute` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL AFTER `available`;
