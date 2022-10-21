-- --------------------- update addresses ------------------
UPDATE `token` SET `token_struct_address` = '0x0c3dbe4f07390f05b19ccfc083fc6aa5bc5d75621d131fc49557c8f4bbc11716'
  WHERE (`token_id` = 'STAR');
UPDATE `token` SET `token_struct_address` = '0x0c3dbe4f07390f05b19ccfc083fc6aa5bc5d75621d131fc49557c8f4bbc11716'
  WHERE (`token_id` = 'XUSDT');

UPDATE `liquidity_token` SET `liquidity_token_address` = '0x0c3dbe4f07390f05b19ccfc083fc6aa5bc5d75621d131fc49557c8f4bbc11716'
  WHERE (`liquidity_token_address` = '0x41422f5825e00c009a86ad42bc104228ac5f841313d8417ce69287e36776d1ee')
    and (`token_x_id` = 'STAR') and (`token_y_id` = 'XUSDT');

UPDATE `liquidity_pool`
  SET `liquidity_token_address` = '0x0c3dbe4f07390f05b19ccfc083fc6aa5bc5d75621d131fc49557c8f4bbc11716',
    `pool_address` = '0x0c3dbe4f07390f05b19ccfc083fc6aa5bc5d75621d131fc49557c8f4bbc11716'
  WHERE (`liquidity_token_address` = '0x41422f5825e00c009a86ad42bc104228ac5f841313d8417ce69287e36776d1ee')
    and (`token_x_id` = 'STAR') and (`token_y_id` = 'XUSDT')
    and (`pool_address` = '0x41422f5825e00c009a86ad42bc104228ac5f841313d8417ce69287e36776d1ee');

UPDATE `liquidity_token_farm`
  SET `farm_address` = '0x0c3dbe4f07390f05b19ccfc083fc6aa5bc5d75621d131fc49557c8f4bbc11716',
    `liquidity_token_address` = '0x0c3dbe4f07390f05b19ccfc083fc6aa5bc5d75621d131fc49557c8f4bbc11716'
  WHERE (`farm_address` = '0x41422f5825e00c009a86ad42bc104228ac5f841313d8417ce69287e36776d1ee')
    and (`liquidity_token_address` = '0x41422f5825e00c009a86ad42bc104228ac5f841313d8417ce69287e36776d1ee')
    and (`token_x_id` = 'STAR') and (`token_y_id` = 'XUSDT');

-- DELETE FROM `syrup_pool` WHERE (`pool_address` = '0xbda17e76b3c4d6c2c004a4dfdf5046e384facedab3e65134a5e1439373df0602') and (`token_id` = 'STAR');
-- DELETE FROM `syrup_pool` WHERE (`pool_address` = '0xee1f1439e9423f2c537e775d4cb92ea2cacdf0886165b7945db8262702c07049') and (`token_id` = 'STAR');



-- trim addresses --

UPDATE `token` SET `token_struct_address` = '0xc3dbe4f07390f05b19ccfc083fc6aa5bc5d75621d131fc49557c8f4bbc11716'
  WHERE (`token_id` = 'STAR');
UPDATE `token` SET `token_struct_address` = '0xc3dbe4f07390f05b19ccfc083fc6aa5bc5d75621d131fc49557c8f4bbc11716'
  WHERE (`token_id` = 'XUSDT');

UPDATE `liquidity_token` SET `liquidity_token_address` = '0xc3dbe4f07390f05b19ccfc083fc6aa5bc5d75621d131fc49557c8f4bbc11716'
  WHERE (`liquidity_token_address` = '0x0c3dbe4f07390f05b19ccfc083fc6aa5bc5d75621d131fc49557c8f4bbc11716')
    and (`token_x_id` = 'APT') and (`token_y_id` = 'STAR');
UPDATE `liquidity_token` SET `liquidity_token_address` = '0xc3dbe4f07390f05b19ccfc083fc6aa5bc5d75621d131fc49557c8f4bbc11716'
  WHERE (`liquidity_token_address` = '0x0c3dbe4f07390f05b19ccfc083fc6aa5bc5d75621d131fc49557c8f4bbc11716')
    and (`token_x_id` = 'STAR') and (`token_y_id` = 'XUSDT');

UPDATE `liquidity_pool` SET `liquidity_token_address` = '0xc3dbe4f07390f05b19ccfc083fc6aa5bc5d75621d131fc49557c8f4bbc11716',
  `pool_address` = '0xc3dbe4f07390f05b19ccfc083fc6aa5bc5d75621d131fc49557c8f4bbc11716'
     WHERE (`liquidity_token_address` = '0x0c3dbe4f07390f05b19ccfc083fc6aa5bc5d75621d131fc49557c8f4bbc11716')
       and (`token_x_id` = 'APT') and (`token_y_id` = 'STAR')
       and (`pool_address` = '0x0c3dbe4f07390f05b19ccfc083fc6aa5bc5d75621d131fc49557c8f4bbc11716');

UPDATE `liquidity_pool` SET `liquidity_token_address` = '0xc3dbe4f07390f05b19ccfc083fc6aa5bc5d75621d131fc49557c8f4bbc11716',
  `pool_address` = '0xc3dbe4f07390f05b19ccfc083fc6aa5bc5d75621d131fc49557c8f4bbc11716'
     WHERE (`liquidity_token_address` = '0x0c3dbe4f07390f05b19ccfc083fc6aa5bc5d75621d131fc49557c8f4bbc11716')
       and (`token_x_id` = 'STAR') and (`token_y_id` = 'XUSDT')
       and (`pool_address` = '0x0c3dbe4f07390f05b19ccfc083fc6aa5bc5d75621d131fc49557c8f4bbc11716');

UPDATE `liquidity_token_farm` SET `farm_address` = '0xc3dbe4f07390f05b19ccfc083fc6aa5bc5d75621d131fc49557c8f4bbc11716',
  `liquidity_token_address` = '0xc3dbe4f07390f05b19ccfc083fc6aa5bc5d75621d131fc49557c8f4bbc11716'
    WHERE (`farm_address` = '0x0c3dbe4f07390f05b19ccfc083fc6aa5bc5d75621d131fc49557c8f4bbc11716')
      and (`liquidity_token_address` = '0x0c3dbe4f07390f05b19ccfc083fc6aa5bc5d75621d131fc49557c8f4bbc11716')
      and (`token_x_id` = 'APT') and (`token_y_id` = 'STAR');

UPDATE `liquidity_token_farm` SET `farm_address` = '0xc3dbe4f07390f05b19ccfc083fc6aa5bc5d75621d131fc49557c8f4bbc11716',
  `liquidity_token_address` = '0xc3dbe4f07390f05b19ccfc083fc6aa5bc5d75621d131fc49557c8f4bbc11716'
    WHERE (`farm_address` = '0x0c3dbe4f07390f05b19ccfc083fc6aa5bc5d75621d131fc49557c8f4bbc11716')
      and (`liquidity_token_address` = '0x0c3dbe4f07390f05b19ccfc083fc6aa5bc5d75621d131fc49557c8f4bbc11716')
      and (`token_x_id` = 'STAR') and (`token_y_id` = 'XUSDT');

UPDATE `syrup_pool` SET `pool_address` = '0xc3dbe4f07390f05b19ccfc083fc6aa5bc5d75621d131fc49557c8f4bbc11716'
  WHERE (`pool_address` = '0x0c3dbe4f07390f05b19ccfc083fc6aa5bc5d75621d131fc49557c8f4bbc11716')
    and (`token_id` = 'STAR');




