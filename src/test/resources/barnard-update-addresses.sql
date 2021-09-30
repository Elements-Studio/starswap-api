-- update addresses
UPDATE liquidity_account
SET
    liquidity_token_address = '0x3db7a2da7444995338a2413b151ee437'
WHERE
    liquidity_token_address = '0x598b8cbfd4536ecbe88aa1cfaffa7a62';

UPDATE liquidity_account
SET
    pool_address = '0x3db7a2da7444995338a2413b151ee437'
WHERE
    pool_address = '0x598b8cbfd4536ecbe88aa1cfaffa7a62';


UPDATE liquidity_pool
SET
    liquidity_token_address = '0x3db7a2da7444995338a2413b151ee437'
WHERE
    liquidity_token_address = '0x598b8cbfd4536ecbe88aa1cfaffa7a62';

UPDATE liquidity_pool
SET
    pool_address = '0x3db7a2da7444995338a2413b151ee437'
WHERE
    pool_address = '0x598b8cbfd4536ecbe88aa1cfaffa7a62';



UPDATE token
SET
    token_struct_address = '0x3db7a2da7444995338a2413b151ee437'
WHERE
    token_struct_address = '0x598b8cbfd4536ecbe88aa1cfaffa7a62'
        AND token_id = 'TBD';


UPDATE liquidity_token
SET
    liquidity_token_address = '0x3db7a2da7444995338a2413b151ee437'
WHERE
    liquidity_token_address = '0x598b8cbfd4536ecbe88aa1cfaffa7a62';

UPDATE liquidity_token
SET
    token_x_struct_address = '0x3db7a2da7444995338a2413b151ee437'
WHERE
    token_x_struct_address = '0x598b8cbfd4536ecbe88aa1cfaffa7a62'
        AND token_x_id = 'TBD';

UPDATE liquidity_token
SET
    token_y_struct_address = '0x3db7a2da7444995338a2413b151ee437'
WHERE
    token_y_struct_address = '0x598b8cbfd4536ecbe88aa1cfaffa7a62'
        AND token_y_id = 'TBD';


UPDATE liquidity_token_farm
SET
    farm_address = '0x3db7a2da7444995338a2413b151ee437'
WHERE
    farm_address = '0x598b8cbfd4536ecbe88aa1cfaffa7a62';

UPDATE liquidity_token_farm
SET
    liquidity_token_address = '0x3db7a2da7444995338a2413b151ee437'
WHERE
    liquidity_token_address = '0x598b8cbfd4536ecbe88aa1cfaffa7a62';


UPDATE liquidity_token_farm_account
SET
    farm_address = '0x3db7a2da7444995338a2413b151ee437'
WHERE
    farm_address = '0x598b8cbfd4536ecbe88aa1cfaffa7a62';

UPDATE liquidity_token_farm_account
SET
    liquidity_token_address = '0x3db7a2da7444995338a2413b151ee437'
WHERE
    liquidity_token_address = '0x598b8cbfd4536ecbe88aa1cfaffa7a62';


-- select * from node_heartbeat;
delete from node_heartbeat;

update pulling_event_task set status ='DONE';

