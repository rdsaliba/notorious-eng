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

import java.io.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;

public class ModelDAOImpl extends DAO implements ModelDAO {
    private static final String GET_LATEST_RMSE = "SELECT rmse FROM trained_model tm, model m WHERE tm.model_id=? AND tm.asset_type_id=? AND tm.status_id=? AND tm.model_id = m.model_id AND m.archived=0";
    private static final String GET_MODEL_STRATEGY = "SELECT serialized_model FROM trained_model tm, model m WHERE tm.model_id=? AND tm.asset_type_id=? AND tm.status_id=? AND tm.model_id=m.model_id AND m.archived = 0";
    private static final String UPDATE_MODEL_STRATEGY = "UPDATE trained_model tm, model m SET tm.serialized_model=?, tm.retrain=true WHERE tm.model_id = ? AND tm.asset_type_id = ? AND tm.status_id = ? AND tm.model_id=m.model_id AND m.archived = 0";
    private static final String UPDATE_MODEL_FOR_ASSET_TYPE = "UPDATE trained_model tm, model m SET tm.model_id = ?, tm.serialized_model =?  WHERE tm.asset_type_id = ? AND tm.status_id = ? AND tm.model_id=m.model_id AND m.archived = 0";
    private static final String UPDATE_RETRAIN = "UPDATE trained_model tm, model m SET retrain = true WHERE tm.asset_type_id = ? AND tm.status_id = ? AND tm.model_id=m.model_id AND m.archived = 0";
    private static final String GET_MODEL_FROM_ASSET_TYPE = "SELECT * FROM trained_model, model WHERE trained_model.model_id = model.model_id AND trained_model.asset_type_id = ? AND trained_model.status_id = ? AND model.archived = 0";

    public static String convertToByteString(Object object) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); ObjectOutput out = new ObjectOutputStream(bos)) {
            out.writeObject(object);
            final byte[] byteArray = bos.toByteArray();
            return Base64.getEncoder().encodeToString(byteArray);
        }
    }

    public static Object convertFromByteString(String byteString) throws IOException, ClassNotFoundException {
        final byte[] bytes = Base64.getDecoder().decode(byteString);
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes); ObjectInput in = new ObjectInputStream(bis)) {
            return in.readObject();
        }
    }

    /**
     * Given an asset type id, this function will return the string corresponding
     * to the name of the model in the database associated with the asset type
     *
     * @param assetTypeID represents an asset type id
     * @author Paul
     */
    @Override
    public String getModelNameAssociatedWithAssetType(String assetTypeID) {
        String name = null;
        try (PreparedStatement ps = getConnection().prepareStatement(GET_MODEL_FROM_ASSET_TYPE)) {
            ps.setString(1, assetTypeID);
            ps.setInt(2, Constants.STATUS_LIVE);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    name = rs.getString("name");
            }
        } catch (SQLException e) {
            logger.error("SQL Exception in getModelNameAssociatedWithAssetType(): ", e);
        }
        return name;
    }

    /**
     * Given an asset type id, this function will return the int corresponding
     * to the ID of the model in the database associated with the asset type
     *
     * @param assetTypeID is the Asset type Id of the asset
     * @author Talal, Jeremie
     **/
    @Override
    public int getModelIDAssociatedWithAssetType(String assetTypeID) {
        int modelID = 0;
        try (PreparedStatement ps = getConnection().prepareStatement(GET_MODEL_FROM_ASSET_TYPE)) {
            ps.setString(1, assetTypeID);
            ps.setInt(2, Constants.STATUS_LIVE);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    modelID = rs.getInt("model_id");
            }
        } catch (SQLException e) {
            logger.error("SQL Exception in getModelIDAssociatedWithAssetType(): ", e);
        }
        return modelID;
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
    public ArrayList<TrainedModel> getModelsByAssetTypeID(String assetTypeID, int statusID) {
        ArrayList<TrainedModel> modelList = new ArrayList<>();
        try (PreparedStatement ps = getConnection().prepareStatement(GET_MODEL_FROM_ASSET_TYPE)) {
            ps.setString(1, assetTypeID);
            ps.setInt(2, statusID);
            try (ResultSet queryResult = ps.executeQuery()) {
                while (queryResult.next()) {
                    modelList.add(createTrainedModelFromResultSet(queryResult));
                }
            }
        } catch (SQLException e) {
            logger.error("Exception getModelsByAssetTypeID(): ", e);
        }
        return modelList;
    }

    /**
     * This function updates which model is associated with the specified asset type
     * to match the selection chosen by the user.
     *
     * @param model       is the corresponding model object
     * @param assetTypeID is the asset type ID of the specified asset type
     * @author Jeremie
     */
    @Override
    public void updateModelAssociatedWithAssetType(Model model, String assetTypeID) {
        try (PreparedStatement ps = getConnection().prepareStatement(UPDATE_MODEL_FOR_ASSET_TYPE)) {
            ps.setInt(1, model.getModelID());
            ps.setString(2, convertToByteString(((TrainedModel) model).getModelStrategy()));
            ps.setString(3, assetTypeID);
            ps.setInt(4, Constants.STATUS_LIVE);
            ps.executeQuery();
        } catch (SQLException | IOException e) {
            logger.error("SQL Exception in updateModelAssociatedWithAssetType(): ", e);
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
            ps.setInt(2, Constants.STATUS_LIVE);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("SQL Exception in setModelToTrain()");
        }
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
            ps.setInt(3, Constants.STATUS_EVALUATION);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    if (rs.getString("serialized_model") != null)
                        modelStrategy = (ModelStrategy) convertFromByteString(rs.getString("serialized_model"));
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            logger.error("IOException or ClassNotFoundException in getModelStrategy", e);
            return null;
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
            ps.setString(1, convertToByteString(tm.getModelStrategy()));
            ps.setInt(2, tm.getModelID());
            ps.setInt(3, tm.getAssetTypeID());
            ps.setInt(4, Constants.STATUS_EVALUATION);
            ps.executeQuery();
        } catch (SQLException | IOException e) {
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
    @Override
    public double getLatestRMSE(int modelID, int assetTypeID) {
        double estimate = -1000000;
        try (PreparedStatement ps = getConnection().prepareStatement(GET_LATEST_RMSE)) {
            ps.setInt(1, modelID);
            ps.setInt(2, assetTypeID);
            ps.setInt(3, Constants.STATUS_EVALUATION);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    estimate = rs.getDouble("rmse");
            }
        } catch (SQLException e) {
            logger.error("SQL Exception in getLatestRMSE()", e);
        }
        return estimate;
    }

    /**
     * Given a result set object, this function will create the corresponding trained model object
     *
     * @param rs represents the result from a trained model query
     * @author Paul
     */
    public TrainedModel createTrainedModelFromResultSet(ResultSet rs) throws SQLException {
        TrainedModel tm = new TrainedModel();
        tm.setModelID(rs.getInt("model_id"));
        tm.setModelName(rs.getString("name"));
        tm.setDescription(rs.getString("description"));
        tm.setRMSE(rs.getString("rmse"));
        tm.setAssetTypeID(rs.getInt("asset_type_id"));
        tm.setRetrain(rs.getBoolean("retrain"));
        tm.setStatusID(rs.getInt("status_id"));
        try {
            if (rs.getString("serialized_model") != null)
                tm.setModelStrategy((ModelStrategy) convertFromByteString(rs.getString("serialized_model")));

        } catch (IOException | ClassNotFoundException e) {
            logger.error("Exception createTrainedModelFromResultSet(): ", e);
            return null;
        }
        return tm;
    }

}