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

    public AssetInfo getMeasurementsFromID(int assetID, int limit) {
        AssetInfo toReturn = new AssetInfo();
        ArrayList<AssetAttribute> measurements = new ArrayList<>();
        try {
            PreparedStatement ps = getConnection().prepareStatement(GET_MEASUREMENTS);
            ps.setInt(1, assetID);
            ps.setInt(2, assetID);
            ps.setInt(3, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                if (measurements.isEmpty() || measurements.get(measurements.size() - 1).getId() != rs.getInt("attribute_id")) {
                    AssetAttribute assetAttribute = new AssetAttribute();
                    assetAttribute.setId(rs.getInt("attribute_id"));
                    measurements.add(assetAttribute);
                }
                measurements.get(measurements.size() - 1).addMeasurement(rs.getInt("time"), rs.getDouble("value"));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        toReturn.setAssetAttributes(measurements);
        return toReturn;
    }

    public void deleteMeasurementsFromID(int assetID, int limit) {

        try {
            PreparedStatement ps = getConnection().prepareStatement(DELETE_MEASUREMENT);
            ps.setInt(1, assetID);
            ps.setInt(2, assetID);
            ps.setInt(3, limit);
            ps.executeQuery();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertMeasurement(Asset asset, int time) {
        try {
            PreparedStatement ps = getConnection().prepareStatement(INSERT_MEASUREMENT_FOR_ASSET);
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
