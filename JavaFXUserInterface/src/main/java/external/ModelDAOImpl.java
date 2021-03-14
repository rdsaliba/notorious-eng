/*
  Implementation of the DAO design pattern
  This class extends the general DAO object and implements the ModelDAO interface

  @author      Paul Micu
  @version     1.0
  @last_edit   12/27/2020
 */
package external;

import app.item.Model;
import app.item.TrainedModel;
import rul.models.ModelStrategy;
import weka.classifiers.Classifier;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ModelDAOImpl extends DAO implements ModelDAO {
    private static final String GET_MODEL_FROM_ASSET_TYPE_ID = "SELECT * from trained_model, model WHERE trained_model.model_id = model.model_id AND trained_model.asset_type_id = ?";
    private static final String GET_ALL_MODELS = "SELECT trained_model.*, model.name, model.description from trained_model, model where trained_model.model_id=model.model_id AND asset_type_id=? AND status_id=2";
    private static final String GET_LATEST_RMSE = "SELECT rmse from trained_model WHERE model_id=? AND asset_type_id=? AND status_id=2";
    private static final String UPDATE_MODEL_FOR_ASSET_TYPE = "UPDATE trained_model set model_id = ? where asset_type_id = ?";
    private static final String UPDATE_RETRAIN = "UPDATE trained_model SET retrain = true WHERE asset_type_id = ?";
    private static final String GET_MODEL_STRATEGY = "SELECT serialized_model from trained_model WHERE model_id=? AND asset_type_id=? AND status_id=2";
    private static final String UPDATE_MODEL_STRATEGY = "UPDATE trained_model SET serialized_model=?,retrain=true WHERE model_id = ? AND asset_type_id = ? and status_id = 2";

    /**
     * Given a asset type id, this function will return the string corresponding
     * to the name of the model in the database associated with the asset type
     *
     * @param assetTypeID represents a asset type id
     * @author Paul
     */
    @Override
    public String getModelNameFromAssetTypeID(String assetTypeID) {
        String name = null;
        try (PreparedStatement ps = getConnection().prepareStatement(GET_MODEL_FROM_ASSET_TYPE_ID)) {
            ps.setString(1, assetTypeID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    name = rs.getString("name");
            }
        } catch (SQLException e) {
            logger.error("Exception getModelNameFromAssetTypeID(): ", e);
        }
        return name;
    }



    /**
     * Given a RMSE model evaluation value for a specific model applied to a specific asset type,
     * this function will updated the RMSE value in the database in the model evaluation table
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

    /**
    * This function return Model ID of an asset type id
    * @param assetTypeID
     * @author Talal
    * */
    @Override
    public int getModelIDFromAssetTypeID(String assetTypeID) {
        int modelID = 0;
        try (PreparedStatement ps = getConnection().prepareStatement(GET_MODEL_FROM_ASSET_TYPE_ID)) {
            ps.setString(1, assetTypeID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    modelID = rs.getInt("model_id");
            }
        } catch (SQLException e) {
            logger.error("Exception getModelsByAssetTypeID(): ", e);
        }
        return modelID;
    }

    /**
     * This function will return a list of all the models that exist in the database and create
     * model objects for each of the model existing in the database
     *
     * @author Jeremie
     */
    @Override
    public List<Model> getAllModels(int assetTypeID) {
        ArrayList<Model> modelList = new ArrayList<>();
        try (PreparedStatement ps = getConnection().prepareStatement(GET_ALL_MODELS)) {
            ps.setInt(1,assetTypeID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Model newModel = new Model();
                    newModel.setModelID(rs.getInt("model_id"));
                    newModel.setModelName(rs.getString("name"));
                    newModel.setDescription(rs.getString("description"));
                    newModel.setRMSE(rs.getString("rmse"));
                    modelList.add(newModel);
                }
            }
        } catch (SQLException e) {
            logger.error("Exception getAllModels(): ", e);
        }
        return modelList;
    }

    /**
     * This function updates which model is associated with the specified asset type
     * to match the selection chosen by the user.
     *
     * @param modelID     is the model ID of the selected model
     * @param assetTypeID is the asset type ID of the specified asset type
     * @author Jeremie
     */
    @Override
    public void updateModelAssociatedWithAssetType(int modelID, String assetTypeID) {
        try (PreparedStatement ps = getConnection().prepareStatement(UPDATE_MODEL_FOR_ASSET_TYPE)) {
            ps.setInt(1, modelID);
            ps.setString(2, assetTypeID);
            ps.executeQuery();
        } catch (SQLException e) {
            logger.error("Exception updateRMSE(): ", e);
        }
    }

    /**
     * This function sets the model associated with the specified asset type to be retrained. It changes
     * the retrain attribute to true.
     *
     * @param assetTypeID is the asset type ID of the specified asset type
     * @author Jeremie
     */
    @Override
    public void setModelToTrain(String assetTypeID) {
        try (PreparedStatement ps = getConnection().prepareStatement(UPDATE_RETRAIN)) {
            ps.setString(1, assetTypeID);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Exception in setModelToTrain()");
        }
    }

    /**
     * This function sets the model associated with the specified asset type to be retrained. It changes
     * the retrain attribute to true.
     *
     * @param assetTypeID is the asset type ID of the specified asset type
     * @return The value of the RMSE under a String format
     * @author Jeremie
     */
    @Override
    public String getGetModelEvaluation(int modelID, String assetTypeID) {
        String rmseValue = null;
        try (PreparedStatement ps = getConnection().prepareStatement(GET_LATEST_RMSE)) {
            ps.setInt(1, modelID);
            ps.setString(2, assetTypeID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    rmseValue = rs.getString("rmse");
            }
        } catch (SQLException e) {
            logger.error("Exception in getGetModelEvaluation()");
        }
        if(rmseValue==null) return "n/a";
        else return rmseValue;
    }

    /**
     *  This function returns the model strategy for a specific model of an assettype
     * @param modelID
     * @param assetTypeID
     * @return modelStrategy
     * @author Talal
     */
    @Override
    public ModelStrategy getModelStrategy(int modelID, int assetTypeID) throws SQLException {
        ModelStrategy modelStrategy = null;
        try (PreparedStatement ps = getConnection().prepareStatement(GET_MODEL_STRATEGY)) {
            ps.setInt(1,modelID);
            ps.setInt(2,assetTypeID);
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    try {
                        byte[] buf = rs.getBytes("serialized_model");
                        if (buf != null)
                            modelStrategy =(ModelStrategy) new ObjectInputStream(new ByteArrayInputStream(buf)).readObject();

                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                        return null;
                    }}
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return modelStrategy;
    }

    /**
     * This function updates model strategy for an model
     * @param modelStrategy
     * @param modelID
     * @param assetTypeID
     * @author talal
     */
    @Override
    public void updateModelStrategy(ModelStrategy modelStrategy, int modelID, int assetTypeID){
        try (PreparedStatement ps = getConnection().prepareStatement(UPDATE_MODEL_STRATEGY)) {
            TrainedModel tm = new TrainedModel();
            tm.setModelStrategy(modelStrategy);
            tm.setAssetTypeID(assetTypeID);
            tm.setModelID(modelID);
            ps.setObject(1, tm.getModelStrategy());
            ps.setInt(2, tm.getModelID());
            ps.setInt(3, tm.getAssetTypeID());
            ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * This function is used to automatically reteive RMSE when a new value is in the database
     * @param modelID
     * @param assetTypeID
     * @author talal
     */
    public double getLatestRMSE(int modelID,int assetTypeID){
        double estimate = -100000;
        try (PreparedStatement ps = getConnection().prepareStatement(GET_LATEST_RMSE)) {
            ps.setInt(1, modelID);
            ps.setInt(2, assetTypeID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    estimate = rs.getDouble("rmse");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return estimate;
    }
}


