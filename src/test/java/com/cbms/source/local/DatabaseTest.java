package com.cbms.source.local;

import com.cbms.app.item.Asset;
import org.junit.BeforeClass;
import org.junit.Test;
import weka.core.Instances;

import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class DatabaseTest {
    private static Database database;

    @BeforeClass
    public static void setUp(){
        database = new Database();
    }

    @Test
    public void getTrainDatasets() throws SQLException {
        ArrayList<Integer> trainSets = database.getTrainDatasets();
        assertEquals("there should currently be 4 train datasets", 4, trainSets.size());

    }

    @Test
    public void getAssetsFromDatasetID() throws SQLException{
        ArrayList<Asset> assets = database.getAssetsFromDatasetID(1);
        assertEquals("there should be 100 assets in the dataset 1", 100, assets.size());
    }

    @Test
    public void getAssetsFromAssetID() throws SQLException{
        Asset asset = database.getAssetsFromAssetID(123);
        assertEquals("the id of this asset should be 123",123,asset.getId());
    }

    @Test
    public void createInstanceFromAssetID() throws SQLException{
        Instances assetInstance = database.createInstanceFromAssetID(1);
        assertEquals("the number of instances for asset id 1 should be 192",192,assetInstance.numInstances());

    }

    @Test
    public void createInstances() throws SQLException {
        Instances assetInstance = database.createInstanceFromAssetID(100);
        Instances assetInstances = database.createInstances(1);
        assertEquals("the last instance of database 1 should be the last instance of asset id 100",assetInstance.lastInstance().toString(),assetInstances.lastInstance().toString());

    }

    @Test
    public void getDatasetNameFromID() throws SQLException{
        assertEquals("dataset 1 should be named FC001","FD001",database.getDatasetNameFromID(1));
    }

    @Test
    public void addAndGetRULEstimate() throws SQLException{
        database.addRULEstimate(1,-1);
        assertEquals("the latest RUL should be -1",-1,database.getLatestRULEstimate(1));
        database.executeQuery("DELETE FROM cbms.asset_model_calculation where asset_id =1 and model_id =1 and value =-1");
    }
}