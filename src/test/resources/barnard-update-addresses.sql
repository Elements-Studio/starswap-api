-- update addresses
UPDATE liquidity_account
SET
    liquidity_token_address = '0x8c109349c6bd91411d6bc962e080c4a3'
WHERE
    liquidity_token_address = '0x4783d08fb16990bd35d83f3e23bf93b8';

UPDATE liquidity_account
SET
    pool_address = '0x8c109349c6bd91411d6bc962e080c4a3'
WHERE
    pool_address = '0x4783d08fb16990bd35d83f3e23bf93b8';


UPDATE liquidity_pool
SET
    liquidity_token_address = '0x8c109349c6bd91411d6bc962e080c4a3'
WHERE
    liquidity_token_address = '0x4783d08fb16990bd35d83f3e23bf93b8';

UPDATE liquidity_pool
SET
    pool_address = '0x8c109349c6bd91411d6bc962e080c4a3'
WHERE
    pool_address = '0x4783d08fb16990bd35d83f3e23bf93b8';



UPDATE token
SET
    token_struct_address = '0x8c109349c6bd91411d6bc962e080c4a3'
WHERE
    token_struct_address = '0x4783d08fb16990bd35d83f3e23bf93b8'
    AND token_id = 'STAR';


UPDATE liquidity_token
SET
    liquidity_token_address = '0x8c109349c6bd91411d6bc962e080c4a3'
WHERE
    liquidity_token_address = '0x4783d08fb16990bd35d83f3e23bf93b8';

-- UPDATE liquidity_token
-- SET
--     token_x_struct_address = '0x8c109349c6bd91411d6bc962e080c4a3'
-- WHERE
--     token_x_struct_address = '0x4783d08fb16990bd35d83f3e23bf93b8'
--         AND token_x_id = 'TBD';

-- UPDATE liquidity_token
-- SET
--     token_y_struct_address = '0x8c109349c6bd91411d6bc962e080c4a3'
-- WHERE
--     token_y_struct_address = '0x4783d08fb16990bd35d83f3e23bf93b8'
--         AND token_y_id = 'TBD';


UPDATE liquidity_token_farm
SET
    farm_address = '0x8c109349c6bd91411d6bc962e080c4a3'
WHERE
    farm_address = '0x4783d08fb16990bd35d83f3e23bf93b8';

UPDATE liquidity_token_farm
SET
    liquidity_token_address = '0x8c109349c6bd91411d6bc962e080c4a3'
WHERE
    liquidity_token_address = '0x4783d08fb16990bd35d83f3e23bf93b8';


UPDATE liquidity_token_farm_account
SET
    farm_address = '0x8c109349c6bd91411d6bc962e080c4a3'
WHERE
    farm_address = '0x4783d08fb16990bd35d83f3e23bf93b8';

UPDATE liquidity_token_farm_account
SET
    liquidity_token_address = '0x8c109349c6bd91411d6bc962e080c4a3'
WHERE
    liquidity_token_address = '0x4783d08fb16990bd35d83f3e23bf93b8';

-- -----------------------
update `syrup_pool`
    set pool_address = '0x8c109349c6bd91411d6bc962e080c4a3'
    where pool_address = '0x4783d08fb16990bd35d83f3e23bf93b8';

UPDATE `syrup_pool_account`
    set pool_address = '0x8c109349c6bd91411d6bc962e080c4a3'
    where pool_address = '0x4783d08fb16990bd35d83f3e23bf93b8';


-- select * from node_heartbeat;
delete from node_heartbeat where node_id != '1qaz';

update pulling_event_task set status ='DONE';

delete from liquidity_token_farm where token_x_id = 'STC' and token_y_id = 'XETH';
delete from liquidity_token_farm where token_x_id = 'STC' and token_y_id = 'XUSDT';

delete from liquidity_token where token_x_id = 'STC' and token_y_id = 'XETH';
delete from liquidity_token where token_x_id = 'STC' and token_y_id = 'XUSDT';

delete from liquidity_pool where token_x_id = 'STC' and token_y_id = 'XETH';
delete from liquidity_pool where token_x_id = 'STC' and token_y_id = 'XUSDT';

-- update token t set t.deactived = true where t.token_id = 'XUSDT';
update token t set t.token_struct_address = '0xb6d69dd935edf7f2054acf12eb884df8' where token_id = 'XUSDT';
