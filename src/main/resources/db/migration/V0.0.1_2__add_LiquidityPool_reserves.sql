alter table liquidity_pool add column token_x_reserve decimal(31,0);
alter table liquidity_pool add column token_x_reserve_in_usd decimal(51,10);
alter table liquidity_pool add column token_y_reserve decimal(31,0);
alter table liquidity_pool add column token_y_reserve_in_usd decimal(51,10);
