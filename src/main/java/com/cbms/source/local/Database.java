package com.cbms.source.local;

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import com.cbms.app.item.*;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class Database {
    private static DatabaseConnection openConnection;
    /**
     * Constructor
     *
     * @author Najim
     */
    public Database() {
        openConnection = DatabaseConnection.start();
    }

    public ArrayList<Integer> getTrainDatasets() throws SQLException {
        ArrayList<Integer> datasets = new ArrayList<>();
        ResultSet datasetQuery = executeQuery("select dataset_id from cbms.dataset where train=1");
        while (datasetQuery.next())
            datasets.add(datasetQuery.getInt("dataset_id"));
        return datasets;
    }


    public ArrayList<Asset> getAssetsFromDatasetID(int datasetID) throws SQLException {
        ArrayList<Asset> assets = new ArrayList<>();
        ResultSet assetsQuery = executeQuery("SELECT a.asset_id, a.type, a.sn, a.location,a.description FROM asset a, dataset_asset_assoc daa WHERE a.asset_id = daa.asset_id AND daa.dataset_id="+datasetID);

        while (assetsQuery.next()){
            Asset newAsset = new Asset();
            newAsset.setId(assetsQuery.getInt("asset_id"));
            newAsset.setAssetType(assetsQuery.getString("type"));
            newAsset.setDescription(assetsQuery.getString("description"));
            newAsset.setLocation(assetsQuery.getString("location"));
            newAsset.setSerialNo(assetsQuery.getString("sn"));
            AssetInfo newAssetInfo = new AssetInfo();
            ResultSet attributesQuery = executeQuery("SELECT * FROM attribute_measurements am, attribute att WHERE att.attribute_id=am.attribute_id and  am.asset_id = "+newAsset.getId());
            int previousAttributeID=1;
            String previousAttributeName="";
            AssetAttribute newAttribute = new AssetAttribute();
            while (attributesQuery.next()){
                int attributeID = attributesQuery.getInt("attribute_id");
                if (previousAttributeID != attributeID || attributesQuery.isLast()) {
                    if(attributesQuery.isLast())
                        newAttribute.addMeasurement(attributesQuery.getInt("time"),attributesQuery.getDouble("value"));
                    newAttribute.setId(previousAttributeID);
                    newAttribute.setName(previousAttributeName);
                    previousAttributeID = attributeID;
                    newAssetInfo.addAttribute(newAttribute);
                    newAttribute = new AssetAttribute();
                }
                previousAttributeName=attributesQuery.getString("attribute_name");
                newAttribute.addMeasurement(attributesQuery.getInt("time"),attributesQuery.getDouble("value"));

            }
            newAsset.setAssetInfo(newAssetInfo);

            assets.add(newAsset);
        }
        return assets;
    }

    public Asset getAssetsFromAssetID(int assetID) throws SQLException {
        ResultSet assetsQuery = executeQuery("SELECT a.asset_id, a.type, a.sn, a.location,a.description FROM asset a WHERE a.asset_id="+assetID);
        Asset newAsset = new Asset();
        while (assetsQuery.next()){
            newAsset.setId(assetsQuery.getInt("asset_id"));
            newAsset.setAssetType(assetsQuery.getString("type"));
            newAsset.setDescription(assetsQuery.getString("description"));
            newAsset.setLocation(assetsQuery.getString("location"));
            newAsset.setSerialNo(assetsQuery.getString("sn"));
            AssetInfo newAssetInfo = new AssetInfo();
            ResultSet attributesQuery = executeQuery("SELECT * FROM attribute_measurements am, attribute att WHERE att.attribute_id=am.attribute_id and  am.asset_id = "+newAsset.getId());
            int previousAttributeID=1;
            String previousAttributeName="";
            AssetAttribute newAttribute = new AssetAttribute();
            while (attributesQuery.next()){
                int attributeID = attributesQuery.getInt("attribute_id");
                if (previousAttributeID != attributeID || attributesQuery.isLast()) {
                    if(attributesQuery.isLast())
                        newAttribute.addMeasurement(attributesQuery.getInt("time"),attributesQuery.getDouble("value"));
                    newAttribute.setId(previousAttributeID);
                    newAttribute.setName(previousAttributeName);
                    previousAttributeID = attributeID;
                    newAssetInfo.addAttribute(newAttribute);
                    newAttribute = new AssetAttribute();
                }
                previousAttributeName=attributesQuery.getString("attribute_name");
                newAttribute.addMeasurement(attributesQuery.getInt("time"),attributesQuery.getDouble("value"));

            }
            newAsset.setAssetInfo(newAssetInfo);


        }
        return newAsset;
    }

    public ResultSet executeQuery(String query){
        ResultSet dataRS = null;
        try {
            Connection conn = DatabaseConnection.start().getConnection();
            Statement stmt = conn.createStatement();

            dataRS= stmt.executeQuery(query);


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return dataRS;
    }

    public Instances createInstanceFromAssetID(int assetID) throws SQLException {
        FastVector atts;
        Instances data;
        double[] vals;
        String datasetName = Integer.toString(assetID);
        Asset  asset = getAssetsFromAssetID(assetID);
        ArrayList<String> attributeNames = getAttributesNameFromAssetID(assetID);


        // 1. set up attributes
        atts = new FastVector();
        // - numeric
        atts.addElement(new Attribute("Asset_id"));
        atts.addElement(new Attribute("Time_Cycle"));
        for (String attributeName : attributeNames) {
            atts.addElement(new Attribute(attributeName));
        }
        // 2. create Instances object
        data = new Instances(datasetName, atts, 0);

        for (int timeCycle = 1; timeCycle <= asset.getAssetInfo().getLastRecorderTimeCycle(); timeCycle++) {
            vals = new double[data.numAttributes()];
            vals[0] = asset.getId();
            vals[1]=timeCycle;
            for (int i =0; i < asset.getAssetInfo().getAssetAttributes().size(); i++) {
                vals[i+2]= asset.getAssetInfo().getAssetAttributes().get(i).getMeasurements(timeCycle);
            }
            data.add(new Instance(1.0, vals));
        }

        return data;
    }

    public Instances createInstances(int datasetID) throws ParseException, SQLException {
        FastVector atts;
        Instances data;
        double[] vals;
        String datasetName = getDatasetNameFromID(datasetID);
        ArrayList<Asset>  assets = getAssetsFromDatasetID(datasetID);
        ArrayList<String> attributeNames = getAttributesNameFromDatasetID(datasetID);


        // 1. set up attributes
        atts = new FastVector();
        // - numeric
        atts.addElement(new Attribute("Asset_id"));
        atts.addElement(new Attribute("Time_Cycle"));
        for (String attributeName : attributeNames) {
            atts.addElement(new Attribute(attributeName));
        }
        // 2. create Instances object
        data = new Instances(datasetName, atts, 0);

        for (Asset asset: assets) {
            for (int timeCycle = 1; timeCycle <= asset.getAssetInfo().getLastRecorderTimeCycle(); timeCycle++) {
                vals = new double[data.numAttributes()];
                vals[0] = asset.getId();
                vals[1]=timeCycle;
                for (int i =0; i < asset.getAssetInfo().getAssetAttributes().size(); i++) {
                    vals[i+2]= asset.getAssetInfo().getAssetAttributes().get(i).getMeasurements(timeCycle);
                }
                data.add(new Instance(1.0, vals));
            }
        }
        return data;
    }

    private ArrayList<String> getAttributesNameFromDatasetID(int datasetID) throws SQLException {
        ArrayList<String> attributeNames = new ArrayList<>();
        String query="SELECT DISTINCT att.attribute_name FROM attribute att, attribute_measurements am, asset a, dataset_asset_assoc daa\n" +
                "WHERE daa.dataset_id=" + datasetID + " AND \n" +
                "daa.asset_id = a.asset_id AND\n" +
                "am.asset_id = a.asset_id AND\n" +
                "att.attribute_id = am.attribute_id " +
                "order by att.attribute_id";
        ResultSet queryResult= executeQuery(query);
        while (queryResult.next())
            attributeNames.add(queryResult.getString("attribute_name"));
        return attributeNames;
    }

    private ArrayList<String> getAttributesNameFromAssetID(int assetID) throws SQLException {
        ArrayList<String> attributeNames = new ArrayList<>();
        String query="SELECT DISTINCT att.attribute_name FROM attribute att, attribute_measurements am, asset a WHERE a.asset_id=" + assetID + " AND am.asset_id = a.asset_id AND att.attribute_id = am.attribute_id order by att.attribute_id";
        ResultSet queryResult= executeQuery(query);
        while (queryResult.next())
            attributeNames.add(queryResult.getString("attribute_name"));
        return attributeNames;
    }

    public String getDatasetNameFromID(int datasetID) throws SQLException {
        String name="null";
        String query="select dataset.name from dataset where dataset_id="+datasetID;
        ResultSet queryResult= executeQuery(query);
        if (queryResult.next())
            name= queryResult.getString("name");
        return name;
    }

    /**
     * Simple function to test the database.
     *
     * @param conn Connection object used to create statements per JDBC's API
     */
    public void test(Connection conn) {
        try {
            Statement stmt = conn.createStatement();

            ResultSet dataRS = stmt.executeQuery("SELECT * FROM dataset");
            while (dataRS.next())
                System.out.println(dataRS.getString("dataset_id") + "  " + dataRS.getString("test_or_train") + "  " + dataRS.getString("name"));

            dataRS.close();
            System.out.println();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

}
