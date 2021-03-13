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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ModelDAOImpl extends DAO implements ModelDAO {
    private static final String GET_MODEL_FROM_ASSET_TYPE_ID = "SELECT * from trained_model, model WHERE trained_model.model_id = model.model_id AND trained_model.asset_type_id = ?";
    private static final String GET_ALL_MODELS = "SELECT * from model";
    private static final String INSERT_To_EVALUATE = "REPLACE INTO model_to_evlaute SET model_name = ?,asset_type_id = ?, train_value = ?, test_value = ?";
    private static final String GET_LATEST_RMSE = "select rmse from model_evaluation where model_id=? and asset_type_id=?";
    private static final String GET_MODEL_EVALUATION = "SELECT rmse FROM model_evaluation WHERE model_id = ? AND asset_type_id = ?";
    private static final String INSERT_RMSE = "REPLACE INTO model_evaluation SET rmse = ?,model_id = ?, asset_type_id = ? ";
    private static final String UPDATE_MODEL_FOR_ASSET_TYPE = "UPDATE trained_model set model_id = ? where asset_type_id = ?";
    private static final String UPDATE_RETRAIN = "UPDATE trained_model SET retrain = true WHERE asset_type_id = ?";
    private static final String GET_MODEL_STRATEGY = "select serialized_model from trained_model where model_id=1 and asset_type_id=1 and status_id=2";
    private static final String UPDATE_MODEL_STRATEGY = "UPDATE trained_model SET retrain = true, serialized_model  = ? WHERE model_id = ? AND asset_type_id = ? and status_id = 2";

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
            e.printStackTrace();
        }
        return name;
    }

//    /**
//     * Given the id of an asset type, this function will return the trained model
//     * corresponding the that asset type. Since only one model can be associated to
//     * an asset type at a time only one TrainedModel object is returned
//     *
//     * @param assetTypeID represents a the id of an asset type
//     * @author Paul
//     */
//    @Override
//    public TrainedModel getModelsByAssetTypeID(String assetTypeID) {
//        TrainedModel tm = null;
//        try (PreparedStatement ps = getConnection().prepareStatement(GET_MODEL_ASSOCIATED_WITH_ASSET_TYPE)) {
//            ps.setString(1, assetTypeID);
//            try (ResultSet queryResult = ps.executeQuery()) {
//                while (queryResult.next()) {
//                    tm = createTrainedModelFromResultSet(queryResult, false);
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return tm;
//    }

    /**
     * Given a result set object, this function will create the corresponding trained model object
     * Given a asset type id, this function will return the int corresponding
     * to the ID of the model in the database associated with the asset type
     *
     * @param assetTypeID represents a asset type id
     * @author Jeremie
     */
    // @Override
    // public int getModelIDFromAssetTypeID(String assetTypeID) {
    //     int ID = 0;
    //     try (PreparedStatement ps = getConnection().prepareStatement(GET_MODEL_FROM_ASSET_TYPE_ID)) {
    //         ps.setString(1, assetTypeID);
    //         try (ResultSet rs = ps.executeQuery()) {
    //             if (rs.next())
    //                 ID = rs.getInt("model_id");
    //         }
    //     } catch (SQLException e) {
    //         e.printStackTrace();
    //     }
    //     return ID;
    // }

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

    @Override
    public int getModelIDFromAssetTypeID(String assetTypeID) {
        int ID = 0;
        try (PreparedStatement ps = getConnection().prepareStatement(GET_MODEL_FROM_ASSET_TYPE_ID)) {
            ps.setString(1, assetTypeID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    ID = rs.getInt("model_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ID;
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
        }
    }

    public void insertToEvaluate(String modelName, int assetTypeId, int trainvalue, int testValue) {
        try (PreparedStatement ps = getConnection().prepareStatement(INSERT_To_EVALUATE)) {

            ps.setString(1, modelName);
            ps.setInt(2, assetTypeId);
            ps.setInt(3, trainvalue);
            ps.setInt(4, testValue);
            ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

//    public double getLatestRMSE(int modelID,int assetTypeID){
//        double estimate = -100000;
//        try (PreparedStatement ps = getConnection().prepareStatement(GET_LATEST_RMSE)) {
//            ps.setInt(1, modelID);
//            ps.setInt(2, assetTypeID);
//            try (ResultSet rs = ps.executeQuery()) {
//                if (rs.next())
//                    estimate = rs.getDouble("rmse");
//            }
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
//        return estimate;
//    }
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
            e.printStackTrace();
        }
        return rmseValue;
    }

    @Override
    public ModelStrategy getModelStrategy(int modelID, int assetTypeID) throws SQLException {
        ModelStrategy modelStrategy = null;
        try (PreparedStatement ps = getConnection().prepareStatement(GET_MODEL_STRATEGY)) {
//            ps.setInt(1,modelID);
//            ps.setInt(2,assetTypeID);
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
    @Override
    public void updateModelStrategy(ModelStrategy modelStrategy, int modelID, int assetTypeID){
        try (PreparedStatement ps = getConnection().prepareStatement(UPDATE_MODEL_STRATEGY)) {
            ps.setObject(1, modelStrategy);
            ps.setInt(2, 1);
            ps.setInt(3, 1);
            ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

