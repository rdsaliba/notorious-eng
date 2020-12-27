package com.cbms.source.local;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RulDAOImpl extends DAO implements RulDAO {
    public static final String GET_LATEST_RUL_FROM_ASSET_ID = "SELECT value FROM asset_model_calculation where asset_id = ? ORDER BY `timestamp` desc LIMIT 1";

    @Override
    public double getLatestRUL(int assetID) {
        double estimate = -100000;
        try {
            PreparedStatement ps = getConnection().prepareStatement(GET_LATEST_RUL_FROM_ASSET_ID);
            ps.setInt(1, assetID);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                estimate = rs.getDouble("value");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            return estimate;
        }
    }
}
