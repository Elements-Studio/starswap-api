use starswap_barnard;

-- ------------------------ tokens ----------------------------
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


-- 0x2d81a0427d64ff61b11ede9085efa5ad::XUSDT::XUSDT
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
('XUSDT',
UNIX_TIMESTAMP() * 1000,
'admin',
false,
'USDT',
'',
2,
'0x2d81a0427d64ff61b11ede9085efa5ad',
'XUSDT',
'XUSDT',
UNIX_TIMESTAMP() * 1000,
'admin',
0,
null);


-- 0x2d81a0427d64ff61b11ede9085efa5ad::XETH::XETH
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
('XETH',
UNIX_TIMESTAMP() * 1000,
'admin',
false,
'ETH',
'',
2,
'0x2d81a0427d64ff61b11ede9085efa5ad',
'XETH',
'XETH',
UNIX_TIMESTAMP() * 1000,
'admin',
0,
null);


-- 0xfe125d419811297dfab03c61efec0bc9::FAI::FAI
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
('FAI',
UNIX_TIMESTAMP() * 1000,
'admin',
false,
'FAI',
'',
2,
'0xfe125d419811297dfab03c61efec0bc9',
'FAI',
'FAI',
UNIX_TIMESTAMP() * 1000,
'admin',
0,
null);


-- 0x4783d08fb16990bd35d83f3e23bf93b8::STAR::STAR
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
('STAR',
UNIX_TIMESTAMP() * 1000,
'admin',
false,
'FAI',
'',
2,
'0x4783d08fb16990bd35d83f3e23bf93b8',
'STAR',
'STAR',
UNIX_TIMESTAMP() * 1000,
'admin',
0,
null);


-- -------------------- liquidity_token pairs ------------------------

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
`updated_at`,
`updated_by`,
`version`)
VALUES
('0x4783d08fb16990bd35d83f3e23bf93b8',
'STC',
'XUSDT',
UNIX_TIMESTAMP() * 1000,
'admin',
false,
'STC / XUSDT',
1,
UNIX_TIMESTAMP() * 1000,
'admin',
0);


INSERT INTO `starswap_barnard`.`liquidity_token`
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
('0x4783d08fb16990bd35d83f3e23bf93b8',
'STC',
'XETH',
UNIX_TIMESTAMP() * 1000,
'admin',
false,
'STC / XETH',
1,
UNIX_TIMESTAMP() * 1000,
'admin',
0);


INSERT INTO `starswap_barnard`.`liquidity_token`
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
('0x4783d08fb16990bd35d83f3e23bf93b8',
'FAI',
'STC',
UNIX_TIMESTAMP() * 1000,
'admin',
false,
'STC / FAI',
1,
UNIX_TIMESTAMP() * 1000,
'admin',
0);

-- update token set token_struct_address = '0x00000000000000000000000000000001' where token_id = 'STC';


-- -------------------- swap pools --------------------
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
('0x4783d08fb16990bd35d83f3e23bf93b8',
'STC',
'XUSDT',
'0x4783d08fb16990bd35d83f3e23bf93b8',
UNIX_TIMESTAMP() * 1000,
'admin',
false,
'STC / XUSDT',
1,
0,
UNIX_TIMESTAMP() * 1000,
'admin',
0);


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
('0x4783d08fb16990bd35d83f3e23bf93b8',
'STC',
'XETH',
'0x4783d08fb16990bd35d83f3e23bf93b8',
UNIX_TIMESTAMP() * 1000,
'admin',
false,
'STC / XETH',
1,
0,
UNIX_TIMESTAMP() * 1000,
'admin',
0);


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
('0x4783d08fb16990bd35d83f3e23bf93b8',
'FAI',
'STC',
'0x4783d08fb16990bd35d83f3e23bf93b8',
UNIX_TIMESTAMP() * 1000,
'admin',
false,
'STC / FAI',
1,
0,
UNIX_TIMESTAMP() * 1000,
'admin',
0);


-- ----------------------- farming ------------------------------
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
('0x4783d08fb16990bd35d83f3e23bf93b8',
'0x4783d08fb16990bd35d83f3e23bf93b8',
'STC',
'XUSDT',
UNIX_TIMESTAMP() * 1000,
'admin',
false,
'STC / XUSDT',
100,
1,
100000000000000000,
UNIX_TIMESTAMP() * 1000,
'admin',
0,
'STAR',
1000000000000000);



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
('0x4783d08fb16990bd35d83f3e23bf93b8',
'0x4783d08fb16990bd35d83f3e23bf93b8',
'FAI',
'STC',
UNIX_TIMESTAMP() * 1000,
'admin',
false,
'STC / FAI',
100,
1,
100000000000000000,
UNIX_TIMESTAMP() * 1000,
'admin',
0,
'STAR',
1000000000000000);



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
('0x4783d08fb16990bd35d83f3e23bf93b8',
'0x4783d08fb16990bd35d83f3e23bf93b8',
'STC',
'XETH',
UNIX_TIMESTAMP() * 1000,
'admin',
false,
'STC / XETH',
100,
1,
100000000000000000,
UNIX_TIMESTAMP() * 1000,
'admin',
0,
'STAR',
1000000000000000);


-- delete some test records.
-- delete from token where token_id in ('Bot', 'Ddd', 'Usdx');
-- delete from liquidity_token where token_x_id in ('Bot', 'Ddd', 'Usdx') or token_y_id in ('Bot', 'Ddd', 'Usdx');
-- delete from liquidity_pool where token_x_id in ('Bot', 'Ddd', 'Usdx') or token_y_id in ('Bot', 'Ddd', 'Usdx');
-- delete from liquidity_token_farm where token_x_id in ('Bot', 'Ddd', 'Usdx') or token_y_id in ('Bot', 'Ddd', 'Usdx');
-- update liquidity_token_farm set description = 'XUSDT / STC' where token_x_id = 'XUSDT' and token_y_id = 'STC';





