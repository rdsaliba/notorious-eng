/* This small script will add and update columns to asset and trained model to more efficiently calculate the RUL 
 * 
 * */

ALTER TABLE asset DROP COLUMN IF EXISTS updated;
ALTER TABLE asset ADD updated int(1) NOT NULL DEFAULT 1;

ALTER TABLE trained_model DROP COLUMN IF EXISTS retrain;
ALTER TABLE trained_model ADD retrain int(1) NOT NULL DEFAULT 0;
