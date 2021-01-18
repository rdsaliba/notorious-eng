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

    @Override
    public void insertAssetType(AssetType assetType) {
        try {
            PreparedStatement ps = getConnection().prepareStatement(INSERT_ASSET_TYPE,
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, assetType.getName());
            ps.executeQuery();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    for (AssetTypeParameter assetTypeParameter: assetType.getThresholdList()) {
                        PreparedStatement statement = getConnection().prepareStatement(INSERT_ASSET_TYPE_PARAMETERS);
                        statement.setInt(1, Integer.parseInt(String.valueOf(generatedKeys.getLong(1))));
                        statement.setString(2, assetTypeParameter.getName());
                        statement.setString(3, String.valueOf(assetTypeParameter.getValue()));
                        statement.executeQuery();
                    }
                }
                else {
                    throw new SQLException("Creating threshold failed, no ID obtained.");
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method retrieves the complete list of possible asset types and return their
     * names
     */
    @Override
    public ArrayList<String> getAssetTypeList() {
        ArrayList<String> assetTypeList = new ArrayList<>();
        try {
            PreparedStatement ps = getConnection().prepareStatement(GET_ASSET_TYPES);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                assetTypeList.add(rs.getString("name"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return assetTypeList;
    }

    @Override
    public String getNameFromID(String id){
        String name = "";
        try {
            PreparedStatement ps = getConnection().prepareStatement(GET_ASSET_TYPE_NAME_FROM_ID);
            ps.setString(1,id);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                name = rs.getString("name");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return name;
    }


}
