-- Add WEN Token --
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
('WEN',
unix_timestamp()*1000,
'admin',
false,
'WEN',
'',
10,
'0x88e2677b89841cd4ee7c15535798e1c8',
'WEN',
'WEN',
unix_timestamp()*1000,
'admin',
0,
null,
null);


-- Add STC / WEN liquidity token and pool --
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
'WEN',
UNIX_TIMESTAMP() * 1000,
'admin',
false,
'STC / WEN',
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
'WEN',
'0x8c109349c6bd91411d6bc962e080c4a3',
UNIX_TIMESTAMP() * 1000,
'admin',
false,
'STC / WEN',
1,
0,
UNIX_TIMESTAMP() * 1000,
'admin',
0);


-- WEN is a USD stable coin --
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
('WEN',
unix_timestamp()*1000,
'admin',
true,
'WEN_USD',
unix_timestamp()*1000,
'admin',
0);


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
'WEN',
1639737823000,
'admin',
false,
'STC / WEN',
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

