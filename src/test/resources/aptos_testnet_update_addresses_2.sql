-- --------------------- update addresses ------------------

UPDATE `token` SET `token_struct_address` = '0xf0b07b5181ce76e447632cdff90525c0411fd15eb61df7da4e835cf88dc05f5b'
  WHERE (`token_id` = 'STAR');
UPDATE `token` SET `token_struct_address` = '0xf0b07b5181ce76e447632cdff90525c0411fd15eb61df7da4e835cf88dc05f5b'
  WHERE (`token_id` = 'XUSDT');

UPDATE `liquidity_token` SET `liquidity_token_address` = '0xf0b07b5181ce76e447632cdff90525c0411fd15eb61df7da4e835cf88dc05f5b'
  WHERE (`liquidity_token_address` = '0xc3dbe4f07390f05b19ccfc083fc6aa5bc5d75621d131fc49557c8f4bbc11716')
    and (`token_x_id` = 'APT') and (`token_y_id` = 'STAR');
UPDATE `liquidity_token` SET `liquidity_token_address` = '0xf0b07b5181ce76e447632cdff90525c0411fd15eb61df7da4e835cf88dc05f5b'
  WHERE (`liquidity_token_address` = '0xc3dbe4f07390f05b19ccfc083fc6aa5bc5d75621d131fc49557c8f4bbc11716')
    and (`token_x_id` = 'APT') and (`token_y_id` = 'XUSDT');

UPDATE `liquidity_pool` SET `liquidity_token_address` = '0xf0b07b5181ce76e447632cdff90525c0411fd15eb61df7da4e835cf88dc05f5b',
  `pool_address` = '0xf0b07b5181ce76e447632cdff90525c0411fd15eb61df7da4e835cf88dc05f5b'
     WHERE (`liquidity_token_address` = '0xc3dbe4f07390f05b19ccfc083fc6aa5bc5d75621d131fc49557c8f4bbc11716')
       and (`token_x_id` = 'APT') and (`token_y_id` = 'STAR')
       and (`pool_address` = '0xc3dbe4f07390f05b19ccfc083fc6aa5bc5d75621d131fc49557c8f4bbc11716');

UPDATE `liquidity_pool` SET `liquidity_token_address` = '0xf0b07b5181ce76e447632cdff90525c0411fd15eb61df7da4e835cf88dc05f5b',
  `pool_address` = '0xf0b07b5181ce76e447632cdff90525c0411fd15eb61df7da4e835cf88dc05f5b'
     WHERE (`liquidity_token_address` = '0xc3dbe4f07390f05b19ccfc083fc6aa5bc5d75621d131fc49557c8f4bbc11716')
       and (`token_x_id` = 'APT') and (`token_y_id` = 'XUSDT')
       and (`pool_address` = '0xc3dbe4f07390f05b19ccfc083fc6aa5bc5d75621d131fc49557c8f4bbc11716');

UPDATE `liquidity_token_farm` SET `farm_address` = '0xf0b07b5181ce76e447632cdff90525c0411fd15eb61df7da4e835cf88dc05f5b',
  `liquidity_token_address` = '0xf0b07b5181ce76e447632cdff90525c0411fd15eb61df7da4e835cf88dc05f5b'
    WHERE (`farm_address` = '0xc3dbe4f07390f05b19ccfc083fc6aa5bc5d75621d131fc49557c8f4bbc11716')
      and (`liquidity_token_address` = '0xc3dbe4f07390f05b19ccfc083fc6aa5bc5d75621d131fc49557c8f4bbc11716')
      and (`token_x_id` = 'APT') and (`token_y_id` = 'STAR');

UPDATE `liquidity_token_farm` SET `farm_address` = '0xf0b07b5181ce76e447632cdff90525c0411fd15eb61df7da4e835cf88dc05f5b',
  `liquidity_token_address` = '0xf0b07b5181ce76e447632cdff90525c0411fd15eb61df7da4e835cf88dc05f5b'
    WHERE (`farm_address` = '0xc3dbe4f07390f05b19ccfc083fc6aa5bc5d75621d131fc49557c8f4bbc11716')
      and (`liquidity_token_address` = '0xc3dbe4f07390f05b19ccfc083fc6aa5bc5d75621d131fc49557c8f4bbc11716')
      and (`token_x_id` = 'APT') and (`token_y_id` = 'XUSDT');

UPDATE `syrup_pool` SET `pool_address` = '0xf0b07b5181ce76e447632cdff90525c0411fd15eb61df7da4e835cf88dc05f5b'
  WHERE (`pool_address` = '0xc3dbe4f07390f05b19ccfc083fc6aa5bc5d75621d131fc49557c8f4bbc11716')
    and (`token_id` = 'STAR');




