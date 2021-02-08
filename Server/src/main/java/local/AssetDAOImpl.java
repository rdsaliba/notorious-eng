/*
  Implementation of the DAO design pattern
  This class extends the general DAO object and implements the AssetDAO interface

  @author      Paul Micu
  @version     1.0
  @last_edit   12/27/2020
 */
package local;

import app.item.Asset;
import app.item.AssetAttribute;
import app.item.AssetInfo;
import app.item.TrainedModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AssetDAOImpl extends DAO implements AssetDAO {

    private static final String GET_ASSETS_TO_UPDATE = "SELECT * FROM asset WHERE archived = false AND updated = true";
    private static final String DELETE_ASSET = "DELETE FROM ? WHERE asset_id = ?";
    private static final String GET_ASSET_INFO_FROM_ASSET_ID = "SELECT * FROM attribute_measurements am, attribute att WHERE att.attribute_id=am.attribute_id AND am.asset_id = ?";
    private static final String GET_ATTRIBUTES_NAMES_FROM_ASSET_ID = "SELECT DISTINCT att.attribute_name FROM attribute att, attribute_measurements am, asset a WHERE a.asset_id= ? AND am.asset_id = a.asset_id AND att.attribute_id = am.attribute_id order by att.attribute_id";
    private static final String GET_ASSETS_FROM_ASSET_TYPE_ID = "SELECT * FROM asset a WHERE a.archived = true AND a.asset_type_id = ?";
    private static final String GET_ASSET_TYPE_NAME_FROM_ASSET_ID = "SELECT at.name FROM asset_type at WHERE at.asset_type_id = ?";
    private static final String GET_ALL_LIVE_ASSETS = "SELECT * FROM asset, asset_type WHERE asset.asset_type_id=asset_type.asset_type_id AND archived = false";
    private static final String GET_ALL_LIVE_ASSETS_DESCENDING = "SELECT DISTINCT asset.*,asset_type.* FROM asset, asset_type, asset_model_calculation WHERE asset.asset_type_id=asset_type.asset_type_id AND asset.asset_id=asset_model_calculation.asset_id AND archived = false ORDER BY Cast(asset_model_calculation.value as DECIMAL(8,2)) DESC";
    private static final String INSERT_NEW_ASSET_MEASUREMENT = "INSERT INTO asset_model_calculation values( ? , ? ,now(), ?)";
    private static final String SET_UPDATED_FALSE = "UPDATE asset set updated = 0 where asset_id = ?";
    private static final String SET_UPDATED_TRUE = "UPDATE asset set updated = 1 where asset_id = ?";
    private static final String INSERT_ASSET = "INSERT INTO asset (name, asset_type_id, description, sn, manufacturer, category, site, location, unit_nb) values(?,?,?,?,?,?,?,?,?)";
    private static final String UPDATE_RECOMMENDATION = "UPDATE asset set recommendation = ? WHERE asset_id = ?";
    private static final String GET_ATTRIBUTE_DETAILS_FROM_ASSET_ID = "SELECT att.* FROM attribute_measurements am, attribute att WHERE att.attribute_id=am.attribute_id AND am.asset_id = ? GROUP by attribute_id";

    /**
     * This will return an arraylist of assets that have the updated tag set to true
     * it will be used to identify which asset need new RUL measurements
     *
     * @author Paul
     */
    @Override
    public ArrayList<Asset> getAssetsToUpdate() {
        ArrayList<Asset> assets = new ArrayList<>();
        try (ResultSet rs = nonParamQuery(GET_ASSETS_TO_UPDATE)) {
            while (rs.next()) {
                assets.add(createAssetFromQueryResult(rs));
            }
        } catch (SQLException e) {
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
        try (PreparedStatement ps = getConnection().prepareStatement(DELETE_ASSET)) {
            for (int i = 0; i < 3; i++) {
                if (i == 0) {
                    ps.setString(1, " attribute_measurements ");
                } else if (i == 1) {
                    ps.setString(1, " asset_model_calculation ");
                } else {
                    ps.setString(1, " asset ");
                }
                ps.setInt(2, assetID);
                ps.executeQuery();
            }
        } catch (SQLException e) {
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
        try (PreparedStatement ps = getConnection().prepareStatement(GET_ATTRIBUTES_NAMES_FROM_ASSET_ID)) {
            ps.setInt(1, assetID);
            try (ResultSet queryResult = ps.executeQuery()) {
                while (queryResult.next())
                    attributeNames.add(queryResult.getString("attribute_name"));
            }
        } catch (SQLException e) {
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
        try (PreparedStatement ps = getConnection().prepareStatement(GET_ASSETS_FROM_ASSET_TYPE_ID)) {
            ps.setInt(1, assetTypeID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    assets.add(createAssetFromQueryResult(rs));
                }
            }
        } catch (SQLException e) {
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
        try (PreparedStatement ps = getConnection().prepareStatement(GET_ASSET_TYPE_NAME_FROM_ASSET_ID)) {
            ps.setString(1, assetTypeID);
            try (ResultSet queryResult = ps.executeQuery()) {
                if (queryResult.next())
                    name = queryResult.getString("name");
            }
        } catch (SQLException e) {
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
        try (ResultSet rs = nonParamQuery(GET_ALL_LIVE_ASSETS)) {
            while (rs.next()) {
                assets.add(createAssetFromQueryResult(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return assets;
    }


    @Override
    public ArrayList<Asset> getAllLiveAssetsDes() {
        ArrayList<Asset> assets = new ArrayList<>();
        ResultSet rs = nonParamQuery(GET_ALL_LIVE_ASSETS_DESCENDING);

        try {
            while (rs.next()) {
                assets.add(createAssetFromQueryResult(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return assets;

    }

    /**
     * given an rul estimation, an asset and a trained model, this function will add
     * a new measurement for that asset and model
     *
     * @param estimation represents the RUL estimation
     * @param asset      represents the asset object
     * @param model      represents the trained model object
     * @author Paul
     */
    @Override
    public void addRULEstimation(Double estimation, Asset asset, TrainedModel model) {
        try (PreparedStatement ps = getConnection().prepareStatement(INSERT_NEW_ASSET_MEASUREMENT)) {
            ps.setInt(1, asset.getId());
            ps.setInt(2, model.getModelID());
            ps.setDouble(3, estimation);
            ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        resetAssetUpdate(asset.getId());
    }

    /**
     * Inserts an asset in the database.
     *
     * @param asset is an asset that is added by the user
     */
    @Override
    public void insertAsset(Asset asset) {
        try (PreparedStatement ps = getConnection().prepareStatement(INSERT_ASSET)) {
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the recommendation column for an Asset.
     *
     * @param assetID        is the ID of the asset being passed for assessing the recommendation
     * @param recommendation is the recommendation label
     */
    @Override
    public void updateRecommendation(int assetID, String recommendation) {
        try (PreparedStatement ps = getConnection().prepareStatement(UPDATE_RECOMMENDATION)) {
            ps.setString(1, recommendation);
            ps.setInt(2, assetID);
            ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Given an asset id, this function will set the updated status of that asset to false;
     *
     * @param assetID represents the asset's ID
     * @author Paul
     */
    @Override
    public void resetAssetUpdate(int assetID) {
        try (PreparedStatement ps = getConnection().prepareStatement(SET_UPDATED_FALSE)) {
            ps.setInt(1, assetID);
            ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setAssetUpdate(int assetID) {
        try (PreparedStatement ps = getConnection().prepareStatement(SET_UPDATED_TRUE)) {
            ps.setInt(1, assetID);
            ps.executeQuery();
        } catch (SQLException e) {
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
    @Override
    public Asset createAssetFromQueryResult(ResultSet assetsQuery) throws SQLException {
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
        newAsset.setRecommendation(assetsQuery.getString("recommendation"));
        newAsset.setAssetInfo(createAssetInfo(newAsset.getId()));
        return newAsset;
    }

    /**
     * Given an asset id, this function will create an assetInfo object containing
     * all the corresponding asset info of the asset identified by the assetID
     *
     * @param assetID represents asset's id
     * @author Paul
     */
    @Override
    public AssetInfo createAssetInfo(int assetID) {
        AssetInfo newAssetInfo = new AssetInfo();
        try (PreparedStatement ps = getConnection().prepareStatement(GET_ASSET_INFO_FROM_ASSET_ID)) {
            ps.setInt(1, assetID);
            try (ResultSet attributesQuery = ps.executeQuery()) {
                int previousAttributeID = 1;
                String previousAttributeName = "";
                AssetAttribute newAttribute = new AssetAttribute();
                while (attributesQuery.next()) {
                    int attributeID = attributesQuery.getInt("attribute_id");
                    if (previousAttributeID != attributeID || !attributesQuery.next()) {
                        if (!attributesQuery.next())
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
            }
            return newAssetInfo;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** this function returns the measurements for a given asset and cycle in a table format
     *  this means that the data comes pre arranged in the ResultSet to simplify adding it to the
     *  table in the UI
     *
     * @param assetID the id of the asset we want the measurement for
     * @param fromTime this represents from what time cycle we want to start retrieving information
     * @author Paul
     */
    public ResultSet createMeasurementsFromAssetIdAndTime(int assetID, int fromTime) {
        StringBuilder preparedStatementPart1 = new StringBuilder();
        StringBuilder preparedStatementPart2 = new StringBuilder();
        ResultSet returned = null;
        try (PreparedStatement ps = getConnection().prepareStatement(GET_ATTRIBUTE_DETAILS_FROM_ASSET_ID)) {
            ps.setInt(1, assetID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                preparedStatementPart1.append(", Coalesce(Sum(`");
                preparedStatementPart1.append(rs.getString("attribute_name"));
                preparedStatementPart1.append("`), 0) AS '");
                preparedStatementPart1.append(rs.getString("attribute_name"));
                preparedStatementPart1.append("'");

                preparedStatementPart2.append(", CASE WHEN tab.attribute_id = ");
                preparedStatementPart2.append(rs.getString("attribute_id"));
                preparedStatementPart2.append(" THEN tab.value end AS `");
                preparedStatementPart2.append(rs.getString("attribute_name"));
                preparedStatementPart2.append("`");
            }

            // i'm unsure why but i cannot do setString on the prepared statement, it keeps failing the query.
            // This way is the same in terms of usage and speed and security.
            try (PreparedStatement measurementStatement = getConnection().prepareStatement("SELECT `Cycle` " + preparedStatementPart1 + " FROM (SELECT tab.`time` as 'Cycle'" + preparedStatementPart2 + " FROM (SELECT am.attribute_id, am.`time`, am.value FROM attribute_measurements am WHERE asset_id = ? AND time > ? ORDER BY attribute_id, time) tab) tab2 GROUP BY `CYCLE` asc;")) {
                measurementStatement.setInt(1, assetID);
                measurementStatement.setInt(2, fromTime);
                returned = measurementStatement.executeQuery();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return returned;
    }
}
