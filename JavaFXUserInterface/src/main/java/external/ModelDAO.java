/*
  Interface for the ModelDAO object

  @author Paul Micu
  @version 1.0
  @last_edit 12/27/2020
 */
package external;

import app.item.Model;
import app.item.TrainedModel;
import rul.models.ModelStrategy;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface ModelDAO {

    String getModelNameFromAssetTypeID(String assetTypeId);

    TrainedModel createTrainedModelFromResultSet(ResultSet rs, boolean withModel) throws SQLException;

     List<Model> getAllModels(int assetTypeID);

    void updateModelAssociatedWithAssetType(int modelID, String assetTypeID);

    void setModelToTrain(String assetTypeID);

    String getGetModelEvaluation(int modelID, String assetTypeID);

    int getModelIDFromAssetTypeID(String assetTypeID);
    public ModelStrategy getModelStrategy(int modelID, int assetTypeID) throws SQLException;
    public void updateModelStrategy(ModelStrategy modelStrategy, int modelID, int assetTypeID);
}