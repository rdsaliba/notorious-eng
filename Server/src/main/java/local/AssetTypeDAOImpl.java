package local;

import app.item.AssetType;
import app.item.AssetTypeParameter;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class AssetTypeDAOImpl extends DAO implements AssetTypeDAO {
    private static final String INSERT_ASSET_TYPE = "INSERT INTO asset_type (name, description) values( ?,? )";
    private static final String INSERT_ASSET_TYPE_PARAMETERS = "INSERT INTO asset_type_parameters (asset_type_id, parameter_name, boundary) values(?, ?, ?)";
    private static final String GET_ASSET_TYPES = "SELECT * FROM asset_type";
    private static final String GET_ASSET_TYPE_NAME_FROM_ID = "SELECT name FROM asset_type where asset_type_id = ?";
    private static final String GET_ASSET_TYPE_BOUNDARY = "SELECT *  FROM asset_type_parameters WHERE parameter_name = ? AND asset_type_id = ?";
    private static final String GET_ASSET_TYPE_BOUNDARIES = "SELECT *  FROM asset_type_parameters WHERE asset_type_id = ?";
    private static final String GET_ASSET_TYPE_ID_COUNT = "SELECT COUNT(*) as 'count' FROM asset WHERE archived = ? and asset_type_id =?";
    private static final String DELETE_ASSET_TYPE = "DELETE FROM asset_type where asset_type_id = ?";
    private static final String UPDATE_ASSET_TYPE = "UPDATE asset_type set name =?, description = ? where asset_type_id = ?";
    private static final String UPDATE_ASSET_TYPE_PARAMETER = "UPDATE asset_type_parameters set boundary = ? where asset_type_id = ? and parameter_name =?";

    /**
     * This will return an arraylist of the count for all the asset type ids from the
     * asset table
     * edit: returns the count for a single asset type
     * @author Shirwa, Paul
     */
    @Override
    public int getAssetTypeIdCount(String assetTypeID, boolean isLive) {

        try (PreparedStatement ps = getConnection().prepareStatement(GET_ASSET_TYPE_ID_COUNT)) {
            ps.setBoolean(1, !isLive);
            ps.setString(2, assetTypeID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return (rs.getInt("count"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public String getAssetTypeBoundary(String asset_type_id, String boundary_type){
        String boundary = "null";
        try (PreparedStatement ps = getConnection().prepareStatement(GET_ASSET_TYPE_BOUNDARY)) {
            ps.setString(1, boundary_type);
            ps.setString(2, asset_type_id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    boundary = rs.getString("boundary");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (boundary == null || boundary.equals("null"))
            boundary= "-";
        return boundary;
    }

    /**
     * Gets the boundary values for an Asset Type.
     *
     * @param asset_type_id is the id of the asset type
     * @return a map of the different boundary labels and their values
     */
    @Override
    public HashMap<String, Double> getAssetTypeBoundaries(String asset_type_id) {
        HashMap<String, Double> boundaries = new HashMap<>();
        try (PreparedStatement ps = getConnection().prepareStatement(GET_ASSET_TYPE_BOUNDARIES)) {
            ps.setString(1, asset_type_id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    boundaries.put(rs.getString("parameter_name"), rs.getDouble("boundary"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return boundaries;
    }


    @Override
    public int insertAssetType(AssetType assetType) {
        try (PreparedStatement ps = getConnection().prepareStatement(INSERT_ASSET_TYPE,
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, assetType.getName());
            ps.setString(2, assetType.getDescription());
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
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating threshold failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * This method retrieves the complete list of possible asset types and return their
     * names
     */
    @Override
    public ArrayList<AssetType> getAssetTypeList() {
        ArrayList<AssetType> assetTypeList = new ArrayList<>();
        try (PreparedStatement ps = getConnection().prepareStatement(GET_ASSET_TYPES)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AssetType newAssetType = new AssetType();
                    newAssetType.setName(rs.getString("name"));
                    newAssetType.setDescription(rs.getString("description"));
                    newAssetType.setId(rs.getString("asset_type_id"));
                    assetTypeList.add(newAssetType);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return assetTypeList;
    }

    @Override
    public String getNameFromID(String id){
        String name = "";
        try (PreparedStatement ps = getConnection().prepareStatement(GET_ASSET_TYPE_NAME_FROM_ID)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    name = rs.getString("name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return name;
    }


    @Override
    public void updateAssetType(AssetType assetType) {
        try (PreparedStatement ps = getConnection().prepareStatement(UPDATE_ASSET_TYPE)) {
            ps.setString(1, assetType.getName());
            ps.setString(2, assetType.getDescription());
            ps.setString(3, assetType.getId());
            ps.executeQuery();
            try (PreparedStatement ps2 = getConnection().prepareStatement(UPDATE_ASSET_TYPE_PARAMETER)) {
                for (AssetTypeParameter assetTypeParameter: assetType.getThresholdList()){
                    if (assetTypeParameter.getValue() == null)
                        ps2.setNull(1, Types.DOUBLE);
                    else
                        ps2.setDouble(1, assetTypeParameter.getValue());
                    ps2.setString(2,assetType.getId());
                    ps2.setString(3,assetTypeParameter.getName());
                    ps2.addBatch();
                }
                ps2.executeBatch();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void deleteAssetTypeByID(String assetTypeID) {
        try (PreparedStatement ps = getConnection().prepareStatement(DELETE_ASSET_TYPE)) {
            ps.setString(1,assetTypeID);
            ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
