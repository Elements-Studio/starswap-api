-- add token XUSDT, and make sure it is a stable coin.
--
-- select * from token_to_usd_price_pair_mapping where is_usd_equivalent_token = true;
--

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

-- then RESTART starswap API service --

