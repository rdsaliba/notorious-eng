/**

this script will add the asset_type table and make the association with asset
the script will also add a value in the type and set all current asset's types to this new one

this script will also create a table to store the trained models
the table will take the modele id, the asset type id and the dataset id as primaries keys
it also has a colomn for storing the path to the model once we encapsulate that in a future issue

@Autor Paul Micu
@version 4.01 (sprint 4, script 1)
*/

SET FOREIGN_KEY_CHECKS=0;
drop table if exists asset_type;
-- -----------------------------------------------------
-- Table `cbms`.`asset_type`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cbms`.`asset_type` (
  `asset_type_id` INT(11) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(150) NOT NULL,
  `boundary_replace` VARCHAR(45) NULL,
  `boundary_repair` VARCHAR(45) NULL,
  PRIMARY KEY (`asset_type_id`))
ENGINE = InnoDB;

-- -----------------------------------------------------
-- alter Table `cbms`.`asset`
-- removing type colomn, addning asset_type_id colomn and adding the association with Asset_type table
-- -----------------------------------------------------
ALTER TABLE asset DROP COLUMN type;
ALTER TABLE asset ADD asset_type_id int;
ALTER TABLE asset MODIFY asset_type_id int AFTER name;
create INDEX `fk_asset_asset_type1_idx` on asset (`asset_type_id` ASC);
ALTER TABLE asset
  add CONSTRAINT `fk_asset_asset_type1`
    FOREIGN KEY (`asset_type_id`)
    REFERENCES `cbms`.`asset_type` (`asset_type_id`);
drop table if exists trained_model;
-- -----------------------------------------------------
-- Table `cbms`.`trained_model`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cbms`.`trained_model` (
  `model_id` INT(11) NOT NULL,
  `asset_type_id` INT(11) NOT NULL,
  `dataset_id` INT(11) NOT NULL,
  `location` VARCHAR(255) NULL,
  PRIMARY KEY (`model_id`, `asset_type_id`, `dataset_id`),
  INDEX `fk_table1_asset_type1_idx` (`asset_type_id` ASC),
  INDEX `fk_table1_dataset1_idx` (`dataset_id` ASC),
  CONSTRAINT `fk_table1_model1`
    FOREIGN KEY (`model_id`)
    REFERENCES `cbms`.`model` (`model_id`),
  CONSTRAINT `fk_table1_asset_type1`
    FOREIGN KEY (`asset_type_id`)
    REFERENCES `cbms`.`asset_type` (`asset_type_id`),
  CONSTRAINT `fk_table1_dataset1`
    FOREIGN KEY (`dataset_id`)
    REFERENCES `cbms`.`dataset` (`dataset_id`))
ENGINE = InnoDB;
SET FOREIGN_KEY_CHECKS=1;

/*
Adding one asset type for engin with dummy values
*/
INSERT INTO asset_type(name, boundary_replace, boundary_repair) VALUES("Engine","99.8","55.5");

/* 
Adding the asset type id "1" to all current asset
*/
UPDATE asset SET asset_type_id = 1 WHERE asset_id>0;

select * from asset