/*
  Implementation of the DAO design pattern
  This class extends the general DAO object and implements the AttributeDAO interface

  @author
  @last_edit 02/7/2020
 */
package external;

import app.item.Measurement;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AttributeDAOImpl extends DAO implements AttributeDAO {

    private static final String GET_LATEST_MEASUREMENTS_FROM_ASSET_AND_ATTRIBUTE_ID = "SELECT * FROM attribute_measurements WHERE asset_id = ? and attribute_id = ? order by time desc limit ?;";

    /**
     * this method will return the latest(identified by limiter) measurements for the asset id and attribute id combination
     *
     * @author Paul
     */
    @Override
    public ArrayList<Measurement> getLastXMeasurementsByAssetIDAndAttributeID(String assetID, String attributeID, int limiter) {
        ArrayList<Measurement> measurements = new ArrayList<>();
        try (PreparedStatement ps = getConnection().prepareStatement(GET_LATEST_MEASUREMENTS_FROM_ASSET_AND_ATTRIBUTE_ID)) {
            ps.setString(1, assetID);
            ps.setString(2, attributeID);
            ps.setInt(3, limiter);
            try (ResultSet queryResult = ps.executeQuery()) {
                while (queryResult.next())
                    measurements.add(new Measurement(queryResult.getInt("time"), queryResult.getDouble("value")));
            }
        } catch (SQLException e) {
            logger.error("Exception: ", e);
        }
        return measurements;
    }
}
