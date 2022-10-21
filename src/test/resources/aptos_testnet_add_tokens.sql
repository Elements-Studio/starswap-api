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
'0xc3dbe4f07390f05b19ccfc083fc6aa5bc5d75621d131fc49557c8f4bbc11716',
'STAR',
'STAR',
unix_timestamp()*1000,
'admin',
0,
null,
null);


-- ---------- insert token APT -----------
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
('APT',
unix_timestamp()*1000,
'admin',
false,
'APT',
'',
10,
'0x1',
'aptos_coin',
'AptosCoin',
unix_timestamp()*1000,
'admin',
0,
null,
'STAR,XUSDT');



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
'0xc3dbe4f07390f05b19ccfc083fc6aa5bc5d75621d131fc49557c8f4bbc11716',
'XUSDT',
'XUSDT',
unix_timestamp()*1000,
'admin',
0,
null,
null);

--
-- add token XUSDT, and make sure it is a stable coin.
--
-- select * from token_to_usd_price_pair_mapping where is_usd_equivalent_token = true;
--

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
'0xc3dbe4f07390f05b19ccfc083fc6aa5bc5d75621d131fc49557c8f4bbc11716',
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

-- ------------- update to-USD exchange rate path ----------------
UPDATE `token` SET `to_usd_exchange_rate_path` = 'XUSDT' WHERE (`token_id` = 'APT');
UPDATE `token` SET `to_usd_exchange_rate_path` = 'APT,XUSDT' WHERE (`token_id` = 'STAR');
