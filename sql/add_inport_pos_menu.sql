-- 添加进货管理相关菜单项
-- 进货管理的 pid 是 3

-- 禁用原来的商品进货和商品退货菜单
UPDATE `sys_permission` SET `available` = 0 WHERE `id` IN (10, 11);

-- 添加商品进货菜单 (ordernum=9)
INSERT INTO `sys_permission` (`id`, `pid`, `type`, `title`, `icon`, `href`, `target`, `available`, `ordernum`, `open`)
VALUES (158, 3, 'menu', '商品进货', 'ShoppingCart', '/bus/toInportPOS', '', 1, 9, 1);

-- 添加进货订单菜单 (ordernum=10)
INSERT INTO `sys_permission` (`id`, `pid`, `type`, `title`, `icon`, `href`, `target`, `available`, `ordernum`, `open`)
VALUES (159, 3, 'menu', '进货订单', 'List', '/bus/toInportOrder', '', 1, 10, 1);

-- 添加退加货记录菜单 (ordernum=11)
INSERT INTO `sys_permission` (`id`, `pid`, `type`, `title`, `icon`, `href`, `target`, `available`, `ordernum`, `open`)
VALUES (160, 3, 'menu', '退加货记录', 'Document', '/bus/toInportRecord', '', 1, 11, 1);

-- 更新盘点管理菜单顺序 (ordernum=12)
UPDATE `sys_permission` SET `ordernum` = 12 WHERE `id` = 141;

-- 为超级管理员角色分配权限
INSERT INTO `sys_role_permission` (`rid`, `pid`) VALUES (1, 158);
INSERT INTO `sys_role_permission` (`rid`, `pid`) VALUES (1, 159);
INSERT INTO `sys_role_permission` (`rid`, `pid`) VALUES (1, 160);

-- 为采购员角色分配权限
INSERT INTO `sys_role_permission` (`rid`, `pid`) VALUES (12, 158);
INSERT INTO `sys_role_permission` (`rid`, `pid`) VALUES (12, 159);
INSERT INTO `sys_role_permission` (`rid`, `pid`) VALUES (12, 160);

-- 为仓库管理员角色分配权限
INSERT INTO `sys_role_permission` (`rid`, `pid`) VALUES (11, 158);
INSERT INTO `sys_role_permission` (`rid`, `pid`) VALUES (11, 159);
INSERT INTO `sys_role_permission` (`rid`, `pid`) VALUES (11, 160);

-- 创建进货日志表
CREATE TABLE IF NOT EXISTS `bus_inport_log` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `order_no` VARCHAR(50),
  `provider_id` INT,
  `goods_id` INT,
  `type` VARCHAR(20),
  `number` INT,
  `price` DECIMAL(10,2),
  `operate_person` VARCHAR(50),
  `operate_time` DATETIME,
  `remark` VARCHAR(500)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
