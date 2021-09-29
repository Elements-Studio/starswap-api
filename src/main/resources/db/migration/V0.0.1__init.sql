SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- MySQL dump 10.13  Distrib 8.0.25, for macos11 (x86_64)
--
-- Host: 127.0.0.1    Database: starswap
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
-- Table structure for table `liquidity_account`
--

-- DROP TABLE IF EXISTS `liquidity_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `liquidity_account` (
  `account_address` varchar(34) NOT NULL,
  `liquidity_token_address` varchar(34) NOT NULL,
  `token_x_id` varchar(15) NOT NULL,
  `token_y_id` varchar(15) NOT NULL,
  `pool_address` varchar(34) NOT NULL,
  `created_at` bigint(20) NOT NULL,
  `created_by` varchar(70) NOT NULL,
  `deactived` bit(1) NOT NULL,
  `liquidity` decimal(31,0) DEFAULT NULL,
  `updated_at` bigint(20) NOT NULL,
  `updated_by` varchar(70) NOT NULL,
  `version` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`account_address`,`liquidity_token_address`,`token_x_id`,`token_y_id`,`pool_address`),
  KEY `idx_token_x_y_id` (`token_x_id`,`token_y_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `liquidity_pool`
--

-- DROP TABLE IF EXISTS `liquidity_pool`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `liquidity_pool` (
  `liquidity_token_address` varchar(34) NOT NULL,
  `token_x_id` varchar(15) NOT NULL,
  `token_y_id` varchar(15) NOT NULL,
  `pool_address` varchar(34) NOT NULL,
  `created_at` bigint(20) NOT NULL,
  `created_by` varchar(70) NOT NULL,
  `deactived` bit(1) NOT NULL,
  `description` varchar(1000) NOT NULL,
  `sequence_number` int(11) NOT NULL,
  `total_liquidity` decimal(31,0) DEFAULT NULL,
  `updated_at` bigint(20) NOT NULL,
  `updated_by` varchar(70) NOT NULL,
  `version` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`liquidity_token_address`,`token_x_id`,`token_y_id`,`pool_address`),
  KEY `idx_token_x_y_id` (`token_x_id`,`token_y_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `liquidity_token`
--

-- DROP TABLE IF EXISTS `liquidity_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `liquidity_token` (
  `liquidity_token_address` varchar(34) NOT NULL,
  `token_x_id` varchar(15) NOT NULL,
  `token_y_id` varchar(15) NOT NULL,
  `created_at` bigint(20) NOT NULL,
  `created_by` varchar(70) NOT NULL,
  `deactived` bit(1) NOT NULL,
  `description` varchar(1000) NOT NULL,
  `sequence_number` int(11) NOT NULL,
  `token_x_struct_address` varchar(34) NOT NULL,
  `token_x_struct_module` varchar(255) NOT NULL,
  `token_x_struct_name` varchar(255) NOT NULL,
  `token_y_struct_address` varchar(34) NOT NULL,
  `token_y_struct_module` varchar(255) NOT NULL,
  `token_y_struct_name` varchar(255) NOT NULL,
  `updated_at` bigint(20) NOT NULL,
  `updated_by` varchar(70) NOT NULL,
  `version` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`liquidity_token_address`,`token_x_id`,`token_y_id`),
  KEY `idx_token_x_y_id` (`token_x_id`,`token_y_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `liquidity_token_farm`
--

-- DROP TABLE IF EXISTS `liquidity_token_farm`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `liquidity_token_farm` (
  `farm_address` varchar(34) NOT NULL,
  `liquidity_token_address` varchar(34) NOT NULL,
  `token_x_id` varchar(15) NOT NULL,
  `token_y_id` varchar(15) NOT NULL,
  `created_at` bigint(20) NOT NULL,
  `created_by` varchar(70) NOT NULL,
  `deactived` bit(1) NOT NULL,
  `description` varchar(1000) DEFAULT NULL,
  `estimated_apy` decimal(31,10) DEFAULT NULL,
  `sequence_number` int(11) NOT NULL,
  `total_stake_amount` decimal(31,0) DEFAULT NULL,
  `updated_at` bigint(20) NOT NULL,
  `updated_by` varchar(70) NOT NULL,
  `version` bigint(20) DEFAULT NULL,
  `reward_token_id` varchar(15) NOT NULL,
  `tvl_in_usd` decimal(51,10) DEFAULT NULL,
  PRIMARY KEY (`farm_address`,`liquidity_token_address`,`token_x_id`,`token_y_id`),
  KEY `idx_token_x_y_id` (`token_x_id`,`token_y_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `liquidity_token_farm_account`
--

-- DROP TABLE IF EXISTS `liquidity_token_farm_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `liquidity_token_farm_account` (
  `account_address` varchar(34) NOT NULL,
  `farm_address` varchar(34) NOT NULL,
  `liquidity_token_address` varchar(34) NOT NULL,
  `token_x_id` varchar(15) NOT NULL,
  `token_y_id` varchar(15) NOT NULL,
  `created_at` bigint(20) NOT NULL,
  `created_by` varchar(70) NOT NULL,
  `deactived` bit(1) NOT NULL,
  `stake_amount` decimal(31,0) DEFAULT NULL,
  `updated_at` bigint(20) NOT NULL,
  `updated_by` varchar(70) NOT NULL,
  `version` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`account_address`,`farm_address`,`liquidity_token_address`,`token_x_id`,`token_y_id`),
  KEY `idx_token_x_y_id` (`token_x_id`,`token_y_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `node_heartbeat`
--

-- DROP TABLE IF EXISTS `node_heartbeat`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `node_heartbeat` (
  `node_id` varchar(34) NOT NULL,
  `started_at` decimal(21,0) NOT NULL,
  `beaten_at` decimal(21,0) DEFAULT NULL,
  `created_at` bigint(20) NOT NULL,
  `created_by` varchar(70) NOT NULL,
  `updated_at` bigint(20) NOT NULL,
  `updated_by` varchar(70) NOT NULL,
  PRIMARY KEY (`node_id`,`started_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pulling_event_task`
--

-- DROP TABLE IF EXISTS `pulling_event_task`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pulling_event_task` (
  `from_block_number` decimal(21,0) NOT NULL,
  `created_at` bigint(20) NOT NULL,
  `created_by` varchar(70) NOT NULL,
  `status` varchar(20) NOT NULL,
  `to_block_number` decimal(21,0) NOT NULL,
  `updated_at` bigint(20) NOT NULL,
  `updated_by` varchar(70) NOT NULL,
  `version` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`from_block_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `token`
--

-- DROP TABLE IF EXISTS `token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `token` (
  `token_id` varchar(15) NOT NULL,
  `created_at` bigint(20) NOT NULL,
  `created_by` varchar(70) NOT NULL,
  `deactived` bit(1) NOT NULL,
  `description` varchar(1000) NOT NULL,
  `icon_url` varchar(1000) NOT NULL,
  `sequence_number` int(11) NOT NULL,
  `token_struct_address` varchar(34) NOT NULL,
  `token_struct_module` varchar(255) NOT NULL,
  `token_struct_name` varchar(255) NOT NULL,
  `updated_at` bigint(20) NOT NULL,
  `updated_by` varchar(70) NOT NULL,
  `version` bigint(20) DEFAULT NULL,
  `scaling_factor` decimal(21,0) DEFAULT NULL,
  PRIMARY KEY (`token_id`),
  UNIQUE KEY `UniqueTokenCode` (`token_struct_address`,`token_struct_module`,`token_struct_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-09-22 17:53:20


SET FOREIGN_KEY_CHECKS = 1;
