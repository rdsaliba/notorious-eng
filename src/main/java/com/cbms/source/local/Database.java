/*
  This object will hold all the queries that we are making to the database

  @author Paul Micu
  @version 1.1
  @last_edit 12/05/2020
 */
package com.cbms.source.local;

import com.cbms.app.item.Asset;
import com.cbms.app.item.AssetAttribute;
import com.cbms.app.item.AssetInfo;
import weka.core.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Database {
    private static DatabaseConnection openConnection;

    public Database() {
        openConnection = DatabaseConnection.start();
    }

    /**
     * When given an sql command in a string object, this method will execute that command
     * and return the corresponding ResultSet.
     *
     * @author Paul Micu
     */
    public ResultSet executeQuery(String query) {
        ResultSet dataRS = null;
        try {
            Connection conn = openConnection.getConnection();
            Statement stmt = conn.createStatement();

            dataRS = stmt.executeQuery(query);


        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return dataRS;
    }

    /**
     * This will return an arrayList containing all the asset type IDs of the assets that
     * have train data. Meaning that the archived tag is set to 0 (train data instead of
     * test data).
     *
     * @author Paul Micu
     * */
    public ArrayList<Integer> getTrainAssetTypes() throws SQLException {
        ArrayList<Integer> assetTypes = new ArrayList<>();
        ResultSet assetTypeQuery = executeQuery("SELECT DISTINCT a.asset_type_id FROM cbms.asset a WHERE a.archived = 0");
        while (assetTypeQuery.next())
            assetTypes.add(assetTypeQuery.getInt("asset_type_id"));
        return assetTypes;
    }

    /**
     * When given an asset type ID, this will return an arraylist of Assets containing all of
     * the assets of that asset type. The asset object will also contain a reference to all the
     * asset attributes(sensors) and all their measurements. The method also allows to choose to
     * return an arraylist of the train assets (1) or test assets (0) with the archived parameter.
     *
     * @author Paul Micu
     */
    public ArrayList<Asset> getAssetsFromAssetTypeID(int assetTypeID, int archived) throws SQLException {
        ArrayList<Asset> assets = new ArrayList<>();
        ResultSet assetsQuery = executeQuery("SELECT a.asset_id, a.asset_type_id, a.sn, a.location,a.description FROM asset a WHERE a.archived =" + archived + " AND a.asset_type_id = " + assetTypeID);

        while (assetsQuery.next()) {
            Asset newAsset = new Asset();
            newAsset.setId(assetsQuery.getInt("asset_id"));
            newAsset.setAssetTypeID(assetsQuery.getString("asset_type_id"));
            newAsset.setDescription(assetsQuery.getString("description"));
            newAsset.setLocation(assetsQuery.getString("location"));
            newAsset.setSerialNo(assetsQuery.getString("sn"));
            AssetInfo newAssetInfo = new AssetInfo();
            ResultSet attributesQuery = executeQuery("SELECT * FROM attribute_measurements am, attribute att WHERE att.attribute_id=am.attribute_id AND am.asset_id = " + newAsset.getId());
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

    /**
     * When given an asset_id, this will return an Asset object containing a reference to all the asset attributes(sensors) and all their measurements.
     *
     * @author Paul Micu
     */
    public Asset getAssetsFromAssetID(int assetID) throws SQLException {
        ResultSet assetsQuery = executeQuery("SELECT a.asset_id, a.asset_type_id, a.sn, a.location,a.description FROM asset a WHERE a.asset_id=" + assetID);
        Asset newAsset = new Asset();
        while (assetsQuery.next()) {
            newAsset.setId(assetsQuery.getInt("asset_id"));
            newAsset.setAssetTypeID(assetsQuery.getString("asset_type_id"));
            newAsset.setDescription(assetsQuery.getString("description"));
            newAsset.setLocation(assetsQuery.getString("location"));
            newAsset.setSerialNo(assetsQuery.getString("sn"));
            AssetInfo newAssetInfo = new AssetInfo();
            ResultSet attributesQuery = executeQuery("SELECT * FROM attribute_measurements am, attribute att WHERE att.attribute_id=am.attribute_id AND am.asset_id = " + newAsset.getId());
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

    /**
     * when given an asset_id, this will query the database for that asset and create an instance object.
     *
     * @author Paul Micu
     */
    public Instances createInstanceFromAssetID(int assetID) throws SQLException {
        FastVector attributesVector;
        Instances data;
        double[] values;
        String assetName = Integer.toString(assetID);
        Asset asset = getAssetsFromAssetID(assetID);
        ArrayList<String> attributeNames = getAttributesNameFromAssetID(assetID);

        // 1. set up attributes
        attributesVector = new FastVector();
        // - numeric
        attributesVector.addElement(new Attribute("Asset_id"));
        attributesVector.addElement(new Attribute("Time_Cycle"));
        for (String attributeName : attributeNames) {
            attributesVector.addElement(new Attribute(attributeName));
        }
        // 2. create Instances object
        data = new Instances(assetName, attributesVector, 0);

        for (int timeCycle = 1; timeCycle <= asset.getAssetInfo().getLastRecorderTimeCycle(); timeCycle++) {
            values = new double[data.numAttributes()];
            values[0] = asset.getId();
            values[1] = timeCycle;
            for (int i = 0; i < asset.getAssetInfo().getAssetAttributes().size(); i++) {
                values[i + 2] = asset.getAssetInfo().getAssetAttributes().get(i).getMeasurements(timeCycle);
            }

            data.add(new DenseInstance(1.0, values));      //Changed from Instance to DenseInstance

        }

        return data;
    }

    /**
     * When given an asset type ID, this will query the database for that asset type
     * and create an instance object containing all the assets that are of that asset type.
     * Additionally, the method also allows to choose to return an arraylist of the train
     * assets (1) or test assets (0) with the archived parameter.
     *
     * @author Paul Micu
     */
    public Instances createInstancesFromAssetTypeID(int assetTypeID, int archived) throws SQLException {
        FastVector attributesVector;
        Instances data;
        double[] values;
        String assetTypeName = getAssetTypeNameFromID(assetTypeID);
        ArrayList<Asset> assets = getAssetsFromAssetTypeID(assetTypeID, archived);
        ArrayList<String> attributeNames = getAttributesNameFromAssetTypeID(assetTypeID);

        // 1. set up attributes
        attributesVector = new FastVector();
        // - numeric
        attributesVector.addElement(new Attribute("Asset_id"));
        attributesVector.addElement(new Attribute("Time_Cycle"));
        for (String attributeName : attributeNames) {
            attributesVector.addElement(new Attribute(attributeName));
        }
        // 2. create Instances object
        data = new Instances(assetTypeName, attributesVector, 0);

        for (Asset asset : assets) {
            for (int timeCycle = 1; timeCycle <= asset.getAssetInfo().getLastRecorderTimeCycle(); timeCycle++) {
                values = new double[data.numAttributes()];
                values[0] = asset.getId();
                values[1] = timeCycle;
                for (int i = 0; i < asset.getAssetInfo().getAssetAttributes().size(); i++) {
                    values[i + 2] = asset.getAssetInfo().getAssetAttributes().get(i).getMeasurements(timeCycle);
                }

                data.add(new DenseInstance(1.0, values));    //Changed to DenseInstance due to updating Weka version
            }
        }
        return data;
    }

    /**
     * When given an asset type ID, this will return an arraylist of all the attribute's names
     * that the assets of that type contain.
     *
     * @author Paul Micu
     */
    private ArrayList<String> getAttributesNameFromAssetTypeID(int assetTypeID) throws SQLException {
        ArrayList<String> attributeNames = new ArrayList<>();
        String query = "SELECT DISTINCT att.attribute_name FROM attribute att, attribute_measurements am, asset a\n" +
                "WHERE a.asset_type_ID = " + assetTypeID + " AND \n" +
                "am.asset_id = a.asset_id AND\n" +
                "att.attribute_id = am.attribute_id " +
                "ORDER by att.attribute_id";
        ResultSet queryResult = executeQuery(query);
        while (queryResult.next())
            attributeNames.add(queryResult.getString("attribute_name"));
        return attributeNames;
    }

    /**
     * When given an asset_id, this will return an arraylist of all that asset attribute's names.
     *
     * @author Paul Micu
     */
    private ArrayList<String> getAttributesNameFromAssetID(int assetID) throws SQLException {
        ArrayList<String> attributeNames = new ArrayList<>();
        String query = "SELECT DISTINCT att.attribute_name FROM attribute att, attribute_measurements am, asset a WHERE a.asset_id=" + assetID + " AND am.asset_id = a.asset_id AND att.attribute_id = am.attribute_id order by att.attribute_id";
        ResultSet queryResult = executeQuery(query);
        while (queryResult.next())
            attributeNames.add(queryResult.getString("attribute_name"));
        return attributeNames;
    }

    /**
     * When given an asset type ID, this will return the asset type Name.
     *
     * @author Paul Micu
     */
    public String getAssetTypeNameFromID(int assetTypeID) throws SQLException {
        String name = "null";
        String query = "SELECT at.name FROM asset_type at WHERE at.asset_type_id = " + assetTypeID;
        ResultSet queryResult = executeQuery(query);
        if (queryResult.next())
            name = queryResult.getString("name");
        return name;
    }

    /**
     * When given an asset_id and an rul estimate, this will add the corresponding entry in
     * the asset_model_calculation table. This only works for Linear regression model.
     *
     * @author Paul Micu
     */
    public void addRULEstimate(int id, double estimate) {
        String query = "INSERT INTO asset_model_calculation values(" + id + ",1,now()," + estimate + ")";
        executeQuery(query);
    }

    /**
     * When given an asset ID this will delete the the asset from the database as well as the corresponding
     * tables that reference the asset ID
     *
     * @param assetID represents the asset's ID
     */
    public void deleteAssetByID(int assetID) {
        String query = "DELETE FROM attribute_measurements WHERE asset_id = " + assetID;
        executeQuery(query);
        query = "DELETE FROM asset_model_calculation WHERE asset_id = " + assetID;
        executeQuery(query);
        query = "DELETE FROM asset WHERE asset_id = " + assetID;
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
