/*
  Implementation of the DAO design pattern
  This class extends the general DAO object and implements the AssetTypeDAO interface
  @author
  @last_edit 02/7/2020
 */
package local;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class AssetTypeDAOImpl extends DAO implements AssetTypeDAO {
    private static final String GET_ASSET_TYPE_BOUNDARIES = "SELECT *  FROM asset_type_parameters WHERE asset_type_id = ? and boundary is not null";

    /**
     * Gets the threshold values for an Asset Type.
     *
     * @param assetTypeId is the id of the asset type
     * @return a map of the different threshold labels and their values
     */
    @Override
    public HashMap<String, Double> getAssetTypeThresholds(String assetTypeId) {
        HashMap<String, Double> thresholds = new HashMap<>();
        try (PreparedStatement ps = getConnection().prepareStatement(GET_ASSET_TYPE_THRESHOLDS)) {
            ps.setString(1, assetTypeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    thresholds.put(rs.getString("parameter_name"), rs.getDouble("boundary"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return thresholds;
    }

}
