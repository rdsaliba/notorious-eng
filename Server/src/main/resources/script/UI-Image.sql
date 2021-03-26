{\rtf1\ansi\ansicpg1252\cocoartf2578
\cocoatextscaling0\cocoaplatform0{\fonttbl\f0\fswiss\fcharset0 Helvetica;}
{\colortbl;\red255\green255\blue255;\red0\green0\blue0;\red255\green255\blue255;\red199\green200\blue201;
}
{\*\expandedcolortbl;;\cssrgb\c0\c0\c0;\cssrgb\c100000\c100000\c99985\c0;\cssrgb\c82147\c82540\c82727;
}
\margl1440\margr1440\vieww11520\viewh8400\viewkind0
\pard\tx566\tx1133\tx1700\tx2267\tx2834\tx3401\tx3968\tx4535\tx5102\tx5669\tx6236\tx6803\pardirnatural\partightenfactor0

\f0\fs24 \cf2 \cb3 CREATE TABLE `cbms`.`picture` (\
  `imageId` INT NOT NULL AUTO_INCREMENT,\
  `image` MEDIUMBLOB NULL DEFAULT NULL,\
  `name` VARCHAR(45) NOT NULL,\
  PRIMARY KEY (`imageId`),\
  UNIQUE INDEX `name_UNIQUE` (`name` ASC) VISIBLE);\
\
\
ALTER TABLE `cbms`.`asset` \
ADD COLUMN `imageId` INT(11) NULL DEFAULT NULL AFTER `recommendation`;\
\
\pard\pardeftab720\partightenfactor0
\cf2 \cb3 \expnd0\expndtw0\kerning0
\outl0\strokewidth0 \strokec4 ALTER TABLE `cbms`.`asset`\
ADD INDEX `fk_asset_picture1_idx` (`imageId` ASC) VISIBLE;\
\
ALTER TABLE `cbms`.`asset`\
ADD CONSTRAINT `fk_asset_picture1`\
\'a0\'a0FOREIGN KEY (`imageId`)\
\'a0\'a0REFERENCES `cbms`.`picture` (`imageId`)\
\'a0\'a0ON DELETE NO ACTION\
\'a0\'a0ON UPDATE NO ACTION;}