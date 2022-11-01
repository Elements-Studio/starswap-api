-- --------------------- update addresses ------------------

UPDATE `token` SET `token_struct_address` = '0x9bf32e42c442ae2adbc87bc7923610621469bf183266364503a7a434fe9d50ca'
  WHERE (`token_id` = 'STAR');
UPDATE `token` SET `token_struct_address` = '0x9bf32e42c442ae2adbc87bc7923610621469bf183266364503a7a434fe9d50ca'
  WHERE (`token_id` = 'XUSDT');

UPDATE `liquidity_token` SET `liquidity_token_address` = '0x9bf32e42c442ae2adbc87bc7923610621469bf183266364503a7a434fe9d50ca'
  WHERE (`liquidity_token_address` = '0xf0b07b5181ce76e447632cdff90525c0411fd15eb61df7da4e835cf88dc05f5b')
    and (`token_x_id` = 'APT') and (`token_y_id` = 'STAR');
UPDATE `liquidity_token` SET `liquidity_token_address` = '0x9bf32e42c442ae2adbc87bc7923610621469bf183266364503a7a434fe9d50ca'
  WHERE (`liquidity_token_address` = '0xf0b07b5181ce76e447632cdff90525c0411fd15eb61df7da4e835cf88dc05f5b')
    and (`token_x_id` = 'APT') and (`token_y_id` = 'XUSDT');

UPDATE `liquidity_pool` SET `liquidity_token_address` = '0x9bf32e42c442ae2adbc87bc7923610621469bf183266364503a7a434fe9d50ca',
  `pool_address` = '0x9bf32e42c442ae2adbc87bc7923610621469bf183266364503a7a434fe9d50ca'
     WHERE (`liquidity_token_address` = '0xf0b07b5181ce76e447632cdff90525c0411fd15eb61df7da4e835cf88dc05f5b')
       and (`token_x_id` = 'APT') and (`token_y_id` = 'STAR')
       and (`pool_address` = '0xf0b07b5181ce76e447632cdff90525c0411fd15eb61df7da4e835cf88dc05f5b');

UPDATE `liquidity_pool` SET `liquidity_token_address` = '0x9bf32e42c442ae2adbc87bc7923610621469bf183266364503a7a434fe9d50ca',
  `pool_address` = '0x9bf32e42c442ae2adbc87bc7923610621469bf183266364503a7a434fe9d50ca'
     WHERE (`liquidity_token_address` = '0xf0b07b5181ce76e447632cdff90525c0411fd15eb61df7da4e835cf88dc05f5b')
       and (`token_x_id` = 'APT') and (`token_y_id` = 'XUSDT')
       and (`pool_address` = '0xf0b07b5181ce76e447632cdff90525c0411fd15eb61df7da4e835cf88dc05f5b');

UPDATE `liquidity_token_farm` SET `farm_address` = '0x9bf32e42c442ae2adbc87bc7923610621469bf183266364503a7a434fe9d50ca',
  `liquidity_token_address` = '0x9bf32e42c442ae2adbc87bc7923610621469bf183266364503a7a434fe9d50ca'
    WHERE (`farm_address` = '0xf0b07b5181ce76e447632cdff90525c0411fd15eb61df7da4e835cf88dc05f5b')
      and (`liquidity_token_address` = '0xf0b07b5181ce76e447632cdff90525c0411fd15eb61df7da4e835cf88dc05f5b')
      and (`token_x_id` = 'APT') and (`token_y_id` = 'STAR');

UPDATE `liquidity_token_farm` SET `farm_address` = '0x9bf32e42c442ae2adbc87bc7923610621469bf183266364503a7a434fe9d50ca',
  `liquidity_token_address` = '0x9bf32e42c442ae2adbc87bc7923610621469bf183266364503a7a434fe9d50ca'
    WHERE (`farm_address` = '0xf0b07b5181ce76e447632cdff90525c0411fd15eb61df7da4e835cf88dc05f5b')
      and (`liquidity_token_address` = '0xf0b07b5181ce76e447632cdff90525c0411fd15eb61df7da4e835cf88dc05f5b')
      and (`token_x_id` = 'APT') and (`token_y_id` = 'XUSDT');

UPDATE `syrup_pool` SET `pool_address` = '0x9bf32e42c442ae2adbc87bc7923610621469bf183266364503a7a434fe9d50ca'
  WHERE (`pool_address` = '0xf0b07b5181ce76e447632cdff90525c0411fd15eb61df7da4e835cf88dc05f5b')
    and (`token_id` = 'STAR');




