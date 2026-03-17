-- MySQL dump 10.13  Distrib 8.4.8, for macos15 (arm64)
--
-- Host: localhost    Database: jswm
-- ------------------------------------------------------
-- Server version	8.4.8

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
-- Current Database: `jswm`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `jswm` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `jswm`;

--
-- Table structure for table `biz_address`
--

DROP TABLE IF EXISTS `biz_address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_address` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `receiver_name` varchar(50) NOT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(20) NOT NULL COMMENT '收货人电话',
  `province` varchar(50) DEFAULT NULL COMMENT '省份',
  `city` varchar(50) DEFAULT NULL COMMENT '城市',
  `district` varchar(50) DEFAULT NULL COMMENT '区/县',
  `detail_address` varchar(255) NOT NULL COMMENT '详细地址',
  `label` varchar(20) DEFAULT NULL COMMENT '地址标签(学校/宿舍/家等)',
  `is_default` tinyint NOT NULL DEFAULT '0' COMMENT '是否默认(0否/1是)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除(0未删除/1已删除)',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_is_default` (`is_default`),
  CONSTRAINT `fk_address_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='收货地址表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_address`
--

LOCK TABLES `biz_address` WRITE;
/*!40000 ALTER TABLE `biz_address` DISABLE KEYS */;
INSERT INTO `biz_address` VALUES (1,2,'张三','13800000001','北京市','海淀区','学院路街道','XX大学学生宿舍1号楼301室','宿舍',0,'2026-03-12 18:10:40','2026-03-13 17:18:24',0),(2,2,'张三','13800000001','北京市','海淀区','学院路街道','XX大学图书馆一楼','图书馆',0,'2026-03-12 18:10:40','2026-03-13 10:10:47',1),(3,3,'李四','13800000002','北京市','海淀区','学院路街道','XX大学学生宿舍2号楼205室','宿舍',1,'2026-03-12 18:10:40','2026-03-12 18:10:40',0),(4,4,'王五','13800000003','北京市','海淀区','学院路街道','XX大学教学楼A座301','教学楼',1,'2026-03-12 18:10:40','2026-03-12 18:10:40',0),(5,2,'测试','13111111234','黑龙江省','讷河市','14组','101','学校',1,'2026-03-13 17:18:24','2026-03-13 17:18:24',0);
/*!40000 ALTER TABLE `biz_address` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biz_cart`
--

DROP TABLE IF EXISTS `biz_cart`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_cart` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `dish_id` bigint NOT NULL COMMENT '菜品ID',
  `merchant_id` bigint NOT NULL COMMENT '商家ID',
  `quantity` int NOT NULL DEFAULT '1' COMMENT '数量',
  `checked` tinyint NOT NULL DEFAULT '1' COMMENT '是否选中(0否/1是)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_dish` (`user_id`,`dish_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_merchant_id` (`merchant_id`),
  KEY `fk_cart_dish` (`dish_id`),
  CONSTRAINT `fk_cart_dish` FOREIGN KEY (`dish_id`) REFERENCES `biz_dish` (`id`),
  CONSTRAINT `fk_cart_merchant` FOREIGN KEY (`merchant_id`) REFERENCES `biz_merchant` (`id`),
  CONSTRAINT `fk_cart_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='购物车表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_cart`
--

LOCK TABLES `biz_cart` WRITE;
/*!40000 ALTER TABLE `biz_cart` DISABLE KEYS */;
/*!40000 ALTER TABLE `biz_cart` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biz_category`
--

DROP TABLE IF EXISTS `biz_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) NOT NULL COMMENT '分类名称',
  `sort` int NOT NULL DEFAULT '0' COMMENT '排序(越小越靠前)',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态(0禁用/1启用)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除(0未删除/1已删除)',
  PRIMARY KEY (`id`),
  KEY `idx_sort` (`sort`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='菜品分类表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_category`
--

LOCK TABLES `biz_category` WRITE;
/*!40000 ALTER TABLE `biz_category` DISABLE KEYS */;
INSERT INTO `biz_category` VALUES (1,'快餐简餐',1,1,'2026-03-12 18:10:40','2026-03-12 18:10:40',0),(2,'面食粉类',2,1,'2026-03-12 18:10:40','2026-03-12 18:10:40',0),(3,'饮品甜点',3,1,'2026-03-12 18:10:40','2026-03-12 18:10:40',0),(4,'特色小吃',4,1,'2026-03-12 18:10:40','2026-03-12 18:10:40',0),(5,'水果生鲜',5,1,'2026-03-12 18:10:40','2026-03-12 18:10:40',0);
/*!40000 ALTER TABLE `biz_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biz_dish`
--

DROP TABLE IF EXISTS `biz_dish`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_dish` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `merchant_id` bigint NOT NULL COMMENT '所属商家ID',
  `category_id` bigint DEFAULT NULL COMMENT '菜品分类ID',
  `name` varchar(100) NOT NULL COMMENT '菜品名称',
  `price` decimal(10,2) NOT NULL COMMENT '价格',
  `original_price` decimal(10,2) DEFAULT NULL COMMENT '原价',
  `image` varchar(255) DEFAULT NULL COMMENT '菜品图片',
  `description` varchar(500) DEFAULT NULL COMMENT '菜品描述',
  `stock` int NOT NULL DEFAULT '0' COMMENT '库存数量',
  `sales` int NOT NULL DEFAULT '0' COMMENT '销量',
  `rating` decimal(2,1) NOT NULL DEFAULT '5.0' COMMENT '评分',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态(0下架/1上架/2审核中)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除(0未删除/1已删除)',
  PRIMARY KEY (`id`),
  KEY `idx_merchant_id` (`merchant_id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_name` (`name`),
  KEY `idx_status` (`status`),
  KEY `idx_sales` (`sales`),
  KEY `idx_rating` (`rating`),
  CONSTRAINT `fk_dish_category` FOREIGN KEY (`category_id`) REFERENCES `biz_category` (`id`),
  CONSTRAINT `fk_dish_merchant` FOREIGN KEY (`merchant_id`) REFERENCES `biz_merchant` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='菜品表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_dish`
--

LOCK TABLES `biz_dish` WRITE;
/*!40000 ALTER TABLE `biz_dish` DISABLE KEYS */;
INSERT INTO `biz_dish` VALUES (1,1,1,'红烧肉盖饭',15.00,18.00,'/images/dish/dish1.jpg','精选五花肉，肥而不腻',100,356,4.8,1,'2026-03-12 18:10:40','2026-03-13 13:49:50',1),(2,1,1,'宫保鸡丁饭',13.00,15.00,'/images/dish/dish2.jpg','经典川菜，香辣可口',100,289,4.7,1,'2026-03-12 18:10:40','2026-03-13 13:49:51',1),(3,1,2,'番茄鸡蛋面',10.00,NULL,'/images/dish/dish3.jpg','家常味道，营养丰富',50,198,4.6,1,'2026-03-12 18:10:40','2026-03-13 13:49:53',1),(4,1,4,'炸鸡腿',8.00,10.00,'/images/dish/dish4.jpg','外酥里嫩，香脆可口',30,421,4.9,1,'2026-03-12 18:10:40','2026-03-13 13:49:46',1),(5,2,1,'黄焖鸡米饭',16.00,18.00,'/images/dish/dish5.jpg','鲜嫩鸡肉，浓郁汤汁',80,512,4.8,1,'2026-03-12 18:10:40','2026-03-13 13:49:44',1),(6,2,1,'鱼香肉丝饭',12.00,14.00,'/images/dish/dish6.jpg','酸甜可口，下饭神器',80,378,4.7,1,'2026-03-12 18:10:40','2026-03-13 13:49:48',1),(7,2,2,'牛肉拉面',14.00,NULL,'/images/dish/dish7.jpg','手工拉面，牛肉大块',60,267,4.6,1,'2026-03-12 18:10:40','2026-03-13 13:49:52',1),(8,3,3,'珍珠奶茶',8.00,10.00,'/images/dish/dish8.jpg','经典口味，Q弹珍珠',200,856,4.9,1,'2026-03-12 18:10:40','2026-03-13 13:49:42',1),(9,3,3,'芒果冰沙',12.00,15.00,'/images/dish/dish9.jpg','新鲜芒果，清凉解暑',100,423,4.8,1,'2026-03-12 18:10:40','2026-03-13 13:49:45',1),(10,3,3,'草莓奶昔',10.00,12.00,'/images/dish/dish10.jpg','香甜可口，奶香浓郁',100,312,4.7,1,'2026-03-12 18:10:40','2026-03-13 13:49:40',1),(11,1,1,'测试菜品',5.00,NULL,'https://liuhaonan-java-ai.oss-cn-shenzhen.aliyuncs.com/JiSongWaiMai/images/2b4c975c-857b-4f4b-a2de-79487b8075f9.jpeg','测试菜品',999,0,5.0,0,'2026-03-13 14:12:12','2026-03-13 14:12:22',1),(12,1,1,'测试菜品',10.00,NULL,'https://liuhaonan-java-ai.oss-cn-shenzhen.aliyuncs.com/JiSongWaiMai/images/8fac26bf-fa75-476b-8762-d5463f123d73.jpeg','测试菜品',999,0,5.0,1,'2026-03-13 14:12:35','2026-03-13 14:26:02',0),(13,5,1,'测试',100.00,NULL,'https://liuhaonan-java-ai.oss-cn-shenzhen.aliyuncs.com/JiSongWaiMai/logo/df72f37a-3338-458e-a511-1c0d0daccc7c.jpeg','测试',999,1,5.0,1,'2026-03-17 14:03:47','2026-03-17 14:17:13',0);
/*!40000 ALTER TABLE `biz_dish` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biz_favorite`
--

DROP TABLE IF EXISTS `biz_favorite`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_favorite` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `merchant_id` bigint NOT NULL COMMENT '商家ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_merchant` (`user_id`,`merchant_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `fk_favorite_merchant` (`merchant_id`),
  CONSTRAINT `fk_favorite_merchant` FOREIGN KEY (`merchant_id`) REFERENCES `biz_merchant` (`id`),
  CONSTRAINT `fk_favorite_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户收藏表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_favorite`
--

LOCK TABLES `biz_favorite` WRITE;
/*!40000 ALTER TABLE `biz_favorite` DISABLE KEYS */;
INSERT INTO `biz_favorite` VALUES (1,2,2,'2026-03-13 17:04:34'),(2,2,5,'2026-03-17 14:30:17');
/*!40000 ALTER TABLE `biz_favorite` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biz_merchant`
--

DROP TABLE IF EXISTS `biz_merchant`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_merchant` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '关联用户ID',
  `shop_name` varchar(100) NOT NULL COMMENT '店铺名称',
  `shop_logo` varchar(255) DEFAULT NULL COMMENT '店铺Logo',
  `shop_address` varchar(255) DEFAULT NULL COMMENT '店铺地址',
  `description` varchar(500) DEFAULT NULL COMMENT '店铺描述',
  `category` varchar(50) DEFAULT NULL COMMENT '经营类别',
  `business_hours` varchar(50) DEFAULT NULL COMMENT '营业时间',
  `rating` decimal(2,1) NOT NULL DEFAULT '5.0' COMMENT '店铺评分',
  `sales` int NOT NULL DEFAULT '0' COMMENT '总销量',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态(0待审核/1营业中/2已关闭)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除(0未删除/1已删除)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  KEY `idx_shop_name` (`shop_name`),
  KEY `idx_category` (`category`),
  KEY `idx_status` (`status`),
  KEY `idx_rating` (`rating`),
  CONSTRAINT `fk_merchant_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商家表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_merchant`
--

LOCK TABLES `biz_merchant` WRITE;
/*!40000 ALTER TABLE `biz_merchant` DISABLE KEYS */;
INSERT INTO `biz_merchant` VALUES (1,5,'美味餐厅','https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=400','食堂一楼A区','正宗家常菜，美味又实惠','快餐简餐','07:00-21:00',4.8,1256,1,'2026-03-12 18:10:40','2026-03-17 14:23:15',0),(2,6,'香喷喷快餐','https://images.unsplash.com/photo-1555939594-58d7cb561ad1?w=400','食堂二楼B区','精选食材，健康美味','快餐简餐','07:00-21:00',4.6,892,1,'2026-03-12 18:10:40','2026-03-17 14:23:15',0),(3,7,'校园奶茶店12','https://images.unsplash.com/photo-1540189549336-e6e99c3679fe?w=400','食堂一楼C区','新鲜现做，各种口味奶茶','饮品甜点','08:00-22:00',4.9,2341,1,'2026-03-12 18:10:40','2026-03-17 14:23:15',0),(4,10,'测试商家店铺',NULL,'校园内','美味可口的菜品','快餐','08:00-22:00',4.5,100,1,'2026-03-17 13:45:42','2026-03-17 13:45:42',0),(5,11,'测试','https://liuhaonan-java-ai.oss-cn-shenzhen.aliyuncs.com/JiSongWaiMai/shop/64d0c08e-0b34-422e-9530-74d749d97ec5.jpeg','校园内','美味快餐测试','快餐','08:00-22:00',4.5,51,1,'2026-03-17 13:59:58','2026-03-17 15:33:36',0);
/*!40000 ALTER TABLE `biz_merchant` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biz_merchant_qualification`
--

DROP TABLE IF EXISTS `biz_merchant_qualification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_merchant_qualification` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `merchant_id` bigint NOT NULL COMMENT '商家ID',
  `qualification_type` varchar(50) NOT NULL COMMENT '资质类型(营业执照/食品经营许可证等)',
  `qualification_no` varchar(100) DEFAULT NULL COMMENT '资质编号',
  `qualification_image` varchar(255) NOT NULL COMMENT '资质图片',
  `expire_date` date DEFAULT NULL COMMENT '有效期',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '审核状态(0待审核/1已通过/2已拒绝)',
  `audit_remark` varchar(200) DEFAULT NULL COMMENT '审核备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除(0未删除/1已删除)',
  PRIMARY KEY (`id`),
  KEY `idx_merchant_id` (`merchant_id`),
  CONSTRAINT `fk_qualification_merchant` FOREIGN KEY (`merchant_id`) REFERENCES `biz_merchant` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商家资质表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_merchant_qualification`
--

LOCK TABLES `biz_merchant_qualification` WRITE;
/*!40000 ALTER TABLE `biz_merchant_qualification` DISABLE KEYS */;
/*!40000 ALTER TABLE `biz_merchant_qualification` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biz_order`
--

DROP TABLE IF EXISTS `biz_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_order` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_no` varchar(32) NOT NULL COMMENT '订单编号',
  `user_id` bigint NOT NULL COMMENT '下单用户ID',
  `merchant_id` bigint NOT NULL COMMENT '商家ID',
  `total_amount` decimal(10,2) NOT NULL COMMENT '订单总额',
  `pay_amount` decimal(10,2) NOT NULL COMMENT '实付金额',
  `delivery_fee` decimal(10,2) DEFAULT '0.00' COMMENT '配送费',
  `discount_amount` decimal(10,2) DEFAULT '0.00' COMMENT '优惠金额',
  `receiver_name` varchar(50) NOT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(20) NOT NULL COMMENT '收货人电话',
  `delivery_address` varchar(255) NOT NULL COMMENT '配送地址',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '订单状态(0待支付/1待接单/2待配送/3配送中/4已完成/5已取消)',
  `pay_type` tinyint DEFAULT NULL COMMENT '支付方式(1微信/2支付宝/3模拟支付)',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `deliver_time` datetime DEFAULT NULL COMMENT '配送时间',
  `complete_time` datetime DEFAULT NULL COMMENT '完成时间',
  `cancel_reason` varchar(200) DEFAULT NULL COMMENT '取消原因',
  `cancel_time` datetime DEFAULT NULL COMMENT '取消时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除(0未删除/1已删除)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_merchant_id` (`merchant_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_order_merchant` FOREIGN KEY (`merchant_id`) REFERENCES `biz_merchant` (`id`),
  CONSTRAINT `fk_order_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_order`
--

LOCK TABLES `biz_order` WRITE;
/*!40000 ALTER TABLE `biz_order` DISABLE KEYS */;
INSERT INTO `biz_order` VALUES (1,'ORD202603131621200001',2,1,10.00,10.00,0.00,0.00,'张三','13800000001','XX大学学生宿舍1号楼301室','',5,3,NULL,NULL,NULL,NULL,'2026-03-13 16:21:37','2026-03-13 16:21:20','2026-03-13 16:21:37',0),(2,'ORD202603131622180002',2,1,10.00,10.00,0.00,0.00,'张三','13800000001','XX大学学生宿舍1号楼301室','',1,3,'2026-03-13 16:52:02',NULL,NULL,NULL,NULL,'2026-03-13 16:22:18','2026-03-13 16:52:02',0),(3,'ORD202603131653160003',2,1,10.00,10.00,0.00,0.00,'张三','13800000001','XX大学学生宿舍1号楼301室','',5,3,'2026-03-13 16:53:18',NULL,NULL,'cesh ',NULL,'2026-03-13 16:53:16','2026-03-17 10:34:12',0),(4,'ORD202603161552060001',2,1,10.00,10.00,0.00,0.00,'测试','13111111234','101','',2,3,'2026-03-16 16:32:58',NULL,NULL,NULL,NULL,'2026-03-16 15:52:06','2026-03-16 18:14:03',0),(5,'ORD202603171416530001',2,5,100.00,100.00,0.00,0.00,'测试','13111111234','101','',4,3,'2026-03-17 14:16:56','2026-03-17 14:17:11','2026-03-17 14:17:14',NULL,NULL,'2026-03-17 14:16:53','2026-03-17 14:17:13',0);
/*!40000 ALTER TABLE `biz_order` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `tr_update_merchant_sales` AFTER UPDATE ON `biz_order` FOR EACH ROW BEGIN
    IF NEW.status = 4 AND OLD.status != 4 THEN
        UPDATE biz_merchant 
        SET sales = sales + (
            SELECT SUM(quantity) 
            FROM biz_order_item 
            WHERE order_id = NEW.id
        ),
        update_time = NOW()
        WHERE id = NEW.merchant_id;
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `tr_update_dish_sales` AFTER UPDATE ON `biz_order` FOR EACH ROW BEGIN
    IF NEW.status = 4 AND OLD.status != 4 THEN
        UPDATE biz_dish d
        SET sales = sales + (
            SELECT quantity 
            FROM biz_order_item oi 
            WHERE oi.order_id = NEW.id AND oi.dish_id = d.id
        ),
        update_time = NOW()
        WHERE id IN (
            SELECT dish_id 
            FROM biz_order_item 
            WHERE order_id = NEW.id
        );
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `biz_order_item`
--

DROP TABLE IF EXISTS `biz_order_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_order_item` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `dish_id` bigint NOT NULL COMMENT '菜品ID',
  `dish_name` varchar(100) NOT NULL COMMENT '菜品名称',
  `dish_image` varchar(255) DEFAULT NULL COMMENT '菜品图片',
  `price` decimal(10,2) NOT NULL COMMENT '单价',
  `quantity` int NOT NULL COMMENT '数量',
  `subtotal` decimal(10,2) NOT NULL COMMENT '小计',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_dish_id` (`dish_id`),
  CONSTRAINT `fk_order_item_dish` FOREIGN KEY (`dish_id`) REFERENCES `biz_dish` (`id`),
  CONSTRAINT `fk_order_item_order` FOREIGN KEY (`order_id`) REFERENCES `biz_order` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单详情表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_order_item`
--

LOCK TABLES `biz_order_item` WRITE;
/*!40000 ALTER TABLE `biz_order_item` DISABLE KEYS */;
INSERT INTO `biz_order_item` VALUES (1,1,12,'测试菜品','https://liuhaonan-java-ai.oss-cn-shenzhen.aliyuncs.com/JiSongWaiMai/images/8fac26bf-fa75-476b-8762-d5463f123d73.jpeg',10.00,1,10.00,'2026-03-13 16:21:20'),(2,2,12,'测试菜品','https://liuhaonan-java-ai.oss-cn-shenzhen.aliyuncs.com/JiSongWaiMai/images/8fac26bf-fa75-476b-8762-d5463f123d73.jpeg',10.00,1,10.00,'2026-03-13 16:22:18'),(3,3,12,'测试菜品','https://liuhaonan-java-ai.oss-cn-shenzhen.aliyuncs.com/JiSongWaiMai/images/8fac26bf-fa75-476b-8762-d5463f123d73.jpeg',10.00,1,10.00,'2026-03-13 16:53:16'),(4,4,12,'测试菜品','https://liuhaonan-java-ai.oss-cn-shenzhen.aliyuncs.com/JiSongWaiMai/images/8fac26bf-fa75-476b-8762-d5463f123d73.jpeg',10.00,1,10.00,'2026-03-16 15:52:06'),(5,5,13,'测试','https://liuhaonan-java-ai.oss-cn-shenzhen.aliyuncs.com/JiSongWaiMai/logo/df72f37a-3338-458e-a511-1c0d0daccc7c.jpeg',100.00,1,100.00,'2026-03-17 14:16:53');
/*!40000 ALTER TABLE `biz_order_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biz_review`
--

DROP TABLE IF EXISTS `biz_review`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `biz_review` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `merchant_id` bigint NOT NULL COMMENT '商家ID',
  `dish_id` bigint DEFAULT NULL COMMENT '菜品ID(可为空，表示对整体订单评价)',
  `rating` tinyint NOT NULL COMMENT '评分(1-5)',
  `content` varchar(500) DEFAULT NULL COMMENT '评价内容',
  `images` varchar(1000) DEFAULT NULL COMMENT '评价图片(JSON数组)',
  `is_anonymous` tinyint NOT NULL DEFAULT '0' COMMENT '是否匿名(0否/1是)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除(0未删除/1已删除)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_id` (`order_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_merchant_id` (`merchant_id`),
  KEY `idx_dish_id` (`dish_id`),
  KEY `idx_rating` (`rating`),
  CONSTRAINT `fk_review_dish` FOREIGN KEY (`dish_id`) REFERENCES `biz_dish` (`id`),
  CONSTRAINT `fk_review_merchant` FOREIGN KEY (`merchant_id`) REFERENCES `biz_merchant` (`id`),
  CONSTRAINT `fk_review_order` FOREIGN KEY (`order_id`) REFERENCES `biz_order` (`id`),
  CONSTRAINT `fk_review_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='评价表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_review`
--

LOCK TABLES `biz_review` WRITE;
/*!40000 ALTER TABLE `biz_review` DISABLE KEYS */;
/*!40000 ALTER TABLE `biz_review` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_notice`
--

DROP TABLE IF EXISTS `sys_notice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_notice` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` varchar(100) NOT NULL COMMENT '公告标题',
  `content` text NOT NULL COMMENT '公告内容',
  `type` tinyint NOT NULL DEFAULT '1' COMMENT '类型(1通知/2公告)',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态(0关闭/1开启)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除(0未删除/1已删除)',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统公告表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_notice`
--

LOCK TABLES `sys_notice` WRITE;
/*!40000 ALTER TABLE `sys_notice` DISABLE KEYS */;
INSERT INTO `sys_notice` VALUES (1,'欢迎使用校园点餐系统','欢迎使用校园点餐系统，祝您用餐愉快！如有问题请联系管理员。',1,1,'2026-03-12 18:10:40','2026-03-12 18:10:40',0),(2,'新商家入驻通知','校园奶茶店已入驻平台，欢迎同学们品尝！',2,1,'2026-03-12 18:10:40','2026-03-12 18:10:40',0);
/*!40000 ALTER TABLE `sys_notice` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_setting`
--

DROP TABLE IF EXISTS `sys_setting`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_setting` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `setting_key` varchar(50) NOT NULL COMMENT '设置键',
  `setting_value` varchar(500) DEFAULT NULL COMMENT '设置值',
  `description` varchar(200) DEFAULT NULL COMMENT '描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_setting_key` (`setting_key`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统设置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_setting`
--

LOCK TABLES `sys_setting` WRITE;
/*!40000 ALTER TABLE `sys_setting` DISABLE KEYS */;
INSERT INTO `sys_setting` VALUES (1,'system_name','吉送外卖','系统名称','2026-03-16 18:26:27','2026-03-17 15:01:08'),(2,'system_logo','https://liuhaonan-java-ai.oss-cn-shenzhen.aliyuncs.com/JiSongWaiMai/logo/14710d2b-836f-4cda-8afc-8e517f49555c.jpeg','系统Logo URL','2026-03-16 18:26:27','2026-03-17 15:01:08'),(3,'announcement','欢迎使用吉送外卖系统，祝您用餐愉快！','系统公告','2026-03-16 18:26:27','2026-03-17 15:01:08'),(4,'auto_cancel_time','15','自动取消时间（分钟）','2026-03-16 18:26:27','2026-03-17 15:01:08'),(5,'delivery_fee','0','配送费（元）','2026-03-16 18:26:27','2026-03-17 15:01:08'),(6,'min_order_amount','15','起送金额（元）','2026-03-16 18:26:27','2026-03-17 15:01:08'),(7,'delivery_time','30','预计配送时间（分钟）','2026-03-16 18:26:27','2026-03-17 15:01:08'),(8,'notify_new_order','true','新订单通知','2026-03-16 18:26:27','2026-03-17 15:01:08'),(9,'notify_order_complete','true','订单完成通知','2026-03-16 18:26:27','2026-03-17 15:01:08'),(10,'notify_merchant_apply','true','商家入驻通知','2026-03-16 18:26:27','2026-03-17 15:01:08'),(11,'login_fail_limit','5','登录失败锁定次数','2026-03-16 18:26:27','2026-03-17 15:01:08'),(12,'lock_time','30','锁定时间（分钟）','2026-03-16 18:26:27','2026-03-17 15:01:08');
/*!40000 ALTER TABLE `sys_setting` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user`
--

DROP TABLE IF EXISTS `sys_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码(BCrypt加密)',
  `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `role` tinyint NOT NULL DEFAULT '0' COMMENT '角色(0学生/1商家/2管理员)',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态(0禁用/1启用)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除(0未删除/1已删除)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_phone` (`phone`),
  KEY `idx_role` (`role`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user`
--

LOCK TABLES `sys_user` WRITE;
/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO `sys_user` VALUES (1,'admin','$2a$10$PZsikOSaU4Rf3lRSU4F1VebcpXtlAMWPbnfw5z9ZGAHwn/iyRkg66','系统管理员','13800000000','admin@jswm.com',NULL,2,1,'2026-03-12 18:10:40','2026-03-16 17:28:58',0),(2,'student1','$2a$10$PZsikOSaU4Rf3lRSU4F1VebcpXtlAMWPbnfw5z9ZGAHwn/iyRkg66','张三1','13800000001','student1@jswm.com','https://liuhaonan-java-ai.oss-cn-shenzhen.aliyuncs.com/JiSongWaiMai/avatars/91bfc40f-0cf2-47f8-8b42-a742dcfef12c.jpeg',0,1,'2026-03-12 18:10:40','2026-03-17 15:00:06',0),(3,'student2','$2a$10$PZsikOSaU4Rf3lRSU4F1VebcpXtlAMWPbnfw5z9ZGAHwn/iyRkg66','李四','13800000002','student2@jswm.com',NULL,0,1,'2026-03-12 18:10:40','2026-03-12 18:13:17',0),(4,'student3','$2a$10$PZsikOSaU4Rf3lRSU4F1VebcpXtlAMWPbnfw5z9ZGAHwn/iyRkg66','王五','13800000003','student3@jswm.com',NULL,0,1,'2026-03-12 18:10:40','2026-03-12 18:13:17',0),(5,'merchant1','$2a$10$PZsikOSaU4Rf3lRSU4F1VebcpXtlAMWPbnfw5z9ZGAHwn/iyRkg66','美味餐厅','13800000010','merchant1@jswm.com',NULL,2,1,'2026-03-12 18:10:40','2026-03-12 18:13:17',0),(6,'merchant2','$2a$10$PZsikOSaU4Rf3lRSU4F1VebcpXtlAMWPbnfw5z9ZGAHwn/iyRkg66','香喷喷快餐','13800000011','merchant2@jswm.com',NULL,2,1,'2026-03-12 18:10:40','2026-03-12 18:13:17',0),(7,'merchant3','$2a$10$PZsikOSaU4Rf3lRSU4F1VebcpXtlAMWPbnfw5z9ZGAHwn/iyRkg66','校园奶茶店','13800000012','merchant3@jswm.com',NULL,2,1,'2026-03-12 18:10:40','2026-03-12 18:13:17',0),(8,'shangjia001','$2a$10$OCso4pYFbEibHuY9.LBPgu.RQiD8wYrFF8Hv8Cu6Jp10ciXyhB1EC','shangjia001','13111112221',NULL,NULL,0,1,'2026-03-16 17:10:19','2026-03-16 17:10:19',0),(9,'shangjia003','$2a$10$rlRwFpl/gx1VFZGuZO.hTeN3nRf/EXIb7U6mrqyQHEp8mWmdyaZ0m','shangjia002','13112345678','2324480152@jisong.com',NULL,0,1,'2026-03-16 17:12:19','2026-03-16 17:40:54',0),(10,'testmerchant','$2a$10$dSliaw5vCN.u6bCqGfRjxeUyWoh6zYJ1z99FzgzBIjN4uMUVUu/RC','测试商家','13999999999','test001@jswm.com',NULL,1,1,'2026-03-16 17:23:41','2026-03-17 10:33:55',0),(11,'merchant4','$2a$10$iRYXPNC4/DdBQbIn859u6edReXY.WbsR6OHbL5kZe0PyiEjm95hmq','merchant4','13099911122',NULL,NULL,1,1,'2026-03-17 13:37:39','2026-03-17 14:26:19',0);
/*!40000 ALTER TABLE `sys_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_setting`
--

DROP TABLE IF EXISTS `user_setting`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_setting` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `order_notify` tinyint DEFAULT '1' COMMENT '订单通知',
  `promotion_notify` tinyint DEFAULT '1' COMMENT '优惠活动通知',
  `system_notify` tinyint DEFAULT '1' COMMENT '系统消息通知',
  `dark_mode` tinyint DEFAULT '0' COMMENT '深色模式',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户设置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_setting`
--

LOCK TABLES `user_setting` WRITE;
/*!40000 ALTER TABLE `user_setting` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_setting` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `v_dish_detail`
--

DROP TABLE IF EXISTS `v_dish_detail`;
/*!50001 DROP VIEW IF EXISTS `v_dish_detail`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `v_dish_detail` AS SELECT 
 1 AS `id`,
 1 AS `dish_name`,
 1 AS `price`,
 1 AS `original_price`,
 1 AS `image`,
 1 AS `description`,
 1 AS `stock`,
 1 AS `sales`,
 1 AS `rating`,
 1 AS `status`,
 1 AS `merchant_id`,
 1 AS `shop_name`,
 1 AS `category_id`,
 1 AS `category_name`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `v_order_detail`
--

DROP TABLE IF EXISTS `v_order_detail`;
/*!50001 DROP VIEW IF EXISTS `v_order_detail`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `v_order_detail` AS SELECT 
 1 AS `order_id`,
 1 AS `order_no`,
 1 AS `user_id`,
 1 AS `user_name`,
 1 AS `user_real_name`,
 1 AS `merchant_id`,
 1 AS `shop_name`,
 1 AS `total_amount`,
 1 AS `pay_amount`,
 1 AS `status`,
 1 AS `receiver_name`,
 1 AS `receiver_phone`,
 1 AS `delivery_address`,
 1 AS `order_time`*/;
SET character_set_client = @saved_cs_client;

--
-- Current Database: `jswm`
--

USE `jswm`;

--
-- Final view structure for view `v_dish_detail`
--

/*!50001 DROP VIEW IF EXISTS `v_dish_detail`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_dish_detail` AS select `d`.`id` AS `id`,`d`.`name` AS `dish_name`,`d`.`price` AS `price`,`d`.`original_price` AS `original_price`,`d`.`image` AS `image`,`d`.`description` AS `description`,`d`.`stock` AS `stock`,`d`.`sales` AS `sales`,`d`.`rating` AS `rating`,`d`.`status` AS `status`,`m`.`id` AS `merchant_id`,`m`.`shop_name` AS `shop_name`,`c`.`id` AS `category_id`,`c`.`name` AS `category_name` from ((`biz_dish` `d` left join `biz_merchant` `m` on((`d`.`merchant_id` = `m`.`id`))) left join `biz_category` `c` on((`d`.`category_id` = `c`.`id`))) where (`d`.`is_deleted` = 0) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_order_detail`
--

/*!50001 DROP VIEW IF EXISTS `v_order_detail`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_order_detail` AS select `o`.`id` AS `order_id`,`o`.`order_no` AS `order_no`,`o`.`user_id` AS `user_id`,`u`.`username` AS `user_name`,`u`.`real_name` AS `user_real_name`,`o`.`merchant_id` AS `merchant_id`,`m`.`shop_name` AS `shop_name`,`o`.`total_amount` AS `total_amount`,`o`.`pay_amount` AS `pay_amount`,`o`.`status` AS `status`,`o`.`receiver_name` AS `receiver_name`,`o`.`receiver_phone` AS `receiver_phone`,`o`.`delivery_address` AS `delivery_address`,`o`.`create_time` AS `order_time` from ((`biz_order` `o` left join `sys_user` `u` on((`o`.`user_id` = `u`.`id`))) left join `biz_merchant` `m` on((`o`.`merchant_id` = `m`.`id`))) where (`o`.`is_deleted` = 0) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-17 17:14:06
