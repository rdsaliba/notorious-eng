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
import utilities.Constants;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ModelDAOImpl extends DAO implements ModelDAO {
    private static final String GET_MODEL_FROM_ASSET_TYPE_ID = "SELECT * from trained_model, model WHERE trained_model.model_id = model.model_id AND trained_model.asset_type_id = ?";
    private static final String GET_ALL_MODELS_FOR_EVALUATION = "SELECT trained_model.*, model.name, model.description from trained_model, model where trained_model.model_id=model.model_id AND asset_type_id=? AND status_id=?";
    private static final String GET_LATEST_RMSE = "SELECT rmse from trained_model WHERE model_id=? AND asset_type_id=? AND status_id=?";
    private static final String GET_MODEL_STRATEGY = "SELECT serialized_model from trained_model WHERE model_id=? AND asset_type_id=? AND status_id=2";
    private static final String UPDATE_MODEL_STRATEGY = "UPDATE trained_model SET serialized_model=?,retrain=true WHERE model_id = ? AND asset_type_id = ? and status_id = 2";
    private static final String UPDATE_MODEL_FOR_ASSET_TYPE = "UPDATE trained_model set model_id = ? where asset_type_id = ? AND status_id = 1";
    private static final String UPDATE_RETRAIN = "UPDATE trained_model SET retrain = true WHERE asset_type_id = ? AND status_id = 1";

    /**
     * Given a asset type id, this function will return the string corresponding
     * to the name of the model in the database associated with the asset type
     *
     * @param assetTypeID represents a asset type id
     * @author Paul
     *
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
     * Given a asset type id, this function will return the int corresponding
     * to the ID of the model in the database associated with the asset type
     *
     * @param assetTypeID is the Asset type Id of the asset
     * @author Talal, Jeremie
     **/
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
    public List<Model> getAllModelsForEvaluation(int assetTypeID) {
        ArrayList<Model> modelList = new ArrayList<>();
        int eval= Constants.STATUS_EVALUATION;
        try (PreparedStatement ps = getConnection().prepareStatement(GET_ALL_MODELS_FOR_EVALUATION)) {
            ps.setInt(1, assetTypeID);
            ps.setInt(2,eval);
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
        int eval=Constants.STATUS_EVALUATION;
        try (PreparedStatement ps = getConnection().prepareStatement(GET_LATEST_RMSE)) {
            ps.setInt(1, modelID);
            ps.setString(2, assetTypeID);
            ps.setInt(3,eval);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    rmseValue = rs.getString("rmse");
            }
        } catch (SQLException e) {
            logger.error("Exception in getGetModelEvaluation()");
        }
        if (rmseValue == null) return "n/a";
        else return rmseValue;
    }

    /**
     * This function returns the model strategy for a specific model of an asset type
     *
     * @param modelID     is the model's ID
     * @param assetTypeID is the asset type'S ID
     * @return modelStrategy
     * @author Talal
     */
    @Override
    public ModelStrategy getModelStrategy(int modelID, int assetTypeID) {
        ModelStrategy modelStrategy = null;
        try (PreparedStatement ps = getConnection().prepareStatement(GET_MODEL_STRATEGY)) {
            ps.setInt(1, modelID);
            ps.setInt(2, assetTypeID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    try {
                        byte[] buf = rs.getBytes("serialized_model");
                        if (buf != null)
                            modelStrategy = (ModelStrategy) new ObjectInputStream(new ByteArrayInputStream(buf)).readObject();

                    } catch (IOException | ClassNotFoundException e) {
                        logger.error("IOException or ClassNotFoundException in getModelStrategy", e);
                        return null;
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("SQL Exception in getModelStrategy", e);
        }
        return modelStrategy;
    }

    /**
     * This function updates model strategy for a model
     *
     * @param modelStrategy is the model strategy to be applied for a model
     * @param modelID       is the model ID of the model on which the model strategy will apply
     * @param assetTypeID   is the asset type ID of the asset type for which the model is trained
     * @author talal
     */
    @Override
    public void updateModelStrategy(ModelStrategy modelStrategy, int modelID, int assetTypeID) {
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
            logger.error("SQL Exception in updateModelStrategy()", e);
        }
    }

    /**
     * This function is used to automatically retrieve the RMSE value from the database whenever
     * it gets updated in the database.
     *
     * @param modelID     is the model's ID
     * @param assetTypeID is the asset type's ID
     * @author talal
     */
    public double getLatestRMSE(int modelID, int assetTypeID) {
        double estimate = -100000;
        int eval=Constants.STATUS_EVALUATION;
        try (PreparedStatement ps = getConnection().prepareStatement(GET_LATEST_RMSE)) {
            ps.setInt(1, modelID);
            ps.setInt(2, assetTypeID);
            ps.setInt(3,eval);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    estimate = rs.getDouble("rmse");
            }
        } catch (SQLException e) {
            logger.error("SQL Exception in getLatestRMSE()", e);
        }
        return estimate;
    }
}