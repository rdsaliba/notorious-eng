DROP TABLE IF EXISTS `picture`;
CREATE TABLE `cbms`.`picture` (
  `imageId` INT NOT NULL AUTO_INCREMENT,
  `image` MEDIUMBLOB NULL DEFAULT NULL,
  `name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`imageId`),
  UNIQUE INDEX `name_UNIQUE` (`name` ASC) VISIBLE);


ALTER TABLE `cbms`.`asset` 
ADD COLUMN `imageId` INT(11) NULL DEFAULT NULL AFTER `recommendation`;

ALTER TABLE `cbms`.`asset`
ADD INDEX `fk_asset_picture1_idx` (`imageId` ASC) VISIBLE;

ALTER TABLE `cbms`.`asset`
ADD CONSTRAINT `fk_asset_picture1`
FOREIGN KEY (`imageId`)
REFERENCES `cbms`.`picture` (`imageId`)
ON DELETE NO ACTION
ON UPDATE NO ACTION;