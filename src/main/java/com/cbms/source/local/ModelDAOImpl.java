package com.cbms.source.local;

import com.cbms.app.TrainedModel;
import weka.classifiers.Classifier;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ModelDAOImpl extends DAO implements ModelDAO {
    private static final String UPDATE_SERIALIZE_OBJECT = "UPDATE trained_model SET retrain = true, serialized_model  = ? WHERE model_id = ? AND asset_type_id = ?";
    private static final String GET_SERIALIZE_OBJECT = "SELECT * FROM trained_model WHERE retrain = true";
    private static final String GET_MODEL_NAME_FROM_ID = "SELECT name from model where model_id = ?";
    private static final String GET_MODEL_FROM_ASSET_TYPE = "SELECT * FROM trained_model WHERE asset_type_id = ?";

    @Override
    public String getModelNameFromModelID(int modelID) {
        String name = null;
        try {
            PreparedStatement ps = getConnection().prepareStatement(GET_MODEL_NAME_FROM_ID);
            ps.setInt(1, modelID);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                name= rs.getString("name");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            return name;
        }
    }

    @Override
    public void setModelsToTrain(ArrayList<TrainedModel> tms) {
        try {
            for (TrainedModel tm : tms) {
                PreparedStatement ps = getConnection().prepareStatement(UPDATE_SERIALIZE_OBJECT);
                ps.setObject(1, tm.getModelClassifier());
                ps.setInt(2, tm.getModelID());
                ps.setInt(3, tm.getAssetTypeID());
                ps.executeUpdate();
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<TrainedModel> getModelsToTrain() {

        ArrayList<TrainedModel> tms = new ArrayList<>();
        try {
            PreparedStatement ps = getConnection().prepareStatement(GET_SERIALIZE_OBJECT);
            ResultSet queryResult = ps.executeQuery();
            while (queryResult.next()){
                tms.add(createTrainedModelFromResultSet(queryResult));
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        } finally {
            return tms;
        }

    }

    @Override
    public TrainedModel getModelsByAssetTypeID(String assetTypeID) {
        TrainedModel tm = null;
        try {
            PreparedStatement ps = getConnection().prepareStatement(GET_MODEL_FROM_ASSET_TYPE);
            ps.setString(1,assetTypeID);
            ResultSet queryResult = ps.executeQuery();
            while (queryResult.next()){
                tm = createTrainedModelFromResultSet(queryResult);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            return tm;
        }
    }

    private TrainedModel createTrainedModelFromResultSet(ResultSet rs) throws SQLException {
        TrainedModel tm = new TrainedModel();
        tm.setModelID(rs.getInt("model_id"));
        tm.setAssetTypeID(rs.getInt("asset_type_id"));
        tm.setRetrain(rs.getBoolean("retrain"));
        try {
            tm.setModelClassifier((Classifier) new ObjectInputStream(new ByteArrayInputStream(rs.getBytes("serialized_model"))).readObject());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return tm;
    }
}
