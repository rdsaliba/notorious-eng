package app;/*
  Implementation of the DAO design pattern.
  This class extends the general DAO object and calculates the RUL
  of assets in real time with new incoming measurements.

  @author
  @last_edit 02/7/2020
 */
import app.item.Asset;
import app.item.AssetAttribute;
import app.item.AssetInfo;
import local.DAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MeasurementDAO extends DAO {
    private static final String GET_MEASUREMENTS = "SELECT * from attribute_measurements where asset_id =? and time > ((SELECT MAX(time) FROM attribute_measurements where asset_id=?) - ?) order by attribute_id";
    private static final String INSERT_MEASUREMENT_FOR_ASSET = "insert into attribute_measurements values (?,?,?,?)";
    private static final String DELETE_MEASUREMENT = "delete from attribute_measurements where asset_id =? and time > ((SELECT MAX(time) FROM attribute_measurements where asset_id=?) - ?) order by attribute_id";

    /** this methods returns an asset info object populated with the attributes measurements
     * of the last x time cycles where x is identified by the limit
     *
     * @param assetID the id of the specific asset
     * @param limit how many entries to get
     * @return AssetInfo
     * @author Paul
     */
    public AssetInfo getMeasurementsFromID(int assetID, int limit) {
        AssetInfo toReturn = new AssetInfo();
        ArrayList<AssetAttribute> measurements = new ArrayList<>();
        try (PreparedStatement ps = getConnection().prepareStatement(GET_MEASUREMENTS)) {
            ps.setInt(1, assetID);
            ps.setInt(2, assetID);
            ps.setInt(3, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    if (measurements.isEmpty() || measurements.get(measurements.size() - 1).getId() != rs.getInt("attribute_id")) {
                        AssetAttribute assetAttribute = new AssetAttribute();
                        assetAttribute.setId(rs.getInt("attribute_id"));
                        measurements.add(assetAttribute);
                    }
                    measurements.get(measurements.size() - 1).addMeasurement(rs.getInt("time"), rs.getDouble("value"));

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        toReturn.setAssetAttributes(measurements);
        return toReturn;
    }

    /** this methods deletes the last x cycles worth of measurements for a specific asset type
     * where x is represented by limit
     *
     * @param assetID the id of the specific asset
     * @param limit how many entries to delete
     * @author Paul
     */
    public void deleteMeasurementsFromID(int assetID, int limit) {

        try (PreparedStatement ps = getConnection().prepareStatement(DELETE_MEASUREMENT)) {
            ps.setInt(1, assetID);
            ps.setInt(2, assetID);
            ps.setInt(3, limit);
            ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** This method makes inserts for the specific time cycle
     * to insert the measurements held in the asset
     *
     * @param asset the asset to insert
     * @param time what time to insert
     * @author Paul
     */
    public void insertMeasurement(Asset asset, int time) {
        try (PreparedStatement ps = getConnection().prepareStatement(INSERT_MEASUREMENT_FOR_ASSET)) {
            for (AssetAttribute assetAttribute : asset.getAssetInfo().getAssetAttributes()) {
                if (time < assetAttribute.getMeasurements().size()) {
                    ps.setInt(1, asset.getId());
                    ps.setInt(2, assetAttribute.getId());
                    ps.setInt(3, assetAttribute.getMeasurements().get(time).getTime());
                    ps.setDouble(4, assetAttribute.getMeasurements().get(time).getValue());
                    ps.addBatch();
                }
            }
            ps.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
