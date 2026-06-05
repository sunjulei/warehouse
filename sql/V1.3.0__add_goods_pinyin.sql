-- 商品表添加拼音和简写字段，支持拼音搜索
ALTER TABLE `bus_goods`
    ADD COLUMN `pinyin` varchar(500) DEFAULT NULL COMMENT '商品名称拼音' AFTER `attribute`,
    ADD COLUMN `abbreviation` varchar(100) DEFAULT NULL COMMENT '商品名称拼音首字母简写' AFTER `pinyin`;

-- 为已有商品生成拼音数据（需在应用层通过 pinyin4j 处理，此处仅为索引准备）
CREATE INDEX `idx_goods_pinyin` ON `bus_goods` (`pinyin`(100));
CREATE INDEX `idx_goods_abbreviation` ON `bus_goods` (`abbreviation`);
