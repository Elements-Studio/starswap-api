---- Hibernate:
--create table aptos_event_handle (
--  account_address varchar(66) not null,
--  event_handle_struct varchar(300) not null,
--  event_handle_field_name varchar(100) not null,
--  created_at bigint not null,
--  created_by varchar(70) not null,
--  event_key varchar(255),
--  next_sequence_number decimal(31,0),
--  updated_at bigint not null,
--  updated_by varchar(70) not null,
--  version bigint,
--  primary key (account_address, event_handle_field_name, event_handle_struct)
--) engine=InnoDB
--;
--
---- this is a test event handle
--INSERT INTO `aptos_event_handle` (
--`account_address`,
--`event_handle_struct`,
--`event_handle_field_name`,
--`created_at`,
--`updated_at`,
--`created_by`,
--`updated_by`,
--`version`
--) VALUES (
--'0x2b490841c230a31fe012f3b2a3f3d146316be073e599eb7d7e5074838073ef14',
--'0x2b490841c230a31fe012f3b2a3f3d146316be073e599eb7d7e5074838073ef14::message::MessageHolder',
--'message_change_events',
--unix_timestamp()*1000,
--unix_timestamp()*1000,
--'admin',
--'admin',
--0
--);
--
--alter table aptos_event_handle add column event_java_type varchar(50);
--alter table aptos_event_handle drop index UniqueEventJavaType;
--alter table aptos_event_handle add constraint UniqueEventJavaType unique (event_java_type);
--
---- this is a test event handle
--UPDATE `aptos_event_handle`
--  SET `event_java_type` = 'HelloBlockchainMessageChangeEvent'
--  WHERE (`account_address` = '0x2b490841c230a31fe012f3b2a3f3d146316be073e599eb7d7e5074838073ef14')
--    and (`event_handle_struct` = '0x2b490841c230a31fe012f3b2a3f3d146316be073e599eb7d7e5074838073ef14::message::MessageHolder')
--    and (`event_handle_field_name` = 'message_change_events');
--
--
--ALTER TABLE `starswap_barnard_new`.`aptos_event_handle`
--CHANGE COLUMN `event_java_type` `event_data_type` VARCHAR(50) NULL DEFAULT NULL ;
--


INSERT INTO `aptos_event_handle` (
`account_address`,
`event_handle_struct`,
`event_handle_field_name`,
`created_at`,
`updated_at`,
`created_by`,
`updated_by`,
`version`,
`event_data_type`
) VALUES (
'0x2b490841c230a31fe012f3b2a3f3d146316be073e599eb7d7e5074838073ef14',
'0x2b490841c230a31fe012f3b2a3f3d146316be073e599eb7d7e5074838073ef14::hello_table::EventStore',
'add_liquidity_event_handle',
unix_timestamp()*1000,
unix_timestamp()*1000,
'admin',
'admin',
0,
'AddLiquidityEvent'
);

