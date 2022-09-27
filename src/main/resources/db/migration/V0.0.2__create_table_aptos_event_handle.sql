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
  UNIQUE KEY `UniqueEventDataType` (`event_data_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;