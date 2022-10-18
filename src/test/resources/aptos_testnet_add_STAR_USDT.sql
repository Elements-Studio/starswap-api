-- Add STAR Token --
INSERT INTO `token`
(`token_id`,
`created_at`,
`created_by`,
`deactived`,
`description`,
`icon_url`,
`sequence_number`,
`token_struct_address`,
`token_struct_module`,
`token_struct_name`,
`updated_at`,
`updated_by`,
`version`,
`scaling_factor`,
`to_usd_exchange_rate_path`)
VALUES
('STAR',
unix_timestamp()*1000,
'admin',
false,
'STAR',
'',
10,
'0x41422f5825e00c009a86ad42bc104228ac5f841313d8417ce69287e36776d1ee',
'STAR',
'STAR',
unix_timestamp()*1000,
'admin',
0,
null,
null);


-- Add XUSDT Token --
INSERT INTO `token`
(`token_id`,
`created_at`,
`created_by`,
`deactived`,
`description`,
`icon_url`,
`sequence_number`,
`token_struct_address`,
`token_struct_module`,
`token_struct_name`,
`updated_at`,
`updated_by`,
`version`,
`scaling_factor`,
`to_usd_exchange_rate_path`)
VALUES
('XUSDT',
unix_timestamp()*1000,
'admin',
false,
'XUSDT',
'',
10,
'0x41422f5825e00c009a86ad42bc104228ac5f841313d8417ce69287e36776d1ee',
'XUSDT',
'XUSDT',
unix_timestamp()*1000,
'admin',
0,
null,
null);


-- add token XUSDT, and make sure it is a stable coin.
--
-- select * from token_to_usd_price_pair_mapping where is_usd_equivalent_token = true;
--

-- Add STAR / STAR liquidity token and pool --
INSERT INTO `liquidity_token`
(`liquidity_token_address`,
`token_x_id`,
`token_y_id`,
`created_at`,
`created_by`,
`deactived`,
`description`,
`sequence_number`,
`updated_at`,
`updated_by`,
`version`)
VALUES
('0x41422f5825e00c009a86ad42bc104228ac5f841313d8417ce69287e36776d1ee',
'STAR',
'XUSDT',
UNIX_TIMESTAMP() * 1000,
'admin',
false,
'STAR / XUSDT',
1,
UNIX_TIMESTAMP() * 1000,
'admin',
0);



INSERT INTO `liquidity_pool`
(`liquidity_token_address`,
`token_x_id`,
`token_y_id`,
`pool_address`,
`created_at`,
`created_by`,
`deactived`,
`description`,
`sequence_number`,
`total_liquidity`,
`updated_at`,
`updated_by`,
`version`,
`poundage_rate_denominator`,
`poundage_rate_numerator`,
`swap_fee_op_rate_v2_denominator`,
`swap_fee_op_rate_v2_numerator`)
VALUES
('0x41422f5825e00c009a86ad42bc104228ac5f841313d8417ce69287e36776d1ee',
'STAR',
'XUSDT',
'0x41422f5825e00c009a86ad42bc104228ac5f841313d8417ce69287e36776d1ee',
UNIX_TIMESTAMP() * 1000,
'admin',
false,
'STAR / XUSDT',
1,
0,
UNIX_TIMESTAMP() * 1000,
'admin',
0,
1000,
3,
60,
10);


INSERT INTO `liquidity_token_farm`
(`farm_address`,
`liquidity_token_address`,
`token_x_id`,
`token_y_id`,
`created_at`,
`created_by`,
`deactived`,
`description`,
`estimated_apy`,
`sequence_number`,
`total_stake_amount`,
`updated_at`,
`updated_by`,
`version`,
`reward_token_id`,
`tvl_in_usd`,
`reward_multiplier`,
`daily_reward`)
VALUES
(
'0x41422f5825e00c009a86ad42bc104228ac5f841313d8417ce69287e36776d1ee',
'0x41422f5825e00c009a86ad42bc104228ac5f841313d8417ce69287e36776d1ee',
'STAR',
'XUSDT',
1639737823000,
'admin',
false,
'STAR / XUSDT',
1078.2467009100,
1,
413829993858,
1645429064042,
'admin',
0,
'STAR',
203.5576343452,
10,
1
);

-- then RESTART starswap API service --

-- --------------------- update addresses ------------------
UPDATE `token` SET `token_struct_address` = '0xbda17e76b3c4d6c2c004a4dfdf5046e384facedab3e65134a5e1439373df0602'
  WHERE (`token_id` = 'STAR');
UPDATE `token` SET `token_struct_address` = '0xbda17e76b3c4d6c2c004a4dfdf5046e384facedab3e65134a5e1439373df0602'
  WHERE (`token_id` = 'XUSDT');

UPDATE `liquidity_token` SET `liquidity_token_address` = '0xbda17e76b3c4d6c2c004a4dfdf5046e384facedab3e65134a5e1439373df0602'
  WHERE (`liquidity_token_address` = '0x41422f5825e00c009a86ad42bc104228ac5f841313d8417ce69287e36776d1ee')
    and (`token_x_id` = 'STAR') and (`token_y_id` = 'XUSDT');

UPDATE `liquidity_pool`
  SET `liquidity_token_address` = '0xbda17e76b3c4d6c2c004a4dfdf5046e384facedab3e65134a5e1439373df0602',
    `pool_address` = '0xbda17e76b3c4d6c2c004a4dfdf5046e384facedab3e65134a5e1439373df0602'
  WHERE (`liquidity_token_address` = '0x41422f5825e00c009a86ad42bc104228ac5f841313d8417ce69287e36776d1ee')
    and (`token_x_id` = 'STAR') and (`token_y_id` = 'XUSDT')
    and (`pool_address` = '0x41422f5825e00c009a86ad42bc104228ac5f841313d8417ce69287e36776d1ee');

UPDATE `liquidity_token_farm`
  SET `farm_address` = '0xbda17e76b3c4d6c2c004a4dfdf5046e384facedab3e65134a5e1439373df0602',
    `liquidity_token_address` = '0xbda17e76b3c4d6c2c004a4dfdf5046e384facedab3e65134a5e1439373df0602'
  WHERE (`farm_address` = '0x41422f5825e00c009a86ad42bc104228ac5f841313d8417ce69287e36776d1ee')
    and (`liquidity_token_address` = '0x41422f5825e00c009a86ad42bc104228ac5f841313d8417ce69287e36776d1ee')
    and (`token_x_id` = 'STAR') and (`token_y_id` = 'XUSDT');


-- ------------- add syrup pool record ----------------
INSERT INTO `syrup_pool`
(`pool_address`,
`token_id`,
`created_at`,
`created_by`,
`deactived`,
`description`,
`estimated_apy`,
`reward_multiplier`,
`reward_token_id`,
`sequence_number`,
`total_stake_amount`,
`tvl_in_usd`,
`updated_at`,
`updated_by`,
`version`,
`daily_reward`)
VALUES
(
'0xbda17e76b3c4d6c2c004a4dfdf5046e384facedab3e65134a5e1439373df0602',
'STAR',
'1643030987000',
'admin',
false,
'STAR',
'44.9099817200', NULL,
'STAR',
'1',
'1615070797703840',
'2561.8202972498',
'1665495043586',
'admin',
'19568', '1987200000000'
);
