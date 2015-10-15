ALTER TABLE `tb_parking_prod_loc` 
ADD INDEX `ix_tb_parking_prod_latlng` (`latitude` ASC, `longitude` ASC);

ALTER TABLE `tb_parking_prod_loc` 
ADD INDEX `ix_tb_parking_prod_type` (`type` ASC);

ALTER TABLE `tb_parking_prod_loc` 
ADD INDEX `ix_tb_parking_prod_open` (`is_open` ASC);

ALTER TABLE `tb_allowance_offer` ADD INDEX `ix_allowance_offer_parkid` (`park_id` ASC);

/*2015-10-15 add client id for adm*/
ALTER TABLE `tb_parking_adm` 
ADD COLUMN `deviceid` VARCHAR(200) NULL COMMENT '' AFTER `userid`,
ADD COLUMN `lastlogin_date` TIMESTAMP NULL COMMENT '' AFTER `deviceid`;