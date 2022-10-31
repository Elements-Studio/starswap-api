-- MySQL dump 10.13  Distrib 8.0.27, for macos11 (x86_64)
--
-- Host: 127.0.0.1    Database: starswap_aptos_testnet
-- ------------------------------------------------------
-- Server version	5.7.34

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `aptos_event_handle`
--

DROP TABLE IF EXISTS `aptos_event_handle`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `aptos_event_handle` (
  `account_address` varchar(66) COLLATE utf8mb4_unicode_ci NOT NULL,
  `event_handle_field_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `event_handle_struct` varchar(300) COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` bigint(20) NOT NULL,
  `created_by` varchar(70) COLLATE utf8mb4_unicode_ci NOT NULL,
  `event_key` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `next_sequence_number` decimal(31,0) DEFAULT NULL,
  `updated_at` bigint(20) NOT NULL,
  `updated_by` varchar(70) COLLATE utf8mb4_unicode_ci NOT NULL,
  `version` bigint(20) DEFAULT NULL,
  `event_data_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`account_address`,`event_handle_field_name`,`event_handle_struct`),
  UNIQUE KEY `UniqueEventJavaType` (`event_data_type`),
  UNIQUE KEY `UniqueEventDataType` (`event_data_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `aptos_event_handle`
--

LOCK TABLES `aptos_event_handle` WRITE;
/*!40000 ALTER TABLE `aptos_event_handle` DISABLE KEYS */;
/*!40000 ALTER TABLE `aptos_event_handle` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `liquidity_account`
--

DROP TABLE IF EXISTS `liquidity_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `liquidity_account` (
  `account_address` varchar(66) COLLATE utf8mb4_unicode_ci NOT NULL,
  `liquidity_token_address` varchar(66) COLLATE utf8mb4_unicode_ci NOT NULL,
  `token_x_id` varchar(15) COLLATE utf8mb4_unicode_ci NOT NULL,
  `token_y_id` varchar(15) COLLATE utf8mb4_unicode_ci NOT NULL,
  `pool_address` varchar(66) COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` bigint(20) NOT NULL,
  `created_by` varchar(70) COLLATE utf8mb4_unicode_ci NOT NULL,
  `deactived` bit(1) NOT NULL,
  `liquidity` decimal(31,0) DEFAULT NULL,
  `updated_at` bigint(20) NOT NULL,
  `updated_by` varchar(70) COLLATE utf8mb4_unicode_ci NOT NULL,
  `version` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`account_address`,`liquidity_token_address`,`token_x_id`,`token_y_id`,`pool_address`),
  KEY `idx_token_x_y_id` (`token_x_id`,`token_y_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `liquidity_account`
--

LOCK TABLES `liquidity_account` WRITE;
/*!40000 ALTER TABLE `liquidity_account` DISABLE KEYS */;
/*!40000 ALTER TABLE `liquidity_account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `liquidity_pool`
--

DROP TABLE IF EXISTS `liquidity_pool`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `liquidity_pool` (
  `liquidity_token_address` varchar(66) COLLATE utf8mb4_unicode_ci NOT NULL,
  `token_x_id` varchar(15) COLLATE utf8mb4_unicode_ci NOT NULL,
  `token_y_id` varchar(15) COLLATE utf8mb4_unicode_ci NOT NULL,
  `pool_address` varchar(66) COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` bigint(20) NOT NULL,
  `created_by` varchar(70) COLLATE utf8mb4_unicode_ci NOT NULL,
  `deactived` bit(1) NOT NULL,
  `description` varchar(1000) COLLATE utf8mb4_unicode_ci NOT NULL,
  `sequence_number` int(11) NOT NULL,
  `total_liquidity` decimal(31,0) DEFAULT NULL,
  `updated_at` bigint(20) NOT NULL,
  `updated_by` varchar(70) COLLATE utf8mb4_unicode_ci NOT NULL,
  `version` bigint(20) DEFAULT NULL,
  `token_x_reserve` decimal(31,0) DEFAULT NULL,
  `token_x_reserve_in_usd` decimal(51,10) DEFAULT NULL,
  `token_y_reserve` decimal(31,0) DEFAULT NULL,
  `token_y_reserve_in_usd` decimal(51,10) DEFAULT NULL,
  `poundage_rate_denominator` bigint(20) NOT NULL,
  `poundage_rate_numerator` bigint(20) NOT NULL,
  `swap_fee_op_rate_v2_denominator` bigint(20) NOT NULL,
  `swap_fee_op_rate_v2_numerator` bigint(20) NOT NULL,
  PRIMARY KEY (`liquidity_token_address`,`token_x_id`,`token_y_id`,`pool_address`),
  KEY `idx_token_x_y_id` (`token_x_id`,`token_y_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `liquidity_pool`
--

LOCK TABLES `liquidity_pool` WRITE;
/*!40000 ALTER TABLE `liquidity_pool` DISABLE KEYS */;
INSERT INTO `liquidity_pool` VALUES ('0x9bf32e42c442ae2adbc87bc7923610621469bf183266364503a7a434fe9d50ca','APT','STAR','0x9bf32e42c442ae2adbc87bc7923610621469bf183266364503a7a434fe9d50ca',1666240493000,'admin',_binary '\0','APT / STAR',1,66301910821,1666883803919,'admin',2202,1155155593,66.5809441284,3806865966801,66.8478584671,1000,3,60,10),('0x9bf32e42c442ae2adbc87bc7923610621469bf183266364503a7a434fe9d50ca','APT','USDT','0x9bf32e42c442ae2adbc87bc7923610621469bf183266364503a7a434fe9d50ca',1666359824000,'admin',_binary '\0','APT / USDT',1,264574131,1666883806787,'admin',1020,1099950000,63.3990000463,63653724,63.6537240000,1000,3,60,10);
/*!40000 ALTER TABLE `liquidity_pool` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `liquidity_token`
--

DROP TABLE IF EXISTS `liquidity_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `liquidity_token` (
  `liquidity_token_address` varchar(66) COLLATE utf8mb4_unicode_ci NOT NULL,
  `token_x_id` varchar(15) COLLATE utf8mb4_unicode_ci NOT NULL,
  `token_y_id` varchar(15) COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` bigint(20) NOT NULL,
  `created_by` varchar(70) COLLATE utf8mb4_unicode_ci NOT NULL,
  `deactived` bit(1) NOT NULL,
  `description` varchar(1000) COLLATE utf8mb4_unicode_ci NOT NULL,
  `sequence_number` int(11) NOT NULL,
  `updated_at` bigint(20) NOT NULL,
  `updated_by` varchar(70) COLLATE utf8mb4_unicode_ci NOT NULL,
  `version` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`liquidity_token_address`,`token_x_id`,`token_y_id`),
  KEY `idx_token_x_y_id` (`token_x_id`,`token_y_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `liquidity_token`
--

LOCK TABLES `liquidity_token` WRITE;
/*!40000 ALTER TABLE `liquidity_token` DISABLE KEYS */;
INSERT INTO `liquidity_token` VALUES ('0x9bf32e42c442ae2adbc87bc7923610621469bf183266364503a7a434fe9d50ca','APT','STAR',1666240493000,'admin',_binary '\0','APT / STAR',1,1666240493000,'admin',0),('0x9bf32e42c442ae2adbc87bc7923610621469bf183266364503a7a434fe9d50ca','APT','USDT',1666359824000,'admin',_binary '\0','APT / USDT',1,1666359824000,'admin',0);
/*!40000 ALTER TABLE `liquidity_token` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `liquidity_token_farm`
--

DROP TABLE IF EXISTS `liquidity_token_farm`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `liquidity_token_farm` (
  `farm_address` varchar(66) COLLATE utf8mb4_unicode_ci NOT NULL,
  `liquidity_token_address` varchar(66) COLLATE utf8mb4_unicode_ci NOT NULL,
  `token_x_id` varchar(15) COLLATE utf8mb4_unicode_ci NOT NULL,
  `token_y_id` varchar(15) COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` bigint(20) NOT NULL,
  `created_by` varchar(70) COLLATE utf8mb4_unicode_ci NOT NULL,
  `deactived` bit(1) NOT NULL,
  `description` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `estimated_apy` decimal(31,10) DEFAULT NULL,
  `sequence_number` int(11) NOT NULL,
  `total_stake_amount` decimal(31,0) DEFAULT NULL,
  `updated_at` bigint(20) NOT NULL,
  `updated_by` varchar(70) COLLATE utf8mb4_unicode_ci NOT NULL,
  `version` bigint(20) DEFAULT NULL,
  `reward_token_id` varchar(15) COLLATE utf8mb4_unicode_ci NOT NULL,
  `tvl_in_usd` decimal(51,10) DEFAULT NULL,
  `reward_multiplier` int(11) DEFAULT NULL,
  `daily_reward` decimal(31,0) DEFAULT NULL,
  PRIMARY KEY (`farm_address`,`liquidity_token_address`,`token_x_id`,`token_y_id`),
  KEY `idx_token_x_y_id` (`token_x_id`,`token_y_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `liquidity_token_farm`
--

LOCK TABLES `liquidity_token_farm` WRITE;
/*!40000 ALTER TABLE `liquidity_token_farm` DISABLE KEYS */;
INSERT INTO `liquidity_token_farm` VALUES ('0x9bf32e42c442ae2adbc87bc7923610621469bf183266364503a7a434fe9d50ca','0x9bf32e42c442ae2adbc87bc7923610621469bf183266364503a7a434fe9d50ca','APT','STAR',1639737823000,'admin',_binary '\0','APT / STAR',5145735.7507743800,1,2321431474,1666883792345,'admin',1942,'STAR',4.6717480218,0,51840000000000),('0x9bf32e42c442ae2adbc87bc7923610621469bf183266364503a7a434fe9d50ca','0x9bf32e42c442ae2adbc87bc7923610621469bf183266364503a7a434fe9d50ca','APT','USDT',1639737823000,'admin',_binary '\0','APT / USDT',0.0000000000,1,0,1666883799064,'admin',1015,'STAR',0.0000000000,0,17280000000000);
/*!40000 ALTER TABLE `liquidity_token_farm` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `liquidity_token_farm_account`
--

DROP TABLE IF EXISTS `liquidity_token_farm_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `liquidity_token_farm_account` (
  `account_address` varchar(66) COLLATE utf8mb4_unicode_ci NOT NULL,
  `farm_address` varchar(66) COLLATE utf8mb4_unicode_ci NOT NULL,
  `liquidity_token_address` varchar(66) COLLATE utf8mb4_unicode_ci NOT NULL,
  `token_x_id` varchar(15) COLLATE utf8mb4_unicode_ci NOT NULL,
  `token_y_id` varchar(15) COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` bigint(20) NOT NULL,
  `created_by` varchar(70) COLLATE utf8mb4_unicode_ci NOT NULL,
  `deactived` bit(1) NOT NULL,
  `stake_amount` decimal(31,0) DEFAULT NULL,
  `updated_at` bigint(20) NOT NULL,
  `updated_by` varchar(70) COLLATE utf8mb4_unicode_ci NOT NULL,
  `version` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`account_address`,`farm_address`,`liquidity_token_address`,`token_x_id`,`token_y_id`),
  KEY `idx_token_x_y_id` (`token_x_id`,`token_y_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `liquidity_token_farm_account`
--

LOCK TABLES `liquidity_token_farm_account` WRITE;
/*!40000 ALTER TABLE `liquidity_token_farm_account` DISABLE KEYS */;
/*!40000 ALTER TABLE `liquidity_token_farm_account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `node_heartbeat`
--

DROP TABLE IF EXISTS `node_heartbeat`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `node_heartbeat` (
  `node_id` varchar(34) COLLATE utf8mb4_unicode_ci NOT NULL,
  `beaten_at` decimal(21,0) DEFAULT NULL,
  `started_at` decimal(21,0) DEFAULT NULL,
  `created_at` bigint(20) NOT NULL,
  `created_by` varchar(70) COLLATE utf8mb4_unicode_ci NOT NULL,
  `updated_at` bigint(20) NOT NULL,
  `updated_by` varchar(70) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`node_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `node_heartbeat`
--

LOCK TABLES `node_heartbeat` WRITE;
/*!40000 ALTER TABLE `node_heartbeat` DISABLE KEYS */;
/*!40000 ALTER TABLE `node_heartbeat` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pulling_event_task`
--

DROP TABLE IF EXISTS `pulling_event_task`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pulling_event_task` (
  `from_block_number` decimal(21,0) NOT NULL,
  `created_at` bigint(20) NOT NULL,
  `created_by` varchar(70) COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `to_block_number` decimal(21,0) NOT NULL,
  `updated_at` bigint(20) NOT NULL,
  `updated_by` varchar(70) COLLATE utf8mb4_unicode_ci NOT NULL,
  `version` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`from_block_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pulling_event_task`
--

LOCK TABLES `pulling_event_task` WRITE;
/*!40000 ALTER TABLE `pulling_event_task` DISABLE KEYS */;
/*!40000 ALTER TABLE `pulling_event_task` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `syrup_pool`
--

DROP TABLE IF EXISTS `syrup_pool`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `syrup_pool` (
  `pool_address` varchar(66) COLLATE utf8mb4_unicode_ci NOT NULL,
  `token_id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` bigint(20) NOT NULL,
  `created_by` varchar(70) COLLATE utf8mb4_unicode_ci NOT NULL,
  `deactived` bit(1) NOT NULL,
  `description` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `estimated_apy` decimal(31,10) DEFAULT NULL,
  `reward_multiplier` int(11) DEFAULT NULL,
  `reward_token_id` varchar(15) COLLATE utf8mb4_unicode_ci NOT NULL,
  `sequence_number` int(11) NOT NULL,
  `total_stake_amount` decimal(31,0) DEFAULT NULL,
  `tvl_in_usd` decimal(51,10) DEFAULT NULL,
  `updated_at` bigint(20) NOT NULL,
  `updated_by` varchar(70) COLLATE utf8mb4_unicode_ci NOT NULL,
  `version` bigint(20) DEFAULT NULL,
  `daily_reward` decimal(31,0) DEFAULT NULL,
  PRIMARY KEY (`pool_address`,`token_id`),
  KEY `idx_token_id` (`token_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `syrup_pool`
--

LOCK TABLES `syrup_pool` WRITE;
/*!40000 ALTER TABLE `syrup_pool` DISABLE KEYS */;
INSERT INTO `syrup_pool` VALUES ('0x9bf32e42c442ae2adbc87bc7923610621469bf183266364503a7a434fe9d50ca','STAR',1643030987000,'admin',_binary '\0','STAR',36061.9777763100,NULL,'STAR',1,2011337271902,35.3187084742,1666882098135,'admin',21465,1987200000000);
/*!40000 ALTER TABLE `syrup_pool` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `syrup_pool_account`
--

DROP TABLE IF EXISTS `syrup_pool_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `syrup_pool_account` (
  `account_address` varchar(66) COLLATE utf8mb4_unicode_ci NOT NULL,
  `pool_address` varchar(66) COLLATE utf8mb4_unicode_ci NOT NULL,
  `token_id` varchar(15) COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` bigint(20) NOT NULL,
  `created_by` varchar(70) COLLATE utf8mb4_unicode_ci NOT NULL,
  `deactived` bit(1) NOT NULL,
  `updated_at` bigint(20) NOT NULL,
  `updated_by` varchar(70) COLLATE utf8mb4_unicode_ci NOT NULL,
  `version` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`account_address`,`pool_address`,`token_id`),
  KEY `idx_token_id` (`token_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `syrup_pool_account`
--

LOCK TABLES `syrup_pool_account` WRITE;
/*!40000 ALTER TABLE `syrup_pool_account` DISABLE KEYS */;
/*!40000 ALTER TABLE `syrup_pool_account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `token`
--

DROP TABLE IF EXISTS `token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `token` (
  `token_id` varchar(15) COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` bigint(20) NOT NULL,
  `created_by` varchar(70) COLLATE utf8mb4_unicode_ci NOT NULL,
  `deactived` bit(1) NOT NULL,
  `description` varchar(1000) COLLATE utf8mb4_unicode_ci NOT NULL,
  `icon_url` varchar(1000) COLLATE utf8mb4_unicode_ci NOT NULL,
  `sequence_number` int(11) NOT NULL,
  `token_struct_address` varchar(66) COLLATE utf8mb4_unicode_ci NOT NULL,
  `token_struct_module` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `token_struct_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `updated_at` bigint(20) NOT NULL,
  `updated_by` varchar(70) COLLATE utf8mb4_unicode_ci NOT NULL,
  `version` bigint(20) DEFAULT NULL,
  `scaling_factor` decimal(21,0) DEFAULT NULL,
  `to_usd_exchange_rate_path` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`token_id`),
  UNIQUE KEY `UniqueTokenCode` (`token_struct_address`,`token_struct_module`,`token_struct_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `token`
--

LOCK TABLES `token` WRITE;
/*!40000 ALTER TABLE `token` DISABLE KEYS */;
INSERT INTO `token` VALUES
('APT',1666239767000,'admin',_binary '\0','APT','',10,'0x1','aptos_coin','AptosCoin',1666239804425,'admin',1,100000000,'USDT'),
('STAR',1665313042000,'admin',_binary '\0','STAR','',10,'0x9bf32e42c442ae2adbc87bc7923610621469bf183266364503a7a434fe9d50ca','STAR','STAR',1665501404798,'admin',6,1000000000,'APT,USDT'),
('USDT',1665313200000,'admin',_binary '\0','USDT','',10,'0xf22bede237a07e121b56d91a491eb7bcdfd1f5907926a9e58338f964a01b17fa','asset','USDT',1665501405574,'admin',6,1000000,NULL)
;
/*!40000 ALTER TABLE `token` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `token_to_usd_price_pair_mapping`
--

DROP TABLE IF EXISTS `token_to_usd_price_pair_mapping`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `token_to_usd_price_pair_mapping` (
  `token_id` varchar(15) COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` bigint(20) NOT NULL,
  `created_by` varchar(70) COLLATE utf8mb4_unicode_ci NOT NULL,
  `is_usd_equivalent_token` bit(1) DEFAULT NULL,
  `pair_id` varchar(25) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updated_at` bigint(20) NOT NULL,
  `updated_by` varchar(70) COLLATE utf8mb4_unicode_ci NOT NULL,
  `version` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`token_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `token_to_usd_price_pair_mapping`
--

LOCK TABLES `token_to_usd_price_pair_mapping` WRITE;
/*!40000 ALTER TABLE `token_to_usd_price_pair_mapping` DISABLE KEYS */;
INSERT INTO `token_to_usd_price_pair_mapping` VALUES
('{TOKEN_ID}',1632567783923,'admin',_binary '\0','{TOKEN_ID}_USD',1632567783923,'admin',0),
('BX_USDT',1632567783987,'admin', true,'BX_USDT_USD',1632567783987,'admin',0),
('FAI',1643294475000,'admin', true,'FAI_USD',1643294475000,'admin',0),
('STC',1632567783999,'admin', _binary '\0','STCUSD',1632567783999,'admin',0),
('WEN',1648538663000,'admin', true,'WEN_USD',1648538663000,'admin',0),
('USDT',1639738530635,'admin', true,'USDT_USD',1639738530635,'admin',0);
/*!40000 ALTER TABLE `token_to_usd_price_pair_mapping` ENABLE KEYS */;
UNLOCK TABLES;


/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-10-31 17:46:21
