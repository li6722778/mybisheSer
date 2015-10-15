CREATE DATABASE /*!32312 IF NOT EXISTS*/`chebole` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `chebole`;


create table tb_counpon_info (
counpon_id                bigint  not null,
counpon_code              varchar(255),
money                     decimal(12,2) default 0.0,
count                     integer default 1,
scancount                 integer default 1,
isable                    integer(2) default 1,
start_date                timestamp NULL,
end_date                  timestamp NULL,
create_date               timestamp NOT NULL,
create_name               varchar(255),
constraint pk_tb_counpon_info primary key (counpon_id))
;

create table tb_log (
log_id                    bigint auto_increment not null,
level                     integer(2) default 1,
operate_name              varchar(100),
operate_date              timestamp null,
content                   varchar(500),
extra_string              varchar(500),
constraint pk_tb_log primary key (log_id))
;

create table tb_order (
order_id                  bigint not null,
order_name                varchar(255),
order_city                varchar(255),
order_fee_type            integer(2) default 0,
parkId                    bigint,
order_status              integer,
order_detail              varchar(1000),
coupon_id                 bigint,
order_date                timestamp NULL,
start_date                timestamp NULL,
end_date                  timestamp NULL,
latitude                  decimal(20,17) default 0,
longitude                 decimal(20,17) default 0,
userid                    bigint,
fee_type_sec_in_scope_hours_order integer(2) default 1,
fee_type_sec_in_scope_hour_money_order decimal(12,2) default 0.0,
fee_type_sec_out_scope_hour_money_order decimal(12,2) default 0.0,
fee_typefixed_hour_money_order decimal(12,2) default 0.0,
is_discount_allday_order  integer(2) default 0,
is_discount_sec_order     integer(2) default 0,
discount_hour_allday_money_order decimal(12,2) default 0.0,
discount_sec_hour_money_order decimal(12,2) default 0.0,
discount_sec_start_hour_order time,
discount_sec_end_hour_order time,
constraint pk_tb_order primary key (order_id))
;


create table tb_order_his (
order_id                  bigint auto_increment not null,
order_name                varchar(255),
order_city                varchar(255),
order_fee_type            integer(2) default 0,
parkId                    bigint,
order_status              integer,
order_detail              varchar(1000),
coupon_id                 bigint,
order_date                timestamp NULL,
start_date                timestamp NULL,
end_date                  timestamp NULL,
latitude                  decimal(20,17) default 0,
longitude                 decimal(20,17) default 0,
userid                    bigint,
fee_type_sec_in_scope_hours_order integer(2) default 1,
fee_type_sec_in_scope_hour_money_order decimal(12,2) default 0.0,
fee_type_sec_out_scope_hour_money_order decimal(12,2) default 0.0,
fee_typefixed_hour_money_order decimal(12,2) default 0.0,
is_discount_allday_order  integer(2) default 0,
is_discount_sec_order     integer(2) default 0,
discount_hour_allday_money_order decimal(12,2) default 0.0,
discount_sec_hour_money_order decimal(12,2) default 0.0,
discount_sec_start_hour_order time,
discount_sec_end_hour_order time,
constraint pk_tb_order_his primary key (order_id))
;

create table tb_order_his_py (
park_py_id                bigint  not null,
orderId                   bigint,
pay_total                 decimal(12,2) default 0.0,
pay_actu                  decimal(12,2) default 0.0,
coupon_used               decimal(12,2) default 0.0,
pay_method                integer default 1,
ack_status                integer default 0,
pay_date                  timestamp NULL,
ack_date                  timestamp NULL,
create_person             varchar(50),
constraint pk_tb_order_his_py primary key (park_py_id))
;

create table tb_order_py (
park_py_id                bigint  not null,
orderId                   bigint,
pay_total                 decimal(12,2) default 0.0,
pay_actu                  decimal(12,2) default 0.0,
coupon_used               decimal(12,2) default 0.0,
pay_method                integer default 1,
ack_status                integer default 0,
pay_date                  timestamp NULL,
ack_date                  timestamp NULL,
create_person             varchar(50),
constraint pk_tb_order_py primary key (park_py_id))
;

create table tb_pkgenerator (
id                        bigint auto_increment  not null,
pk_table                  varchar(100) not null,
pk_column_name            varchar(100) not null,
pk_column_value           bigint(100) not null,
initial_value             bigint default 1,
allocation_size           bigint default 1,
constraint pk_tb_pkgenerator primary key (id))
;

create table tb_parking (
park_id                   bigint  not null,
parkname                  varchar(100) not null,
detail                    TEXT,
address                   varchar(500),
vender                    varchar(200),
owner                     varchar(30) not null,
owner_phone               bigint(30) not null,
vender_bank_name          varchar(255),
vender_bank_number        varchar(255),
fee_type                  integer(2) default 1,
fee_type_sec_in_scope_hours integer(2) default 1,
fee_type_sec_in_scope_hour_money decimal(12,2) default 0.0,
fee_type_sec_out_scope_hour_money decimal(12,2) default 0.0,
fee_type_sec_minute_of_activite integer default 0,
fee_type_fixed_minute_of_in_activite integer default 0,
fee_typefixed_hour_money  decimal(12,2) default 0.0,
is_discount_allday        integer(2) default 0,
is_discount_sec           integer(2) default 0,
discount_hour_allday_money decimal(12,2) default 0.0,
discount_sec_hour_money   decimal(12,2) default 0.0,
discount_sec_start_hour   time,
discount_sec_end_hour     time,
create_date               timestamp NULL,
update_date               timestamp NULL,
create_person             varchar(50),
update_person             varchar(50),
constraint pk_tb_parking primary key (park_id))
;

create table tb_parking_prod_img (
park_img_id               bigint  not null,
parkId                    bigint,
img_url_header            varchar(100) not null,
img_url_path              varchar(255) not null,
detail                    varchar(255),
create_date               timestamp,
update_date               timestamp,
create_person             varchar(50),
update_person             varchar(50),
constraint pk_tb_parking_prod_img primary key (park_img_id))
;

create table tb_parking_prod_loc (
park_loc_id               bigint  not null,
parkId                    bigint,
is_open                   integer(2) default 1,
park_free_count           integer,
type                      integer(2) default 1,
latitude                  decimal(20,17) NOT NULL,
longitude                 decimal(20,17) NOT NULL,
create_date               timestamp,
update_date               timestamp,
create_person             varchar(50),
update_person             varchar(50),
constraint pk_tb_parking_prod_loc primary key (park_loc_id))
;

create table tb_parking_prod (
park_id                   bigint  not null,
parkname                  varchar(100) not null,
detail                    TEXT,
address                   varchar(500),
vender                    varchar(200),
owner                     varchar(30) not null,
owner_phone               bigint(30) not null,
vender_bank_name          varchar(255),
vender_bank_number        varchar(255),
fee_type                  integer(2) default 1,
fee_type_sec_in_scope_hours integer(2) default 1,
fee_type_sec_in_scope_hour_money decimal(12,2) default 0.0,
fee_type_sec_out_scope_hour_money decimal(12,2) default 0.0,
fee_type_sec_minute_of_activite integer default 0,
fee_type_fixed_minute_of_in_activite integer default 0,
fee_typefixed_hour_money  decimal(12,2) default 0.0,
is_discount_allday        integer(2) default 0,
is_discount_sec           integer(2) default 0,
discount_hour_allday_money decimal(12,2) default 0.0,
discount_sec_hour_money   decimal(12,2) default 0.0,
discount_sec_start_hour   time,
discount_sec_end_hour     time,
averagerat                float default 0.0,
create_date               timestamp NULL,
update_date               timestamp NULL,
create_person             varchar(50),
update_person             varchar(50),
approve_date              timestamp NULL,
approve_person            varchar(50),
constraint pk_tb_parking_prod primary key (park_id))
;

create table tb_parking_comment (
park_com_id               bigint  not null,
parkId                    bigint,
comments                  varchar(255),
rating                    float,
create_date               timestamp,
create_person             varchar(50),
constraint pk_tb_parking_comment primary key (park_com_id))
;

create table tb_parking_img (
park_img_id               bigint  not null,
parkId                    bigint,
img_url_header            varchar(100) not null,
img_url_path              varchar(255) not null,
detail                    varchar(255),
create_date               timestamp,
update_date               timestamp,
create_person             varchar(50),
update_person             varchar(50),
constraint pk_tb_parking_img primary key (park_img_id))
;

create table tb_parking_loc (
park_loc_id               bigint  not null,
parkId                    bigint,
is_open                   integer(2) default 1,
park_free_count           integer,
type                      integer(2) default 1,
latitude                  decimal(20,17) NOT NULL,
longitude                 decimal(20,17) NOT NULL,
create_date               timestamp,
update_date               timestamp,
create_person             varchar(50),
update_person             varchar(50),
constraint pk_tb_parking_loc primary key (park_loc_id))
;

create table tb_parking_adm (
park_adm_id               bigint  not null,
parkId                    bigint,
userid                    bigint,
deviceid                  varchar(200),
lastlogin_date            timestamp NULL,
constraint pk_tb_parking_adm primary key (park_adm_id))
;

create table tb_counpon_use (
id                        bigint  not null,
userid                    bigint,
counpon_id                 bigint,
isable                    integer(2) default 1,
type                    integer(2) default 0,
scan_date                 timestamp NULL,
use_date                  timestamp NULL,
constraint pk_tb_counpon_use primary key (id))
;

create table tb_verify_code (
phone                     bigint  not null,
verifycode                varchar(10) not null,
create_date               timestamp,
constraint pk_tb_verify_code primary key (phone))
;

create table tb_client_ver (
version_id                bigint auto_increment not null,
force_update              integer(2) default 0,
version                   bigint,
update_url                varchar(100),
updates_content           varchar(100),
download_target           varchar(1000),
constraint pk_tb_client_ver primary key (version_id))
;

create table tb_user (
userid                    bigint  not null,
user_name                 varchar(50) not null,
passwd                    varchar(255),
user_phone                bigint(30) not null,
email                     varchar(255),
user_type                 integer(3) default 10,
create_date               timestamp NULL,
update_date               timestamp NULL,
create_person             varchar(50),
update_person             varchar(50),
constraint pk_tb_user primary key (userid))
;

create table tb_takecash (
takecashid                bigint not null,
takemoney                 decimal(12,2) default 0.0,
cardnumber                varchar(100) not null,
cardownername             varchar(100) not null,
cardname                  varchar(100) not null,
askdata                   timestamp NULL,
bank_handle_data          timestamp NULL,
ok_data                   timestamp NULL,
status                    integer(2) default 0,
parkid                    bigint(100) not null,
handle_name               varchar(50),
constraint pk_tb_takecash primary key (takecashid))
;


create table tb_income (
income_id                 bigint auto_increment not null,
parkId                    bigint,
incometotal               decimal(12,2) default 0.0,
cashtotal                 decimal(12,2) default 0.0,
counpontotal              decimal(12,2) default 0.0,
create_date               timestamp NULL,
update_date               timestamp NULL,
constraint pk_tb_income primary key (income_id))
;

create table tb_options_set (
option_id                 bigint auto_increment not null,
option_type               integer(2) default 0,
long_text_object          longtext NULL,
text_object               varchar(500),
update_date               timestamp NULL,
update_person             varchar(50),
constraint pk_tb_options_set primary key (option_id))
;

create table tb_allowance (
allowance_id              bigint auto_increment not null,
money                     decimal(12,2) default 0.0,
isopen                    integer(2) default 1,
allowance_count           integer default 1,
allowance_pay_type        integer(2) default 1,
allowance_type            integer(2) default 1,
allowance_type_value      integer default 0,
allowance_timer           varchar(2000),
update_date               timestamp NULL,
update_name               varchar(255),
constraint pk_tb_allowance primary key (allowance_id))
;

create table tb_allowance_offer (
offer_id                  bigint  not null,
money                     decimal(12,2) default 0.0,
park_id                   bigint,
park_name                 varchar(255),
order_his_id              bigint,
order_name                varchar(255),
create_date               timestamp NULL,
constraint pk_tb_allowance_offer primary key (offer_id))
;

create table tb_parking_comment_keyword (
keyword_id                bigint auto_increment not null,
content                   varchar(255),
create_date               timestamp,
constraint pk_tb_parking_comment_keyword primary key (keyword_id))
;



create table tb_share (
userid                    bigint auto_increment not null,
share                     integer(3) default 0,
sharet_date               timestamp NULL,
constraint pk_tb_share primary key (userid))
;


create table tb_uniqueurl (
url                       varchar(255) not null,
sharetime                 integer(3) default 0,
sharet_date               timestamp NULL,
userphone_object          varchar(150),
constraint pk_tb_uniqueurl primary key (url))
;


create table tb_unregisteruser (
user_phone                bigint auto_increment not null,
sharetime                 integer(3) default 0,
sharet_date               timestamp NULL,
constraint pk_tb_unregisteruser primary key (user_phone))
;




create table tb_counpon_use_his (
id                        bigint auto_increment not null,
userid                    bigint,
counpon_id                bigint,
isable                    integer(2) default 1,
type                      integer(2) default 0,
scan_date                 timestamp NULL,
use_date                  timestamp NULL,
constraint pk_tb_counpon_use_his primary key (id))
;


create table tb_counpon_info_his (
counpon_id                bigint  not null,
counpon_code              varchar(255),
money                     decimal(12,2) default 0.0,
count                     integer default 1,
scancount                 integer default 1,
isable                    integer(2) default 1,
start_date                timestamp NULL,
end_date                  timestamp NULL,
create_date               timestamp NOT NULL,
create_name               varchar(255),
constraint pk_tb_counpon_info_his primary key (counpon_id))
;

create table tb_wxuser (
openid                    varchar(100),
nickname                  varchar(100),
headimgurl                varchar(1000),
sharet_date               timestamp NULL,
shareword                 varchar(1000),
getcoupnprice             integer,
uniqueurl                 varchar(50),
userphone                 varchar(30))
;

create table tb_smssenduser (
id                        integer auto_increment not null,
telephone                 varchar(20),
type                      integer,
cotent                    varchar(1000),
constraint pk_tb_smssenduser primary key (id))
;

create table tb_smsmodel (
type                      integer auto_increment not null,
cotent                    varchar(1000),
descripe                  varchar(500),
constraint pk_tb_smsmodel primary key (type))
;

alter table tb_order add constraint fk_tb_order_parkInfo_1 foreign key (parkId) references tb_parking_prod (park_id) on delete restrict on update restrict;
create index ix_tb_order_parkInfo_1 on tb_order (parkId);
alter table tb_order add constraint fk_tb_order_userInfo_2 foreign key (userid) references tb_user (userid) on delete restrict on update restrict;
create index ix_tb_order_userInfo_2 on tb_order (userid);
alter table tb_order_his add constraint fk_tb_order_his_parkInfo_3 foreign key (parkId) references tb_parking_prod (park_id) on delete restrict on update restrict;
create index ix_tb_order_his_parkInfo_3 on tb_order_his (parkId);
alter table tb_order_his add constraint fk_tb_order_his_userInfo_4 foreign key (userid) references tb_user (userid) on delete restrict on update restrict;
create index ix_tb_order_his_userInfo_4 on tb_order_his (userid);
alter table tb_order_his_py add constraint fk_tb_order_his_py_order_5 foreign key (orderId) references tb_order_his (order_id) on delete restrict on update restrict;
create index ix_tb_order_his_py_order_5 on tb_order_his_py (orderId);
alter table tb_order_py add constraint fk_tb_order_py_order_6 foreign key (orderId) references tb_order (order_id) on delete restrict on update restrict;
create index ix_tb_order_py_order_6 on tb_order_py (orderId);
alter table tb_parking_prod_img add constraint fk_tb_parking_prod_img_parkInfo_7 foreign key (parkId) references tb_parking_prod (park_id) on delete restrict on update restrict;
create index ix_tb_parking_prod_img_parkInfo_7 on tb_parking_prod_img (parkId);
alter table tb_parking_prod_loc add constraint fk_tb_parking_prod_loc_parkInfo_8 foreign key (parkId) references tb_parking_prod (park_id) on delete restrict on update restrict;
create index ix_tb_parking_prod_loc_parkInfo_8 on tb_parking_prod_loc (parkId);
alter table tb_parking_comment add constraint fk_tb_parking_comment_parkInfo_9 foreign key (parkId) references tb_parking_prod (park_id) on delete restrict on update restrict;
create index ix_tb_parking_comment_parkInfo_9 on tb_parking_comment (parkId);
alter table tb_parking_img add constraint fk_tb_parking_img_parkInfo_10 foreign key (parkId) references tb_parking (park_id) on delete restrict on update restrict;
create index ix_tb_parking_img_parkInfo_10 on tb_parking_img (parkId);
alter table tb_parking_loc add constraint fk_tb_parking_loc_parkInfo_11 foreign key (parkId) references tb_parking (park_id) on delete restrict on update restrict;
create index ix_tb_parking_loc_parkInfo_11 on tb_parking_loc (parkId);
alter table tb_parking_adm add constraint fk_tb_parking_adm_parkInfo_12 foreign key (parkId) references tb_parking_prod (park_id) on delete restrict on update restrict;
create index ix_tb_parking_adm_parkInfo_12 on tb_parking_adm (parkId);
alter table tb_parking_adm add constraint fk_tb_parking_adm_userInfo_13 foreign key (userid) references tb_user (userid) on delete restrict on update restrict;
create index ix_tb_parking_adm_userInfo_13 on tb_parking_adm (userid);
alter table tb_counpon_use add constraint fk_tb_counpon_use_userInfo_14 foreign key (userid) references tb_user (userid) on delete restrict on update restrict;
create index ix_tb_counpon_use_userInfo_14 on tb_counpon_use (userid);
alter table tb_income add constraint fk_tb_income_parkInfo_1 foreign key (parkId) references tb_parking_prod (park_id) on delete restrict on update restrict;
create index ix_tb_income_parkInfo_1 on tb_income (parkId);
