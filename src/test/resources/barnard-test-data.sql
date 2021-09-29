-- SELECT * FROM starswap_barnard.token;

-- 000000000000000000000000001::STC::STC
INSERT INTO `starswap_barnard`.`token`
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
`scaling_factor`)
VALUES
('STC',
UNIX_TIMESTAMP() * 1000,
'admin',
false,
'STC',
'',
1,
'000000000000000000000000001',
'STC',
'STC',
UNIX_TIMESTAMP() * 1000,
'admin',
0,
null);



-- 0x9350502a3af6c617e9a42fa9e306a385::BX_USDT::BX_USDT
INSERT INTO `starswap_barnard`.`token`
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
`scaling_factor`)
VALUES
('BX_USDT',
UNIX_TIMESTAMP() * 1000,
'admin',
false,
'BX USDT',
'',
2,
'0x9350502a3af6c617e9a42fa9e306a385',
'BX_USDT',
'BX_USDT',
UNIX_TIMESTAMP() * 1000,
'admin',
0,
null);

-- SELECT * FROM starswap_barnard.liquidity_token;

INSERT INTO `starswap_barnard`.`liquidity_token`
(`liquidity_token_address`,
`token_x_id`,
`token_y_id`,
`created_at`,
`created_by`,
`deactived`,
`description`,
`sequence_number`,
`token_x_struct_address`,
`token_x_struct_module`,
`token_x_struct_name`,
`token_y_struct_address`,
`token_y_struct_module`,
`token_y_struct_name`,
`updated_at`,
`updated_by`,
`version`)
VALUES
('0x598b8cbfd4536ecbe88aa1cfaffa7a62',
'BX_USDT',
'STC',
UNIX_TIMESTAMP() * 1000,
'admin',
false,
'STC / BX_USDT',
1,
'0x9350502a3af6c617e9a42fa9e306a385',
'BX_USDT',
'BX_USDT',
'000000000000000000000000001',
'STC',
'STC',
UNIX_TIMESTAMP() * 1000,
'admin',
0);

update token set token_struct_address = '0x00000000000000000000000000000001' where token_id = 'STC';

-- SELECT * FROM starswap_barnard.liquidity_pool;

INSERT INTO `starswap_barnard`.`liquidity_pool`
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
`version`)
VALUES
('0x598b8cbfd4536ecbe88aa1cfaffa7a62',
'BX_USDT',
'STC',
'0x598b8cbfd4536ecbe88aa1cfaffa7a62',
UNIX_TIMESTAMP() * 1000,
'admin',
false,
'STC / BX_USDT',
1,
0,
UNIX_TIMESTAMP() * 1000,
'admin',
0);

-- SELECT * FROM starswap_barnard.liquidity_token_farm;

INSERT INTO `starswap_barnard`.`liquidity_token_farm`
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
`tvl_in_usd`)
VALUES
('0x598b8cbfd4536ecbe88aa1cfaffa7a62',
'0x598b8cbfd4536ecbe88aa1cfaffa7a62',
'BX_USDT',
'STC',
UNIX_TIMESTAMP() * 1000,
'admin',
false,
'STC / BX_USDT',
100,
1,
100000000000000000,
UNIX_TIMESTAMP() * 1000,
'admin',
0,
'TBD',
1000000000000000);

-- delete some test records.

delete from token where token_id in ('Bot', 'Ddd', 'Usdx');

delete from liquidity_token where token_x_id in ('Bot', 'Ddd', 'Usdx') or token_y_id in ('Bot', 'Ddd', 'Usdx');

delete from liquidity_pool where token_x_id in ('Bot', 'Ddd', 'Usdx') or token_y_id in ('Bot', 'Ddd', 'Usdx');

delete from liquidity_token_farm where token_x_id in ('Bot', 'Ddd', 'Usdx') or token_y_id in ('Bot', 'Ddd', 'Usdx');

update liquidity_token_farm set description = 'BX_USDT / STC' where token_x_id = 'BX_USDT' and token_y_id = 'STC';



