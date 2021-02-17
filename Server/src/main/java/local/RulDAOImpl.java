/*
  Implementation of the DAO design pattern
  This class extends the general DAO object and implements the RulDAO interface

  @author      Paul Micu
  @last_edit   12/27/2020
 */
package local;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RulDAOImpl extends DAO implements RulDAO {
    public static final String GET_LATEST_RUL_FROM_ASSET_ID = "SELECT value FROM asset_model_calculation where asset_id = ? ORDER BY `timestamp` desc LIMIT 1";

    /**
     * Given an asset id this function will return the double value of
     * the latest RUL measurement regardless of the model or timestamp
     *
     * @param assetID represents an asset's id
     * @author Paul
     */
    @Override
    public double getLatestRUL(int assetID) {
        double estimate = -100000;
        try (PreparedStatement ps = getConnection().prepareStatement(GET_LATEST_RUL_FROM_ASSET_ID)) {
            ps.setInt(1, assetID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    estimate = rs.getDouble("value");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return estimate;
    }
}
