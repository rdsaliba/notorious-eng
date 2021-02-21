/*
  Implementation of the DAO design pattern
  This class extends the general DAO object and implements the ModelDAO interface

  @author      Paul Micu
  @version     1.0
  @last_edit   12/27/2020
 */
package local;

import app.item.TrainedModel;
import weka.classifiers.Classifier;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ModelDAOImpl extends DAO implements ModelDAO {
    private static final String UPDATE_SERIALIZE_OBJECT = "UPDATE trained_model SET retrain = false, serialized_model  = ? WHERE model_id = ? AND asset_type_id = ?";
    private static final String GET_SERIALIZE_OBJECT = "SELECT * FROM trained_model WHERE retrain = true";
    private static final String GET_MODEL_NAME_FROM_ID = "SELECT name from model where model_id = ?";
    private static final String GET_MODEL_FROM_ASSET_TYPE = "SELECT * FROM trained_model WHERE asset_type_id = ?";

    /**
     * Given a model id, this function will return the string corresponding
     * to the name of the model in the database
     *
     * @param modelID represents a model's id
     * @author Paul
     */
    @Override
    public String getModelNameFromModelID(int modelID) {
        String name = null;
        try (PreparedStatement ps = getConnection().prepareStatement(GET_MODEL_NAME_FROM_ID)) {
            ps.setInt(1, modelID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    name = rs.getString("name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return name;
    }

    /**
     * This function will return an arraylist of all the models that need to be retrained
     * by looking at the retrain tag. it also deserializes the current model stored in the db
     *
     * @author Paul
     */
    @Override
    public ArrayList<TrainedModel> getModelsToTrain() {

        ArrayList<TrainedModel> tms = new ArrayList<>();
        try (PreparedStatement ps = getConnection().prepareStatement(GET_SERIALIZE_OBJECT)) {
            try (ResultSet queryResult = ps.executeQuery()) {
                while (queryResult.next()) {
                    tms.add(createTrainedModelFromResultSet(queryResult, false));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tms;
    }

    /**
     * Given an arraylist of trained models this function will write the new Classifier object
     * to the corresponding trained model entry after an model training phase
     *
     * @param tms<TrainedModel> represents a list of trained models
     * @author Paul
     */
    @Override
    public void setModelsToTrain(ArrayList<TrainedModel> tms) {
        try {
            for (TrainedModel tm : tms) {
                try (PreparedStatement ps = getConnection().prepareStatement(UPDATE_SERIALIZE_OBJECT)) {
                    ps.setObject(1, tm.getModelClassifier());
                    ps.setInt(2, tm.getModelID());
                    ps.setInt(3, tm.getAssetTypeID());
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Given the id of an asset type, this function will return the trained model
     * corresponding the that asset type. Since only one model can be associated to
     * an asset type at a time only one TrainedModel object is returned
     *
     * @param assetTypeID represents a the id of an asset type
     * @author Paul
     */
    @Override
    public TrainedModel getModelsByAssetTypeID(String assetTypeID) {
        TrainedModel tm = null;
        try (PreparedStatement ps = getConnection().prepareStatement(GET_MODEL_FROM_ASSET_TYPE)) {
            ps.setString(1, assetTypeID);
            try (ResultSet queryResult = ps.executeQuery()) {
                while (queryResult.next()) {
                    tm = createTrainedModelFromResultSet(queryResult, true);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tm;
    }

    /**
     * Given a result set object, this function will create the corresponding trained model object
     *
     * @param rs represents the result from a trained model query
     * @author Paul
     */
    @Override
    public TrainedModel createTrainedModelFromResultSet(ResultSet rs, boolean withModel) throws SQLException {
        TrainedModel tm = new TrainedModel();
        tm.setModelID(rs.getInt("model_id"));
        tm.setAssetTypeID(rs.getInt("asset_type_id"));
        tm.setRetrain(rs.getBoolean("retrain"));
        try {
            if (withModel) {
                tm.setModelClassifier((Classifier) new ObjectInputStream(new ByteArrayInputStream(rs.getBytes("serialized_model"))).readObject());
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return tm;
    }
}
