ALTER TABLE model
ADD archived boolean;

UPDATE `cbms`.`model` SET `archived` = '0' WHERE (`model_id` = '1');
UPDATE `cbms`.`model` SET `archived` = '0' WHERE (`model_id` = '2');
UPDATE `cbms`.`model` SET `archived` = '0' WHERE (`model_id` = '3');
UPDATE `cbms`.`model` SET `archived` = '0' WHERE (`model_id` = '4');
UPDATE `cbms`.`model` SET `archived` = '0' WHERE (`model_id` = '5');
UPDATE `cbms`.`model` SET `archived` = '0' WHERE (`model_id` = '6');
UPDATE `cbms`.`model` SET `archived` = '0' WHERE (`model_id` = '7');
UPDATE `cbms`.`model` SET `archived` = '0' WHERE (`model_id` = '8');