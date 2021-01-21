package local;

import app.item.AssetType;
import app.item.AssetTypeParameter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class AssetTypeDAOImpl extends DAO implements AssetTypeDAO {
    private static final String INSERT_ASSET_TYPE = "INSERT INTO asset_type (name) values( ? )";
    private static final String INSERT_ASSET_TYPE_PARAMETERS = "INSERT INTO asset_type_parameters (asset_type_id, parameter_name, boundary) values(?, ?, ?)";
    private static final String GET_ASSET_TYPES = "SELECT * FROM asset_type";
    private static final String GET_ASSET_TYPE_NAME_FROM_ID = "SELECT name FROM asset_type where asset_type_id = ?";
    private static final String GET_ASSET_TYPE_BOUNDARY = "SELECT *  FROM asset_type_parameters WHERE parameter_name = ? AND asset_type_id = ?";
    private static final String GET_ASSET_TYPE_ID_COUNT = "SELECT asset_type_id, COUNT(*) as 'count' FROM asset WHERE archived = 0 GROUP BY asset_type_id";
    private static final String DELETE_ASSET_TYPE = "DELETE FROM asset_type where asset_type_id = ?";
    /**
     * This will return an arraylist of the count for all the asset type ids from the
     * asset table
     * @author Shirwa
     */
    public ArrayList<Integer> getAssetTypeIdCount() {
        ArrayList<Integer> assets = new ArrayList<>();

        ResultSet rs;
        try (PreparedStatement ps = getConnection().prepareStatement(GET_ASSET_TYPE_ID_COUNT)) {
            rs = ps.executeQuery();
            while (rs.next())
                assets.add(rs.getInt("count"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return assets;
    }

    public double getAssetTypeBoundary(int asset_type_id, int boundary_type){
        double boundary = 0.0;
        ResultSet rs;
        try (PreparedStatement ps = getConnection().prepareStatement(GET_ASSET_TYPE_BOUNDARY)) {
            ps.setInt(1, boundary_type);
            ps.setInt(2, asset_type_id);
            rs = ps.executeQuery();
            if (rs.next())
                boundary = rs.getDouble("boundary");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return boundary;
    }


    @Override
    public void insertAssetType(AssetType assetType) {
        try (PreparedStatement ps = getConnection().prepareStatement(INSERT_ASSET_TYPE,
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, assetType.getName());
            ps.executeQuery();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    for (AssetTypeParameter assetTypeParameter : assetType.getThresholdList()) {
                        try (PreparedStatement statement = getConnection().prepareStatement(INSERT_ASSET_TYPE_PARAMETERS)) {
                            statement.setInt(1, Integer.parseInt(String.valueOf(generatedKeys.getLong(1))));
                            statement.setString(2, assetTypeParameter.getName());
                            statement.setString(3, String.valueOf(assetTypeParameter.getValue()));
                            statement.executeQuery();
                        }
                    }
                } else {
                    throw new SQLException("Creating threshold failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method retrieves the complete list of possible asset types and return their
     * names
     */
    @Override
    public ArrayList<AssetType> getAssetTypeList() {
        ArrayList<AssetType> assetTypeList = new ArrayList<>();
        ResultSet rs;
        try (PreparedStatement ps = getConnection().prepareStatement(GET_ASSET_TYPES)) {
            rs = ps.executeQuery();
            while (rs.next()) {
                AssetType newAssetType = new AssetType();
                newAssetType.setName(rs.getString("name"));
                newAssetType.setId(rs.getString("asset_type_id"));
                assetTypeList.add(newAssetType);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return assetTypeList;
    }

    @Override
    public String getNameFromID(String id){
        String name = "";
        ResultSet rs;
        try (PreparedStatement ps = getConnection().prepareStatement(GET_ASSET_TYPE_NAME_FROM_ID)) {
            ps.setString(1, id);
            rs = ps.executeQuery();
            if (rs.next())
                name = rs.getString("name");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return name;
    }


    public void deleteAssetTypeByID(String assetTypeID) {
        try (PreparedStatement ps = getConnection().prepareStatement(DELETE_ASSET_TYPE)) {
            ps.setString(1,assetTypeID);
            ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
