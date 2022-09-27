
--
-- Table structure for table `aptos_event_handle`
--
ALTER TABLE `aptos_event_handle`
  CHANGE COLUMN `account_address` `account_address` varchar(66) COLLATE utf8mb4_unicode_ci NOT NULL;


--
-- Table structure for table `liquidity_account`
--
ALTER TABLE `liquidity_account`
  CHANGE COLUMN `account_address` `account_address` varchar(66) COLLATE utf8mb4_unicode_ci NOT NULL,
  CHANGE COLUMN `liquidity_token_address` `liquidity_token_address` varchar(66) COLLATE utf8mb4_unicode_ci NOT NULL,
  CHANGE COLUMN `pool_address` `pool_address` varchar(66) COLLATE utf8mb4_unicode_ci NOT NULL;


--
-- Table structure for table `liquidity_pool`
--
ALTER TABLE `liquidity_pool`
  CHANGE COLUMN `liquidity_token_address` `liquidity_token_address` varchar(66) COLLATE utf8mb4_unicode_ci NOT NULL,
  CHANGE COLUMN `pool_address` `pool_address` varchar(66) COLLATE utf8mb4_unicode_ci NOT NULL;


--
-- Table structure for table `liquidity_token`
--
ALTER TABLE `liquidity_token`
  CHANGE COLUMN `liquidity_token_address` `liquidity_token_address` varchar(66) COLLATE utf8mb4_unicode_ci NOT NULL;


--
-- Table structure for table `liquidity_token_farm`
--
ALTER TABLE `liquidity_token_farm`
  CHANGE COLUMN `farm_address` `farm_address` varchar(66) COLLATE utf8mb4_unicode_ci NOT NULL,
  CHANGE COLUMN `liquidity_token_address` `liquidity_token_address` varchar(66) COLLATE utf8mb4_unicode_ci NOT NULL;


--
-- Table structure for table `liquidity_token_farm_account`
--
ALTER TABLE `liquidity_token_farm_account`
  CHANGE COLUMN `account_address` `account_address` varchar(66) COLLATE utf8mb4_unicode_ci NOT NULL,
  CHANGE COLUMN `farm_address` `farm_address` varchar(66) COLLATE utf8mb4_unicode_ci NOT NULL,
  CHANGE COLUMN `liquidity_token_address` `liquidity_token_address` varchar(66) COLLATE utf8mb4_unicode_ci NOT NULL;


--
-- Table structure for table `syrup_pool`
--
ALTER TABLE `syrup_pool`
  CHANGE COLUMN `pool_address` `pool_address` varchar(66) COLLATE utf8mb4_unicode_ci NOT NULL;


--
-- Table structure for table `syrup_pool_account`
--
ALTER TABLE `syrup_pool_account`
  CHANGE COLUMN `account_address` `account_address` varchar(66) COLLATE utf8mb4_unicode_ci NOT NULL,
  CHANGE COLUMN `pool_address` `pool_address` varchar(66) COLLATE utf8mb4_unicode_ci NOT NULL;


--
-- Table structure for table `token`
--
ALTER TABLE `token`
  CHANGE COLUMN `token_struct_address` `token_struct_address` varchar(66) COLLATE utf8mb4_unicode_ci NOT NULL;



