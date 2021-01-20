-- This script will make the changes AS requested in issue #112

-- Adding the Archived column in asset to replace the train from dataset and transfer values
ALTER TABLE cbms.asset ADD COLUMN archived INT(1) NOT NULL DEFAULT '0';
UPDATE cbms.asset a, cbms.dataset_asset_assoc daa, cbms.dataset d SET a.archived='1' WHERE 
a.asset_id = daa.asset_id AND
daa.dataset_id = d.dataset_id AND
d.train = '1';

-- Remove all constraints, drop the primary key, remake constraints, remake primary key without dataset
-- Remove dataset_id column from table trained model
ALTER TABLE cbms.trained_model ENGINE=InnoDB;
ALTER TABLE cbms.trained_model DROP FOREIGN KEY fk_table1_dataset1;
ALTER TABLE cbms.trained_model DROP FOREIGN KEY fk_table1_asset_type1;
ALTER TABLE cbms.trained_model DROP FOREIGN KEY fk_table1_model1;
DROP INDEX fk_table1_dataset1_idx ON cbms.trained_model;
DROP INDEX PRIMARY ON cbms.trained_model;

ALTER TABLE cbms.trained_model
  ADD CONSTRAINT fk_table1_asset_type1
    FOREIGN KEY (asset_type_id)
    REFERENCES cbms.asset_type (asset_type_id);
   
ALTER TABLE cbms.trained_model
  ADD CONSTRAINT fk_table1_model1
    FOREIGN KEY (model_id)
    REFERENCES cbms.model (model_id);
   
ALTER TABLE cbms.trained_model ADD CONSTRAINT PRIMARY KEY (asset_type_id,model_id);

ALTER TABLe cbms.trained_model DROP COLUMN dataset_id;


-- First we need to expand the size of the type column and then add the  new attributes
ALTER TABLE cbms.attribute MODIFY type  varchar(50);
INSERT INTO cbms.attribute(attribute_name, type) VALUES ('Condition', 'Operational Condition');
INSERT INTO cbms.attribute(attribute_name, type) VALUES ('Fault Mode', 'Operational Condition');

-- Adding the correct attribute measurements from the operational condition table for all the conditions and assets
INSERT INTO cbms.attribute_measurements (cbms.attribute_measurements.asset_id,cbms.attribute_measurements.attribute_id,cbms.attribute_measurements.time,cbms.attribute_measurements.value)
SELECT am.asset_id ,'25' AS attribute_id ,am.time , '1' AS value FROM cbms.attribute_measurements am, cbms.asset a, cbms.dataset_asset_assoc daa, cbms.dataset d, cbms.dataset_condition_assoc dca 
WHERE d.dataset_id = daa.dataset_id  AND 
daa.asset_id =a.asset_id  AND 
d.dataset_id =dca.dataset_id  AND 
am.asset_id =a.asset_id AND
dca.oc_name = 'Conditions: one'AND 
am.attribute_id =1;

INSERT INTO cbms.attribute_measurements (cbms.attribute_measurements.asset_id,cbms.attribute_measurements.attribute_id,cbms.attribute_measurements.time,cbms.attribute_measurements.value)
SELECT am.asset_id ,'25' AS attribute_id ,am.time , '6' AS value FROM cbms.attribute_measurements am, cbms.asset a, cbms.dataset_asset_assoc daa, cbms.dataset d, cbms.dataset_condition_assoc dca 
WHERE d.dataset_id = daa.dataset_id  AND 
daa.asset_id =a.asset_id  AND 
d.dataset_id =dca.dataset_id  AND 
am.asset_id =a.asset_id AND
dca.oc_name = 'Conditions: six'AND 
am.attribute_id =1;


INSERT INTO cbms.attribute_measurements (cbms.attribute_measurements.asset_id,cbms.attribute_measurements.attribute_id,cbms.attribute_measurements.time,cbms.attribute_measurements.value)
SELECT am.asset_id ,'26' AS attribute_id ,am.time , '1' AS value FROM cbms.attribute_measurements am, cbms.asset a, cbms.dataset_asset_assoc daa, cbms.dataset d, cbms.dataset_condition_assoc dca 
WHERE d.dataset_id = daa.dataset_id  AND 
daa.asset_id =a.asset_id  AND 
d.dataset_id =dca.dataset_id  AND 
am.asset_id =a.asset_id AND
dca.oc_name = 'Fault Modes: ONE'AND 
am.attribute_id =1;

INSERT INTO cbms.attribute_measurements (cbms.attribute_measurements.asset_id,cbms.attribute_measurements.attribute_id,cbms.attribute_measurements.time,cbms.attribute_measurements.value)
SELECT am.asset_id ,'26' AS attribute_id ,am.time , '2' AS value FROM cbms.attribute_measurements am, cbms.asset a, cbms.dataset_asset_assoc daa, cbms.dataset d, cbms.dataset_condition_assoc dca 
WHERE d.dataset_id = daa.dataset_id  AND 
daa.asset_id =a.asset_id  AND 
d.dataset_id =dca.dataset_id  AND 
am.asset_id =a.asset_id AND
dca.oc_name = 'Fault Modes: TWO'AND 
am.attribute_id =1;

-- Removing the dataset and related tables 
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE cbms.operational_condition;
DROP TABLE cbms.dataset_condition_assoc;
DROP TABLE cbms.dataset_asset_assoc ;
DROP TABLE cbms.dataset;
SET FOREIGN_KEY_CHECKS = 1;
COMMIT;



