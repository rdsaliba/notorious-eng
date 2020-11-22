/**
 * This object will hold all the queries that we are making to the database
 *
 * @author Paul Micu
 * @version 1.0
 * @last_edit 11/08/2020
 */
package com.cbms.source.local;

import com.cbms.app.item.Asset;
import com.cbms.app.item.AssetAttribute;
import com.cbms.app.item.AssetInfo;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;

public class Database {
    private static DatabaseConnection openConnection;


    public Database() {
        openConnection = DatabaseConnection.start();
    }

    /** When given an sql command in a string object, this method will execute that command
     * and return the corresponding ResultSet
     *
     * @autor Paul Micu
     * */
    public ResultSet executeQuery(String query) {
        ResultSet dataRS = null;
        try {
            Connection conn = openConnection.getConnection();
            Statement stmt = conn.createStatement();

            dataRS = stmt.executeQuery(query);


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return dataRS;
    }

    /** This will return an arrayList containing all the dataset_id of the datasets with a train tag
     *
     * @autor Paul Micu
     * */
    public ArrayList<Integer> getTrainDatasets() throws SQLException {
        ArrayList<Integer> datasets = new ArrayList<>();
        ResultSet datasetQuery = executeQuery("select dataset_id from cbms.dataset where train=1");
        while (datasetQuery.next())
            datasets.add(datasetQuery.getInt("dataset_id"));
        return datasets;
    }

    /** When given a dataset_id, this will return an arraylist of Assets containing all of the assets in that dataset
     * the asset object will also contain a reference to all the asset attributes(sensors) and all their measurements
     *
     * @autor Paul Micu
     * */
    public ArrayList<Asset> getAssetsFromDatasetID(int datasetID) throws SQLException {
        ArrayList<Asset> assets = new ArrayList<>();
        ResultSet assetsQuery = executeQuery("SELECT a.asset_id, a.type, a.sn, a.location,a.description FROM asset a, dataset_asset_assoc daa WHERE a.asset_id = daa.asset_id AND daa.dataset_id=" + datasetID);

        while (assetsQuery.next()) {
            Asset newAsset = new Asset();
            newAsset.setId(assetsQuery.getInt("asset_id"));
            newAsset.setAssetType(assetsQuery.getString("type"));
            newAsset.setDescription(assetsQuery.getString("description"));
            newAsset.setLocation(assetsQuery.getString("location"));
            newAsset.setSerialNo(assetsQuery.getString("sn"));
            AssetInfo newAssetInfo = new AssetInfo();
            ResultSet attributesQuery = executeQuery("SELECT * FROM attribute_measurements am, attribute att WHERE att.attribute_id=am.attribute_id and  am.asset_id = " + newAsset.getId());
            int previousAttributeID = 1;
            String previousAttributeName = "";
            AssetAttribute newAttribute = new AssetAttribute();
            while (attributesQuery.next()) {
                int attributeID = attributesQuery.getInt("attribute_id");
                if (previousAttributeID != attributeID || attributesQuery.isLast()) {
                    if (attributesQuery.isLast())
                        newAttribute.addMeasurement(attributesQuery.getInt("time"), attributesQuery.getDouble("value"));
                    newAttribute.setId(previousAttributeID);
                    newAttribute.setName(previousAttributeName);
                    previousAttributeID = attributeID;
                    newAssetInfo.addAttribute(newAttribute);
                    newAttribute = new AssetAttribute();
                }
                previousAttributeName = attributesQuery.getString("attribute_name");
                newAttribute.addMeasurement(attributesQuery.getInt("time"), attributesQuery.getDouble("value"));

            }
            newAsset.setAssetInfo(newAssetInfo);

            assets.add(newAsset);
        }
        return assets;
    }

    /** When given an asset_id, this will return an Asset object containing a reference to all the asset attributes(sensors) and all their measurements
     *
     * @autor Paul Micu
     * */
    public Asset getAssetsFromAssetID(int assetID) throws SQLException {
        ResultSet assetsQuery = executeQuery("SELECT a.asset_id, a.type, a.sn, a.location,a.description FROM asset a WHERE a.asset_id=" + assetID);
        Asset newAsset = new Asset();
        while (assetsQuery.next()) {
            newAsset.setId(assetsQuery.getInt("asset_id"));
            newAsset.setAssetType(assetsQuery.getString("type"));
            newAsset.setDescription(assetsQuery.getString("description"));
            newAsset.setLocation(assetsQuery.getString("location"));
            newAsset.setSerialNo(assetsQuery.getString("sn"));
            AssetInfo newAssetInfo = new AssetInfo();
            ResultSet attributesQuery = executeQuery("SELECT * FROM attribute_measurements am, attribute att WHERE att.attribute_id=am.attribute_id and  am.asset_id = " + newAsset.getId());
            int previousAttributeID = 1;
            String previousAttributeName = "";
            AssetAttribute newAttribute = new AssetAttribute();
            while (attributesQuery.next()) {
                int attributeID = attributesQuery.getInt("attribute_id");
                if (previousAttributeID != attributeID || attributesQuery.isLast()) {
                    if (attributesQuery.isLast())
                        newAttribute.addMeasurement(attributesQuery.getInt("time"), attributesQuery.getDouble("value"));
                    newAttribute.setId(previousAttributeID);
                    newAttribute.setName(previousAttributeName);
                    previousAttributeID = attributeID;
                    newAssetInfo.addAttribute(newAttribute);
                    newAttribute = new AssetAttribute();
                }
                previousAttributeName = attributesQuery.getString("attribute_name");
                newAttribute.addMeasurement(attributesQuery.getInt("time"), attributesQuery.getDouble("value"));

            }
            newAsset.setAssetInfo(newAssetInfo);


        }
        return newAsset;
    }

    /** when given an asset_id, this will query the database for that asset and create an instance object
     *
     * @autor Paul Micu
     * */
    public Instances createInstanceFromAssetID(int assetID) throws SQLException {
        FastVector atts;
        Instances data;
        double[] vals;
        String datasetName = Integer.toString(assetID);
        Asset asset = getAssetsFromAssetID(assetID);
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
            vals[1] = timeCycle;
            for (int i = 0; i < asset.getAssetInfo().getAssetAttributes().size(); i++) {
                vals[i + 2] = asset.getAssetInfo().getAssetAttributes().get(i).getMeasurements(timeCycle);
            }
            data.add(new Instance(1.0, vals));
        }

        return data;
    }

    /** when given an dataset_id, this will query the database for that asset and create an instance object
     * containing all the assets that are part of that dataset
     *
     * @autor Paul Micu
     * */
    public Instances createInstances(int datasetID) throws ParseException, SQLException {
        FastVector atts;
        Instances data;
        double[] vals;
        String datasetName = getDatasetNameFromID(datasetID);
        ArrayList<Asset> assets = getAssetsFromDatasetID(datasetID);
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

        for (Asset asset : assets) {
            for (int timeCycle = 1; timeCycle <= asset.getAssetInfo().getLastRecorderTimeCycle(); timeCycle++) {
                vals = new double[data.numAttributes()];
                vals[0] = asset.getId();
                vals[1] = timeCycle;
                for (int i = 0; i < asset.getAssetInfo().getAssetAttributes().size(); i++) {
                    vals[i + 2] = asset.getAssetInfo().getAssetAttributes().get(i).getMeasurements(timeCycle);
                }
                data.add(new Instance(1.0, vals));
            }
        }
        return data;
    }

    /** When given an dataset_id this will return an arraylist of all the attributes name that the assets in that dataset have
     *
     * @autor Paul Micu
     * */
    private ArrayList<String> getAttributesNameFromDatasetID(int datasetID) throws SQLException {
        ArrayList<String> attributeNames = new ArrayList<>();
        String query = "SELECT DISTINCT att.attribute_name FROM attribute att, attribute_measurements am, asset a, dataset_asset_assoc daa\n" +
                "WHERE daa.dataset_id=" + datasetID + " AND \n" +
                "daa.asset_id = a.asset_id AND\n" +
                "am.asset_id = a.asset_id AND\n" +
                "att.attribute_id = am.attribute_id " +
                "order by att.attribute_id";
        ResultSet queryResult = executeQuery(query);
        while (queryResult.next())
            attributeNames.add(queryResult.getString("attribute_name"));
        return attributeNames;
    }

    /** When given an asset_id this will return an arraylist of all the attributes name that specific asset has
     *
     * @autor Paul Micu
     * */
    private ArrayList<String> getAttributesNameFromAssetID(int assetID) throws SQLException {
        ArrayList<String> attributeNames = new ArrayList<>();
        String query = "SELECT DISTINCT att.attribute_name FROM attribute att, attribute_measurements am, asset a WHERE a.asset_id=" + assetID + " AND am.asset_id = a.asset_id AND att.attribute_id = am.attribute_id order by att.attribute_id";
        ResultSet queryResult = executeQuery(query);
        while (queryResult.next())
            attributeNames.add(queryResult.getString("attribute_name"));
        return attributeNames;
    }

    /** When given an dataset_id this will return the name of the dataset
     *
     * @autor Paul Micu
     * */
    public String getDatasetNameFromID(int datasetID) throws SQLException {
        String name = "null";
        String query = "select dataset.name from dataset where dataset_id=" + datasetID;
        ResultSet queryResult = executeQuery(query);
        if (queryResult.next())
            name = queryResult.getString("name");
        return name;
    }

    /**
     * When given and an asset_id and an rul estimate, this will add the corresponfing entry in the asset_model_calculation table
     * this only works for Linear regression model
     *
     * @autor Paul Micu
     */
    public void addRULEstimate(int id, double estimate) {
        String query = "insert into asset_model_calculation values(" + id + ",1,now()," + estimate + ")";
        executeQuery(query);
    }

    /**
     * Stops the connection to the database
     *
     * @author Najim
     */
    public void stop() {
        openConnection.stop();
    }
}
