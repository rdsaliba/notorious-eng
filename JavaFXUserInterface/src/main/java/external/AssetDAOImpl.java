/*
  Implementation of the DAO design pattern
  This class extends the general DAO object and implements the AssetDAO interface

  @author      Paul Micu
  @version     1.0
  @last_edit   12/27/2020
 */
package external;

import app.item.Asset;
import app.item.AssetAttribute;
import app.item.AssetInfo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AssetDAOImpl extends DAO implements AssetDAO {

    public static final String ATTRIBUTE_NAME = "attribute_name";
    private static final String DELETE_ASSET = "DELETE FROM asset WHERE asset_id = ?";
    private static final String GET_ASSET_INFO_FROM_ASSET_ID = "SELECT DISTINCT att.* FROM attribute_measurements am, attribute att WHERE att.attribute_id=am.attribute_id AND am.asset_id = ?";
    private static final String GET_LIVE_ASSETS_FROM_ASSET_TYPE_ID = "SELECT * FROM asset a WHERE a.archived = false AND a.asset_type_id = ?";
    private static final String GET_ARCHIVED_ASSETS_FROM_ASSET_TYPE_ID = "SELECT * FROM asset a WHERE a.archived = true AND a.asset_type_id = ?";
    private static final String INSERT_ASSET = "INSERT INTO asset (name, asset_type_id, description, sn, manufacturer, category, site, location) values(?,?,?,?,?,?,?,?)";
    private static final String SET_UPDATED_TRUE = "UPDATE asset set updated = 1 where asset_id = ?";
    private static final String GET_ATTRIBUTE_DETAILS_FROM_ASSET_ID = "SELECT att.* FROM attribute_measurements am, attribute att WHERE att.attribute_id=am.attribute_id AND am.asset_id = ? GROUP by attribute_id";

    /**
     * When given an asset ID this will delete the the asset from the database as well as the corresponding
     * tables that reference the asset ID
     *
     * @param assetID represents the asset's ID
     * @author Jeff, Paul
     */
    @Override
    public void deleteAssetByID(int assetID) {
        try (PreparedStatement ps = getConnection().prepareStatement(DELETE_ASSET)) {
            ps.setInt(1, assetID);
            ps.executeQuery();
        } catch (SQLException e) {
            logger.error("Exception in deleteAssetByID(): ", e);
        }
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
            ps.executeQuery();
        } catch (SQLException e) {
            logger.error("Exception in insertAsset(): ", e);
        }
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
        StringBuilder preparedStatementPart1 = new StringBuilder();
        StringBuilder preparedStatementPart2 = new StringBuilder();

        try (PreparedStatement ps = getConnection().prepareStatement(GET_ASSET_INFO_FROM_ASSET_ID)) {
            ps.setInt(1, assetID);
            try (ResultSet attributesQuery = ps.executeQuery()) {
                while (attributesQuery.next()) {
                    AssetAttribute newAtt = new AssetAttribute(attributesQuery.getInt("attribute_id"), attributesQuery.getString(ATTRIBUTE_NAME));
                    preparedStatementPart1.append(", Coalesce(Sum(`").append(newAtt.getName()).append("`), 0) AS '").append(newAtt.getName()).append("'");
                    preparedStatementPart2.append(", CASE WHEN tab.attribute_id = ").append(newAtt.getId()).append(" THEN tab.value end AS `").append(newAtt.getName()).append("`");
                    newAssetInfo.addAttribute(newAtt);
                }
                try (PreparedStatement measurementStatement = getConnection().prepareStatement("SELECT `Cycle` " + preparedStatementPart1 + " FROM (SELECT tab.`time` as 'Cycle'" + preparedStatementPart2 + " FROM (SELECT am.attribute_id, am.`time`, am.value FROM attribute_measurements am WHERE asset_id = ?  ORDER BY attribute_id, time) tab) tab2 GROUP BY `CYCLE` asc;")) {
                    measurementStatement.setInt(1, assetID);
                    try (ResultSet measurementQuery = measurementStatement.executeQuery()) {
                        while (measurementQuery.next()) {
                            for (int i = 0; i < measurementQuery.getMetaData().getColumnCount() - 1; i++) {
                                newAssetInfo.getAssetAttributes().get(i).addMeasurement(measurementQuery.getInt("Cycle"), measurementQuery.getDouble(newAssetInfo.getAssetAttributes().get(i).getName()));
                            }
                        }
                    }
                }
            }
            return newAssetInfo;
        } catch (SQLException e) {
            logger.error("Exception in createAssetInfo(): ", e);
        }
        return null;
    }

    /**
     * this function returns the measurements for a given asset and cycle in a table format
     * this means that the data comes pre arranged in the ResultSet to simplify adding it to the
     * table in the UI
     *
     * @param assetID  the id of the asset we want the measurement for
     * @param fromTime this represents from what time cycle we want to start retrieving information
     * @author Paul
     */
    public ResultSet createMeasurementsFromAssetIdAndTime(int assetID, int fromTime) {
        StringBuilder preparedStatementPart1 = new StringBuilder();
        StringBuilder preparedStatementPart2 = new StringBuilder();

        ResultSet returned = null;
        try (PreparedStatement ps = getConnection().prepareStatement(GET_ATTRIBUTE_DETAILS_FROM_ASSET_ID)) {
            ps.setInt(1, assetID);
            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    preparedStatementPart1.append(", Coalesce(Sum(`");
                    preparedStatementPart1.append(rs.getString(ATTRIBUTE_NAME));
                    preparedStatementPart1.append("`), 0) AS '");
                    preparedStatementPart1.append(rs.getString(ATTRIBUTE_NAME));
                    preparedStatementPart1.append("'");

                    preparedStatementPart2.append(", CASE WHEN tab.attribute_id = ");
                    preparedStatementPart2.append(rs.getString("attribute_id"));
                    preparedStatementPart2.append(" THEN tab.value end AS `");
                    preparedStatementPart2.append(rs.getString(ATTRIBUTE_NAME));
                    preparedStatementPart2.append("`");
                }
            }

            // i'm unsure why but i cannot do setString on the prepared statement, it keeps failing the query.
            // This way is the same in terms of usage and speed and security.
            try (PreparedStatement measurementStatement = getConnection().prepareStatement("SELECT `Cycle` " + preparedStatementPart1 + " FROM (SELECT tab.`time` as 'Cycle'" + preparedStatementPart2 + " FROM (SELECT am.attribute_id, am.`time`, am.value FROM attribute_measurements am WHERE asset_id = ? AND time > ? ORDER BY attribute_id, time) tab) tab2 GROUP BY `CYCLE` asc;")) {
                measurementStatement.setInt(1, assetID);
                measurementStatement.setInt(2, fromTime);
                returned = measurementStatement.executeQuery();
            }
        } catch (SQLException e) {
            logger.error("Exception in createMeasurementsFromAssetIdAndTime(): ", e);
        }
        return returned;
    }

    /**
     * When given an asset type id it will return an ArrayList of asset
     * that are archived and of that type
     * used for classifier calculation
     *
     * @param assetTypeID represents the asset's type ID
     * @return a list of all the live assets for a specific asset type
     * @author Paul
     */
    @Override
    public List<Asset> getLiveAssetsFromAssetTypeID(int assetTypeID) {
        ArrayList<Asset> assets = new ArrayList<>();
        try (PreparedStatement ps = getConnection().prepareStatement(GET_LIVE_ASSETS_FROM_ASSET_TYPE_ID)) {
            ps.setInt(1, assetTypeID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    assets.add(createFullAssetFromQueryResult(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Exception in getLiveAssetsFromAssetTypeID(): ", e);
        }
        return assets;
    }

    /**
     * Given a result set of assets, this function will create the Asset object corresponding to the current
     * result set values
     *
     * @param assetsQuery represents the result set of asset query
     * @author Jeff, Paul
     */
    @Override
    public Asset createFullAssetFromQueryResult(ResultSet assetsQuery) throws SQLException {
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
     * When given an asset type id it will return an ArrayList of asset
     * that are archived and of that type
     * used for classifier calculation
     *
     * @param assetTypeID represents the asset's type ID
     * @return a list of all the archived asset for a specific asset type
     * @author Paul
     */
    @Override
    public List<Asset> getArchivedAssetsFromAssetTypeID(int assetTypeID) {
        ArrayList<Asset> assets = new ArrayList<>();
        try (PreparedStatement ps = getConnection().prepareStatement(GET_ARCHIVED_ASSETS_FROM_ASSET_TYPE_ID)) {
            ps.setInt(1, assetTypeID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    assets.add(createFullAssetFromQueryResult(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Exception in getArchivedAssetsFromAssetTypeID(): ", e);
        }
        return assets;
    }

    /**
     * This method changes the update indicator of the assert in the database to true
     *
     * @param assetID the specific id of the asset
     * @author Paul
     */
    @Override
    public void setAssetToBeUpdated(int assetID) {
        try (PreparedStatement ps = getConnection().prepareStatement(SET_UPDATED_TRUE)) {
            ps.setInt(1, assetID);
            ps.executeQuery();
        } catch (SQLException e) {
            logger.error("Exception in setAssetToBeUpdated(): ", e);
        }
    }
}