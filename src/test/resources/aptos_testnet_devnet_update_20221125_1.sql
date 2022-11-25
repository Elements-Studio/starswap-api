
-- use USDC, remove USDT

DELETE FROM `token` WHERE (`token_id` = 'USDT');
INSERT INTO `token` VALUES
('USDC',1665313200000,'admin',_binary '\0','USDC','',10,
  '0xc755e4c8d7a6ab6d56f9289d97c43c1c94bde75ec09147c90d35cd1be61c8fb9','asset','USDC',
  1665501405574,'admin',6,1000000,NULL
);

UPDATE `liquidity_token` SET
  `token_y_id` = 'USDC',
  `description` = 'APT / USDC'
  WHERE (
    `liquidity_token_address` = '0x9bf32e42c442ae2adbc87bc7923610621469bf183266364503a7a434fe9d50ca')
      and (`token_x_id` = 'APT')
      and (`token_y_id` = 'USDT')
;

UPDATE `liquidity_pool` SET
  `token_y_id` = 'USDC', `description` = 'APT / USDC'
  WHERE (
    `liquidity_token_address` = '0x9bf32e42c442ae2adbc87bc7923610621469bf183266364503a7a434fe9d50ca')
       and (`token_x_id` = 'APT')
       and (`token_y_id` = 'USDT')
       and (`pool_address` = '0x9bf32e42c442ae2adbc87bc7923610621469bf183266364503a7a434fe9d50ca')
;

UPDATE `liquidity_token_farm` SET
  `token_y_id` = 'USDC', `description` = 'APT / USDC'
  WHERE (
    `liquidity_token_address` = '0x9bf32e42c442ae2adbc87bc7923610621469bf183266364503a7a434fe9d50ca')
       and (`token_x_id` = 'APT')
       and (`token_y_id` = 'USDT')
;

INSERT INTO `token_to_usd_price_pair_mapping` (
  `token_id`,
  `created_at`,
  `created_by`,
  `is_usd_equivalent_token`,
  `pair_id`,
  `updated_at`,
  `updated_by`,
  `version`
) VALUES (
  'USDC', 1632567783987, 'admin',
  true,
  'USDC_USD',
  1632567783987,
  'admin',
  0
);

UPDATE `token` SET `to_usd_exchange_rate_path` = 'USDC' WHERE (`token_id` = 'APT');
UPDATE `token` SET `to_usd_exchange_rate_path` = 'APT,USDC' WHERE (`token_id` = 'STAR');

-- --------------------- update addresses ------------------

UPDATE `token` SET `token_struct_address` = '0xc755e4c8d7a6ab6d56f9289d97c43c1c94bde75ec09147c90d35cd1be61c8fb9'
  WHERE (`token_id` = 'STAR');

UPDATE `liquidity_token` SET `liquidity_token_address` = '0xc755e4c8d7a6ab6d56f9289d97c43c1c94bde75ec09147c90d35cd1be61c8fb9'
  WHERE (`liquidity_token_address` = '0x9bf32e42c442ae2adbc87bc7923610621469bf183266364503a7a434fe9d50ca')
    and (`token_x_id` = 'APT') and (`token_y_id` = 'STAR');
UPDATE `liquidity_token` SET `liquidity_token_address` = '0xc755e4c8d7a6ab6d56f9289d97c43c1c94bde75ec09147c90d35cd1be61c8fb9'
  WHERE (`liquidity_token_address` = '0x9bf32e42c442ae2adbc87bc7923610621469bf183266364503a7a434fe9d50ca')
    and (`token_x_id` = 'APT') and (`token_y_id` = 'USDC');

UPDATE `liquidity_pool` SET `liquidity_token_address` = '0xc755e4c8d7a6ab6d56f9289d97c43c1c94bde75ec09147c90d35cd1be61c8fb9',
  `pool_address` = '0xc755e4c8d7a6ab6d56f9289d97c43c1c94bde75ec09147c90d35cd1be61c8fb9'
     WHERE (`liquidity_token_address` = '0x9bf32e42c442ae2adbc87bc7923610621469bf183266364503a7a434fe9d50ca')
       and (`token_x_id` = 'APT') and (`token_y_id` = 'STAR')
       and (`pool_address` = '0x9bf32e42c442ae2adbc87bc7923610621469bf183266364503a7a434fe9d50ca');

UPDATE `liquidity_pool` SET `liquidity_token_address` = '0xc755e4c8d7a6ab6d56f9289d97c43c1c94bde75ec09147c90d35cd1be61c8fb9',
  `pool_address` = '0xc755e4c8d7a6ab6d56f9289d97c43c1c94bde75ec09147c90d35cd1be61c8fb9'
     WHERE (`liquidity_token_address` = '0x9bf32e42c442ae2adbc87bc7923610621469bf183266364503a7a434fe9d50ca')
       and (`token_x_id` = 'APT') and (`token_y_id` = 'USDC')
       and (`pool_address` = '0x9bf32e42c442ae2adbc87bc7923610621469bf183266364503a7a434fe9d50ca');

UPDATE `liquidity_token_farm` SET `farm_address` = '0xc755e4c8d7a6ab6d56f9289d97c43c1c94bde75ec09147c90d35cd1be61c8fb9',
  `liquidity_token_address` = '0xc755e4c8d7a6ab6d56f9289d97c43c1c94bde75ec09147c90d35cd1be61c8fb9'
    WHERE (`farm_address` = '0x9bf32e42c442ae2adbc87bc7923610621469bf183266364503a7a434fe9d50ca')
      and (`liquidity_token_address` = '0x9bf32e42c442ae2adbc87bc7923610621469bf183266364503a7a434fe9d50ca')
      and (`token_x_id` = 'APT') and (`token_y_id` = 'STAR');

UPDATE `liquidity_token_farm` SET `farm_address` = '0xc755e4c8d7a6ab6d56f9289d97c43c1c94bde75ec09147c90d35cd1be61c8fb9',
  `liquidity_token_address` = '0xc755e4c8d7a6ab6d56f9289d97c43c1c94bde75ec09147c90d35cd1be61c8fb9'
    WHERE (`farm_address` = '0x9bf32e42c442ae2adbc87bc7923610621469bf183266364503a7a434fe9d50ca')
      and (`liquidity_token_address` = '0x9bf32e42c442ae2adbc87bc7923610621469bf183266364503a7a434fe9d50ca')
      and (`token_x_id` = 'APT') and (`token_y_id` = 'USDC');

UPDATE `syrup_pool` SET `pool_address` = '0xc755e4c8d7a6ab6d56f9289d97c43c1c94bde75ec09147c90d35cd1be61c8fb9'
  WHERE (`pool_address` = '0x9bf32e42c442ae2adbc87bc7923610621469bf183266364503a7a434fe9d50ca')
    and (`token_id` = 'STAR');



