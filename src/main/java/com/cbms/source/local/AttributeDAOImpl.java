package com.cbms.source.local;

import com.cbms.app.Main;
import com.cbms.app.item.Measurement;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AttributeDAOImpl extends DAO implements AttributeDAO {

    private static final String GET_LATEST_MEASUREMENTS_FROM_ASSET_AND_ATTRIBUTE_ID = "SELECT * FROM attribute_measurements WHERE asset_id = ? and attribute_id = ? order by time desc limit ?;";
    private static final String GET_LATEST_MEASUREMENTS_FROM_ASSET_AND_ATTRIBUTE_ID_REAL_TIME = "SELECT * FROM attribute_measurements WHERE asset_id = ? and attribute_id = ? and time <= ? order by time desc limit ?;";

    @Override
    public ArrayList<Measurement> getLastXMeasurementsByAssetIDAndAttributeID(String assetID, String attributeID, int limiter) {
        ArrayList<Measurement> measurements = new ArrayList<>();
        try {
            PreparedStatement ps;
            if (Main.isRealTime())
                ps = getConnection().prepareStatement(GET_LATEST_MEASUREMENTS_FROM_ASSET_AND_ATTRIBUTE_ID_REAL_TIME);
            else
                ps = getConnection().prepareStatement(GET_LATEST_MEASUREMENTS_FROM_ASSET_AND_ATTRIBUTE_ID);
            ps.setString(1,assetID);
            ps.setString(2,attributeID);
            if(Main.isRealTime()){
                ps.setInt(3, Main.getTime());
                ps.setInt(4,limiter);
            }
            else
                ps.setInt(3, limiter);
            ResultSet queryResult =  ps.executeQuery();
            while (queryResult.next())
                measurements.add(new Measurement(queryResult.getInt("time"),queryResult.getDouble("value")));
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            closeConnection();
            return measurements;
        }
    }
}
