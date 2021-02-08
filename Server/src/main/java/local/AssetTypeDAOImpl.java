package local;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class AssetTypeDAOImpl extends DAO implements AssetTypeDAO {
    private static final String GET_ASSET_TYPE_BOUNDARIES = "SELECT *  FROM asset_type_parameters WHERE asset_type_id = ? and boundary is not null";

    /**
     * Gets the boundary values for an Asset Type.
     *
     * @param assetTypeId is the id of the asset type
     * @return a map of the different boundary labels and their values
     */
    @Override
    public HashMap<String, Double> getAssetTypeBoundaries(String assetTypeId) {
        HashMap<String, Double> boundaries = new HashMap<>();
        try (PreparedStatement ps = getConnection().prepareStatement(GET_ASSET_TYPE_BOUNDARIES)) {
            ps.setString(1, assetTypeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    boundaries.put(rs.getString("parameter_name"), rs.getDouble("boundary"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return boundaries;
    }

}
