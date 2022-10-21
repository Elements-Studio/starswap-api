
DELETE FROM `liquidity_token`
  WHERE (`liquidity_token_address` = '0xc3dbe4f07390f05b19ccfc083fc6aa5bc5d75621d131fc49557c8f4bbc11716')
    and (`token_x_id` = 'STAR') and (`token_y_id` = 'XUSDT');

DELETE FROM `liquidity_pool`
  WHERE (`liquidity_token_address` = '0xc3dbe4f07390f05b19ccfc083fc6aa5bc5d75621d131fc49557c8f4bbc11716')
     and (`token_x_id` = 'STAR') and (`token_y_id` = 'XUSDT')
     and (`pool_address` = '0xc3dbe4f07390f05b19ccfc083fc6aa5bc5d75621d131fc49557c8f4bbc11716');

DELETE FROM `liquidity_token_farm`
  WHERE (`farm_address` = '0xc3dbe4f07390f05b19ccfc083fc6aa5bc5d75621d131fc49557c8f4bbc11716')
     and (`liquidity_token_address` = '0xc3dbe4f07390f05b19ccfc083fc6aa5bc5d75621d131fc49557c8f4bbc11716')
     and (`token_x_id` = 'STAR') and (`token_y_id` = 'XUSDT');



