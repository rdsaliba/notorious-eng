package com.cbms.source.local;

import com.cbms.app.item.AssetType;
import com.cbms.app.item.AssetTypeParameter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AssetTypeDAOImpl extends DAO implements AssetTypeDAO {
    private static final String INSERT_ASSET_TYPE = "INSERT INTO asset_type (name) values( ? )";
    private static final String INSERT_ASSET_TYPE_PARAMETERS = "INSERT INTO asset_type_parameters (asset_type_id, parameter_name, boundary) values(?, ?, ?)";

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
}
