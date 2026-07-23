-- 2026-07-23 退货流程统一迁移
-- 背景：退货写操作已统一到订单页新流程（/sales|inport|retail/returnSingleGoods、returnOrder），
-- 老的 bus_*back 退货流程（addSalesback/addOutport/addRetailback/cancel*）接口与前端页面已下线。
-- 本脚本：隐藏老的"零售退货"菜单(135)，补齐零售订单/零售退回记录菜单(166/167)并授权。
-- 说明：166/167 在 warehouse.sql 种子中已存在，远程库属历史漂移缺失，故用 NOT EXISTS 幂等插入。

-- 1. 下线老零售退货页菜单
UPDATE sys_permission SET available = 0 WHERE id = 135;

-- 2. 补齐零售订单 / 零售退回记录菜单（幂等）
INSERT INTO sys_permission (id, pid, type, title, percode, icon, href, target, open, ordernum, available)
SELECT 166, 133, 'menu', '零售订单', NULL, 'Document', '/business/retail-order', NULL, 0, 11, 1
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE id = 166);

INSERT INTO sys_permission (id, pid, type, title, percode, icon, href, target, open, ordernum, available)
SELECT 167, 133, 'menu', '零售退回记录', NULL, 'List', '/business/retail-record', NULL, 0, 14, 1
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE id = 167);

-- 3. 菜单授权：超级管理员(1)与销售员(13)（与原 135 菜单的授权角色一致，幂等）
INSERT INTO sys_role_permission (rid, pid)
SELECT 1, 166 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_role_permission WHERE rid = 1 AND pid = 166);
INSERT INTO sys_role_permission (rid, pid)
SELECT 1, 167 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_role_permission WHERE rid = 1 AND pid = 167);
INSERT INTO sys_role_permission (rid, pid)
SELECT 13, 166 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_role_permission WHERE rid = 13 AND pid = 166);
INSERT INTO sys_role_permission (rid, pid)
SELECT 13, 167 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_role_permission WHERE rid = 13 AND pid = 167);
