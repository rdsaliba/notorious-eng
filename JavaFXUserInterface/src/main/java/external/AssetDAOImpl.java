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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AssetDAOImpl extends DAO implements AssetDAO {

    Logger logger = LoggerFactory.getLogger(AssetDAOImpl.class);

    private static final String DELETE_ASSET = "DELETE FROM asset WHERE asset_id = ?";
    private static final String GET_ASSET_INFO_FROM_ASSET_ID = "SELECT DISTINCT att.* FROM attribute_measurements am, attribute att WHERE att.attribute_id=am.attribute_id AND am.asset_id = ?";
    private static final String GET_ALL_LIVE_ASSETS = "SELECT * FROM asset, asset_type WHERE asset.asset_type_id=asset_type.asset_type_id AND archived = false";
    private static final String INSERT_ASSET = "INSERT INTO asset (name, asset_type_id, description, sn, manufacturer, category, site, location) values(?,?,?,?,?,?,?,?)";
    private static final String GET_ATTRIBUTE_DETAILS_FROM_ASSET_ID = "SELECT att.* FROM attribute_measurements am, attribute att WHERE att.attribute_id=am.attribute_id AND am.asset_id = ? GROUP by attribute_id";
    public static final String ATTRIBUTE_NAME = "attribute_name";

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
            logger.error("Exception in getAllLiveAssets(): ", e);
        }
        return assets;
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
        StringBuilder preparedStatementPart1 = new StringBuilder();
        StringBuilder preparedStatementPart2 = new StringBuilder();
        String query = "SELECT `Cycle` " + preparedStatementPart1 + " FROM (SELECT tab.`time` as 'Cycle'" + preparedStatementPart2 + " FROM (SELECT am.attribute_id, am.`time`, am.value FROM attribute_measurements am WHERE asset_id = ?  ORDER BY attribute_id, time) tab) tab2 GROUP BY `CYCLE` asc;";

        try (PreparedStatement ps = getConnection().prepareStatement(GET_ASSET_INFO_FROM_ASSET_ID)) {
            ps.setInt(1, assetID);
            try (ResultSet attributesQuery = ps.executeQuery()) {
                while (attributesQuery.next()) {
                    AssetAttribute newAtt = new AssetAttribute(attributesQuery.getInt("attribute_id"), attributesQuery.getString(ATTRIBUTE_NAME));
                    preparedStatementPart1.append(", Coalesce(Sum(`").append(newAtt.getName()).append("`), 0) AS '").append(newAtt.getName()).append("'");
                    preparedStatementPart2.append(", CASE WHEN tab.attribute_id = ").append(newAtt.getId()).append(" THEN tab.value end AS `").append(newAtt.getName()).append("`");
                    newAssetInfo.addAttribute(newAtt);
                }
                try (PreparedStatement measurementStatement = getConnection().prepareStatement(query)) {
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
        String query = "SELECT `Cycle` " + preparedStatementPart1 + " FROM (SELECT tab.`time` as 'Cycle'" + preparedStatementPart2 + " FROM (SELECT am.attribute_id, am.`time`, am.value FROM attribute_measurements am WHERE asset_id = ? AND time > ? ORDER BY attribute_id, time) tab) tab2 GROUP BY `CYCLE` asc;";

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
            try (PreparedStatement measurementStatement = getConnection().prepareStatement(query)) {
                measurementStatement.setInt(1, assetID);
                measurementStatement.setInt(2, fromTime);
                returned = measurementStatement.executeQuery();
            }
        } catch (SQLException e) {
            logger.error("Exception in createMeasurementsFromAssetIdAndTime(): ", e);
        }
        return returned;
    }
}
