/*
  Implementation of the DAO design pattern
  This class extends the general DAO object and implements the ModelDAO interface

  @author      Paul Micu
  @version     1.0
  @last_edit   12/27/2020
 */
package external;

import app.item.Model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ModelDAOImpl extends DAO implements ModelDAO {
    private static final String GET_MODEL_FROM_ASSET_TYPE_ID = "SELECT * from trained_model, model WHERE trained_model.model_id = model.model_id AND trained_model.asset_type_id = ?";
    private static final String GET_ALL_MODELS = "SELECT * from model";
    private static final String GET_MODEL_EVALUATION = "SELECT rmse FROM model_evaluation WHERE model_id = ? AND asset_type_id = ?";
    private static final String INSERT_RMSE = "REPLACE INTO model_evaluation SET rmse = ?,model_id = ?, asset_type_id = ? ";
    private static final String UPDATE_MODEL_FOR_ASSET_TYPE = "UPDATE trained_model set model_id = ? where asset_type_id = ?";
    private static final String UPDATE_RETRAIN = "UPDATE trained_model SET retrain = true WHERE asset_type_id = ?";

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
     * Given a asset type id, this function will return the int corresponding
     * to the ID of the model in the database associated with the asset type
     *
     * @param assetTypeID represents a asset type id
     * @author Jeremie
     */
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
     * Given a RMSE model evaluation value for a specific model applied to a specific asset type,
     * this function will updated the RMSE value in the database in the model evaluation table
     *
     * @param rmse        is the value of the model evaluation (root mean square error)
     * @param modelId     is the model ID of the specific model
     * @param assetTypeId is the asset type ID of the specific asset type
     * @author Talal
     */
    @Override
    public void updateRMSE(Double rmse, int modelId, int assetTypeId) {
        try (PreparedStatement ps = getConnection().prepareStatement(INSERT_RMSE)) {
            ps.setDouble(1, rmse);
            ps.setInt(2, modelId);
            ps.setInt(3, assetTypeId);

            ps.executeQuery();
        } catch (SQLException e) {
            logger.error("Exception in updateRMSE(), e");
        }
    }

    /**
     * This function will return a list of all the models that exist in the database and create
     * model objects for each of the model existing in the database
     *
     * @author Jeremie
     */
    @Override
    public List<Model> getAllModels() {
        ArrayList<Model> modelList = new ArrayList<>();
        try (PreparedStatement ps = getConnection().prepareStatement(GET_ALL_MODELS)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Model newModel = new Model();
                    newModel.setModelID(rs.getInt("model_id"));
                    newModel.setModelName(rs.getString("name"));
                    newModel.setDescription(rs.getString("description"));
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
        try (PreparedStatement ps = getConnection().prepareStatement(GET_MODEL_EVALUATION)) {
            ps.setInt(1, modelID);
            ps.setString(2, assetTypeID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    rmseValue = rs.getString("rmse");
            }
        } catch (SQLException e) {
            logger.error("Exception in getGetModelEvaluation()");
        }
        return rmseValue;
    }
}
