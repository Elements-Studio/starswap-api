
-- update XUSDT to USDT
UPDATE `token` SET `token_id` = 'USDT', `description` = 'USDT', `token_struct_module` = 'asset', `token_struct_name` = 'USDT'
  WHERE (`token_id` = 'XUSDT');

UPDATE `token` SET `to_usd_exchange_rate_path` = 'USDT' WHERE (`token_id` = 'APT');

UPDATE `token` SET `to_usd_exchange_rate_path` = 'APT,USDT' WHERE (`token_id` = 'STAR');


UPDATE `liquidity_token` SET `token_y_id` = 'USDT', `description` = 'APT / USDT'
  WHERE (`liquidity_token_address` = '0x9bf32e42c442ae2adbc87bc7923610621469bf183266364503a7a434fe9d50ca')
    and (`token_x_id` = 'APT') and (`token_y_id` = 'XUSDT');

UPDATE `liquidity_pool` SET `token_y_id` = 'USDT', `description` = 'APT / USDT'
  WHERE (`liquidity_token_address` = '0x9bf32e42c442ae2adbc87bc7923610621469bf183266364503a7a434fe9d50ca')
    and (`token_x_id` = 'APT') and (`token_y_id` = 'XUSDT')
    and (`pool_address` = '0x9bf32e42c442ae2adbc87bc7923610621469bf183266364503a7a434fe9d50ca');

UPDATE `liquidity_token_farm` SET `token_y_id` = 'USDT', `description` = 'APT / USDT'
  WHERE (`farm_address` = '0x9bf32e42c442ae2adbc87bc7923610621469bf183266364503a7a434fe9d50ca')
    and (`liquidity_token_address` = '0x9bf32e42c442ae2adbc87bc7923610621469bf183266364503a7a434fe9d50ca')
    and (`token_x_id` = 'APT') and (`token_y_id` = 'XUSDT');


UPDATE `token_to_usd_price_pair_mapping` SET `token_id` = 'USDT', `pair_id` = 'USDT_USD'
  WHERE (`token_id` = 'BX_USDT');




