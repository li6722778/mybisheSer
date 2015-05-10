# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table tb_pkgenerator (
  id                        bigint auto_increment not null,
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
  owner_phone               integer(30) not null,
  vender_bank_name          varchar(255),
  vender_bank_number        varchar(255),
  create_date               datetime,
  update_date               datetime,
  create_person             varchar(50),
  update_person             varchar(50),
  constraint pk_tb_parking primary key (park_id))
;

create table tb_parking_img (
  park_img_id               bigint  not null,
  parkId                    bigint,
  img_url_header            varchar(100) not null,
  img_url_path              varchar(255) not null,
  create_date               datetime,
  update_date               datetime,
  create_person             varchar(50),
  update_person             varchar(50),
  constraint pk_tb_parking_img primary key (park_img_id))
;

create table tb_parking_loc (
  park_loc_id               bigint  not null,
  parkId                    bigint,
  type                      integer,
  latitude                  double,
  longitude                 double,
  create_date               datetime,
  update_date               datetime,
  create_person             varchar(50),
  update_person             varchar(50),
  constraint pk_tb_parking_loc primary key (park_loc_id))
;

create table tb_parking_adm (
  park_adm_id               bigint  not null,
  parkId                    bigint,
  userid                    bigint,
  constraint pk_tb_parking_adm primary key (park_adm_id))
;

create table tb_user (
  userid                    bigint  not null,
  user_name                 varchar(50) not null,
  passwd                    varchar(255),
  user_phone                bigint(30) not null,
  email                     varchar(255),
  user_type                 integer,
  create_date               datetime,
  update_date               datetime,
  create_person             varchar(50),
  update_person             varchar(50),
  constraint pk_tb_user primary key (userid))
;

alter table tb_parking_img add constraint fk_tb_parking_img_parkInfo_1 foreign key (parkId) references tb_parking (park_id) on delete restrict on update restrict;
create index ix_tb_parking_img_parkInfo_1 on tb_parking_img (parkId);
alter table tb_parking_loc add constraint fk_tb_parking_loc_parkInfo_2 foreign key (parkId) references tb_parking (park_id) on delete restrict on update restrict;
create index ix_tb_parking_loc_parkInfo_2 on tb_parking_loc (parkId);
alter table tb_parking_adm add constraint fk_tb_parking_adm_parkInfo_3 foreign key (parkId) references tb_parking (park_id) on delete restrict on update restrict;
create index ix_tb_parking_adm_parkInfo_3 on tb_parking_adm (parkId);
alter table tb_parking_adm add constraint fk_tb_parking_adm_userInfo_4 foreign key (userid) references tb_user (userid) on delete restrict on update restrict;
create index ix_tb_parking_adm_userInfo_4 on tb_parking_adm (userid);

