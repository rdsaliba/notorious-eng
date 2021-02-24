/*
  Implementation of the DAO design pattern
  This class extends the general DAO object and implements the ModelDAO interface

  @author      Paul Micu
  @version     1.0
  @last_edit   12/27/2020
 */
package external;

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
    private static final String GET_MODEL_NAME_FROM_ID = "SELECT name from trained_model, model where trained_model.model_id = model.model_id and asset_type_id = ?";
    private static final String GET_MODEL_FROM_ASSET_TYPE = "SELECT * FROM trained_model WHERE asset_type_id = ?";
    private static final String GET_MODELS_LIST = "select * from model";
    private static final String INSERT_RMSE = "REPLACE INTO model_evaluation SET rmse = ?,model_id = ?, asset_type_id = ? ";
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
        try (PreparedStatement ps = getConnection().prepareStatement(GET_MODEL_NAME_FROM_ID)) {
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
                    tm = createTrainedModelFromResultSet(queryResult, false);
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


    @Override
    public ArrayList<String> getListOfModels(){
        ArrayList<String> models=new ArrayList<String>();
        String name="";
        try (PreparedStatement ps = getConnection().prepareStatement(GET_MODELS_LIST)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()){
                    name = rs.getString("name");
                    models.add(name);
                    models.size();}
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return models;

    }
    public void updateRMSE(Double rmse, int modelId, int assetTypeId) {
        try (PreparedStatement ps = getConnection().prepareStatement(INSERT_RMSE)) {
            ps.setDouble(1, rmse);
            ps.setInt(2, modelId);
            ps.setInt(3, assetTypeId);

            ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

