--
-- Host: localhost    Database: warehouse
-- ------------------------------------------------------
-- Server version	8.0.39

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `bus_category`
--

DROP TABLE IF EXISTS `bus_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bus_category` (
  `id` int NOT NULL AUTO_INCREMENT,
  `catename` varchar(255) DEFAULT NULL,
  `available` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bus_category`
--

LOCK TABLES `bus_category` WRITE;
/*!40000 ALTER TABLE `bus_category` DISABLE KEYS */;
INSERT INTO `bus_category` VALUES (1,'食品饮料',1),(2,'日用百货',1),(3,'生鲜水果',1),(4,'粮油调味',1),(5,'零食糕点',1),(7,'快捷创建',1);
/*!40000 ALTER TABLE `bus_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bus_commission_record`
--

DROP TABLE IF EXISTS `bus_commission_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bus_commission_record` (
  `id` int NOT NULL AUTO_INCREMENT,
  `operator` varchar(50) DEFAULT NULL,
  `year_month` varchar(10) DEFAULT NULL,
  `total_sales` decimal(12,2) DEFAULT NULL,
  `total_orders` int DEFAULT NULL,
  `commission_rate` decimal(10,2) DEFAULT NULL,
  `commission_amount` decimal(12,2) DEFAULT NULL,
  `rule_id` int DEFAULT NULL,
  `status` int DEFAULT '0',
  `create_time` datetime DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bus_commission_record`
--

LOCK TABLES `bus_commission_record` WRITE;
/*!40000 ALTER TABLE `bus_commission_record` DISABLE KEYS */;
INSERT INTO `bus_commission_record` VALUES (1,'超级管理员','2026-06',100001.00,2,50.00,50000.50,2,0,NULL,NULL),(2,'超级管理员','2026-05',369651.00,9,50.00,184825.50,2,0,NULL,NULL),(3,'李月素','2026-05',170150.00,13,50.00,85075.00,2,0,NULL,NULL),(4,'赵六','2026-05',189187.50,11,50.00,94593.75,2,0,NULL,NULL),(5,'落亦-','2026-05',384762.50,12,50.00,192381.25,2,0,NULL,NULL);
/*!40000 ALTER TABLE `bus_commission_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bus_commission_rule`
--

DROP TABLE IF EXISTS `bus_commission_rule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bus_commission_rule` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL,
  `type` varchar(20) DEFAULT NULL,
  `rate` decimal(10,2) DEFAULT NULL,
  `status` int DEFAULT '1',
  `create_time` datetime DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bus_commission_rule`
--

LOCK TABLES `bus_commission_rule` WRITE;
/*!40000 ALTER TABLE `bus_commission_rule` DISABLE KEYS */;
INSERT INTO `bus_commission_rule` VALUES (2,'AA','fixed',50.00,1,NULL,'10'),(3,'a','tiered',NULL,1,NULL,'a');
/*!40000 ALTER TABLE `bus_commission_rule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bus_commission_tier`
--

DROP TABLE IF EXISTS `bus_commission_tier`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bus_commission_tier` (
  `id` int NOT NULL AUTO_INCREMENT,
  `rule_id` int DEFAULT NULL,
  `min_amount` decimal(10,2) DEFAULT NULL,
  `max_amount` decimal(10,2) DEFAULT NULL,
  `rate` decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bus_commission_tier`
--

LOCK TABLES `bus_commission_tier` WRITE;
/*!40000 ALTER TABLE `bus_commission_tier` DISABLE KEYS */;
INSERT INTO `bus_commission_tier` VALUES (3,3,3.00,4.00,2.00),(4,3,2.00,1.00,1.00);
/*!40000 ALTER TABLE `bus_commission_tier` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bus_customer`
--

DROP TABLE IF EXISTS `bus_customer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bus_customer` (
  `id` int NOT NULL AUTO_INCREMENT,
  `customername` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `zip` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `address` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `telephone` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `connectionperson` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `phone` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `bank` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `account` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `email` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `fax` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `available` int DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bus_customer`
--

LOCK TABLES `bus_customer` WRITE;
/*!40000 ALTER TABLE `bus_customer` DISABLE KEYS */;
INSERT INTO `bus_customer` VALUES (1,'小张超市','430000','武汉市武昌区中南路12号','027-88012345','张大明','15279230588','中国银行','6216610100011111111','zhangdm@shop.com','027-88012346',1),(2,'小明超市','518000','深圳市南山区科技园路8号','0755-26012345','张小明','13064154936','中国银行','6216610100012222222','zhangxm@shop.com','0755-26012346',1),(3,'快七超市','430000','武汉市洪山区光谷大道56号','027-87012345','雷文杰','13617020687','招商银行','6225881234567777','leiwj@shop.com','027-87012346',1),(4,'丽云超市','432000','孝感市孝南区城站路103号','0712-2801234','射可可','13648524759','建设银行','6227001215820444444','shekk@shop.com','0712-2801235',1),(5,'永辉超市（光谷店）','430000','武汉市东湖高新区关山大道300号','027-87654321','刘永辉','15927001234','中国工商银行','6222021812005555555','lyh@yonghui.com','027-87654322',1),(6,'中百仓储（珞狮路店）','430000','武汉市洪山区珞狮路200号','027-87650001','王建国','13871002345','中国银行','6216610100016666666','wjg@zhongbai.com','027-87650002',1),(7,'武商超市（亚贸店）','430000','武汉市武昌区武珞路628号','027-87651111','李武商','13871112345','中国建设银行','6227001215820777777','lws@wushang.com','027-87651112',1),(8,'联华超市（汉口店）','430000','武汉市江汉区解放大道688号','027-85660001','赵联华','15272001234','招商银行','6225881234568888','zlh@lianhua.com','027-85660002',1),(9,'华联超市（光谷步行街店）','430000','武汉市东湖高新区步行街101号','027-87652222','孙华联','13871222345','中国农业银行','6228480402569999999','shl@hualian.com','027-87652223',1),(10,'物美超市（南湖店）','430000','武汉市洪山区南湖大道150号','027-87653333','周物美','15273001234','中国工商银行','6222021812001010101','zwm@wumei.com','027-87653334',1),(11,'家家悦超市（江夏店）','430000','武汉市江夏区纸坊大街88号','027-87990001','钱家悦','13871992345','中国银行','6216610100011112222','qjj@jiajiayue.com','027-87990002',1),(12,'芙蓉兴盛（青山店）','430000','武汉市青山区和平大道500号','027-86330001','冯兴盛','15274001234','中国建设银行','6227001215820333333','fxs@furong.com','027-86330002',1);
/*!40000 ALTER TABLE `bus_customer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bus_goods`
--

DROP TABLE IF EXISTS `bus_goods`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bus_goods` (
  `id` int NOT NULL AUTO_INCREMENT,
  `goodsname` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `providerid` int DEFAULT NULL,
  `categoryid` int DEFAULT NULL,
  `produceplace` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `size` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `goodspackage` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `productcode` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `promitcode` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `description` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `price` double DEFAULT NULL,
  `number` int DEFAULT NULL,
  `dangernum` int DEFAULT NULL,
  `goodsimg` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `available` int DEFAULT NULL,
  `attribute` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `pinyin` varchar(500) DEFAULT NULL COMMENT '商品名称拼音',
  `abbreviation` varchar(100) DEFAULT NULL COMMENT '商品名称拼音首字母简写',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `FK_sq0btr2v2lq8gt8b4gb8tlk0i` (`providerid`) USING BTREE,
  CONSTRAINT `bus_goods_ibfk_1` FOREIGN KEY (`providerid`) REFERENCES `bus_provider` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bus_goods`
--

LOCK TABLES `bus_goods` WRITE;
/*!40000 ALTER TABLE `bus_goods` DISABLE KEYS */;
INSERT INTO `bus_goods` VALUES (1,'旺旺雪饼（大包）',1,5,'湖北省仙桃市','258g/包','袋','SC10120250001','SP10120250001','经典雪饼，香脆可口',12.5,70,100,NULL,1,NULL,NULL,NULL),(2,'旺旺大礼包',1,5,'湖北省仙桃市','500g/盒','盒','SC10120250002','SP10120250002','节日送礼佳品',35,35,50,NULL,1,NULL,NULL,NULL),(3,'旺旺小馒头',1,5,'湖北省仙桃市','210g/包','袋','SC10120250003','SP10120250003','入口即化，老少皆宜',8.5,300,80,NULL,1,NULL,NULL,NULL),(4,'旺旺仙贝',1,5,'湖北省仙桃市','200g/包','袋','SC10120250004','SP10120250004','仙贝脆片',10,279,80,NULL,1,NULL,NULL,NULL),(5,'娃哈哈AD钙奶',3,1,'浙江省杭州市','220ML*24瓶/箱','箱','SC30120250001','SP30120250001','经典AD钙奶',28,514,100,NULL,1,NULL,NULL,NULL),(6,'娃哈哈纯净水',3,1,'浙江省杭州市','596ML*24瓶/箱','箱','SC30120250002','SP30120250002','纯净水',22,931,150,NULL,1,NULL,NULL,NULL),(7,'娃哈哈八宝粥',3,1,'浙江省杭州市','360g*12罐/箱','箱','SC30120250003','SP30120250003','桂圆莲子八宝粥',45,300,60,NULL,1,NULL,NULL,NULL),(8,'达利园小面包',2,5,'湖北省汉川市','400g/包','袋','SC20120250001','SP20120250001','软面包，早餐优选',9.8,368,100,NULL,1,NULL,NULL,NULL),(9,'达利园蛋黄派',2,5,'湖北省汉川市','360g/盒','盒','SC20120250002','SP20120250002','蛋黄夹心',13.5,220,60,NULL,1,NULL,NULL,NULL),(10,'达利园好吃点',2,5,'湖北省汉川市','200g/包','袋','SC20120250003','SP20120250003','香脆饼干',7.5,260,80,NULL,1,NULL,NULL,NULL),(11,'蒙牛纯牛奶',4,1,'内蒙古呼和浩特','250ML*24盒/箱','箱','SC40120250001','SP40120250001','纯牛奶',58,461,80,NULL,1,NULL,NULL,NULL),(12,'蒙牛酸奶',4,1,'内蒙古呼和浩特','200g*12杯/箱','箱','SC40120250002','SP40120250002','原味酸奶',42,449,60,NULL,1,NULL,NULL,NULL),(13,'伊利纯牛奶',5,1,'内蒙古呼和浩特','250ML*24盒/箱','箱','SC50120250001','SP50120250001','纯牛奶',56,526,80,NULL,1,NULL,NULL,NULL),(14,'伊利安慕希',5,1,'内蒙古呼和浩特','205g*12盒/箱','箱','SC50120250002','SP50120250002','希腊酸奶',52,375,60,NULL,1,NULL,NULL,NULL),(15,'伊利金典',5,1,'内蒙古呼和浩特','250ML*12盒/箱','箱','SC50120250003','SP50120250003','高端有机奶',68,150,40,NULL,1,NULL,NULL,NULL),(16,'可口可乐',6,1,'上海市','330ML*24罐/箱','箱','SC60120250001','SP60120250001','经典可乐',48,704,100,NULL,1,NULL,NULL,NULL),(17,'百事可乐',6,1,'上海市','330ML*24罐/箱','箱','SC60120250002','SP60120250002','百事可乐',46,600,100,NULL,1,NULL,NULL,NULL),(18,'雪碧',6,1,'上海市','330ML*24罐/箱','箱','SC60120250003','SP60120250003','柠檬味汽水',48,395,100,NULL,1,NULL,NULL,NULL),(19,'康师傅红烧牛肉面',7,4,'天津市','108g*5包/袋','袋','SC70120250001','SP70120250001','经典红烧牛肉面',12.5,1046,120,NULL,1,NULL,NULL,NULL),(20,'康师傅冰红茶',7,1,'天津市','500ML*15瓶/箱','箱','SC70120250002','SP70120250002','冰红茶',32,690,100,NULL,1,NULL,NULL,NULL),(21,'统一老坛酸菜面',7,4,'天津市','110g*5包/袋','袋','SC70120250003','SP70120250003','老坛酸菜牛肉面',13,440,80,NULL,1,NULL,NULL,NULL),(22,'农夫山泉',8,1,'浙江省杭州市','550ML*24瓶/箱','箱','SC80120250001','SP80120250001','天然矿泉水',36,1170,150,NULL,1,NULL,NULL,NULL),(23,'农夫山泉（大桶）',8,1,'浙江省杭州市','4L/桶','桶','SC80120250002','SP80120250002','家庭装矿泉水',12,427,60,NULL,1,NULL,NULL,NULL),(24,'维他命水',8,1,'浙江省杭州市','500ML*15瓶/箱','箱','SC80120250003','SP80120250003','补充维他命',38,250,50,NULL,1,NULL,NULL,NULL),(25,'红牛',8,1,'浙江省杭州市','250ML*24罐/箱','箱','SC80120250004','SP80120250004','功能饮料',120,250,500,NULL,1,NULL,NULL,NULL),(27,'燕麦',1,1,NULL,'1','1','1','1',NULL,11,17,10,NULL,1,NULL,NULL,NULL);
/*!40000 ALTER TABLE `bus_goods` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bus_inport`
--

DROP TABLE IF EXISTS `bus_inport`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bus_inport` (
  `id` int NOT NULL AUTO_INCREMENT,
  `paytype` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `inporttime` datetime DEFAULT NULL,
  `operateperson` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `number` int DEFAULT NULL,
  `remark` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `inportprice` double DEFAULT NULL,
  `providerid` int DEFAULT NULL,
  `goodsid` int DEFAULT NULL,
  `orderno` varchar(50) DEFAULT NULL,
  `order_status` int DEFAULT '0',
  `isdelete` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `bus_inport_ibfk_1` (`providerid`) USING BTREE,
  KEY `bus_inport_ibfk_2` (`goodsid`) USING BTREE,
  CONSTRAINT `bus_inport_ibfk_1` FOREIGN KEY (`providerid`) REFERENCES `bus_provider` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `bus_inport_ibfk_2` FOREIGN KEY (`goodsid`) REFERENCES `bus_goods` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=114 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bus_inport`
--

LOCK TABLES `bus_inport` WRITE;
/*!40000 ALTER TABLE `bus_inport` DISABLE KEYS */;
INSERT INTO `bus_inport` VALUES (1,'银联','2026-03-02 09:15:00','超级管理员',400,'月初备货',8.5,1,1,'IP1780822409',0,0),(2,'银联','2026-03-02 09:30:00','超级管理员',100,'月初备货',22,1,2,'IP1780822410',0,0),(3,'银联','2026-03-02 10:00:00','超级管理员',200,'月初备货',5.5,1,3,'IP1780822411',0,0),(4,'银联','2026-03-02 10:15:00','超级管理员',150,'月初备货',6,1,4,'IP1780822412',0,0),(5,'支付宝','2026-03-03 08:30:00','落亦-',300,'补货',18,3,5,'IP1780822413',0,0),(6,'支付宝','2026-03-03 09:00:00','落亦-',400,'补货',14,3,6,'IP1780822414',0,0),(7,'支付宝','2026-03-03 09:30:00','落亦-',150,'补货',28,3,7,'IP1780822415',0,0),(8,'微信','2026-03-04 08:45:00','李月素',200,'常规进货',6,2,8,'IP1780822416',0,0),(9,'微信','2026-03-04 09:00:00','李月素',150,'常规进货',8.5,2,9,'IP1780822417',0,0),(10,'微信','2026-03-04 09:15:00','李月素',200,'常规进货',4.5,2,10,'IP1780822418',0,0),(11,'银联','2026-03-05 08:30:00','超级管理员',200,'乳品备货',38,4,11,'IP1780822419',0,0),(12,'银联','2026-03-05 09:00:00','超级管理员',150,'乳品备货',28,4,12,'IP1780822420',0,0),(13,'银联','2026-03-05 09:30:00','超级管理员',200,'乳品备货',37,5,13,'IP1780822421',0,0),(14,'银联','2026-03-05 10:00:00','超级管理员',180,'乳品备货',34,5,14,'IP1780822422',0,0),(15,'银联','2026-03-05 10:30:00','超级管理员',100,'高端奶备货',45,5,15,'IP1780822423',0,0),(16,'支付宝','2026-03-06 08:30:00','落亦-',250,'饮料备货',32,6,16,'IP1780822424',0,0),(17,'支付宝','2026-03-06 09:00:00','落亦-',250,'饮料备货',30,6,17,'IP1780822425',0,0),(18,'支付宝','2026-03-06 09:30:00','落亦-',200,'饮料备货',32,6,18,'IP1780822426',0,0),(19,'微信','2026-03-07 08:30:00','赵六',300,'方便面备货',7.5,7,19,'IP1780822427',0,0),(20,'微信','2026-03-07 09:00:00','赵六',250,'饮料备货',20,7,20,'IP1780822428',0,0),(21,'微信','2026-03-07 09:30:00','赵六',200,'方便面备货',8,7,21,'IP1780822429',0,0),(22,'银联','2026-03-08 08:30:00','超级管理员',400,'水饮备货',22,8,22,'IP1780822430',0,0),(23,'银联','2026-03-08 09:00:00','超级管理员',200,'大桶水',7,8,23,'IP1780822431',0,0),(24,'银联','2026-03-08 09:30:00','超级管理员',150,'维他命水',24,8,24,'IP1780822432',0,0),(25,'银联','2026-03-08 10:00:00','超级管理员',100,'红牛',78,8,25,'IP1780822433',0,0),(26,'支付宝','2026-03-15 09:00:00','落亦-',120,'雪饼补货',8.5,1,1,'IP1780822434',0,0),(27,'支付宝','2026-03-15 09:30:00','落亦-',100,'大礼包补货',22,1,2,'IP1780822435',0,0),(28,'微信','2026-03-18 08:30:00','李月素',200,'AD钙奶补货',18,3,5,'IP1780822436',0,0),(29,'微信','2026-03-18 09:00:00','李月素',100,'八宝粥补货',28,3,7,'IP1780822437',0,0),(30,'银联','2026-03-20 08:30:00','超级管理员',100,'蒙牛纯牛奶补货',38,4,11,'IP1780822438',0,0),(31,'银联','2026-03-22 09:00:00','超级管理员',150,'农夫山泉补货',22,8,22,'IP1780822439',0,0),(32,'支付宝','2026-03-25 08:30:00','落亦-',100,'可乐补货',32,6,16,'IP1780822440',0,0),(33,'支付宝','2026-03-25 09:00:00','落亦-',0,'雪碧补货',32,6,18,'IP1780822441',1,0),(34,'微信','2026-03-28 08:30:00','赵六',150,'方便面补货',7.5,7,19,'IP1780822442',0,0),(35,'微信','2026-03-28 09:00:00','赵六',100,'冰红茶补货',20,7,20,'IP1780822443',0,0),(36,'银联','2026-04-01 09:00:00','超级管理员',150,'月初补货',8.5,1,1,'IP1780822444',0,0),(37,'银联','2026-04-01 09:30:00','超级管理员',100,'月初补货',5.5,1,3,'IP1780822445',0,0),(38,'银联','2026-04-01 10:00:00','超级管理员',100,'月初补货',6,1,4,'IP1780822446',0,0),(39,'支付宝','2026-04-02 08:30:00','落亦-',200,'AD钙奶进货',18,3,5,'IP1780822447',0,0),(40,'支付宝','2026-04-02 09:00:00','落亦-',200,'纯净水进货',14,3,6,'IP1780822448',0,0),(41,'微信','2026-04-03 08:30:00','李月素',100,'小面包进货',6,2,8,'IP1780822449',0,0),(42,'微信','2026-04-03 09:00:00','李月素',100,'蛋黄派进货',8.5,2,9,'IP1780822450',0,0),(43,'银联','2026-04-05 08:30:00','超级管理员',150,'蒙牛纯牛奶',38,4,11,'IP1780822451',0,0),(44,'银联','2026-04-05 09:00:00','超级管理员',100,'蒙牛酸奶',28,4,12,'IP1780822452',0,0),(45,'银联','2026-04-05 09:30:00','超级管理员',150,'伊利纯牛奶',37,5,13,'IP1780822453',0,0),(46,'银联','2026-04-05 10:00:00','超级管理员',100,'安慕希',34,5,14,'IP1780822454',0,0),(47,'支付宝','2026-04-07 08:30:00','落亦-',150,'可口可乐',32,6,16,'IP1780822455',0,0),(48,'支付宝','2026-04-07 09:00:00','落亦-',150,'百事可乐',30,6,17,'IP1780822456',0,0),(49,'支付宝','2026-04-07 09:30:00','落亦-',100,'雪碧',32,6,18,'IP1780822457',0,0),(50,'微信','2026-04-08 08:30:00','赵六',200,'红烧牛肉面',7.5,7,19,'IP1780822458',0,0),(51,'微信','2026-04-08 09:00:00','赵六',150,'冰红茶',20,7,20,'IP1780822459',0,0),(52,'微信','2026-04-08 09:30:00','赵六',100,'老坛酸菜面',8,7,21,'IP1780822460',0,0),(53,'银联','2026-04-10 08:30:00','超级管理员',300,'农夫山泉',22,8,22,'IP1780822461',0,0),(54,'银联','2026-04-10 09:00:00','超级管理员',100,'大桶水',7,8,23,'IP1780822462',0,0),(55,'银联','2026-04-10 09:30:00','超级管理员',80,'红牛',78,8,25,'IP1780822463',0,0),(56,'支付宝','2026-04-15 09:00:00','落亦-',100,'旺旺雪饼补货',8.5,1,1,'IP1780822464',0,0),(57,'支付宝','2026-04-15 09:30:00','落亦-',80,'旺旺仙贝补货',6,1,4,'IP1780822465',0,0),(58,'微信','2026-04-18 08:30:00','李月素',150,'纯净水补货',14,3,6,'IP1780822466',0,0),(59,'微信','2026-04-18 09:00:00','李月素',100,'八宝粥补货',28,3,7,'IP1780822467',0,0),(60,'银联','2026-04-20 08:30:00','超级管理员',100,'金典补货',45,5,15,'IP1780822468',0,0),(61,'银联','2026-04-22 09:00:00','超级管理员',100,'伊利安慕希补货',34,5,14,'IP1780822469',0,0),(62,'支付宝','2026-04-25 08:30:00','落亦-',100,'可口可乐补货',32,6,16,'IP1780822470',0,0),(63,'微信','2026-04-28 08:30:00','赵六',100,'方便面补货',7.5,7,19,'IP1780822471',0,0),(64,'银联','2026-05-02 09:00:00','超级管理员',200,'月初备货',8.5,1,1,'IP1780822472',0,0),(65,'银联','2026-05-02 09:30:00','超级管理员',100,'月初备货',22,1,2,'IP1780822473',0,0),(66,'银联','2026-05-02 10:00:00','超级管理员',150,'月初备货',5.5,1,3,'IP1780822474',0,0),(67,'支付宝','2026-05-03 08:30:00','落亦-',250,'AD钙奶',18,3,5,'IP1780822475',0,0),(68,'支付宝','2026-05-03 09:00:00','落亦-',300,'纯净水',14,3,6,'IP1780822476',0,0),(69,'微信','2026-05-04 08:30:00','李月素',150,'小面包',6,2,8,'IP1780822477',0,0),(70,'微信','2026-05-04 09:00:00','李月素',100,'好吃点',4.5,2,10,'IP1780822478',0,0),(71,'银联','2026-05-05 08:30:00','超级管理员',200,'蒙牛纯牛奶',38,4,11,'IP1780822479',0,0),(72,'银联','2026-05-05 09:00:00','超级管理员',150,'蒙牛酸奶',28,4,12,'IP1780822480',0,0),(73,'银联','2026-05-05 09:30:00','超级管理员',200,'伊利纯牛奶',37,5,13,'IP1780822481',0,0),(74,'银联','2026-05-05 10:00:00','超级管理员',150,'安慕希',34,5,14,'IP1780822482',0,0),(75,'支付宝','2026-05-06 08:30:00','落亦-',200,'可口可乐',32,6,16,'IP1780822483',0,0),(76,'支付宝','2026-05-06 09:00:00','落亦-',200,'百事可乐',30,6,17,'IP1780822484',0,0),(77,'支付宝','2026-05-06 09:30:00','落亦-',150,'雪碧',32,6,18,'IP1780822485',0,0),(78,'微信','2026-05-07 08:30:00','赵六',250,'红烧牛肉面',7.5,7,19,'IP1780822486',0,0),(79,'微信','2026-05-07 09:00:00','赵六',200,'冰红茶',20,7,20,'IP1780822487',0,0),(80,'微信','2026-05-07 09:30:00','赵六',150,'老坛酸菜面',8,7,21,'IP1780822488',0,0),(81,'银联','2026-05-08 08:30:00','超级管理员',300,'农夫山泉',22,8,22,'IP1780822489',0,0),(82,'银联','2026-05-08 09:00:00','超级管理员',150,'大桶水',7,8,23,'IP1780822490',0,0),(83,'银联','2026-05-08 09:30:00','超级管理员',100,'维他命水',24,8,24,'IP1780822491',0,0),(84,'银联','2026-05-08 10:00:00','超级管理员',80,'红牛',78,8,25,'IP1780822492',0,0),(85,'支付宝','2026-05-15 09:00:00','落亦-',100,'旺旺雪饼补货',8.5,1,1,'IP1780822493',0,0),(86,'微信','2026-05-15 09:30:00','李月素',100,'小馒头补货',5.5,1,3,'IP1780822494',0,0),(87,'银联','2026-05-16 08:30:00','超级管理员',100,'蒙牛纯牛奶补货',38,4,11,'IP1780822495',0,0),(88,'支付宝','2026-05-18 09:00:00','落亦-',100,'可口可乐补货',32,6,16,'IP1780822496',0,0),(89,'微信','2026-05-19 08:30:00','赵六',100,'方便面补货',7.5,7,19,'IP1780822497',0,0),(90,'银联','2026-05-19 09:00:00','超级管理员',150,'农夫山泉补货',22,8,22,'IP1780822498',0,0),(91,'银行卡','2026-05-21 07:15:47','超级管理员',20,'1',10,8,23,'IP1780822499',0,0),(92,'现金','2026-05-21 07:42:07','超级管理员',49,NULL,2,4,12,'IP1780822500',0,0),(93,'微信','2026-05-22 04:41:43','超级管理员',10,NULL,1,1,27,'IP1780822501',0,0),(96,'现金','2026-06-06 03:32:40','超级管理员',10,NULL,100,1,3,'IP1780822504',0,0),(97,'微信','2026-06-07 08:36:04','超级管理员',2,'',12.5,1,1,'IP1780822505',0,0),(98,'微信','2026-06-07 08:36:04','超级管理员',3,'',28,1,5,'IP1780822506',0,0),(99,'微信','2026-06-07 08:36:04','超级管理员',1,'',58,1,11,'IP1780822507',0,0),(100,'微信','2026-06-07 08:58:28','超级管理员',1,'a',12.5,2,1,'IP17808227078569615',0,0),(101,'微信','2026-06-07 08:58:28','超级管理员',2,'a',28,2,5,'IP17808227078569615',0,0),(102,'微信','2026-06-07 08:58:28','超级管理员',2,'a',58,2,11,'IP17808227078569615',0,0),(103,'现金','2026-06-07 09:01:51','超级管理员',1,'',35,1,2,'IP17808229114780528',0,0),(104,'现金','2026-06-07 09:01:51','超级管理员',2,'',8.5,1,3,'IP17808229114780528',0,0),(105,'现金','2026-06-07 09:01:51','超级管理员',3,'',11,1,27,'IP17808229114780528',0,0),(106,NULL,'2026-06-07 09:04:08','超级管理员',0,NULL,12.5,6,1,'IP1780822441',1,0),(107,NULL,'2026-06-07 09:04:19','超级管理员',0,NULL,8.5,6,3,'IP1780822441',1,0),(108,'现金','2026-06-07 09:50:54','超级管理员',1,'',35,1,2,'IP202606071750540001',0,0),(109,'现金','2026-06-07 09:54:21','超级管理员',2,'',12.5,1,1,'IP202606071754200001',0,0),(110,'现金','2026-06-07 09:54:21','超级管理员',2,'',8.5,1,3,'IP202606071754200001',0,0),(111,'银行卡','2026-06-07 15:12:15','超级管理员',1,'123',11,1,27,'IP202606072312140001',0,0),(112,'银行卡','2026-06-07 15:12:15','超级管理员',2,'123',35,1,2,'IP202606072312140001',0,0),(113,'银行卡','2026-06-07 15:12:15','超级管理员',3,'123',12.5,1,1,'IP202606072312140001',0,0);
/*!40000 ALTER TABLE `bus_inport` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bus_inport_log`
--

DROP TABLE IF EXISTS `bus_inport_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bus_inport_log` (
  `id` int NOT NULL AUTO_INCREMENT,
  `order_no` varchar(50) DEFAULT NULL,
  `provider_id` int DEFAULT NULL,
  `goods_id` int DEFAULT NULL,
  `type` varchar(20) DEFAULT NULL,
  `number` int DEFAULT NULL,
  `price` decimal(10,2) DEFAULT NULL,
  `operate_person` varchar(50) DEFAULT NULL,
  `operate_time` datetime DEFAULT NULL,
  `remark` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bus_inport_log`
--

LOCK TABLES `bus_inport_log` WRITE;
/*!40000 ALTER TABLE `bus_inport_log` DISABLE KEYS */;
INSERT INTO `bus_inport_log` VALUES (1,'IP17808227078569615',2,1,'inport',1,12.50,'超级管理员','2026-06-07 08:58:28','a'),(2,'IP17808227078569615',2,5,'inport',2,28.00,'超级管理员','2026-06-07 08:58:28','a'),(3,'IP17808227078569615',2,11,'inport',2,58.00,'超级管理员','2026-06-07 08:58:28','a'),(4,'IP17808229114780528',1,2,'inport',1,35.00,'超级管理员','2026-06-07 09:01:51',''),(5,'IP17808229114780528',1,3,'inport',2,8.50,'超级管理员','2026-06-07 09:01:51',''),(6,'IP17808229114780528',1,27,'inport',3,11.00,'超级管理员','2026-06-07 09:01:51',''),(7,'IP1780822441',6,1,'add',1,12.50,'超级管理员','2026-06-07 09:04:08',NULL),(8,'IP1780822441',6,3,'add',2,8.50,'超级管理员','2026-06-07 09:04:19',NULL),(9,'IP1780822441',6,18,'return',1,32.00,'落亦-','2026-06-07 09:04:55','单商品退货（部分）'),(10,'IP1780822441',6,18,'return',99,32.00,'落亦-','2026-06-07 09:10:14','整单退货'),(11,'IP1780822441',6,1,'return',1,12.50,'超级管理员','2026-06-07 09:10:14','整单退货'),(12,'IP1780822441',6,3,'return',2,8.50,'超级管理员','2026-06-07 09:10:14','整单退货'),(13,'IP202606071750540001',1,2,'inport',1,35.00,'超级管理员','2026-06-07 09:50:54',''),(14,'IP202606071754200001',1,1,'inport',2,12.50,'超级管理员','2026-06-07 09:54:21',''),(15,'IP202606071754200001',1,3,'inport',2,8.50,'超级管理员','2026-06-07 09:54:21',''),(16,'IP202606072312140001',1,27,'inport',1,11.00,'超级管理员','2026-06-07 15:12:15','123'),(17,'IP202606072312140001',1,2,'inport',2,35.00,'超级管理员','2026-06-07 15:12:15','123'),(18,'IP202606072312140001',1,1,'inport',3,12.50,'超级管理员','2026-06-07 15:12:15','123');
/*!40000 ALTER TABLE `bus_inport_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bus_member`
--

DROP TABLE IF EXISTS `bus_member`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bus_member` (
  `id` int NOT NULL AUTO_INCREMENT,
  `member_no` varchar(50) DEFAULT NULL,
  `name` varchar(50) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `gender` varchar(10) DEFAULT NULL,
  `balance` decimal(10,2) DEFAULT '0.00',
  `total_recharge` decimal(10,2) DEFAULT '0.00',
  `total_consume` decimal(10,2) DEFAULT '0.00',
  `points` int DEFAULT '0',
  `level` int DEFAULT '1',
  `status` int DEFAULT '1',
  `create_time` datetime DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bus_member`
--

LOCK TABLES `bus_member` WRITE;
/*!40000 ALTER TABLE `bus_member` DISABLE KEYS */;
INSERT INTO `bus_member` VALUES (1,'VIP202606060001','张三','1','男',900.00,1000000.00,999100.00,999100,4,1,NULL,'1');
/*!40000 ALTER TABLE `bus_member` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bus_member_level_rule`
--

DROP TABLE IF EXISTS `bus_member_level_rule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bus_member_level_rule` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `level_name` varchar(50) NOT NULL COMMENT '等级名称',
  `level_value` int NOT NULL COMMENT '等级值',
  `min_recharge` decimal(10,2) DEFAULT '0.00' COMMENT '累计充值达标金额',
  `min_consume` decimal(10,2) DEFAULT '0.00' COMMENT '累计消费达标金额',
  `condition_type` int DEFAULT '1' COMMENT '达标条件关系: 1满足其一 2同时满足',
  `discount_rate` decimal(5,2) DEFAULT '100.00' COMMENT '折扣率(%)',
  `points_rate` decimal(5,2) DEFAULT '1.00' COMMENT '积分倍率',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `sort_order` int DEFAULT '0' COMMENT '排序',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='会员等级规则';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bus_member_level_rule`
--

LOCK TABLES `bus_member_level_rule` WRITE;
/*!40000 ALTER TABLE `bus_member_level_rule` DISABLE KEYS */;
INSERT INTO `bus_member_level_rule` VALUES (1,'普通会员',1,0.00,0.00,1,100.00,1.00,NULL,1,'2026-06-06 10:21:20'),(2,'银卡会员',2,1000.00,2000.00,1,95.00,1.50,NULL,2,'2026-06-06 10:21:20'),(3,'金卡会员',3,5000.00,10000.00,1,90.00,2.00,NULL,3,'2026-06-06 10:21:20'),(4,'钻石会员',4,20000.00,20000.00,2,85.00,3.00,NULL,4,'2026-06-06 10:21:20');
/*!40000 ALTER TABLE `bus_member_level_rule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bus_member_record`
--

DROP TABLE IF EXISTS `bus_member_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bus_member_record` (
  `id` int NOT NULL AUTO_INCREMENT,
  `member_id` int DEFAULT NULL,
  `type` varchar(20) DEFAULT NULL,
  `amount` decimal(10,2) DEFAULT NULL,
  `balance_after` decimal(10,2) DEFAULT NULL,
  `operator` varchar(50) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bus_member_record`
--

LOCK TABLES `bus_member_record` WRITE;
/*!40000 ALTER TABLE `bus_member_record` DISABLE KEYS */;
INSERT INTO `bus_member_record` VALUES (1,1,'充值',1000000.00,1000000.00,'超级管理员','2026-06-06 01:10:28','储值卡充值'),(2,1,'消费',100.00,999900.00,'超级管理员','2026-06-06 01:10:40','储值卡消费'),(3,1,'消费',900000.00,99900.00,'超级管理员','2026-06-06 02:18:44','储值卡消费'),(4,1,'消费',99000.00,900.00,'超级管理员','2026-06-06 02:19:02','储值卡消费');
/*!40000 ALTER TABLE `bus_member_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bus_operation_log`
--

DROP TABLE IF EXISTS `bus_operation_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bus_operation_log` (
  `id` int NOT NULL AUTO_INCREMENT,
  `type` varchar(20) NOT NULL COMMENT '操作类型：添加、修改、删除',
  `module` varchar(50) NOT NULL COMMENT '模块名称',
  `description` varchar(500) DEFAULT NULL COMMENT '操作描述',
  `operateperson` varchar(50) DEFAULT NULL COMMENT '操作人',
  `operatetime` datetime DEFAULT NULL COMMENT '操作时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=152 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bus_operation_log`
--

LOCK TABLES `bus_operation_log` WRITE;
/*!40000 ALTER TABLE `bus_operation_log` DISABLE KEYS */;
INSERT INTO `bus_operation_log` VALUES (1,'删除','商品管理','删除商品: 康师傅白开水','超级管理员','2026-05-21 08:21:25'),(2,'添加','供应商管理','添加供应商: 测试','超级管理员','2026-05-21 08:21:40'),(3,'添加','退回供应商','进货退货, 进货单ID=92, 数量=1','超级管理员','2026-05-21 08:21:54'),(4,'删除','销售退回','取消销售退货 ID=12','超级管理员','2026-05-21 08:23:20'),(5,'添加','商品销售','销售商品: 旺旺大礼包, 数量: 20','超级管理员','2026-05-21 08:27:54'),(6,'添加','商品管理','添加商品: 燕麦','超级管理员','2026-05-22 04:31:50'),(7,'修改','商品管理','修改商品: 燕麦','超级管理员','2026-05-22 04:32:02'),(8,'添加','商品进货','进货商品: 燕麦, 数量: 10','超级管理员','2026-05-22 04:41:43'),(9,'添加','商品销售','销售商品: 燕麦, 数量: 10','超级管理员','2026-05-22 04:42:05'),(10,'添加','供应商管理','添加供应商: 1','超级管理员','2026-05-22 05:03:59'),(11,'添加','商品分类','添加分类: 1','超级管理员','2026-05-22 05:04:08'),(12,'删除','商品分类','删除分类: 1','超级管理员','2026-05-22 05:09:49'),(13,'上架','商品管理','上架商品: null','超级管理员','2026-05-25 03:17:29'),(14,'修改','商品管理','修改商品: 燕麦','超级管理员','2026-05-25 03:17:33'),(15,'修改','商品管理','修改商品: 燕麦','超级管理员','2026-05-25 03:18:44'),(16,'修改','供应商管理','修改供应商: 1','超级管理员','2026-05-25 06:36:09'),(17,'添加','商品分类','添加分类: 快捷创建','超级管理员','2026-05-25 07:52:46'),(18,'删除','退回供应商','删除退货记录 ID=11','超级管理员','2026-05-25 08:09:21'),(19,'添加','商品进货','进货商品: 燕麦, 数量: 100','超级管理员','2026-05-25 08:27:03'),(20,'添加','退回供应商','进货退货, 进货单ID=94, 数量=10','超级管理员','2026-05-25 08:27:12'),(21,'删除','商品进货','删除进货记录 ID=94','超级管理员','2026-05-25 08:27:47'),(22,'添加','商品进货','进货商品: 燕麦, 数量: 100','超级管理员','2026-05-25 08:46:14'),(23,'添加','退回供应商','进货退货, 进货单ID=95, 数量=10','超级管理员','2026-05-25 08:46:26'),(24,'添加','退回供应商','进货退货, 进货单ID=95, 数量=20','超级管理员','2026-05-25 08:46:42'),(25,'删除','退回供应商','取消退货 ID=14','超级管理员','2026-05-25 08:46:48'),(26,'删除','商品进货','删除进货记录 ID=95','超级管理员','2026-05-25 08:47:02'),(27,'添加','商品销售','销售商品: 伊利安慕希, 数量: 100','超级管理员','2026-05-25 08:47:26'),(28,'添加','销售退回','销售退货, 销售单ID=125, 数量=10','超级管理员','2026-05-25 08:47:52'),(29,'添加','销售退回','销售退货, 销售单ID=125, 数量=20','超级管理员','2026-05-25 08:48:03'),(30,'删除','销售退回','取消销售退货 ID=13','超级管理员','2026-05-25 08:48:08'),(31,'删除','商品销售','删除销售记录 ID=1','超级管理员','2026-05-25 09:37:18'),(32,'删除','商品销售','删除销售记录 ID=2','超级管理员','2026-05-25 09:37:20'),(33,'删除','商品销售','删除销售记录 ID=7','超级管理员','2026-05-25 09:37:23'),(34,'删除','商品销售','删除销售记录 ID=8','超级管理员','2026-05-25 09:37:25'),(35,'删除','商品销售','删除销售记录 ID=9','超级管理员','2026-05-25 09:37:27'),(36,'添加','会员管理','\'添加会员: \' + #args[0].name','超级管理员','2026-06-06 01:09:24'),(37,'充值','会员管理','\'会员充值ID: \' + #args[0] + \', 金额: \' + #args[1]','超级管理员','2026-06-06 01:10:23'),(38,'充值','会员管理','\'会员充值ID: \' + #args[0] + \', 金额: \' + #args[1]','超级管理员','2026-06-06 01:10:27'),(39,'充值','会员管理','\'会员充值ID: \' + #args[0] + \', 金额: \' + #args[1]','超级管理员','2026-06-06 01:10:29'),(40,'消费','会员管理','\'会员消费ID: \' + #args[0] + \', 金额: \' + #args[1]','超级管理员','2026-06-06 01:10:40'),(41,'添加','盘点管理','\'创建盘点单\'','超级管理员','2026-06-06 01:17:42'),(42,'添加','商品销售','\'销售商品ID: \' + #args[0].goodsid + \', 数量: \' + #args[0].number','超级管理员','2026-06-06 01:22:42'),(43,'添加','提成管理','\'保存提成规则: \' + #args[0].name','超级管理员','2026-06-06 01:31:29'),(44,'添加','提成管理','\'保存提成规则: \' + #args[0].name','超级管理员','2026-06-06 01:32:52'),(45,'计算','提成管理','\'计算提成月份: \' + #args[0]','超级管理员','2026-06-06 01:32:58'),(46,'计算','提成管理','\'计算提成月份: \' + #args[0]','超级管理员','2026-06-06 01:33:05'),(47,'计算','提成管理','\'计算提成月份: \' + #args[0]','超级管理员','2026-06-06 01:37:44'),(48,'删除','提成管理','\'删除提成规则ID: \' + #args[0]','超级管理员','2026-06-06 01:37:58'),(49,'添加','提成管理','\'保存提成规则: \' + #args[0].name','超级管理员','2026-06-06 01:38:03'),(50,'计算','提成管理','\'计算提成月份: \' + #args[0]','超级管理员','2026-06-06 01:41:15'),(51,'计算','提成管理','\'计算提成月份: \' + #args[0]','超级管理员','2026-06-06 01:41:17'),(52,'添加','提成管理','\'保存提成规则: \' + #args[0].name','超级管理员','2026-06-06 01:43:23'),(53,'添加','提成管理','\'保存提成规则: \' + #args[0].name','超级管理员','2026-06-06 01:43:23'),(54,'添加','提成管理','\'保存提成规则: \' + #args[0].name','超级管理员','2026-06-06 01:43:27'),(55,'计算','提成管理','\'计算提成月份: \' + #args[0]','超级管理员','2026-06-06 01:43:29'),(56,'计算','提成管理','\'计算提成月份: \' + #args[0]','超级管理员','2026-06-06 01:43:31'),(57,'计算','提成管理','\'计算提成月份: \' + #args[0]','超级管理员','2026-06-06 01:43:38'),(58,'计算','提成管理','\'计算提成月份: \' + #args[0]','超级管理员','2026-06-06 01:43:40'),(59,'添加','提成管理','\'保存提成规则: \' + #args[0].name','超级管理员','2026-06-06 01:43:45'),(60,'添加','提成管理','\'保存提成规则: \' + #args[0].name','超级管理员','2026-06-06 01:45:00'),(61,'添加','提成管理','\'保存提成规则: \' + #args[0].name','超级管理员','2026-06-06 01:49:47'),(62,'计算','提成管理','\'计算提成月份: \' + #args[0]','超级管理员','2026-06-06 01:50:17'),(63,'添加','提成管理','\'保存提成规则: \' + #args[0].name','超级管理员','2026-06-06 01:50:31'),(64,'添加','商品销售','\'销售商品ID: \' + #args[0].goodsid + \', 数量: \' + #args[0].number','超级管理员','2026-06-06 01:50:45'),(65,'计算','提成管理','\'计算提成月份: \' + #args[0]','超级管理员','2026-06-06 01:51:02'),(66,'计算','提成管理','\'计算提成月份: \' + #args[0]','超级管理员','2026-06-06 01:51:11'),(67,'计算','提成管理','\'计算提成月份: \' + #args[0]','超级管理员','2026-06-06 01:51:16'),(68,'计算','提成管理','\'计算提成月份: \' + #args[0]','超级管理员','2026-06-06 01:51:19'),(69,'计算','提成管理','\'计算提成月份: \' + #args[0]','超级管理员','2026-06-06 01:51:51'),(70,'计算','提成管理','\'计算提成月份: \' + #args[0]','超级管理员','2026-06-06 01:57:22'),(71,'计算','提成管理','\'计算提成月份: \' + #args[0]','超级管理员','2026-06-06 02:08:56'),(72,'计算','提成管理','\'计算提成月份: \' + #args[0]','超级管理员','2026-06-06 02:09:06'),(73,'计算','提成管理','\'计算提成月份: \' + #args[0]','超级管理员','2026-06-06 02:15:52'),(74,'计算','提成管理','\'计算提成月份: \' + #args[0]','超级管理员','2026-06-06 02:16:20'),(75,'计算','提成管理','\'计算提成月份: \' + #args[0]','超级管理员','2026-06-06 02:16:57'),(76,'消费','会员管理','\'会员消费ID: \' + #args[0] + \', 金额: \' + #args[1]','超级管理员','2026-06-06 02:18:44'),(77,'消费','会员管理','\'会员消费ID: \' + #args[0] + \', 金额: \' + #args[1]','超级管理员','2026-06-06 02:18:55'),(78,'消费','会员管理','\'会员消费ID: \' + #args[0] + \', 金额: \' + #args[1]','超级管理员','2026-06-06 02:19:02'),(79,'修改','会员管理','\'保存会员等级规则\'','超级管理员','2026-06-06 02:46:57'),(80,'添加','散客零售','\'批量零售, 共\' + #args[0].size() + \'种商品\'','超级管理员','2026-06-06 02:51:24'),(81,'修改','会员管理','\'保存会员等级规则\'','超级管理员','2026-06-06 02:55:04'),(82,'修改','会员管理','\'保存会员等级规则\'','超级管理员','2026-06-06 02:55:10'),(83,'取消','盘点管理','\'取消盘点单ID: \' + #args[0]','超级管理员','2026-06-06 03:21:18'),(84,'添加','盘点管理','\'创建盘点单\'','超级管理员','2026-06-06 03:21:21'),(85,'提交','盘点管理','\'提交盘点单ID: \' + #args[0]','超级管理员','2026-06-06 03:21:58'),(86,'提交','盘点管理','\'提交盘点单ID: \' + #args[0]','超级管理员','2026-06-06 03:24:22'),(87,'添加','盘点管理','\'创建盘点单\'','超级管理员','2026-06-06 03:30:59'),(88,'添加','商品进货','\'进货商品ID: \' + #args[0].goodsid + \', 数量: \' + #args[0].number','超级管理员','2026-06-06 03:32:40'),(89,'添加','商品销售','\'销售商品ID: \' + #args[0].goodsid + \', 数量: \' + #args[0].number','超级管理员','2026-06-06 16:20:52'),(90,'添加','商品销售','\'销售商品ID: \' + #args[0].goodsid + \', 数量: \' + #args[0].number','超级管理员','2026-06-07 05:00:52'),(91,'添加','商品销售','\'批量销售, 共\' + #args[0].size() + \'种商品\'','未知用户','2026-06-07 05:42:04'),(92,'添加','商品销售','\'批量销售, 共\' + #args[0].size() + \'种商品\'','超级管理员','2026-06-07 05:56:40'),(93,'添加','商品销售','\'批量销售, 共\' + #args[0].size() + \'种商品\'','超级管理员','2026-06-07 06:06:14'),(94,'添加','商品销售','\'批量销售, 共\' + #args[0].size() + \'种商品\'','超级管理员','2026-06-07 06:16:33'),(95,'退货','商品销售','\'单商品退货, 销售ID: \' + #args[0]','超级管理员','2026-06-07 06:18:09'),(96,'添加','商品销售','\'批量销售, 共\' + #args[0].size() + \'种商品\'','超级管理员','2026-06-07 06:18:34'),(97,'退货','商品销售','\'单商品退货, 销售ID: \' + #args[0]','超级管理员','2026-06-07 06:18:44'),(98,'添加','商品销售','\'批量销售, 共\' + #args[0].size() + \'种商品\'','超级管理员','2026-06-07 06:27:36'),(99,'加货','商品销售','\'向订单 \' + #args[0].orderno + \' 添加商品\'','超级管理员','2026-06-07 06:27:49'),(100,'加货','商品销售','\'向订单 \' + #args[0].orderno + \' 添加商品\'','超级管理员','2026-06-07 06:28:28'),(101,'加货','商品销售','\'向订单 \' + #args[0].orderno + \' 添加商品\'','超级管理员','2026-06-07 06:33:15'),(102,'退货','商品销售','\'单商品退货, 销售ID: \' + #args[0]','超级管理员','2026-06-07 06:33:33'),(103,'加货','商品销售','\'向订单 \' + #args[0].orderno + \' 添加商品\'','超级管理员','2026-06-07 06:34:16'),(104,'添加','商品销售','\'批量销售, 共\' + #args[0].size() + \'种商品\'','超级管理员','2026-06-07 06:44:35'),(105,'退货','商品销售','\'单商品退货, 销售ID: \' + #args[0]','超级管理员','2026-06-07 06:44:51'),(106,'退货','商品销售','\'单商品退货, 销售ID: \' + #args[0]','超级管理员','2026-06-07 06:50:18'),(107,'添加','商品销售','\'批量销售, 共\' + #args[0].size() + \'种商品\'','超级管理员','2026-06-07 06:57:48'),(108,'加货','商品销售','\'向订单 \' + #args[0].orderno + \' 添加商品\'','超级管理员','2026-06-07 06:58:18'),(109,'退货','商品销售','\'单商品退货, 销售ID: \' + #args[0]','超级管理员','2026-06-07 06:58:45'),(110,'退货','商品销售','\'单商品退货, 销售ID: \' + #args[0]','超级管理员','2026-06-07 07:04:07'),(111,'退货','商品销售','\'整单退货, 订单号: \' + #args[0]','超级管理员','2026-06-07 07:04:20'),(112,'退货','商品销售','\'整单退货, 订单号: \' + #args[0]','超级管理员','2026-06-07 07:09:01'),(113,'退货','商品销售','\'单商品退货, 销售ID: \' + #args[0]','超级管理员','2026-06-07 07:09:22'),(114,'添加','商品销售','\'批量销售, 共\' + #args[0].size() + \'种商品\'','超级管理员','2026-06-07 07:15:43'),(115,'退货','商品销售','\'单商品退货, 销售ID: \' + #args[0]','超级管理员','2026-06-07 07:15:54'),(116,'加货','商品销售','\'向订单 \' + #args[0].orderno + \' 添加商品\'','超级管理员','2026-06-07 07:16:26'),(117,'加货','商品销售','\'向订单 \' + #args[0].orderno + \' 添加商品\'','超级管理员','2026-06-07 07:25:55'),(118,'退货','商品销售','\'单商品退货, 销售ID: \' + #args[0]','超级管理员','2026-06-07 07:35:05'),(119,'退货','商品销售','\'单商品退货, 销售ID: \' + #args[0]','超级管理员','2026-06-07 07:35:17'),(120,'退货','商品销售','\'单商品退货, 销售ID: \' + #args[0]','超级管理员','2026-06-07 07:35:17'),(121,'加货','商品销售','\'向订单 \' + #args[0].orderno + \' 添加商品\'','超级管理员','2026-06-07 07:35:28'),(122,'添加','商品进货','\'批量进货, 共\' + #args[0].size() + \'种商品\'','超级管理员','2026-06-07 08:36:04'),(123,'添加','商品进货','\'批量进货, 共\' + #args[0].size() + \'种商品\'','超级管理员','2026-06-07 08:58:28'),(124,'添加','商品进货','\'批量进货, 共\' + #args[0].size() + \'种商品\'','超级管理员','2026-06-07 09:01:52'),(125,'加货','商品进货','\'向订单 \' + #args[0].orderno + \' 添加商品\'','超级管理员','2026-06-07 09:04:08'),(126,'加货','商品进货','\'向订单 \' + #args[0].orderno + \' 添加商品\'','超级管理员','2026-06-07 09:04:19'),(127,'退货','商品进货','\'单商品退货, 进货ID: \' + #args[0]','超级管理员','2026-06-07 09:04:56'),(128,'退货','商品进货','\'整单退货, 订单号: \' + #args[0]','超级管理员','2026-06-07 09:10:14'),(129,'添加','商品进货','\'批量进货, 共\' + #args[0].size() + \'种商品\'','超级管理员','2026-06-07 09:50:54'),(130,'添加','商品进货','\'批量进货, 共\' + #args[0].size() + \'种商品\'','超级管理员','2026-06-07 09:54:21'),(131,'添加','商品销售','\'批量销售, 共\' + #args[0].size() + \'种商品\'','超级管理员','2026-06-07 09:54:30'),(132,'添加','商品销售','\'批量销售, 共\' + #args[0].size() + \'种商品\'','超级管理员','2026-06-07 09:56:11'),(133,'添加','商品销售','\'批量销售, 共\' + #args[0].size() + \'种商品\'','超级管理员','2026-06-07 09:58:44'),(134,'添加','商品销售','\'批量销售, 共\' + #args[0].size() + \'种商品\'','超级管理员','2026-06-07 09:59:52'),(135,'添加','商品销售','批量销售, 共3种商品','超级管理员','2026-06-07 10:02:26'),(136,'添加','商品销售','批量销售, 共3种商品','超级管理员','2026-06-07 15:04:10'),(137,'添加','商品进货','批量进货, 订单号: IP202606072312140001, 共3种商品, 总数量: 6','超级管理员','2026-06-07 15:12:15'),(138,'取消','盘点管理','取消盘点单ID: 3','超级管理员','2026-06-07 15:15:03'),(139,'添加','散客零售','批量零售, 共1种商品','超级管理员','2026-06-07 15:16:44'),(140,'添加','零售退回','零售退货, 零售单ID: 1, 数量: 1','超级管理员','2026-06-07 15:16:56'),(141,'添加','零售退回','零售退货, 零售单ID: 2, 数量: 1','超级管理员','2026-06-07 15:16:59'),(142,'添加','零售退回','零售退货, 零售单ID: 1, 数量: 1','超级管理员','2026-06-07 15:17:09'),(143,'删除','零售退回','取消零售退货ID: 1','超级管理员','2026-06-07 15:17:25'),(144,'添加','零售退回','零售退货, 零售单ID: 1, 数量: 1','超级管理员','2026-06-07 15:22:02'),(145,'添加','零售退回','零售退货, 零售单ID: 2, 数量: 4','超级管理员','2026-06-07 15:22:08'),(146,'添加','零售退回','零售退货, 零售单ID: 2, 数量: 4','超级管理员','2026-06-07 15:22:11'),(147,'添加','零售退回','零售退货, 零售单ID: 2, 数量: 1','超级管理员','2026-06-07 15:22:22'),(148,'添加','零售退回','零售退货, 零售单ID: 2, 数量: 2','超级管理员','2026-06-07 15:22:26'),(149,'删除','零售退回','取消零售退货ID: 3','超级管理员','2026-06-07 15:22:29'),(150,'添加','商品销售','批量销售, 订单号: SO17808476232355252, 共4种商品, 总数量: 10','超级管理员','2026-06-07 15:53:43'),(151,'添加','商品销售','\'\'','超级管理员','2026-06-07 15:53:43');
/*!40000 ALTER TABLE `bus_operation_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bus_outport`
--

DROP TABLE IF EXISTS `bus_outport`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bus_outport` (
  `id` int NOT NULL AUTO_INCREMENT,
  `providerid` int DEFAULT NULL,
  `paytype` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `outputtime` datetime DEFAULT NULL,
  `operateperson` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `outportprice` double(10,2) DEFAULT NULL,
  `number` int DEFAULT NULL,
  `remark` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `goodsid` int DEFAULT NULL,
  `inportid` int DEFAULT NULL,
  `isdelete` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bus_outport`
--

LOCK TABLES `bus_outport` WRITE;
/*!40000 ALTER TABLE `bus_outport` DISABLE KEYS */;
INSERT INTO `bus_outport` VALUES (1,3,'支付宝','2026-03-12 14:30:00','落亦-',18.00,20,'部分AD钙奶临期退回',5,NULL,0),(2,1,'微信','2026-03-20 10:15:00','李月素',8.50,15,'包装破损',1,NULL,0),(3,4,'银联','2026-03-28 16:00:00','超级管理员',38.00,10,'蒙牛纯牛奶胀包',11,NULL,0),(4,6,'支付宝','2026-04-05 11:30:00','落亦-',32.00,8,'可口可乐罐体凹陷',16,NULL,0),(5,2,'微信','2026-04-12 09:45:00','李月素',6.00,12,'小面包临期退回',8,NULL,0),(6,5,'银联','2026-04-20 15:00:00','超级管理员',37.00,5,'伊利纯牛奶过期退回',13,NULL,0),(7,7,'微信','2026-04-28 10:30:00','赵六',7.50,10,'方便面外包装损坏',19,NULL,0),(8,8,'银联','2026-05-05 14:00:00','超级管理员',22.00,15,'农夫山泉瓶盖漏水',22,NULL,0),(9,3,'支付宝','2026-05-12 11:00:00','落亦-',28.00,8,'八宝粥罐体生锈',7,NULL,0),(10,5,'银联','2026-05-18 16:30:00','超级管理员',45.00,5,'金典包装破损',15,NULL,0);
/*!40000 ALTER TABLE `bus_outport` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bus_provider`
--

DROP TABLE IF EXISTS `bus_provider`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bus_provider` (
  `id` int NOT NULL AUTO_INCREMENT,
  `providername` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `zip` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `address` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `telephone` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `connectionperson` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `phone` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `bank` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `account` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `email` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `fax` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `available` int DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bus_provider`
--

LOCK TABLES `bus_provider` WRITE;
/*!40000 ALTER TABLE `bus_provider` DISABLE KEYS */;
INSERT INTO `bus_provider` VALUES (1,'旺旺食品集团','434000','湖北省仙桃市旺旺工业园','0728-4124312','林志明','13413441141','中国农业银行','6228480402564893017','wangwang@food.com','0728-4124313',1),(2,'达利园食品集团','430000','湖北省武汉市汉川经济开发区','027-85861234','陈大明','13871234567','中国工商银行','6222021812003456789','daliyuan@food.com','027-85861235',1),(3,'娃哈哈集团','310000','浙江省杭州市清泰街160号','0571-87026111','王建林','13957123456','中国建设银行','6227001215820345678','wahaha@group.com','0571-87026112',1),(4,'蒙牛乳业集团','010000','内蒙古呼和浩特市和林格尔县','0471-7392888','张丽华','13617252689','中国银行','6216610100012345678','mengniu@dairy.com','0471-7392889',1),(5,'伊利实业集团','010000','内蒙古呼和浩特市金山开发区','0471-3388888','李雪琴','13698560566','中国建设银行','6227001215820987654','yili@dairy.com','0471-3388889',1),(6,'可口可乐（中国）','200000','上海市闵行区紫竹科学园区','021-23061888','赵明强','13816123456','汇丰银行','6222021812009876543','cocacola@cn.com','021-23061889',1),(7,'康师傅控股','300000','天津市经济技术开发区','022-25321888','周杰伦','13821234567','中国农业银行','6228480402567890123','kangshifu@food.com','022-25321889',1),(8,'农夫山泉股份','310000','浙江省杭州市西湖区','0571-87091888','钟小慧','13957987654','招商银行','6225881234567890','nongfu@water.com','0571-87091889',1),(9,'测试',NULL,'1',NULL,'1','1',NULL,NULL,NULL,NULL,1),(10,'1',NULL,'4',NULL,'2','3',NULL,NULL,NULL,NULL,1);
/*!40000 ALTER TABLE `bus_provider` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bus_retail`
--

DROP TABLE IF EXISTS `bus_retail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bus_retail` (
  `id` int NOT NULL AUTO_INCREMENT,
  `goodsid` int DEFAULT NULL,
  `paytype` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `retailtime` datetime DEFAULT NULL,
  `operateperson` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `number` int DEFAULT NULL,
  `remark` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `retailprice` decimal(10,2) DEFAULT NULL,
  `isdelete` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bus_retail`
--

LOCK TABLES `bus_retail` WRITE;
/*!40000 ALTER TABLE `bus_retail` DISABLE KEYS */;
INSERT INTO `bus_retail` VALUES (1,1,'现金','2026-06-06 02:51:24','超级管理员',1,'',12.50,0),(2,2,'现金','2026-06-07 15:16:44','超级管理员',1,'',38.00,0);
/*!40000 ALTER TABLE `bus_retail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bus_retailback`
--

DROP TABLE IF EXISTS `bus_retailback`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bus_retailback` (
  `id` int NOT NULL AUTO_INCREMENT,
  `goodsid` int DEFAULT NULL,
  `retailid` int DEFAULT NULL,
  `paytype` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `retailbacktime` datetime DEFAULT NULL,
  `retailbackprice` double(10,2) DEFAULT NULL,
  `operateperson` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `number` int DEFAULT NULL,
  `remark` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `isdelete` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bus_retailback`
--

LOCK TABLES `bus_retailback` WRITE;
/*!40000 ALTER TABLE `bus_retailback` DISABLE KEYS */;
INSERT INTO `bus_retailback` VALUES (2,2,2,'现金','2026-06-07 15:16:59',38.00,'超级管理员',1,'',0),(4,2,2,'现金','2026-06-07 15:22:22',38.00,'超级管理员',1,'',0);
/*!40000 ALTER TABLE `bus_retailback` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bus_sales`
--

DROP TABLE IF EXISTS `bus_sales`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bus_sales` (
  `id` int NOT NULL AUTO_INCREMENT,
  `orderno` varchar(64) DEFAULT NULL,
  `customerid` int DEFAULT NULL,
  `paytype` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `salestime` datetime DEFAULT NULL,
  `operateperson` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `number` int DEFAULT NULL,
  `remark` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `saleprice` decimal(10,2) DEFAULT NULL,
  `goodsid` int DEFAULT NULL,
  `isdelete` int NOT NULL DEFAULT '0',
  `order_status` int DEFAULT '0' COMMENT '订单状态: 0=正常, 1=已退完',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_orderno` (`orderno`)
) ENGINE=InnoDB AUTO_INCREMENT=178 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bus_sales`
--

LOCK TABLES `bus_sales` WRITE;
/*!40000 ALTER TABLE `bus_sales` DISABLE KEYS */;
INSERT INTO `bus_sales` VALUES (1,NULL,1,'微信','2026-03-03 10:20:00','落亦-',20,'小张超市进货',250.00,1,1,0),(2,NULL,1,'微信','2026-03-03 10:25:00','落亦-',10,'小张超市进货',350.00,2,1,0),(3,NULL,3,'支付宝','2026-03-04 09:15:00','李月素',30,'快七超市进货',840.00,5,0,0),(4,NULL,3,'支付宝','2026-03-04 09:20:00','李月素',20,'快七超市进货',440.00,6,0,0),(5,NULL,2,'银联','2026-03-05 14:00:00','赵六',15,'小明超市进货',187.50,1,0,0),(6,NULL,2,'银联','2026-03-05 14:05:00','赵六',10,'小明超市进货',125.00,4,0,0),(7,NULL,5,'微信','2026-03-06 08:45:00','落亦-',50,'永辉超市进货',625.00,1,1,0),(8,NULL,5,'微信','2026-03-06 08:50:00','落亦-',30,'永辉超市进货',1050.00,2,1,0),(9,NULL,5,'微信','2026-03-06 09:00:00','落亦-',40,'永辉超市进货',1120.00,5,1,0),(10,NULL,6,'支付宝','2026-03-07 09:30:00','李月素',25,'中百仓储进货',312.50,1,0,0),(11,NULL,6,'支付宝','2026-03-07 09:35:00','李月素',20,'中百仓储进货',250.00,3,0,0),(12,NULL,4,'微信','2026-03-08 10:00:00','落亦-',10,'丽云超市进货',125.00,1,0,0),(13,NULL,4,'微信','2026-03-08 10:05:00','落亦-',8,'丽云超市进货',100.00,4,0,0),(14,NULL,7,'银联','2026-03-10 09:00:00','赵六',40,'武商超市进货',500.00,1,0,0),(15,NULL,7,'银联','2026-03-10 09:05:00','赵六',25,'武商超市进货',875.00,2,0,0),(16,NULL,7,'银联','2026-03-10 09:10:00','赵六',30,'武商超市进货',840.00,5,0,0),(17,NULL,8,'支付宝','2026-03-11 08:30:00','落亦-',30,'联华超市进货',375.00,1,0,0),(18,NULL,8,'支付宝','2026-03-11 08:35:00','落亦-',20,'联华超市进货',700.00,2,0,0),(19,NULL,9,'微信','2026-03-12 14:00:00','李月素',35,'华联超市进货',980.00,11,0,0),(20,NULL,9,'微信','2026-03-12 14:05:00','李月素',20,'华联超市进货',1040.00,14,0,0),(21,NULL,10,'银联','2026-03-14 09:00:00','赵六',20,'物美超市进货',250.00,1,0,0),(22,NULL,10,'银联','2026-03-14 09:05:00','赵六',15,'物美超市进货',187.50,3,0,0),(23,NULL,11,'微信','2026-03-15 10:30:00','落亦-',25,'家家悦进货',600.00,16,0,0),(24,NULL,11,'微信','2026-03-15 10:35:00','落亦-',20,'家家悦进货',600.00,18,0,0),(25,NULL,12,'支付宝','2026-03-17 09:00:00','李月素',15,'芙蓉兴盛进货',187.50,19,0,0),(26,NULL,12,'支付宝','2026-03-17 09:05:00','李月素',10,'芙蓉兴盛进货',320.00,20,0,0),(27,NULL,1,'微信','2026-03-19 14:30:00','落亦-',15,'小张超市补货',187.50,1,0,0),(28,NULL,1,'微信','2026-03-19 14:35:00','落亦-',10,'小张超市补货',85.00,3,0,0),(29,NULL,5,'银联','2026-03-21 09:00:00','赵六',30,'永辉超市补货',1740.00,11,0,0),(30,NULL,5,'银联','2026-03-21 09:05:00','赵六',20,'永辉超市补货',1160.00,13,0,0),(31,NULL,6,'支付宝','2026-03-23 08:30:00','落亦-',20,'中百仓储补货',250.00,1,0,0),(32,NULL,6,'支付宝','2026-03-23 08:35:00','落亦-',15,'中百仓储补货',525.00,7,0,0),(33,NULL,3,'微信','2026-03-25 10:00:00','李月素',21,'快七超市补货',240.00,8,0,0),(34,NULL,3,'微信','2026-03-25 10:05:00','李月素',15,'快七超市补货',202.50,9,0,0),(35,NULL,9,'银联','2026-03-27 14:00:00','赵六',25,'华联超市补货',350.00,16,0,0),(36,NULL,9,'银联','2026-03-27 14:05:00','赵六',20,'华联超市补货',680.00,22,0,0),(37,NULL,10,'微信','2026-03-29 09:30:00','落亦-',15,'物美超市补货',187.50,19,0,0),(38,NULL,10,'微信','2026-03-29 09:35:00','落亦-',10,'物美超市补货',125.00,21,0,0),(39,NULL,12,'支付宝','2026-03-31 10:00:00','李月素',10,'芙蓉兴盛月末进货',125.00,1,0,0),(40,NULL,12,'支付宝','2026-03-31 10:05:00','李月素',8,'芙蓉兴盛月末进货',96.00,23,0,0),(41,NULL,5,'银联','2026-04-01 08:30:00','超级管理员',60,'永辉月初大单',750.00,1,0,0),(42,NULL,5,'银联','2026-04-01 08:35:00','超级管理员',40,'永辉月初大单',1400.00,2,0,0),(43,NULL,5,'银联','2026-04-01 08:40:00','超级管理员',50,'永辉月初大单',1400.00,5,0,0),(44,NULL,6,'微信','2026-04-02 09:00:00','落亦-',30,'中百仓储进货',375.00,1,0,0),(45,NULL,6,'微信','2026-04-02 09:05:00','落亦-',25,'中百仓储进货',312.50,3,0,0),(46,NULL,7,'支付宝','2026-04-03 14:00:00','李月素',35,'武商超市进货',437.50,1,0,0),(47,NULL,7,'支付宝','2026-04-03 14:05:00','李月素',20,'武商超市进货',700.00,2,0,0),(48,NULL,7,'支付宝','2026-04-03 14:10:00','李月素',30,'武商超市进货',840.00,5,0,0),(49,NULL,8,'银联','2026-04-05 09:00:00','赵六',40,'联华超市进货',500.00,1,0,0),(50,NULL,8,'银联','2026-04-05 09:05:00','赵六',30,'联华超市进货',1050.00,2,0,0),(51,NULL,9,'微信','2026-04-06 08:30:00','落亦-',30,'华联超市进货',840.00,11,0,0),(52,NULL,9,'微信','2026-04-06 08:35:00','落亦-',25,'华联超市进货',1300.00,14,0,0),(53,NULL,10,'支付宝','2026-04-07 09:30:00','李月素',25,'物美超市进货',312.50,1,0,0),(54,NULL,10,'支付宝','2026-04-07 09:35:00','李月素',20,'物美超市进货',680.00,22,0,0),(55,NULL,11,'银联','2026-04-08 10:00:00','赵六',30,'家家悦进货',375.00,1,0,0),(56,NULL,11,'银联','2026-04-08 10:05:00','赵六',20,'家家悦进货',250.00,4,0,0),(57,NULL,12,'微信','2026-04-09 14:00:00','落亦-',20,'芙蓉兴盛进货',640.00,16,0,0),(58,NULL,12,'微信','2026-04-09 14:05:00','落亦-',15,'芙蓉兴盛进货',450.00,18,0,0),(59,NULL,1,'支付宝','2026-04-10 09:00:00','李月素',20,'小张超市进货',250.00,1,0,0),(60,NULL,1,'支付宝','2026-04-10 09:05:00','李月素',15,'小张超市进货',127.50,8,0,0),(61,NULL,3,'银联','2026-04-12 10:00:00','赵六',25,'快七超市进货',312.50,1,0,0),(62,NULL,3,'银联','2026-04-12 10:05:00','赵六',20,'快七超市进货',560.00,11,0,0),(63,NULL,5,'微信','2026-04-14 08:30:00','落亦-',40,'永辉超市补货',1120.00,5,0,0),(64,NULL,5,'微信','2026-04-14 08:35:00','落亦-',30,'永辉超市补货',1560.00,16,0,0),(65,NULL,6,'支付宝','2026-04-16 09:00:00','李月素',25,'中百仓储补货',875.00,7,0,0),(66,NULL,6,'支付宝','2026-04-16 09:05:00','李月素',20,'中百仓储补货',270.00,10,0,0),(67,NULL,8,'银联','2026-04-18 14:00:00','赵六',30,'联华超市补货',360.00,22,0,0),(68,NULL,8,'银联','2026-04-18 14:05:00','赵六',20,'联华超市补货',240.00,23,0,0),(69,NULL,9,'微信','2026-04-20 09:00:00','落亦-',25,'华联超市补货',700.00,11,0,0),(70,NULL,9,'微信','2026-04-20 09:05:00','落亦-',20,'华联超市补货',1160.00,15,0,0),(71,NULL,11,'支付宝','2026-04-22 10:00:00','李月素',20,'家家悦补货',240.00,8,0,0),(72,NULL,11,'支付宝','2026-04-22 10:05:00','李月素',15,'家家悦补货',202.50,9,0,0),(73,NULL,12,'银联','2026-04-24 14:00:00','赵六',15,'芙蓉兴盛补货',187.50,19,0,0),(74,NULL,12,'银联','2026-04-24 14:05:00','赵六',10,'芙蓉兴盛补货',120.00,25,0,0),(75,NULL,2,'微信','2026-04-26 09:00:00','落亦-',20,'小明超市进货',250.00,1,0,0),(76,NULL,2,'微信','2026-04-26 09:05:00','落亦-',15,'小明超市进货',420.00,5,0,0),(77,NULL,4,'支付宝','2026-04-28 10:00:00','李月素',10,'丽云超市进货',125.00,1,0,0),(78,NULL,4,'支付宝','2026-04-28 10:05:00','李月素',8,'丽云超市进货',100.00,4,0,0),(79,NULL,10,'银联','2026-04-30 09:00:00','赵六',20,'物美超市月末',250.00,1,0,0),(80,NULL,10,'银联','2026-04-30 09:05:00','赵六',15,'物美超市月末',187.50,3,0,0),(81,NULL,5,'银联','2026-05-02 08:30:00','超级管理员',80,'永辉超市五月大单',1000.00,1,0,0),(82,NULL,5,'银联','2026-05-02 08:35:00','超级管理员',50,'永辉超市五月大单',1750.00,2,0,0),(83,NULL,5,'银联','2026-05-02 08:40:00','超级管理员',60,'永辉超市五月大单',1680.00,5,0,0),(84,NULL,5,'银联','2026-05-02 08:45:00','超级管理员',40,'永辉超市五月大单',2320.00,11,0,0),(85,NULL,6,'微信','2026-05-03 09:00:00','落亦-',40,'中百仓储进货',500.00,1,0,0),(86,NULL,6,'微信','2026-05-03 09:05:00','落亦-',30,'中百仓储进货',375.00,3,0,0),(87,NULL,7,'支付宝','2026-05-04 14:00:00','李月素',40,'武商超市进货',500.00,1,0,0),(88,NULL,7,'支付宝','2026-05-04 14:05:00','李月素',30,'武商超市进货',1050.00,2,0,0),(89,NULL,7,'支付宝','2026-05-04 14:10:00','李月素',35,'武商超市进货',980.00,5,0,0),(90,NULL,8,'银联','2026-05-05 09:00:00','赵六',50,'联华超市进货',625.00,1,0,0),(91,NULL,8,'银联','2026-05-05 09:05:00','赵六',30,'联华超市进货',1050.00,2,0,0),(92,NULL,9,'微信','2026-05-06 08:30:00','落亦-',40,'华联超市进货',1120.00,11,0,0),(93,NULL,9,'微信','2026-05-06 08:35:00','落亦-',30,'华联超市进货',1560.00,14,0,0),(94,NULL,10,'支付宝','2026-05-07 09:30:00','李月素',30,'物美超市进货',375.00,1,0,0),(95,NULL,10,'支付宝','2026-05-07 09:35:00','李月素',25,'物美超市进货',850.00,22,0,0),(96,NULL,11,'银联','2026-05-08 10:00:00','赵六',35,'家家悦进货',437.50,1,0,0),(97,NULL,11,'银联','2026-05-08 10:05:00','赵六',25,'家家悦进货',875.00,5,0,0),(98,NULL,12,'微信','2026-05-09 14:00:00','落亦-',25,'芙蓉兴盛进货',800.00,16,0,0),(99,NULL,12,'微信','2026-05-09 14:05:00','落亦-',20,'芙蓉兴盛进货',600.00,18,0,0),(100,NULL,1,'支付宝','2026-05-10 09:00:00','李月素',25,'小张超市进货',312.50,1,0,0),(101,NULL,1,'支付宝','2026-05-10 09:05:00','李月素',15,'小张超市进货',127.50,8,0,0),(102,NULL,3,'银联','2026-05-11 10:00:00','赵六',30,'快七超市进货',375.00,1,0,0),(103,NULL,3,'银联','2026-05-11 10:05:00','赵六',25,'快七超市进货',700.00,11,0,0),(104,NULL,5,'微信','2026-05-13 08:30:00','落亦-',50,'永辉超市补货',1400.00,5,0,0),(105,NULL,5,'微信','2026-05-13 08:35:00','落亦-',40,'永辉超市补货',2080.00,16,0,0),(106,NULL,6,'支付宝','2026-05-14 09:00:00','李月素',30,'中百仓储补货',375.00,1,0,0),(107,NULL,6,'支付宝','2026-05-14 09:05:00','李月素',20,'中百仓储补货',680.00,22,0,0),(108,NULL,8,'银联','2026-05-15 14:00:00','赵六',35,'联华超市补货',437.50,1,0,0),(109,NULL,8,'银联','2026-05-15 14:05:00','赵六',25,'联华超市补货',875.00,5,0,0),(110,NULL,9,'微信','2026-05-16 09:00:00','落亦-',30,'华联超市补货',840.00,11,0,0),(111,NULL,9,'微信','2026-05-16 09:05:00','落亦-',25,'华联超市补货',1300.00,15,0,0),(112,NULL,11,'支付宝','2026-05-17 10:00:00','李月素',25,'家家悦补货',312.50,1,0,0),(113,NULL,11,'支付宝','2026-05-17 10:05:00','李月素',20,'家家悦补货',270.00,10,0,0),(114,NULL,12,'银联','2026-05-18 14:00:00','赵六',20,'芙蓉兴盛补货',640.00,16,0,0),(115,NULL,12,'银联','2026-05-18 14:05:00','赵六',15,'芙蓉兴盛补货',180.00,23,0,0),(116,NULL,2,'微信','2026-05-19 09:00:00','落亦-',25,'小明超市进货',312.50,1,0,0),(117,NULL,2,'微信','2026-05-19 09:05:00','落亦-',20,'小明超市进货',560.00,5,0,0),(118,NULL,4,'支付宝','2026-05-19 10:00:00','李月素',15,'丽云超市进货',187.50,1,0,0),(119,NULL,4,'支付宝','2026-05-19 10:05:00','李月素',10,'丽云超市进货',125.00,4,0,0),(120,NULL,10,'银联','2026-05-19 14:00:00','赵六',25,'物美超市进货',312.50,1,0,0),(121,NULL,2,NULL,'2026-05-21 07:40:07','超级管理员',5,NULL,50.00,2,0,0),(122,NULL,3,NULL,'2026-05-21 07:48:13','超级管理员',1,NULL,1.00,1,0,0),(123,NULL,2,NULL,'2026-05-21 08:27:54','超级管理员',20,'1',10.00,2,0,0),(124,NULL,2,NULL,'2026-05-22 04:42:05','超级管理员',10,NULL,10.00,27,0,0),(125,NULL,5,NULL,'2026-05-25 08:47:26','超级管理员',80,NULL,100.00,14,0,0),(126,NULL,1,NULL,'2026-06-06 01:22:42','超级管理员',1,'1',1.00,5,0,0),(127,NULL,2,NULL,'2026-06-06 01:50:45','超级管理员',100,NULL,1000.00,3,0,0),(128,NULL,NULL,NULL,'2026-06-06 16:20:51','超级管理员',4,NULL,NULL,27,0,0),(129,NULL,NULL,NULL,'2026-06-07 05:00:52','超级管理员',3,NULL,NULL,27,0,0),(130,NULL,4,NULL,'2026-06-07 05:56:40','超级管理员',1,'123',8.50,3,0,0),(131,NULL,4,NULL,'2026-06-07 05:56:40','超级管理员',2,'123',45.00,7,0,0),(132,NULL,3,NULL,'2026-06-07 06:06:14','超级管理员',1,'',8.50,3,0,0),(133,NULL,3,NULL,'2026-06-07 06:06:14','超级管理员',100,'',22.00,6,0,0),(134,'SO17808129933448809',2,NULL,'2026-06-07 06:16:33','超级管理员',1,'',35.00,2,1,0),(135,'SO17808129933448809',2,NULL,'2026-06-07 06:16:33','超级管理员',1,'',8.50,3,1,0),(136,'SO17808131137101384',1,NULL,'2026-06-07 06:18:34','超级管理员',10,'10个',12.50,1,1,0),(137,'SO17808136556655137',9,NULL,'2026-06-07 06:27:36','超级管理员',1,'测试',12.50,1,0,0),(138,'SO17808136556655137',9,NULL,'2026-06-07 06:27:36','超级管理员',2,'测试',35.00,2,1,0),(139,'SO17808136556655137',9,NULL,'2026-06-07 06:27:36','超级管理员',3,'测试',8.50,3,0,0),(140,'SO17808136556655137',9,NULL,'2026-06-07 06:33:15','超级管理员',1,NULL,28.00,5,0,0),(141,'SO17808136556655137',9,NULL,'2026-06-07 06:34:16','超级管理员',2,NULL,28.00,5,0,0),(142,'SO17808146747053852',5,NULL,'2026-06-07 06:44:35','超级管理员',1,'',12.50,1,1,0),(143,'SO17808146747053852',5,NULL,'2026-06-07 06:44:35','超级管理员',1,'',35.00,2,1,0),(144,'SO17808146747053852',5,NULL,'2026-06-07 06:44:35','超级管理员',1,'',8.50,3,1,0),(145,'SO17808154682492508',6,NULL,'2026-06-07 06:57:48','超级管理员',1,'1',12.50,1,1,0),(146,'SO17808154682492508',6,NULL,'2026-06-07 06:57:48','超级管理员',2,'1',8.50,3,1,0),(147,'SO17808154682492508',6,NULL,'2026-06-07 06:57:48','超级管理员',2,'1',28.00,5,1,0),(148,'SO17808154682492508',6,NULL,'2026-06-07 06:58:18','超级管理员',1,NULL,10.00,4,1,0),(149,'SO17808165429009926',4,NULL,'2026-06-07 07:15:43','超级管理员',0,'ces',12.50,1,0,1),(150,'SO17808165429009926',4,NULL,'2026-06-07 07:15:43','超级管理员',1,'ces',35.00,2,0,0),(151,'SO17808165429009926',4,NULL,'2026-06-07 07:15:43','超级管理员',0,'ces',8.50,3,0,1),(152,'SO17808165429009926',4,NULL,'2026-06-07 07:15:43','超级管理员',1,'ces',10.00,4,0,0),(153,'SO17808165429009926',4,NULL,'2026-06-07 07:15:43','超级管理员',1,'ces',28.00,5,0,0),(154,'SO17808165429009926',4,NULL,'2026-06-07 07:15:43','超级管理员',0,'ces',22.00,6,0,1),(155,'SO17808165429009926',4,NULL,'2026-06-07 07:16:26','超级管理员',2,NULL,8.50,3,0,0),(156,'SO17808165429009926',4,NULL,'2026-06-07 07:25:55','超级管理员',1,NULL,8.50,3,0,0),(157,'SO17808165429009926',4,NULL,'2026-06-07 07:35:28','超级管理员',2,NULL,8.50,3,0,0),(158,'SO17808260704380591',2,NULL,'2026-06-07 09:54:30','超级管理员',1,'',58.00,11,0,0),(159,'SO17808260704380591',2,NULL,'2026-06-07 09:54:30','超级管理员',1,'',28.00,5,0,0),(160,'SO17808261707830087',3,NULL,'2026-06-07 09:56:11','超级管理员',2,'',58.00,11,0,0),(161,'SO17808261707830087',3,NULL,'2026-06-07 09:56:11','超级管理员',1,'',48.00,16,0,0),(162,'SO17808261707830087',3,NULL,'2026-06-07 09:56:11','超级管理员',1,'',28.00,5,0,0),(163,'SO17808261707830087',3,NULL,'2026-06-07 09:56:11','超级管理员',1,'',12.50,1,0,0),(164,'SO17808263236519933',1,NULL,'2026-06-07 09:58:44','超级管理员',2,'',58.00,11,0,0),(165,'SO17808263236519933',1,NULL,'2026-06-07 09:58:44','超级管理员',1,'',48.00,16,0,0),(166,'SO17808263920488655',5,NULL,'2026-06-07 09:59:52','超级管理员',1,'',28.00,5,0,0),(167,'SO17808263920488655',5,NULL,'2026-06-07 09:59:52','超级管理员',2,'',58.00,11,0,0),(168,'SO17808265460644308',7,NULL,'2026-06-07 10:02:26','超级管理员',1,'',12.50,1,0,0),(169,'SO17808265460644308',7,NULL,'2026-06-07 10:02:26','超级管理员',2,'',48.00,16,0,0),(170,'SO17808265460644308',7,NULL,'2026-06-07 10:02:26','超级管理员',1,'',8.50,3,0,0),(171,'SO17808446500282855',4,NULL,'2026-06-07 15:04:10','超级管理员',1,'',12.50,1,0,0),(172,'SO17808446500282855',4,NULL,'2026-06-07 15:04:10','超级管理员',1,'',28.00,5,0,0),(173,'SO17808446500282855',4,NULL,'2026-06-07 15:04:10','超级管理员',1,'',8.50,3,0,0),(174,'SO17808476232355252',4,NULL,'2026-06-07 15:53:43','超级管理员',4,'1',12.50,1,0,0),(175,'SO17808476232355252',4,NULL,'2026-06-07 15:53:43','超级管理员',3,'1',28.00,5,0,0),(176,'SO17808476232355252',4,NULL,'2026-06-07 15:53:43','超级管理员',2,'1',8.50,3,0,0),(177,'SO17808476232355252',4,NULL,'2026-06-07 15:53:43','超级管理员',1,'1',58.00,11,0,0);
/*!40000 ALTER TABLE `bus_sales` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bus_sales_log`
--

DROP TABLE IF EXISTS `bus_sales_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bus_sales_log` (
  `id` int NOT NULL AUTO_INCREMENT,
  `order_no` varchar(64) DEFAULT NULL COMMENT '订单号',
  `customer_id` int DEFAULT NULL COMMENT '客户ID',
  `goods_id` int DEFAULT NULL COMMENT '商品ID',
  `type` varchar(20) NOT NULL COMMENT '类型: sale=销售, add=加货, return=退货',
  `number` int DEFAULT NULL COMMENT '数量',
  `price` decimal(10,2) DEFAULT NULL COMMENT '单价',
  `operate_person` varchar(50) DEFAULT NULL COMMENT '操作人',
  `operate_time` datetime DEFAULT NULL COMMENT '操作时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_order_no` (`order_no`),
  KEY `idx_customer_id` (`customer_id`),
  KEY `idx_operate_time` (`operate_time`)
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='退加货操作日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bus_sales_log`
--

LOCK TABLES `bus_sales_log` WRITE;
/*!40000 ALTER TABLE `bus_sales_log` DISABLE KEYS */;
INSERT INTO `bus_sales_log` VALUES (1,'SO17808154682492508',6,1,'sale',1,12.50,'超级管理员','2026-06-07 06:57:48','1'),(2,'SO17808154682492508',6,3,'sale',2,8.50,'超级管理员','2026-06-07 06:57:48','1'),(3,'SO17808154682492508',6,5,'sale',3,28.00,'超级管理员','2026-06-07 06:57:48','1'),(4,'SO17808154682492508',6,4,'add',1,10.00,'超级管理员','2026-06-07 06:58:18',NULL),(5,'SO17808154682492508',6,3,'return',2,8.50,'超级管理员','2026-06-07 06:58:45','单商品退货'),(6,'SO17808154682492508',6,5,'return',1,28.00,'超级管理员','2026-06-07 07:04:07','单商品退货（部分）'),(7,'SO17808154682492508',6,1,'return',1,12.50,'超级管理员','2026-06-07 07:04:20','整单退货'),(8,'SO17808154682492508',6,5,'return',2,28.00,'超级管理员','2026-06-07 07:04:20','整单退货'),(9,'SO17808154682492508',6,4,'return',1,10.00,'超级管理员','2026-06-07 07:04:20','整单退货'),(10,'SO17808146747053852',5,1,'return',1,12.50,'超级管理员','2026-06-07 07:09:01','整单退货'),(11,'SO17808129933448809',2,3,'return',1,8.50,'超级管理员','2026-06-07 07:09:22','单商品退货（全部）'),(12,'SO17808165429009926',4,1,'sale',1,12.50,'超级管理员','2026-06-07 07:15:43','ces'),(13,'SO17808165429009926',4,2,'sale',1,35.00,'超级管理员','2026-06-07 07:15:43','ces'),(14,'SO17808165429009926',4,3,'sale',1,8.50,'超级管理员','2026-06-07 07:15:43','ces'),(15,'SO17808165429009926',4,4,'sale',1,10.00,'超级管理员','2026-06-07 07:15:43','ces'),(16,'SO17808165429009926',4,5,'sale',1,28.00,'超级管理员','2026-06-07 07:15:43','ces'),(17,'SO17808165429009926',4,6,'sale',2,22.00,'超级管理员','2026-06-07 07:15:43','ces'),(18,'SO17808165429009926',4,1,'return',1,12.50,'超级管理员','2026-06-07 07:15:54','单商品退货（全部）'),(19,'SO17808165429009926',4,3,'add',2,8.50,'超级管理员','2026-06-07 07:16:26',NULL),(20,'SO17808165429009926',4,3,'add',1,8.50,'超级管理员','2026-06-07 07:25:55',NULL),(21,'SO17808165429009926',4,6,'return',1,22.00,'超级管理员','2026-06-07 07:35:05','单商品退货（部分）'),(22,'SO17808165429009926',4,3,'return',1,8.50,'超级管理员','2026-06-07 07:35:17','单商品退货（全部）'),(23,'SO17808165429009926',4,6,'return',1,22.00,'超级管理员','2026-06-07 07:35:17','单商品退货（全部）'),(24,'SO17808165429009926',4,3,'add',2,8.50,'超级管理员','2026-06-07 07:35:28',NULL),(25,'SO17808260704380591',2,11,'sale',1,58.00,'超级管理员','2026-06-07 09:54:30',''),(26,'SO17808260704380591',2,5,'sale',1,28.00,'超级管理员','2026-06-07 09:54:30',''),(27,'SO17808261707830087',3,11,'sale',2,58.00,'超级管理员','2026-06-07 09:56:11',''),(28,'SO17808261707830087',3,16,'sale',1,48.00,'超级管理员','2026-06-07 09:56:11',''),(29,'SO17808261707830087',3,5,'sale',1,28.00,'超级管理员','2026-06-07 09:56:11',''),(30,'SO17808261707830087',3,1,'sale',1,12.50,'超级管理员','2026-06-07 09:56:11',''),(31,'SO17808263236519933',1,11,'sale',2,58.00,'超级管理员','2026-06-07 09:58:44',''),(32,'SO17808263236519933',1,16,'sale',1,48.00,'超级管理员','2026-06-07 09:58:44',''),(33,'SO17808263920488655',5,5,'sale',1,28.00,'超级管理员','2026-06-07 09:59:52',''),(34,'SO17808263920488655',5,11,'sale',2,58.00,'超级管理员','2026-06-07 09:59:52',''),(35,'SO17808265460644308',7,1,'sale',1,12.50,'超级管理员','2026-06-07 10:02:26',''),(36,'SO17808265460644308',7,16,'sale',2,48.00,'超级管理员','2026-06-07 10:02:26',''),(37,'SO17808265460644308',7,3,'sale',1,8.50,'超级管理员','2026-06-07 10:02:26',''),(38,'SO17808446500282855',4,1,'sale',1,12.50,'超级管理员','2026-06-07 15:04:10',''),(39,'SO17808446500282855',4,5,'sale',1,28.00,'超级管理员','2026-06-07 15:04:10',''),(40,'SO17808446500282855',4,3,'sale',1,8.50,'超级管理员','2026-06-07 15:04:10',''),(41,'SO17808476232355252',4,1,'sale',4,12.50,'超级管理员','2026-06-07 15:53:43','1'),(42,'SO17808476232355252',4,5,'sale',3,28.00,'超级管理员','2026-06-07 15:53:43','1'),(43,'SO17808476232355252',4,3,'sale',2,8.50,'超级管理员','2026-06-07 15:53:43','1'),(44,'SO17808476232355252',4,11,'sale',1,58.00,'超级管理员','2026-06-07 15:53:43','1');
/*!40000 ALTER TABLE `bus_sales_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bus_salesback`
--

DROP TABLE IF EXISTS `bus_salesback`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bus_salesback` (
  `id` int NOT NULL AUTO_INCREMENT,
  `customerid` int DEFAULT NULL,
  `paytype` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `salesbacktime` datetime DEFAULT NULL,
  `salebackprice` double(10,2) DEFAULT NULL,
  `operateperson` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `number` int DEFAULT NULL,
  `remark` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `goodsid` int DEFAULT NULL,
  `salesid` int DEFAULT NULL,
  `isdelete` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bus_salesback`
--

LOCK TABLES `bus_salesback` WRITE;
/*!40000 ALTER TABLE `bus_salesback` DISABLE KEYS */;
INSERT INTO `bus_salesback` VALUES (1,5,'银联','2026-03-10 11:00:00',12.50,'落亦-',1,'雪饼口味不对',1,7,1),(2,6,'支付宝','2026-03-15 14:30:00',35.00,'李月素',1,'大礼包包装破损',2,10,0),(3,9,'微信','2026-03-22 10:00:00',58.00,'赵六',1,'蒙牛纯牛奶胀包',11,19,0),(4,7,'银联','2026-04-05 16:00:00',35.00,'超级管理员',1,'大礼包临期退回',2,15,0),(5,8,'支付宝','2026-04-12 11:30:00',28.00,'落亦-',1,'AD钙奶变质',5,9,1),(6,10,'微信','2026-04-18 14:00:00',12.50,'李月素',1,'雪饼碎了',1,21,0),(7,11,'银联','2026-04-25 09:30:00',48.00,'赵六',1,'可口可乐漏气',16,23,0),(8,5,'支付宝','2026-05-05 10:00:00',35.00,'超级管理员',1,'大礼包少件',2,42,0),(9,6,'微信','2026-05-10 15:00:00',22.00,'落亦-',1,'纯净水有异味',6,31,0),(10,9,'银联','2026-05-12 11:00:00',56.00,'李月素',1,'伊利纯牛奶过期',13,51,0),(11,12,'支付宝','2026-05-15 14:30:00',12.50,'赵六',1,'方便面碎了',19,25,0),(14,5,NULL,'2026-05-25 08:48:03',100.00,'超级管理员',20,'',14,125,0);
/*!40000 ALTER TABLE `bus_salesback` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bus_stocktake`
--

DROP TABLE IF EXISTS `bus_stocktake`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bus_stocktake` (
  `id` int NOT NULL AUTO_INCREMENT,
  `stocktake_no` varchar(50) DEFAULT NULL,
  `status` int DEFAULT '0',
  `operator` varchar(50) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `finish_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bus_stocktake`
--

LOCK TABLES `bus_stocktake` WRITE;
/*!40000 ALTER TABLE `bus_stocktake` DISABLE KEYS */;
INSERT INTO `bus_stocktake` VALUES (1,'ST20260606091742',2,'超级管理员','测试','2026-06-06 01:17:42',NULL),(2,'ST20260606112120',1,'超级管理员','','2026-06-06 03:21:21','2026-06-06 03:24:22'),(3,'ST20260606113058',2,'超级管理员','','2026-06-06 03:30:59',NULL);
/*!40000 ALTER TABLE `bus_stocktake` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bus_stocktake_item`
--

DROP TABLE IF EXISTS `bus_stocktake_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bus_stocktake_item` (
  `id` int NOT NULL AUTO_INCREMENT,
  `stocktake_id` int DEFAULT NULL,
  `goodsid` int DEFAULT NULL,
  `system_num` int DEFAULT NULL,
  `actual_num` int DEFAULT NULL,
  `diff_num` int DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=79 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bus_stocktake_item`
--

LOCK TABLES `bus_stocktake_item` WRITE;
/*!40000 ALTER TABLE `bus_stocktake_item` DISABLE KEYS */;
INSERT INTO `bus_stocktake_item` VALUES (1,1,1,71,70,-1,NULL),(2,1,2,33,NULL,NULL,NULL),(3,1,3,435,NULL,NULL,NULL),(4,1,4,274,NULL,NULL,NULL),(5,1,5,521,NULL,NULL,NULL),(6,1,6,1031,NULL,NULL,NULL),(7,1,7,302,NULL,NULL,NULL),(8,1,8,368,NULL,NULL,NULL),(9,1,9,220,NULL,NULL,NULL),(10,1,10,260,NULL,NULL,NULL),(11,1,11,466,NULL,NULL,NULL),(12,1,12,449,NULL,NULL,NULL),(13,1,13,526,NULL,NULL,NULL),(14,1,14,375,NULL,NULL,NULL),(15,1,15,150,NULL,NULL,NULL),(16,1,16,708,NULL,NULL,NULL),(17,1,17,600,NULL,NULL,NULL),(18,1,18,495,NULL,NULL,NULL),(19,1,19,1046,NULL,NULL,NULL),(20,1,20,690,NULL,NULL,NULL),(21,1,21,440,NULL,NULL,NULL),(22,1,22,1170,NULL,NULL,NULL),(23,1,23,427,NULL,NULL,NULL),(24,1,24,250,NULL,NULL,NULL),(25,1,25,250,NULL,NULL,NULL),(26,1,27,20,NULL,NULL,NULL),(27,2,1,70,70,0,NULL),(28,2,2,33,33,0,NULL),(29,2,3,335,300,-35,NULL),(30,2,4,274,280,6,NULL),(31,2,5,520,520,0,NULL),(32,2,6,1031,1031,0,NULL),(33,2,7,302,302,0,NULL),(34,2,8,368,368,0,NULL),(35,2,9,220,220,0,NULL),(36,2,10,260,260,0,NULL),(37,2,11,466,466,0,NULL),(38,2,12,449,449,0,NULL),(39,2,13,526,526,0,NULL),(40,2,14,375,375,0,NULL),(41,2,15,150,150,0,NULL),(42,2,16,708,708,0,NULL),(43,2,17,600,600,0,NULL),(44,2,18,495,495,0,NULL),(45,2,19,1046,1046,0,NULL),(46,2,20,690,690,0,NULL),(47,2,21,440,440,0,NULL),(48,2,22,1170,1170,0,NULL),(49,2,23,427,427,0,NULL),(50,2,24,250,250,0,NULL),(51,2,25,250,250,0,NULL),(52,2,27,20,20,0,NULL),(53,3,1,70,NULL,NULL,NULL),(54,3,2,33,NULL,NULL,NULL),(55,3,3,300,NULL,NULL,NULL),(56,3,4,280,NULL,NULL,NULL),(57,3,5,520,NULL,NULL,NULL),(58,3,6,1031,NULL,NULL,NULL),(59,3,7,302,NULL,NULL,NULL),(60,3,8,368,NULL,NULL,NULL),(61,3,9,220,NULL,NULL,NULL),(62,3,10,260,NULL,NULL,NULL),(63,3,11,466,NULL,NULL,NULL),(64,3,12,449,NULL,NULL,NULL),(65,3,13,526,NULL,NULL,NULL),(66,3,14,375,NULL,NULL,NULL),(67,3,15,150,NULL,NULL,NULL),(68,3,16,708,NULL,NULL,NULL),(69,3,17,600,NULL,NULL,NULL),(70,3,18,495,NULL,NULL,NULL),(71,3,19,1046,NULL,NULL,NULL),(72,3,20,690,NULL,NULL,NULL),(73,3,21,440,NULL,NULL,NULL),(74,3,22,1170,NULL,NULL,NULL),(75,3,23,427,NULL,NULL,NULL),(76,3,24,250,NULL,NULL,NULL),(77,3,25,250,NULL,NULL,NULL),(78,3,27,20,NULL,NULL,NULL);
/*!40000 ALTER TABLE `bus_stocktake_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_dept`
--

DROP TABLE IF EXISTS `sys_dept`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_dept` (
  `id` int NOT NULL AUTO_INCREMENT,
  `pid` int DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `open` int DEFAULT NULL,
  `remark` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `address` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `available` int DEFAULT NULL COMMENT '状态【0不可用1可用】',
  `ordernum` int DEFAULT NULL COMMENT '排序码【为了调试显示顺序】',
  `createtime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_dept`
--

LOCK TABLES `sys_dept` WRITE;
/*!40000 ALTER TABLE `sys_dept` DISABLE KEYS */;
INSERT INTO `sys_dept` VALUES (1,0,'总经办',1,'大BOSS','深圳',1,1,'2019-04-10 14:06:32'),(2,1,'销售部',0,'程序员','武汉',1,2,'2019-04-10 14:06:32'),(3,1,'运营部',0,'无','武汉',1,3,'2019-04-10 14:06:32'),(4,1,'生产部',0,'无','武汉',1,4,'2019-04-10 14:06:32'),(5,2,'销售一部',0,'销售一部','武汉',1,5,'2019-04-10 14:06:32'),(6,2,'销售二部',0,'销售二部','武汉',1,6,'2019-04-10 14:06:32'),(7,3,'运营一部',0,'运营一部','武汉',1,7,'2019-04-10 14:06:32'),(8,2,'销售三部',0,'销售三部','11',1,8,'2019-04-10 14:06:32'),(9,2,'销售四部',0,'销售四部','222',1,9,'2019-04-10 14:06:32'),(10,2,'销售五部',0,'销售五部','33',1,10,'2019-04-10 14:06:32'),(18,4,'生产一部',0,'生产食品','武汉',1,11,'2019-04-13 09:49:38');
/*!40000 ALTER TABLE `sys_dept` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_loginfo`
--

DROP TABLE IF EXISTS `sys_loginfo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_loginfo` (
  `id` int NOT NULL AUTO_INCREMENT,
  `loginname` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `loginip` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `logintime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=244 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_loginfo`
--

LOCK TABLES `sys_loginfo` WRITE;
/*!40000 ALTER TABLE `sys_loginfo` DISABLE KEYS */;
INSERT INTO `sys_loginfo` VALUES (1,'超级管理员-system','0:0:0:0:0:0:0:1','2026-03-02 08:25:00'),(2,'落亦--luoyi','0:0:0:0:0:0:0:1','2026-03-02 08:30:00'),(3,'李月素-liyuesu','0:0:0:0:0:0:0:1','2026-03-02 08:35:00'),(4,'赵六-zhaoliu','0:0:0:0:0:0:0:1','2026-03-02 08:40:00'),(5,'超级管理员-system','0:0:0:0:0:0:0:1','2026-03-03 08:28:00'),(6,'落亦--luoyi','0:0:0:0:0:0:0:1','2026-03-03 08:32:00'),(7,'李月素-liyuesu','0:0:0:0:0:0:0:1','2026-03-03 08:38:00'),(8,'超级管理员-system','0:0:0:0:0:0:0:1','2026-03-04 08:20:00'),(9,'落亦--luoyi','0:0:0:0:0:0:0:1','2026-03-04 08:35:00'),(10,'李月素-liyuesu','0:0:0:0:0:0:0:1','2026-03-04 08:40:00'),(11,'超级管理员-system','0:0:0:0:0:0:0:1','2026-03-05 08:15:00'),(12,'落亦--luoyi','0:0:0:0:0:0:0:1','2026-03-05 08:30:00'),(13,'赵六-zhaoliu','0:0:0:0:0:0:0:1','2026-03-05 08:42:00'),(14,'超级管理员-system','0:0:0:0:0:0:0:1','2026-03-06 08:22:00'),(15,'落亦--luoyi','0:0:0:0:0:0:0:1','2026-03-06 08:35:00'),(16,'超级管理员-system','0:0:0:0:0:0:0:1','2026-03-07 08:18:00'),(17,'李月素-liyuesu','0:0:0:0:0:0:0:1','2026-03-07 08:30:00'),(18,'赵六-zhaoliu','0:0:0:0:0:0:0:1','2026-03-07 08:38:00'),(19,'超级管理员-system','0:0:0:0:0:0:0:1','2026-03-08 08:25:00'),(20,'落亦--luoyi','0:0:0:0:0:0:0:1','2026-03-08 08:35:00'),(21,'超级管理员-system','0:0:0:0:0:0:0:1','2026-03-10 08:20:00'),(22,'落亦--luoyi','0:0:0:0:0:0:0:1','2026-03-10 08:30:00'),(23,'赵六-zhaoliu','0:0:0:0:0:0:0:1','2026-03-10 08:40:00'),(24,'超级管理员-system','0:0:0:0:0:0:0:1','2026-03-11 08:22:00'),(25,'落亦--luoyi','0:0:0:0:0:0:0:1','2026-03-11 08:28:00'),(26,'李月素-liyuesu','0:0:0:0:0:0:0:1','2026-03-11 08:35:00'),(27,'超级管理员-system','0:0:0:0:0:0:0:1','2026-03-12 08:18:00'),(28,'落亦--luoyi','0:0:0:0:0:0:0:1','2026-03-12 08:30:00'),(29,'李月素-liyuesu','0:0:0:0:0:0:0:1','2026-03-12 08:42:00'),(30,'超级管理员-system','0:0:0:0:0:0:0:1','2026-03-13 08:25:00'),(31,'赵六-zhaoliu','0:0:0:0:0:0:0:1','2026-03-13 08:35:00'),(32,'超级管理员-system','0:0:0:0:0:0:0:1','2026-03-14 08:20:00'),(33,'落亦--luoyi','0:0:0:0:0:0:0:1','2026-03-14 08:30:00'),(34,'赵六-zhaoliu','0:0:0:0:0:0:0:1','2026-03-14 08:38:00'),(35,'超级管理员-system','0:0:0:0:0:0:0:1','2026-03-15 08:22:00'),(36,'落亦--luoyi','0:0:0:0:0:0:0:1','2026-03-15 08:35:00'),(37,'超级管理员-system','0:0:0:0:0:0:0:1','2026-03-17 08:18:00'),(38,'李月素-liyuesu','0:0:0:0:0:0:0:1','2026-03-17 08:30:00'),(39,'超级管理员-system','0:0:0:0:0:0:0:1','2026-03-18 08:20:00'),(40,'李月素-liyuesu','0:0:0:0:0:0:0:1','2026-03-18 08:32:00'),(41,'超级管理员-system','0:0:0:0:0:0:0:1','2026-03-19 08:22:00'),(42,'落亦--luoyi','0:0:0:0:0:0:0:1','2026-03-19 08:30:00'),(43,'超级管理员-system','0:0:0:0:0:0:0:1','2026-03-20 08:18:00'),(44,'超级管理员-system','0:0:0:0:0:0:0:1','2026-03-20 14:30:00'),(45,'超级管理员-system','0:0:0:0:0:0:0:1','2026-03-21 08:20:00'),(46,'赵六-zhaoliu','0:0:0:0:0:0:0:1','2026-03-21 08:35:00'),(47,'超级管理员-system','0:0:0:0:0:0:0:1','2026-03-22 08:22:00'),(48,'超级管理员-system','0:0:0:0:0:0:0:1','2026-03-23 08:18:00'),(49,'落亦--luoyi','0:0:0:0:0:0:0:1','2026-03-23 08:30:00'),(50,'超级管理员-system','0:0:0:0:0:0:0:1','2026-03-25 08:20:00'),(51,'落亦--luoyi','0:0:0:0:0:0:0:1','2026-03-25 08:28:00'),(52,'李月素-liyuesu','0:0:0:0:0:0:0:1','2026-03-25 08:35:00'),(53,'超级管理员-system','0:0:0:0:0:0:0:1','2026-03-27 08:22:00'),(54,'赵六-zhaoliu','0:0:0:0:0:0:0:1','2026-03-27 08:30:00'),(55,'超级管理员-system','0:0:0:0:0:0:0:1','2026-03-28 08:18:00'),(56,'赵六-zhaoliu','0:0:0:0:0:0:0:1','2026-03-28 08:32:00'),(57,'超级管理员-system','0:0:0:0:0:0:0:1','2026-03-29 08:20:00'),(58,'落亦--luoyi','0:0:0:0:0:0:0:1','2026-03-29 08:30:00'),(59,'超级管理员-system','0:0:0:0:0:0:0:1','2026-03-31 08:22:00'),(60,'李月素-liyuesu','0:0:0:0:0:0:0:1','2026-03-31 08:35:00'),(61,'超级管理员-system','0:0:0:0:0:0:0:1','2026-04-01 08:15:00'),(62,'落亦--luoyi','0:0:0:0:0:0:0:1','2026-04-01 08:28:00'),(63,'李月素-liyuesu','0:0:0:0:0:0:0:1','2026-04-01 08:35:00'),(64,'赵六-zhaoliu','0:0:0:0:0:0:0:1','2026-04-01 08:42:00'),(65,'超级管理员-system','0:0:0:0:0:0:0:1','2026-04-02 08:20:00'),(66,'落亦--luoyi','0:0:0:0:0:0:0:1','2026-04-02 08:30:00'),(67,'李月素-liyuesu','0:0:0:0:0:0:0:1','2026-04-02 08:38:00'),(68,'超级管理员-system','0:0:0:0:0:0:0:1','2026-04-03 08:18:00'),(69,'李月素-liyuesu','0:0:0:0:0:0:0:1','2026-04-03 08:30:00'),(70,'超级管理员-system','0:0:0:0:0:0:0:1','2026-04-05 08:15:00'),(71,'超级管理员-system','0:0:0:0:0:0:0:1','2026-04-05 09:00:00'),(72,'落亦--luoyi','0:0:0:0:0:0:0:1','2026-04-05 08:30:00'),(73,'赵六-zhaoliu','0:0:0:0:0:0:0:1','2026-04-05 08:38:00'),(74,'超级管理员-system','0:0:0:0:0:0:0:1','2026-04-06 08:22:00'),(75,'落亦--luoyi','0:0:0:0:0:0:0:1','2026-04-06 08:28:00'),(76,'超级管理员-system','0:0:0:0:0:0:0:1','2026-04-07 08:18:00'),(77,'落亦--luoyi','0:0:0:0:0:0:0:1','2026-04-07 08:30:00'),(78,'李月素-liyuesu','0:0:0:0:0:0:0:1','2026-04-07 08:38:00'),(79,'超级管理员-system','0:0:0:0:0:0:0:1','2026-04-08 08:20:00'),(80,'赵六-zhaoliu','0:0:0:0:0:0:0:1','2026-04-08 08:32:00'),(81,'超级管理员-system','0:0:0:0:0:0:0:1','2026-04-09 08:22:00'),(82,'落亦--luoyi','0:0:0:0:0:0:0:1','2026-04-09 08:30:00'),(83,'超级管理员-system','0:0:0:0:0:0:0:1','2026-04-10 08:18:00'),(84,'李月素-liyuesu','0:0:0:0:0:0:0:1','2026-04-10 08:30:00'),(85,'超级管理员-system','0:0:0:0:0:0:0:1','2026-04-12 08:20:00'),(86,'赵六-zhaoliu','0:0:0:0:0:0:0:1','2026-04-12 08:35:00'),(87,'超级管理员-system','0:0:0:0:0:0:0:1','2026-04-14 08:18:00'),(88,'落亦--luoyi','0:0:0:0:0:0:0:1','2026-04-14 08:30:00'),(89,'超级管理员-system','0:0:0:0:0:0:0:1','2026-04-15 08:22:00'),(90,'落亦--luoyi','0:0:0:0:0:0:0:1','2026-04-15 08:28:00'),(91,'超级管理员-system','0:0:0:0:0:0:0:1','2026-04-16 08:20:00'),(92,'李月素-liyuesu','0:0:0:0:0:0:0:1','2026-04-16 08:35:00'),(93,'超级管理员-system','0:0:0:0:0:0:0:1','2026-04-18 08:18:00'),(94,'赵六-zhaoliu','0:0:0:0:0:0:0:1','2026-04-18 08:30:00'),(95,'超级管理员-system','0:0:0:0:0:0:0:1','2026-04-20 08:22:00'),(96,'落亦--luoyi','0:0:0:0:0:0:0:1','2026-04-20 08:30:00'),(97,'超级管理员-system','0:0:0:0:0:0:0:1','2026-04-22 08:18:00'),(98,'李月素-liyuesu','0:0:0:0:0:0:0:1','2026-04-22 08:32:00'),(99,'超级管理员-system','0:0:0:0:0:0:0:1','2026-04-24 08:20:00'),(100,'赵六-zhaoliu','0:0:0:0:0:0:0:1','2026-04-24 08:35:00'),(101,'超级管理员-system','0:0:0:0:0:0:0:1','2026-04-26 08:22:00'),(102,'落亦--luoyi','0:0:0:0:0:0:0:1','2026-04-26 08:30:00'),(103,'超级管理员-system','0:0:0:0:0:0:0:1','2026-04-28 08:18:00'),(104,'李月素-liyuesu','0:0:0:0:0:0:0:1','2026-04-28 08:30:00'),(105,'超级管理员-system','0:0:0:0:0:0:0:1','2026-04-30 08:20:00'),(106,'赵六-zhaoliu','0:0:0:0:0:0:0:1','2026-04-30 08:35:00'),(107,'超级管理员-system','0:0:0:0:0:0:0:1','2026-05-02 08:15:00'),(108,'落亦--luoyi','0:0:0:0:0:0:0:1','2026-05-02 08:25:00'),(109,'李月素-liyuesu','0:0:0:0:0:0:0:1','2026-05-02 08:35:00'),(110,'赵六-zhaoliu','0:0:0:0:0:0:0:1','2026-05-02 08:42:00'),(111,'超级管理员-system','0:0:0:0:0:0:0:1','2026-05-03 08:20:00'),(112,'落亦--luoyi','0:0:0:0:0:0:0:1','2026-05-03 08:30:00'),(113,'超级管理员-system','0:0:0:0:0:0:0:1','2026-05-04 08:18:00'),(114,'李月素-liyuesu','0:0:0:0:0:0:0:1','2026-05-04 08:30:00'),(115,'超级管理员-system','0:0:0:0:0:0:0:1','2026-05-05 08:15:00'),(116,'超级管理员-system','0:0:0:0:0:0:0:1','2026-05-05 08:50:00'),(117,'落亦--luoyi','0:0:0:0:0:0:0:1','2026-05-05 08:28:00'),(118,'赵六-zhaoliu','0:0:0:0:0:0:0:1','2026-05-05 08:38:00'),(119,'超级管理员-system','0:0:0:0:0:0:0:1','2026-05-06 08:22:00'),(120,'落亦--luoyi','0:0:0:0:0:0:0:1','2026-05-06 08:30:00'),(121,'超级管理员-system','0:0:0:0:0:0:0:1','2026-05-07 08:18:00'),(122,'赵六-zhaoliu','0:0:0:0:0:0:0:1','2026-05-07 08:32:00'),(123,'超级管理员-system','0:0:0:0:0:0:0:1','2026-05-08 08:20:00'),(124,'超级管理员-system','0:0:0:0:0:0:0:1','2026-05-08 09:10:00'),(125,'落亦--luoyi','0:0:0:0:0:0:0:1','2026-05-08 08:28:00'),(126,'超级管理员-system','0:0:0:0:0:0:0:1','2026-05-09 08:22:00'),(127,'落亦--luoyi','0:0:0:0:0:0:0:1','2026-05-09 08:30:00'),(128,'超级管理员-system','0:0:0:0:0:0:0:1','2026-05-10 08:18:00'),(129,'李月素-liyuesu','0:0:0:0:0:0:0:1','2026-05-10 08:30:00'),(130,'超级管理员-system','0:0:0:0:0:0:0:1','2026-05-11 08:20:00'),(131,'赵六-zhaoliu','0:0:0:0:0:0:0:1','2026-05-11 08:35:00'),(132,'超级管理员-system','0:0:0:0:0:0:0:1','2026-05-13 08:18:00'),(133,'落亦--luoyi','0:0:0:0:0:0:0:1','2026-05-13 08:28:00'),(134,'超级管理员-system','0:0:0:0:0:0:0:1','2026-05-14 08:22:00'),(135,'李月素-liyuesu','0:0:0:0:0:0:0:1','2026-05-14 08:35:00'),(136,'超级管理员-system','0:0:0:0:0:0:0:1','2026-05-15 08:20:00'),(137,'落亦--luoyi','0:0:0:0:0:0:0:1','2026-05-15 08:30:00'),(138,'超级管理员-system','0:0:0:0:0:0:0:1','2026-05-16 08:18:00'),(139,'落亦--luoyi','0:0:0:0:0:0:0:1','2026-05-16 08:28:00'),(140,'超级管理员-system','0:0:0:0:0:0:0:1','2026-05-17 08:22:00'),(141,'李月素-liyuesu','0:0:0:0:0:0:0:1','2026-05-17 08:35:00'),(142,'超级管理员-system','0:0:0:0:0:0:0:1','2026-05-18 08:20:00'),(143,'赵六-zhaoliu','0:0:0:0:0:0:0:1','2026-05-18 08:30:00'),(144,'超级管理员-system','0:0:0:0:0:0:0:1','2026-05-19 08:18:00'),(145,'落亦--luoyi','0:0:0:0:0:0:0:1','2026-05-19 08:28:00'),(146,'李月素-liyuesu','0:0:0:0:0:0:0:1','2026-05-19 08:35:00'),(147,'赵六-zhaoliu','0:0:0:0:0:0:0:1','2026-05-19 08:42:00'),(148,'超级管理员-system','0:0:0:0:0:0:0:1','2026-05-20 08:15:00'),(149,'落亦--luoyi','0:0:0:0:0:0:0:1','2026-05-20 08:25:00'),(150,'李月素-liyuesu','0:0:0:0:0:0:0:1','2026-05-20 08:35:00'),(151,'超级管理员-system','0:0:0:0:0:0:0:1','2026-05-21 07:15:25'),(152,'超级管理员-system','0:0:0:0:0:0:0:1','2026-05-21 07:39:32'),(153,'超级管理员-system','0:0:0:0:0:0:0:1','2026-05-21 08:02:46'),(154,'超级管理员-system','0:0:0:0:0:0:0:1','2026-05-21 08:35:26'),(155,'超级管理员-system','0:0:0:0:0:0:0:1','2026-05-21 08:47:17'),(156,'超级管理员-system','0:0:0:0:0:0:0:1','2026-05-22 04:29:50'),(157,'超级管理员-system','0:0:0:0:0:0:0:1','2026-05-22 07:12:01'),(158,'超级管理员-system','0:0:0:0:0:0:0:1','2026-05-22 07:12:51'),(159,'超级管理员-system','0:0:0:0:0:0:0:1','2026-05-22 07:36:29'),(160,'超级管理员-system','0:0:0:0:0:0:0:1','2026-05-22 09:20:58'),(161,'超级管理员-system','0:0:0:0:0:0:0:1','2026-05-25 00:23:38'),(162,'超级管理员-system','0:0:0:0:0:0:0:1','2026-05-25 02:03:06'),(163,'超级管理员-system','0:0:0:0:0:0:0:1','2026-05-25 03:13:08'),(164,'超级管理员-system','0:0:0:0:0:0:0:1','2026-05-25 03:49:13'),(165,'超级管理员-system','0:0:0:0:0:0:0:1','2026-05-25 06:35:44'),(166,'超级管理员-system','0:0:0:0:0:0:0:1','2026-05-25 06:53:01'),(167,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-05-25 06:58:09'),(168,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-05-25 07:43:14'),(169,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-05-25 08:09:08'),(170,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-05-25 08:26:31'),(171,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-05-25 08:35:35'),(172,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-05-25 08:46:01'),(173,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-05-25 09:05:54'),(174,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-05-25 09:36:35'),(175,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-05-25 09:41:50'),(176,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-05-25 10:06:01'),(177,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-05-26 04:47:30'),(178,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-05-27 04:57:48'),(179,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-06-06 00:52:50'),(180,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-06-06 01:15:57'),(181,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-06-06 01:18:26'),(182,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-06-06 01:21:47'),(183,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-06-06 01:25:51'),(184,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-06-06 01:30:50'),(185,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-06-06 01:37:36'),(186,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-06-06 01:49:25'),(187,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-06-06 02:30:01'),(188,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-06-06 02:37:33'),(189,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-06-06 03:30:32'),(190,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-06-06 03:34:18'),(191,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-06-06 04:03:36'),(192,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-06-06 05:02:02'),(193,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-06-06 14:09:57'),(194,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-06-06 14:10:42'),(195,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-06-06 14:28:08'),(196,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-06-06 14:34:05'),(197,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-06-06 14:39:51'),(198,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-06-06 15:16:32'),(199,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-06-06 15:39:19'),(200,'仓库管理员-warehouse','0:0:0:0:0:0:0:1','2026-06-06 15:51:45'),(201,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-06-06 15:51:54'),(202,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-06-06 16:05:22'),(203,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-06-06 16:10:02'),(204,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-06-06 16:16:44'),(205,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-06-06 16:18:27'),(206,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-06-06 23:58:37'),(207,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-06-07 01:46:24'),(208,'仓库管理员-warehouse','0:0:0:0:0:0:0:1','2026-06-07 02:23:15'),(209,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-06-07 03:01:26'),(210,'采购员-purchase','0:0:0:0:0:0:0:1','2026-06-07 03:03:48'),(211,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-06-07 03:04:46'),(212,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-06-07 03:05:11'),(213,'超级管理员-admin','172.20.10.1','2026-06-07 03:15:33'),(214,'超级管理员-admin','172.20.10.1','2026-06-07 03:27:23'),(215,'超级管理员-admin','172.20.10.1','2026-06-07 03:32:33'),(216,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-06-07 03:42:44'),(217,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-06-07 05:00:05'),(218,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-06-07 05:07:04'),(219,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-06-07 05:56:17'),(220,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-06-07 06:16:10'),(221,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-06-07 06:27:10'),(222,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-06-07 06:31:29'),(223,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-06-07 06:41:45'),(224,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-06-07 06:49:24'),(225,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-06-07 06:56:58'),(226,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-06-07 07:03:26'),(227,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-06-07 07:15:13'),(228,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-06-07 07:44:21'),(229,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-06-07 08:03:25'),(230,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-06-07 08:09:04'),(231,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-06-07 08:21:19'),(232,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-06-07 08:33:59'),(233,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-06-07 08:35:02'),(234,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-06-07 08:56:09'),(235,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-06-07 09:44:16'),(236,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-06-07 09:47:16'),(237,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-06-07 09:59:41'),(238,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-06-07 10:02:17'),(239,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-06-07 15:11:51'),(240,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-06-07 15:21:40'),(241,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-06-07 15:33:59'),(242,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-06-07 15:35:28'),(243,'超级管理员-admin','0:0:0:0:0:0:0:1','2026-06-07 15:37:12');
/*!40000 ALTER TABLE `sys_loginfo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_notice`
--

DROP TABLE IF EXISTS `sys_notice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_notice` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `content` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci,
  `createtime` datetime DEFAULT NULL,
  `opername` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_notice`
--

LOCK TABLES `sys_notice` WRITE;
/*!40000 ALTER TABLE `sys_notice` DISABLE KEYS */;
INSERT INTO `sys_notice` VALUES (1,'仓库管理系统上线通知','各位同事，仓库管理系统已于2026年3月1日正式上线，请各部门及时登录系统完成日常业务操作。如有问题请联系系统管理员。','2026-03-01 09:00:00','超级管理员'),(2,'3月份盘点安排','定于2026年3月15日（周日）进行仓库月度盘点，请各部门提前做好准备，确保库存数据准确。盘点期间暂停出入库操作。','2026-03-10 14:00:00','超级管理员'),(3,'供应商结算通知','3月份供应商对账单已生成，请各采购负责人在3月25日前完成对账确认。如有差异请及时联系财务部。','2026-03-20 10:00:00','落亦-'),(4,'清明节放假安排','清明节放假时间：4月4日至4月6日，共3天。放假期间仓库暂停收发货，请各部门提前安排好工作。','2026-04-01 09:00:00','超级管理员'),(5,'4月份新品上架通知','新增商品：红牛功能饮料（250ML*24罐/箱），供应商：农夫山泉股份，已入库上架，请销售部门关注。','2026-04-08 14:00:00','落亦-'),(6,'系统维护通知','系统将于4月12日（周日）22:00-次日02:00进行例行维护，届时系统将暂停服务，请各部门合理安排工作时间。','2026-04-10 10:00:00','超级管理员'),(7,'五一劳动节放假安排','五一劳动节放假时间：5月1日至5月5日，共5天。放假期间仓库安排值班人员，紧急情况请联系值班经理。','2026-04-28 09:00:00','超级管理员'),(8,'5月份促销活动备货通知','5月份将开展\"夏日清凉\"促销活动，涉及饮料、冷饮类商品，请采购部门提前备货，确保库存充足。','2026-04-30 14:00:00','落亦-'),(9,'库存预警提醒','以下商品库存已低于安全库存线：旺旺大礼包（320件/安全线50件）、伊利金典（280件/安全线40件），请及时补货。','2026-05-10 10:00:00','超级管理员'),(10,'5月份供应商结算通知','5月份供应商对账单已生成，请各采购负责人在5月25日前完成对账确认。本月进货量较大，请仔细核对。','2026-05-18 14:00:00','落亦-'),(11,'放假','放假放假放假放假放假放假放假放假放假放假放假放假','2026-06-06 03:34:50','超级管理员');
/*!40000 ALTER TABLE `sys_notice` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_permission`
--

DROP TABLE IF EXISTS `sys_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_permission` (
  `id` int NOT NULL AUTO_INCREMENT,
  `pid` int DEFAULT NULL,
  `type` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '权限类型[menu/permission]',
  `title` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `percode` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '权限编码[只有type= permission才有  user:view]',
  `icon` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `href` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `target` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `open` int DEFAULT NULL,
  `ordernum` int DEFAULT NULL,
  `available` int DEFAULT NULL COMMENT '状态【0不可用1可用】',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=166 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_permission`
--

LOCK TABLES `sys_permission` WRITE;
/*!40000 ALTER TABLE `sys_permission` DISABLE KEYS */;
INSERT INTO `sys_permission` VALUES (1,0,'menu','仓库管理系统',NULL,'Box','','',1,1,1),(3,1,'menu','进货管理',NULL,'ShoppingBag','',NULL,0,2,1),(4,1,'menu','销售管理',NULL,'ShoppingCart','','',0,3,1),(5,1,'menu','系统管理',NULL,'Setting','','',1,99,1),(7,151,'menu','客户管理',NULL,'User','/bus/toCustomerManager','',0,4,1),(8,151,'menu','供应商管理',NULL,'Van','/bus/toProviderManager','',0,3,1),(9,151,'menu','商品管理',NULL,'Goods','/bus/toGoodsManager','',0,1,1),(10,3,'menu','商品进货',NULL,'Download','/bus/toInportManager','',0,10,0),(11,3,'menu','商品退货',NULL,'Upload','/bus/toOutportManager','',0,12,0),(12,4,'menu','销售记录',NULL,'Sell','/business/sales','',0,13,0),(13,4,'menu','商品退货',NULL,'RefreshLeft','/bus/toSalesbackManager','',0,14,0),(14,154,'menu','部门管理',NULL,'OfficeBuilding','/sys/toDeptManager','',0,3,1),(15,5,'menu','菜单管理',NULL,'Document','/sys/toMenuManager','',0,15,1),(16,5,'menu','权限管理','','Lock','/sys/toPermissionManager','',0,16,1),(17,154,'menu','角色管理','','UserFilled','/sys/toRoleManager','',0,2,1),(18,154,'menu','人员信息','','User','/sys/toUserManager','',0,1,1),(21,5,'menu','登陆日志',NULL,'Document','/sys/toLoginfoManager','',0,21,1),(22,5,'menu','系统公告',NULL,'Bell','/sys/toNoticeManager',NULL,0,22,1),(23,5,'menu','图标管理',NULL,'Picture','../resources/page/icon.html',NULL,0,23,1),(30,14,'permission','添加部门','dept:create','',NULL,NULL,0,24,1),(31,14,'permission','修改部门','dept:update','',NULL,NULL,0,26,1),(32,14,'permission','删除部门','dept:delete','',NULL,NULL,0,27,1),(34,15,'permission','添加菜单','menu:create','','','',0,29,1),(35,15,'permission','修改菜单','menu:update','',NULL,NULL,0,30,1),(36,15,'permission','删除菜单','menu:delete','',NULL,NULL,0,31,1),(38,16,'permission','添加权限','permission:create','',NULL,NULL,0,33,1),(39,16,'permission','修改权限','permission:update','',NULL,NULL,0,34,1),(40,16,'permission','删除权限','permission:delete','',NULL,NULL,0,35,1),(42,17,'permission','添加角色','role:create','',NULL,NULL,0,37,1),(43,17,'permission','修改角色','role:update','',NULL,NULL,0,38,1),(44,17,'permission','删除角色','role:delete','',NULL,NULL,0,39,1),(46,17,'permission','分配权限','role:selectPermission','',NULL,NULL,0,41,1),(47,18,'permission','添加用户','user:create','',NULL,NULL,0,42,1),(48,18,'permission','修改用户','user:update','',NULL,NULL,0,43,1),(49,18,'permission','删除用户','user:delete','',NULL,NULL,0,44,1),(51,18,'permission','用户分配角色','user:selectRole','',NULL,NULL,0,46,1),(52,18,'permission','重置密码','user:resetPwd',NULL,NULL,NULL,0,47,1),(53,14,'permission','部门查询','dept:view',NULL,NULL,NULL,0,48,1),(54,15,'permission','菜单查询','menu:view',NULL,NULL,NULL,0,49,1),(55,16,'permission','权限查询','permission:view',NULL,NULL,NULL,0,50,1),(56,17,'permission','角色查询','role:view',NULL,NULL,NULL,0,51,1),(57,18,'permission','用户查询','user:view',NULL,NULL,NULL,0,52,1),(68,7,'permission','客户查询','customer:view',NULL,NULL,NULL,NULL,60,1),(69,7,'permission','客户添加','customer:create',NULL,NULL,NULL,NULL,61,1),(70,7,'permission','客户修改','customer:update',NULL,NULL,NULL,NULL,62,1),(71,7,'permission','客户删除','customer:delete',NULL,NULL,NULL,NULL,63,1),(73,21,'permission','日志查询','info:view',NULL,NULL,NULL,NULL,65,1),(74,21,'permission','日志删除','info:delete',NULL,NULL,NULL,NULL,66,1),(75,21,'permission','日志批量删除','info:batchdelete',NULL,NULL,NULL,NULL,67,1),(76,22,'permission','公告查询','notice:view',NULL,NULL,NULL,NULL,68,1),(77,22,'permission','公告添加','notice:create',NULL,NULL,NULL,NULL,69,1),(78,22,'permission','公告修改','notice:update',NULL,NULL,NULL,NULL,70,1),(79,22,'permission','公告删除','notice:delete',NULL,NULL,NULL,NULL,71,1),(81,8,'permission','供应商查询','provider:view',NULL,NULL,NULL,NULL,73,1),(82,8,'permission','供应商添加','provider:create',NULL,NULL,NULL,NULL,74,1),(83,8,'permission','供应商修改','provider:update',NULL,NULL,NULL,NULL,75,1),(84,8,'permission','供应商删除','provider:delete',NULL,NULL,NULL,NULL,76,1),(86,22,'permission','公告查看','notice:viewnotice',NULL,NULL,NULL,NULL,78,1),(91,9,'permission','商品查询','goods:view',NULL,NULL,NULL,0,79,1),(92,9,'permission','商品添加','goods:create',NULL,NULL,NULL,0,80,1),(116,9,'permission','商品删除','goods:delete',NULL,NULL,NULL,0,84,1),(117,9,'permission','商品修改','goods:update',NULL,NULL,NULL,0,85,1),(118,9,'permission','商品查询','goods:view',NULL,NULL,NULL,0,86,1),(119,22,'permission','公告批量删除','notice:batchdelete',NULL,NULL,NULL,0,87,1),(125,1,'menu','报表统计',NULL,'DataAnalysis','',NULL,0,8,1),(126,125,'menu','进销金额分析',NULL,'Histogram','/bus/toInportAnalysis',NULL,0,92,1),(127,125,'menu','进销商品分析',NULL,'TrendCharts','/bus/toGoodsAnalysis',NULL,0,93,1),(128,125,'menu','利润分析',NULL,'TrendCharts','/bus/toProfitAnalysis',NULL,0,94,1),(130,151,'menu','商品分类',NULL,'Folder','/bus/toCategoryManager','',0,2,1),(132,5,'menu','操作日志',NULL,'Document','/system/operation-log',NULL,0,25,1),(133,1,'menu','零售管理',NULL,'ShoppingCart','',NULL,0,4,1),(134,133,'menu','散客零售',NULL,'Sell','/business/retail',NULL,0,10,1),(135,133,'menu','零售退货',NULL,'RefreshLeft','/business/retailback',NULL,0,13,1),(136,152,'menu','会员列表',NULL,'UserFilled','/business/member','',0,1,1),(137,136,'permission','会员查看','member:view',NULL,NULL,NULL,0,1,1),(138,136,'permission','会员添加','member:create',NULL,NULL,NULL,0,2,1),(139,136,'permission','会员修改','member:update',NULL,NULL,NULL,0,3,1),(140,136,'permission','会员删除','member:delete',NULL,NULL,NULL,0,4,1),(141,3,'menu','盘点管理',NULL,'Finished','/business/stocktake','',0,12,1),(142,141,'permission','盘点查看','stocktake:view',NULL,NULL,NULL,0,1,1),(143,141,'permission','盘点添加','stocktake:create',NULL,NULL,NULL,0,2,1),(144,141,'permission','盘点完成','stocktake:finish',NULL,NULL,NULL,0,3,1),(145,153,'menu','店员提成',NULL,'Money','/business/commission','',0,2,1),(146,145,'permission','提成查看','commission:view',NULL,NULL,NULL,0,1,1),(147,145,'permission','提成计算','commission:calculate',NULL,NULL,NULL,0,2,1),(148,153,'menu','业绩排名',NULL,'Trophy','/business/performance','',0,1,1),(149,148,'permission','排名查看','performance:view',NULL,NULL,NULL,0,1,1),(150,153,'menu','我的提成',NULL,'Coin','/business/my-commission','',0,3,1),(151,1,'menu','基础数据','','Grid','',NULL,1,1,1),(152,1,'menu','会员中心','','UserFilled','',NULL,1,5,1),(153,1,'menu','绩效管理','','Trophy','',NULL,1,7,1),(154,1,'menu','人员管理',NULL,'User',NULL,NULL,1,6,1),(155,4,'menu','商品销售',NULL,'Sell','/business/sales-pos',NULL,0,10,1),(156,4,'menu','销售订单',NULL,'Document','/business/sales-order',NULL,0,12,1),(157,4,'menu','退加货记录',NULL,'List','/business/sales-record',NULL,0,15,1),(158,3,'menu','商品进货',NULL,'ShoppingCart','/bus/toInportPOS','',1,9,1),(159,3,'menu','进货订单',NULL,'List','/bus/toInportOrder','',1,10,1),(160,3,'menu','退加货记录',NULL,'Document','/bus/toInportRecord','',1,11,1),(161,152,'menu','等级规则',NULL,'Medal','/business/member-level','',0,2,1),(162,161,'permission','等级查看','level:view',NULL,NULL,NULL,0,1,1),(163,161,'permission','等级添加','level:create',NULL,NULL,NULL,0,2,1),(164,161,'permission','等级修改','level:update',NULL,NULL,NULL,0,3,1),(165,161,'permission','等级删除','level:delete',NULL,NULL,NULL,0,4,1);
/*!40000 ALTER TABLE `sys_permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role`
--

DROP TABLE IF EXISTS `sys_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `remark` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `available` int DEFAULT NULL,
  `createtime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role`
--

LOCK TABLES `sys_role` WRITE;
/*!40000 ALTER TABLE `sys_role` DISABLE KEYS */;
INSERT INTO `sys_role` VALUES (1,'超级管理员','拥有所有菜单权限',1,'2019-04-10 14:06:32'),(11,'仓库管理员','负责商品、库存、进货、退货、盘点等仓库日常管理',1,'2026-06-06 23:49:09'),(12,'采购员','负责供应商管理和商品采购进货',1,'2026-06-06 23:49:09'),(13,'销售员','负责客户管理、商品销售和零售业务',1,'2026-06-06 23:49:09'),(14,'财务人员','负责查看报表统计和财务分析（只读）',1,'2026-06-06 23:49:09');
/*!40000 ALTER TABLE `sys_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role_permission`
--

DROP TABLE IF EXISTS `sys_role_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role_permission` (
  `rid` int NOT NULL,
  `pid` int NOT NULL,
  PRIMARY KEY (`pid`,`rid`) USING BTREE,
  KEY `sys_role_permission_ibfk_1` (`pid`) USING BTREE,
  KEY `sys_role_permission_ibfk_2` (`rid`) USING BTREE,
  CONSTRAINT `sys_role_permission_ibfk_1` FOREIGN KEY (`pid`) REFERENCES `sys_permission` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `sys_role_permission_ibfk_2` FOREIGN KEY (`rid`) REFERENCES `sys_role` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role_permission`
--

LOCK TABLES `sys_role_permission` WRITE;
/*!40000 ALTER TABLE `sys_role_permission` DISABLE KEYS */;
INSERT INTO `sys_role_permission` VALUES (1,1),(11,3),(12,3),(13,4),(1,5),(1,7),(11,7),(13,7),(1,8),(11,8),(12,8),(1,9),(11,9),(1,10),(11,10),(12,10),(1,11),(11,11),(12,11),(1,12),(13,12),(1,13),(13,13),(1,14),(1,15),(1,16),(1,17),(1,18),(1,21),(1,22),(1,23),(1,30),(1,31),(1,32),(1,34),(1,35),(1,36),(1,38),(1,39),(1,40),(1,42),(1,43),(1,44),(1,46),(1,47),(1,48),(1,49),(1,51),(1,52),(1,53),(1,54),(1,55),(1,56),(1,57),(1,68),(11,68),(13,68),(1,69),(11,69),(13,69),(1,70),(11,70),(13,70),(1,71),(11,71),(13,71),(1,73),(1,74),(1,75),(1,76),(1,77),(1,78),(1,79),(1,81),(11,81),(12,81),(1,82),(11,82),(12,82),(1,83),(11,83),(12,83),(1,84),(11,84),(12,84),(1,86),(1,91),(11,91),(1,92),(11,92),(1,116),(11,116),(1,117),(11,117),(1,118),(1,125),(14,125),(1,126),(14,126),(14,127),(14,128),(11,130),(1,132),(1,133),(13,133),(1,134),(13,134),(1,135),(13,135),(1,136),(13,136),(1,137),(13,137),(1,138),(13,138),(1,139),(13,139),(1,140),(13,140),(1,141),(11,141),(1,142),(11,142),(1,143),(11,143),(1,144),(11,144),(1,145),(1,146),(1,147),(1,148),(1,149),(1,150),(13,150),(1,151),(11,151),(12,151),(13,151),(1,152),(13,152),(1,153),(13,153),(1,158),(11,158),(12,158),(1,159),(11,159),(12,159),(1,160),(11,160),(12,160);
/*!40000 ALTER TABLE `sys_role_permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user`
--

DROP TABLE IF EXISTS `sys_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL,
  `loginname` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL,
  `pwd` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL,
  `address` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL,
  `sex` int DEFAULT NULL,
  `remark` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL,
  `deptid` int DEFAULT NULL,
  `hiredate` datetime DEFAULT NULL,
  `mgr` int DEFAULT NULL COMMENT '上级领导id',
  `available` int DEFAULT '1' COMMENT '是否可用，0不可用，1可用',
  `ordernum` int DEFAULT NULL COMMENT '排序码',
  `type` int DEFAULT NULL COMMENT '用户类型[0超级管理员，1管理员，2普通用户]',
  `imgpath` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL COMMENT '用户头像地址',
  `salt` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL COMMENT '盐',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `sys_user_loginname` (`loginname`) USING BTREE COMMENT '登陆名称唯一',
  KEY `FK_sys_dept_sys_user` (`deptid`) USING BTREE,
  CONSTRAINT `FK_sys_dept_sys_user` FOREIGN KEY (`deptid`) REFERENCES `sys_dept` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user`
--

LOCK TABLES `sys_user` WRITE;
/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO `sys_user` VALUES (1,'超级管理员','admin','532ac00e86893901af5f0be6b704dbc7','广东',1,'系统最高权限账号，请勿删除',1,'2018-06-25 11:06:34',NULL,1,1,0,'2020-02-24/8258FCECC0D64A1DB3B457E7D51D6AB5.jpg','04A93C74C8294AA09A8B974FD1F4ECBB'),(20,'仓库管理员','warehouse','532ac00e86893901af5f0be6b704dbc7','武汉',1,'仓库管理员账号',3,'2026-06-06 23:49:09',NULL,1,1,1,NULL,'04A93C74C8294AA09A8B974FD1F4ECBB'),(21,'采购员','purchase','532ac00e86893901af5f0be6b704dbc7','武汉',1,'采购员账号',3,'2026-06-06 23:49:09',NULL,1,2,1,NULL,'04A93C74C8294AA09A8B974FD1F4ECBB'),(22,'销售员','sales','532ac00e86893901af5f0be6b704dbc7','武汉',1,'销售员账号',2,'2026-06-06 23:49:09',NULL,1,3,1,NULL,'04A93C74C8294AA09A8B974FD1F4ECBB'),(23,'财务人员','finance','532ac00e86893901af5f0be6b704dbc7','武汉',1,'财务人员账号',1,'2026-06-06 23:49:09',NULL,1,4,1,NULL,'04A93C74C8294AA09A8B974FD1F4ECBB');
/*!40000 ALTER TABLE `sys_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user_role`
--

DROP TABLE IF EXISTS `sys_user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user_role` (
  `uid` int NOT NULL,
  `rid` int NOT NULL,
  PRIMARY KEY (`uid`,`rid`) USING BTREE,
  KEY `FK_sys_user_role_1` (`rid`) USING BTREE,
  CONSTRAINT `FK_sys_user_role_1` FOREIGN KEY (`rid`) REFERENCES `sys_role` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FK_sys_user_role_2` FOREIGN KEY (`uid`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user_role`
--

LOCK TABLES `sys_user_role` WRITE;
/*!40000 ALTER TABLE `sys_user_role` DISABLE KEYS */;
INSERT INTO `sys_user_role` VALUES (1,1),(20,11),(21,12),(22,13),(23,14);
/*!40000 ALTER TABLE `sys_user_role` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-08  0:04:15
