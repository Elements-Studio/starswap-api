
-- Add STAR / USDT liquidity token and pool and farm --
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
('0xc3dbe4f07390f05b19ccfc083fc6aa5bc5d75621d131fc49557c8f4bbc11716',
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
('0xc3dbe4f07390f05b19ccfc083fc6aa5bc5d75621d131fc49557c8f4bbc11716',
'STAR',
'XUSDT',
'0xc3dbe4f07390f05b19ccfc083fc6aa5bc5d75621d131fc49557c8f4bbc11716',
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
'0xc3dbe4f07390f05b19ccfc083fc6aa5bc5d75621d131fc49557c8f4bbc11716',
'0xc3dbe4f07390f05b19ccfc083fc6aa5bc5d75621d131fc49557c8f4bbc11716',
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
