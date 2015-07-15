CREATE 
VIEW `vw_map_makers` AS
    select 
        `tb_parking_prod_loc`.`park_loc_id` AS `park_loc_id`,
        `tb_parking_prod_loc`.`parkId` AS `parkId`,
        `tb_parking_prod_loc`.`is_open` AS `is_open`,
        `tb_parking_prod_loc`.`park_free_count` AS `park_free_count`,
        `tb_parking_prod_loc`.`latitude` AS `latitude`,
        `tb_parking_prod_loc`.`longitude` AS `longitude`
    from
        `tb_parking_prod_loc`