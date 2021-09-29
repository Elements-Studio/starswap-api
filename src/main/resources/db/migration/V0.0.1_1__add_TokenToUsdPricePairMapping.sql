CREATE TABLE `token_to_usd_price_pair_mapping` (
  `token_id` varchar(15) NOT NULL,
  `created_at` bigint(20) NOT NULL,
  `created_by` varchar(70) NOT NULL,
  `is_usd_equivalent_token` bit(1) DEFAULT NULL,
  `pair_id` varchar(25) DEFAULT NULL,
  `updated_at` bigint(20) NOT NULL,
  `updated_by` varchar(70) NOT NULL,
  `version` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`token_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;