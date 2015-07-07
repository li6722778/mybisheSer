ALTER TABLE `tb_parking_prod_loc` 
ADD INDEX `ix_tb_parking_prod_latlng` (`latitude` ASC, `longitude` ASC);

ALTER TABLE `tb_parking_prod_loc` 
ADD INDEX `ix_tb_parking_prod_type` (`type` ASC);

ALTER TABLE `tb_parking_prod_loc` 
ADD INDEX `ix_tb_parking_prod_open` (`is_open` ASC);