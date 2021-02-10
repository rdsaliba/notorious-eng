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

public class AssetDAOImpl extends DAO implements AssetDAO {

    private static final String DELETE_ASSET = "DELETE FROM ? WHERE asset_id = ?";
    private static final String GET_ASSET_INFO_FROM_ASSET_ID = "SELECT DISTINCT att.* FROM attribute_measurements am, attribute att WHERE att.attribute_id=am.attribute_id AND am.asset_id = ?";
    private static final String GET_ALL_LIVE_ASSETS = "SELECT * FROM asset, asset_type WHERE asset.asset_type_id=asset_type.asset_type_id AND archived = false";
    private static final String INSERT_ASSET = "INSERT INTO asset (name, asset_type_id, description, sn, manufacturer, category, site, location, unit_nb) values(?,?,?,?,?,?,?,?,?)";


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
        try (PreparedStatement ps = getConnection().prepareStatement(GET_ASSET_INFO_FROM_ASSET_ID)) {
            ps.setInt(1, assetID);
            try (ResultSet attributesQuery = ps.executeQuery()) {
                while (attributesQuery.next()) {
                    AssetAttribute newAtt = new AssetAttribute(attributesQuery.getInt("attribute_id"), attributesQuery.getString("attribute_name"));
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
            e.printStackTrace();
        }
        return null;
    }
}
