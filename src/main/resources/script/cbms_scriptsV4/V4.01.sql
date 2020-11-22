/**

The script will add the asset_type table 
The script will also add an entry for this new asset type table and set 
all current asset's types (in the asset table) to this new asset type entry 

The script will also create asset_type_parameters table
This table will hold the parameters used to determine the recommendations,
Both the number of parameters and their boundaries will be driven by the user 
and be unique to each asset type

The script will also create a table to store the trained models
The trained models table will be used to keep track of the trained models, each trained model will be unique to
The combination of asset type (Engine, etc.), the type of model (linear regression, lstm, etc.) and the dataset.
Both the training and testing dataset of the same type can point to the same model or there could be a 
Trained model that is a work in progress while a previously trained model can still be used for live data.
The combination type-model-training dataset will hold the path to the developing version of the model/classifier
The combination type-model-testing dataset will hold the path to the live version of the model/classifier

@Autor Paul Micu
@version 4.01 (sprint 4, script 1)
*/

SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS asset_type;
-- -----------------------------------------------------
-- Table `cbms`.`asset_type`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cbms`.`asset_type`(
                                                  `asset_type_id` INT(11) NOT NULL AUTO_INCREMENT,
                                                  `name` VARCHAR(150) NOT NULL,
                                                  PRIMARY KEY (`asset_type_id`)
)
    ENGINE = InnoDB;


ALTER TABLE `asset`
    DROP FOREIGN KEY if exists `fk_asset_asset_type1`;
ALTER TABLE `asset`
    DROP INDEX if exists `fk_asset_asset_type1_idx`;
-- -----------------------------------------------------
-- alter Table `cbms`.`asset`
-- removing type column, addining asset_type_id colomn and adding the association with Asset_type table
-- -----------------------------------------------------
ALTER TABLE asset
    DROP COLUMN if exists type;
ALTER TABLE asset
    ADD if not exists asset_type_id int;
ALTER TABLE asset
    MODIFY asset_type_id int AFTER name;
CREATE INDEX `fk_asset_asset_type1_idx` on asset (`asset_type_id` ASC);
ALTER TABLE asset
    ADD CONSTRAINT `fk_asset_asset_type1`
        FOREIGN KEY (`asset_type_id`)
            REFERENCES `cbms`.`asset_type` (`asset_type_id`) ON DELETE CASCADE;
    
    
DROP TABLE IF EXISTS trained_model;
-- -----------------------------------------------------
-- Table `cbms`.`trained_model`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cbms`.`trained_model`
(
    `model_id`      INT(11)      NOT NULL,
    `asset_type_id` INT(11)      NOT NULL,
    `dataset_id`    INT(11)      NOT NULL,
    `path`          VARCHAR(255) NULL,
    PRIMARY KEY (`model_id`, `asset_type_id`, `dataset_id`),
    INDEX `fk_table1_asset_type1_idx` (`asset_type_id` ASC),
    INDEX `fk_table1_dataset1_idx` (`dataset_id` ASC),
    CONSTRAINT `fk_table1_model1`
        FOREIGN KEY (`model_id`)
            REFERENCES `cbms`.`model` (`model_id`) ON DELETE CASCADE,
    CONSTRAINT `fk_table1_asset_type1`
        FOREIGN KEY (`asset_type_id`)
            REFERENCES `cbms`.`asset_type` (`asset_type_id`) ON DELETE CASCADE,
    CONSTRAINT `fk_table1_dataset1`
        FOREIGN KEY (`dataset_id`)
            REFERENCES `cbms`.`dataset` (`dataset_id`) ON DELETE CASCADE
)
    ENGINE = InnoDB;
    
    
DROP TABLE IF EXISTS asset_type_parameters;
-- -----------------------------------------------------
-- Table `cbms`.`asset_type_parameters`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cbms`.`asset_type_parameters`
(
    `asset_type_parameters_id` INT         NOT NULL AUTO_INCREMENT,
    `asset_type_id`            INT(11)     NOT NULL,
    `parameter_name`           VARCHAR(45) NOT NULL,
    `boundary`                 VARCHAR(45) NOT NULL,
    PRIMARY KEY (`asset_type_parameters_id`),
    INDEX `fk_asset_type_parameters_asset_type1_idx` (`asset_type_id` ASC),
    CONSTRAINT `fk_asset_type_parameters_asset_type1`
        FOREIGN KEY (`asset_type_id`)
            REFERENCES `cbms`.`asset_type` (`asset_type_id`) ON DELETE CASCADE
)
    ENGINE = InnoDB;
    
    
SET FOREIGN_KEY_CHECKS=1;


/*
Adding one asset type for engine with dummy values
*/
INSERT INTO asset_type(name) VALUES("Engine");

/* 
Adding the asset type id "1" to all current asset
*/
UPDATE asset SET asset_type_id = 1 WHERE asset_id>0;