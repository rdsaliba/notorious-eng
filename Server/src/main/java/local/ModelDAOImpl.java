/*
  Implementation of the DAO design pattern
  This class extends the general DAO object and implements the ModelDAO interface

  @author      Paul Micu
  @last_edit   12/27/2020
 */
package local;

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
    private static final String UPDATE_SERIALIZE_OBJECT = "UPDATE trained_model tm, model m SET tm.retrain = false, tm.serialized_model = ? WHERE tm.model_id = ? AND tm.asset_type_id = ? AND tm.status_id = ? AND tm.model_id = m.model_id AND m.archived = 0";
    private static final String GET_SERIALIZE_OBJECT = "SELECT * FROM trained_model, model WHERE trained_model.model_id = model.model_id AND trained_model.retrain = true AND model.archived = 0";
    private static final String GET_MODEL_NAME_FROM_ID = "SELECT name from model where model.model_id = ? AND model.archived = 0";
    private static final String GET_MODEL_FROM_ASSET_TYPE = "SELECT * FROM trained_model, model WHERE trained_model.model_id = model.model_id AND trained_model.asset_type_id = ? and trained_model.status_id = ? AND model.archived = 0";
    private static final String INSERT_RMSE = "UPDATE trained_model SET rmse = ?, retrain = 0 WHERE model_id = ? AND asset_type_id = ? AND status_id=? ";

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
     * Given a model id, this function will return the string corresponding
     * to the name of the specified model in the database
     *
     * @param modelID represents a model's id
     * @author Paul
     */
    @Override
    public String getModelNameFromModelID(String modelID) {
        String name = null;
        try (PreparedStatement ps = getConnection().prepareStatement(GET_MODEL_NAME_FROM_ID)) {
            ps.setString(1, modelID);
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
                    tms.add(createTrainedModelFromResultSet(queryResult));
                }
            }
        } catch (SQLException e) {
            logger.error("Exception getModelsToTrain(): ", e);
        }
        return tms;
    }

    /**
     * Given an trained model this function will write the Model implementation object(including the classifier) object
     * to the corresponding trained model entry after an model training phase
     *
     * @param tm represents a trained model
     * @author Paul
     */
    @Override
    public void setModelToTrain(TrainedModel tm) {
        try (PreparedStatement ps = getConnection().prepareStatement(UPDATE_SERIALIZE_OBJECT)) {
            ps.setString(1, convertToByteString(tm.getModelStrategy()));
            ps.setInt(2, tm.getModelID());
            ps.setInt(3, tm.getAssetTypeID());
            ps.setInt(4, tm.getStatusID());
            ps.executeUpdate();
        } catch (SQLException | IOException e) {
            logger.error("Exception setModelsToTrain(): ", e);
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
    public TrainedModel getModelsByAssetTypeID(String assetTypeID, int statusID) {
        TrainedModel tm = null;
        try (PreparedStatement ps = getConnection().prepareStatement(GET_MODEL_FROM_ASSET_TYPE)) {
            ps.setString(1, assetTypeID);
            ps.setInt(2, statusID);
            try (ResultSet queryResult = ps.executeQuery()) {
                while (queryResult.next()) {
                    tm = createTrainedModelFromResultSet(queryResult);
                }
            }
        } catch (SQLException e) {
            logger.error("Exception getModelsByAssetTypeID(): ", e);
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
    public TrainedModel createTrainedModelFromResultSet(ResultSet rs) throws SQLException {
        TrainedModel tm = new TrainedModel();
        tm.setModelID(rs.getInt("model_id"));
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

    /**
     * Given a RMSE model evaluation value for a specific model applied to a specific asset type,
     * this function will updated the RMSE value in the database in the trained model table.
     *
     * @param rmse        is the value of the model evaluation (root mean square error)
     * @param modelId     is the model ID of the specific model
     * @param assetTypeId is the asset type ID of the specific asset type
     * @author Talal
     */
    @Override
    public void updateEvaluationRMSE(Double rmse, int modelId, int assetTypeId) {
        try (PreparedStatement ps = getConnection().prepareStatement(INSERT_RMSE)) {
            ps.setDouble(1, rmse);
            ps.setInt(2, modelId);
            ps.setInt(3, assetTypeId);
            ps.setInt(4, Constants.STATUS_EVALUATION);

            ps.executeQuery();
        } catch (SQLException e) {
            logger.error("SQL Exception in updateEvaluationRMSE", e);
        }
    }
}