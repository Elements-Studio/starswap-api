-- 0x416b32009fe49fcab1d5f2ba0153838f::XUSDT::XUSDT
update token set token_struct_address = '0x416b32009fe49fcab1d5f2ba0153838f',
  description = 'XUSDT',
  scaling_factor = null
  where token_id = 'XUSDT';

-- 0x416b32009fe49fcab1d5f2ba0153838f::XETH::XETH
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
('XETH',
unix_timestamp()*1000,
'admin',
false,
'XETH',
'',
10,
'0x416b32009fe49fcab1d5f2ba0153838f',
'XETH',
'XETH',
unix_timestamp()*1000,
'admin',
0,
null,
null);

-- select * from token_to_usd_price_pair_mapping where is_usd_equivalent_token = true;
-- Assert XUSDT is USD equivalent token


-- Add STC / XUSDT liquidity token and pool --
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
('0x8c109349c6bd91411d6bc962e080c4a3',
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
`version`)
VALUES
('0x8c109349c6bd91411d6bc962e080c4a3',
'STC',
'XUSDT',
'0x8c109349c6bd91411d6bc962e080c4a3',
UNIX_TIMESTAMP() * 1000,
'admin',
false,
'STC / XUSDT',
1,
0,
UNIX_TIMESTAMP() * 1000,
'admin',
0);


-- Add STC / XETH liquidity token and pool --
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
('0x8c109349c6bd91411d6bc962e080c4a3',
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
`version`)
VALUES
('0x8c109349c6bd91411d6bc962e080c4a3',
'STC',
'XETH',
'0x8c109349c6bd91411d6bc962e080c4a3',
UNIX_TIMESTAMP() * 1000,
'admin',
false,
'STC / XETH',
1,
0,
UNIX_TIMESTAMP() * 1000,
'admin',
0);


-- Add FAI / XUSDT liquidity token and pool --
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
('0x8c109349c6bd91411d6bc962e080c4a3',
'FAI',
'XUSDT',
UNIX_TIMESTAMP() * 1000,
'admin',
false,
'FAI / XUSDT',
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
`version`)
VALUES
('0x8c109349c6bd91411d6bc962e080c4a3',
'FAI',
'XUSDT',
'0x8c109349c6bd91411d6bc962e080c4a3',
UNIX_TIMESTAMP() * 1000,
'admin',
false,
'STC / XUSDT',
1,
0,
UNIX_TIMESTAMP() * 1000,
'admin',
0);



-- ----------------- ADD FARMS ------------------
-- STC / XUSDT
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
'0x8c109349c6bd91411d6bc962e080c4a3',
'0x8c109349c6bd91411d6bc962e080c4a3',
'STC',
'XUSDT',
1639737823000,
'admin',
false,
'STC / XUSDT',
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

-- STC / XETH
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
'0x8c109349c6bd91411d6bc962e080c4a3',
'0x8c109349c6bd91411d6bc962e080c4a3',
'STC',
'XETH',
1639737823000,
'admin',
false,
'STC / XETH',
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

-- FAI / XUSDT
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
'0x8c109349c6bd91411d6bc962e080c4a3',
'0x8c109349c6bd91411d6bc962e080c4a3',
'FAI',
'XUSDT',
1639737823000,
'admin',
false,
'FAI / XUSDT',
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


-- update to-USD price pair info. --
INSERT INTO `token_to_usd_price_pair_mapping`
(`token_id`,
`created_at`,
`created_by`,
`is_usd_equivalent_token`,
`pair_id`,
`updated_at`,
`updated_by`,
`version`)
VALUES
('XETH',
1641964693187,
'admin',
false,
'ETH_USD',
1641964693187,
'admin',
0);

-- update to_usd_exchange_rate_path of XETH --
update token set to_usd_exchange_rate_path = 'STC,FAI' where token_id = 'XETH';
