/*
  Implementation of the DAO design pattern
  This class extends the general DAO object and implements the AssetDAO interface

  @author      Paul Micu
  @version     1.0
  @last_edit   12/27/2020
 */
package local;

import app.TrainedModel;
import app.item.Asset;
import app.item.AssetAttribute;
import app.item.AssetInfo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AssetDAOImpl extends DAO implements AssetDAO {

    private static final String GET_ASSETS_TO_UPDATE = "SELECT * FROM asset WHERE archived = false AND updated = true";
    private static final String DELETE_ASSET = "DELETE FROM ? WHERE asset_id = ?";
    private static final String GET_ASSET_INFO_FROM_ASSET_ID = "SELECT * FROM attribute_measurements am, attribute att WHERE att.attribute_id=am.attribute_id AND am.asset_id = ?";
    private static final String GET_ATTRIBUTES_NAMES_FROM_ASSET_ID="SELECT DISTINCT att.attribute_name FROM attribute att, attribute_measurements am, asset a WHERE a.asset_id= ? AND am.asset_id = a.asset_id AND att.attribute_id = am.attribute_id order by att.attribute_id";
    private static final String GET_ASSETS_FROM_ASSET_TYPE_ID = "SELECT * FROM asset a WHERE a.archived = true AND a.asset_type_id = ?";
    private static final String GET_ASSET_TYPE_NAME_FROM_ASSET_ID="SELECT at.name FROM asset_type at WHERE at.asset_type_id = ?";
    private static final String GET_ALL_LIVE_ASSETS="SELECT * FROM asset, asset_type WHERE asset.asset_type_id=asset_type.asset_type_id AND archived = false";
    private static final String GET_ALL_LIVE_ASSETS_DESCENDING="SELECT DISTINCT asset.*,asset_type.* FROM asset, asset_type, asset_model_calculation WHERE asset.asset_type_id=asset_type.asset_type_id AND asset.asset_id=asset_model_calculation.asset_id AND archived = false ORDER BY Cast(asset_model_calculation.value as DECIMAL(8,2)) DESC";
    private static final String INSERT_NEW_ASSET_MEASUREMENT="INSERT INTO asset_model_calculation values( ? , ? ,now(), ?)";
    private static final String SET_UPDATED_FALSE="UPDATE asset set updated = 0 where asset_id = ?";
    private static final String SET_UPDATED_TRUE = "UPDATE asset set updated = 1 where asset_id = ?";
    private static final String GET_ASSET_FROM_ASSET_ID="select * from asset where asset_id = ?";
    private static final String GET_LATEST_MEASUREMENT_TIME_FROM_ASSET_ID ="SELECT time FROM attribute_measurements am, attribute att WHERE att.attribute_id=am.attribute_id AND am.asset_id = ? order by time desc limit 1";
    private static final String INSERT_ASSET = "INSERT INTO asset (name, asset_type_id, description, sn, manufacturer, category, site, location, unit_nb) values(?,?,?,?,?,?,?,?,?)";

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
        return name;

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


    public ArrayList<Asset> getAllLiveAssetsDes() {
        ArrayList<Asset> assets = new ArrayList<>();
        ResultSet rs= nonParamQuery(GET_ALL_LIVE_ASSETS_DESCENDING);

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
     * Inserts an asset in the database.
     *
     * @param asset
     */
    @Override
    public void insertAsset(Asset asset) {
        try {
            PreparedStatement ps = getConnection().prepareStatement(INSERT_ASSET);
            ps.setString(1, asset.getName());
            ps.setInt(2, Integer.parseInt(asset.getAssetTypeID()));
            ps.setString(3, asset.getDescription());
            ps.setString(4, asset.getSerialNo());
            ps.setString(5, asset.getManufacturer());
            ps.setString(6, asset.getCategory());
            ps.setString(7, asset.getSite());
            ps.setString(8, asset.getLocation());
            ps.setString(9, asset.getSerialNo());
            ps.executeQuery();
        } catch (SQLException e){
            e.printStackTrace();
        }
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

    public void setAssetUpdate(int assetID){
        try {
            PreparedStatement ps = getConnection().prepareStatement(SET_UPDATED_TRUE);
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
        newAsset.setName(assetsQuery.getString("name"));
        newAsset.setAssetTypeID(assetsQuery.getString("asset_type_id"));
        newAsset.setDescription(assetsQuery.getString("description"));
        newAsset.setLocation(assetsQuery.getString("location"));
        newAsset.setCategory(assetsQuery.getString("category"));
        newAsset.setManufacturer(assetsQuery.getString("manufacturer"));
        newAsset.setSite(assetsQuery.getString("site"));
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
        PreparedStatement ps;
        try {
            ps = getConnection().prepareStatement(GET_ASSET_INFO_FROM_ASSET_ID);
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

    private Boolean isAssetArchived (int assetID){
        try {
            PreparedStatement ps = getConnection().prepareStatement(GET_ASSET_FROM_ASSET_ID);
            ps.setInt(1, assetID);
            ResultSet queryResult = ps.executeQuery();
            if (queryResult.next())
                return queryResult.getBoolean("archived");

        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    private int lastAssetMeasurementTime (int assetID){
        try {
            PreparedStatement ps = getConnection().prepareStatement(GET_LATEST_MEASUREMENT_TIME_FROM_ASSET_ID);
            ps.setInt(1, assetID);
            ResultSet queryResult = ps.executeQuery();
            if (queryResult.next())
                return queryResult.getInt("time");

        } catch (SQLException e){
            e.printStackTrace();
        }
        return -1;
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
