/*
  Implementation of the DAO design pattern
  This class extends the general DAO object and implements the AssetDAO interface

  @author      Paul Micu
  @version     1.0
  @last_edit   12/27/2020
 */
package com.cbms.source.local;

import com.cbms.app.TrainedModel;
import com.cbms.app.item.Asset;
import com.cbms.app.item.AssetAttribute;
import com.cbms.app.item.AssetInfo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AssetDAOImpl extends DAO implements AssetDAO {

    private static final String GET_ASSETS_TO_UPDATE = "SELECT * FROM asset WHERE archived = false AND updated = true";
    private static final String DELETE_ASSET = "DELETE FROM ? WHERE asset_id = ?";
    private static final String GET_ASSET_INFO_FROM_ASSET_ID = "SELECT * FROM attribute_measurements am, attribute att WHERE att.attribute_id=am.attribute_id AND am.asset_id = ?";
    private static final String GET_ATTRIBUTES_NAMES_FROM_ASSET_ID="SELECT DISTINCT att.attribute_name FROM attribute att, attribute_measurements am, asset a WHERE a.asset_id= ? AND am.asset_id = a.asset_id AND att.attribute_id = am.attribute_id order by att.attribute_id";
    private static final String GET_ASSETS_FROM_ASSET_TYPE_ID = "SELECT a.asset_id, a.asset_type_id, a.sn, a.location,a.description FROM asset a WHERE a.archived = true AND a.asset_type_id = ?";
    private static final String GET_ASSET_TYPE_NAME_FROM_ASSET_ID="SELECT at.name FROM asset_type at WHERE at.asset_type_id = ?";
    private static final String GET_ALL_LIVE_ASSETS="SELECT * FROM asset WHERE archived = false";
    private static final String INSERT_NEW_ASSET_MEASUREMENT="INSERT INTO asset_model_calculation values( ? , ? ,now(), ?)";
    private static final String SET_UPDATED_FALSE="UPDATE asset set updated = 0 where asset_id = ?";

    /**
     * This will return an arraylist of assets that have the updated tag set to true
     * it will be used to identify which asset need new RUL measurements
     *
     * @author Paul
     */
    @Override
    public ArrayList<Asset> getAssetsToUpdate() {
        ArrayList<Asset> assets = new ArrayList<>();
        ResultSet rs = nonParamQuery(GET_ASSETS_TO_UPDATE);

        try{
            while (rs.next()) {
                assets.add(createAssetFromQueryResult(rs));
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return assets;
    }

    /**
     * When given an asset ID this will delete the the asset from the database as well as the corresponding
     * tables that reference the asset ID
     *
     * @param assetID represents the asset's ID
     * @author Jeff
     */
    @Override
    public void deleteAssetByID(int assetID) {
        try {
            PreparedStatement ps = getConnection().prepareStatement(DELETE_ASSET);
            ps.setString(1 ," attribute_measurements ");
            ps.setInt(2 , assetID);
            ps.executeQuery();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            PreparedStatement ps = getConnection().prepareStatement(DELETE_ASSET);
            ps.setString(1 ," asset_model_calculation ");
            ps.setInt(2 , assetID);
            ps.executeQuery();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            PreparedStatement ps = getConnection().prepareStatement(DELETE_ASSET);
            ps.setString(1 ," asset ");
            ps.setInt(2 , assetID);
            ps.executeQuery();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * When given an asset ID this return an arraylist of string containing all the names of the attributes
     * that have a relationship to the asset
     *
     * @param assetID represents the asset's ID
     * @author Paul
     */
    @Override
    public ArrayList<String> getAttributesNameFromAssetID(int assetID) {
        ArrayList<String> attributeNames = new ArrayList<>();
        try {
            PreparedStatement ps = getConnection().prepareStatement(GET_ATTRIBUTES_NAMES_FROM_ASSET_ID);
            ps.setInt(1,assetID);
            ResultSet queryResult =  ps.executeQuery();
            while (queryResult.next())
                attributeNames.add(queryResult.getString("attribute_name"));
        }
        catch (SQLException e){
            e.printStackTrace();
        }

        return attributeNames;
    }

    /**
     * When given an asset type id it will return an ArrayList of asset
     * that are archived and of that type
     * used for classifier calculation
     *
     * @param assetTypeID represents the asset's type ID
     * @author Paul
     */
    @Override
    public ArrayList<Asset> getAssetsFromAssetTypeID(int assetTypeID) {
        ArrayList<Asset> assets = new ArrayList<>();

        try{
            PreparedStatement ps = getConnection().prepareStatement(GET_ASSETS_FROM_ASSET_TYPE_ID);
            ps.setInt(1,assetTypeID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                assets.add(createAssetFromQueryResult(rs));
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return assets;
    }

    /**
     * When given an asset type id it will return the name of that asset type
     *
     * @param assetTypeID represents the asset's type ID
     * @author Paul
     */
    @Override
    public String getAssetTypeNameFromID(String assetTypeID) {
        String name = "null";
        try {
            PreparedStatement ps = getConnection().prepareStatement(GET_ASSET_TYPE_NAME_FROM_ASSET_ID);
            ps.setString(1, assetTypeID);
            ResultSet queryResult = ps.executeQuery();
            if (queryResult.next())
                name = queryResult.getString("name");

        } catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            return name;
        }
    }

    /**
     * This will return an arraylist of all assets that are not archived
     *
     * @author Paul
     */
    @Override
    public ArrayList<Asset> getAllLiveAssets() {
        ArrayList<Asset> assets = new ArrayList<>();
        ResultSet rs = nonParamQuery(GET_ALL_LIVE_ASSETS);

        try{
            while (rs.next()) {
                assets.add(createAssetFromQueryResult(rs));
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return assets;
    }

    /**
     * given an rul estimation, an asset and a trained model, this function will add
     * a new measurement for that asset and model
     *
     * @param estimation represents the RUL estimation
     * @param asset represents the asset object
     * @param model represents the trained model object
     * @author Paul
     */
    @Override
    public void addRULEstimation(Double estimation, Asset asset, TrainedModel model) {
        try {
            PreparedStatement ps = getConnection().prepareStatement(INSERT_NEW_ASSET_MEASUREMENT);
            ps.setInt(1, asset.getId());
            ps.setInt(2, model.getModelID());
            ps.setDouble(3, estimation);
            ps.executeQuery();
        } catch (SQLException e){
            e.printStackTrace();
        }
        resetAssetUpdate(asset.getId());
    }

    /**
     * Given an asset id, this function will set the updated status of that asset to false;
     *
     * @param assetID represents the asset's ID
     * @author Paul
     */
    private void resetAssetUpdate(int assetID){
        try {
            PreparedStatement ps = getConnection().prepareStatement(SET_UPDATED_FALSE);
            ps.setInt(1, assetID);
            ps.executeQuery();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Given a result set of assets, this function will create the Asset object corresponding to the current
     * result set values
     *
     * @param assetsQuery represents the result set of asset query
     * @author Paul
     */
    private Asset createAssetFromQueryResult(ResultSet assetsQuery) throws SQLException {
        Asset newAsset = new Asset();
        newAsset.setId(assetsQuery.getInt("asset_id"));
        newAsset.setAssetTypeID(assetsQuery.getString("asset_type_id"));
        newAsset.setDescription(assetsQuery.getString("description"));
        newAsset.setLocation(assetsQuery.getString("location"));
        newAsset.setSerialNo(assetsQuery.getString("sn"));
        newAsset.setAssetInfo(createAssetInfo(newAsset.getId()));
        return newAsset;
    }

    /**
     * Given an asset id, this function will create an assetInfo object containing
     * all the corresponding assetinfo of the asset identified by the assetID
     *
     * @param assetID represents asset's id
     * @author Paul
     */
    private AssetInfo createAssetInfo(int assetID){
        AssetInfo newAssetInfo = new AssetInfo();

        try {
            PreparedStatement ps = getConnection().prepareStatement(GET_ASSET_INFO_FROM_ASSET_ID);
            ps.setInt(1,assetID);
            ResultSet attributesQuery = ps.executeQuery();
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
            return newAssetInfo;
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    private ResultSet nonParamQuery(String query){
        ResultSet rs = null;
        try {
            PreparedStatement ps = getConnection().prepareStatement(query);
            rs = ps.executeQuery();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            return rs;
        }

    }

}
