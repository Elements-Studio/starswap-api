alter table liquidity_pool add column poundage_rate_denominator bigint not null;

alter table liquidity_pool add column poundage_rate_numerator bigint not null;

alter table liquidity_pool add column swap_fee_op_rate_v2_denominator bigint not null;

alter table liquidity_pool add column swap_fee_op_rate_v2_numerator bigint not null;
