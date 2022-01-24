create table syrup_pool (
pool_address varchar(34) not null,
token_id varchar(255) not null,
created_at bigint not null,
created_by varchar(70) not null,
deactived bit not null,
description varchar(1000),
estimated_apy decimal(31,10),
reward_multiplier integer,
reward_token_id varchar(15) not null,
sequence_number integer not null,
total_stake_amount decimal(31,0),
tvl_in_usd decimal(51,10),
updated_at bigint not null,
updated_by varchar(70) not null,
version bigint,
primary key (pool_address, token_id)) engine=InnoDB;

create table syrup_pool_account (
account_address varchar(34) not null,
pool_address varchar(34) not null,
token_id varchar(15) not null,
created_at bigint not null,
created_by varchar(70) not null,
deactived bit not null,
updated_at bigint not null,
updated_by varchar(70) not null,
version bigint,
primary key (account_address, pool_address, token_id)) engine=InnoDB;

create index idx_token_id on syrup_pool (token_id);

create index idx_token_id on syrup_pool_account (token_id);
