/*
  Interface for the ModelDAO object

  @author Paul Micu
  @last_edit 12/27/2020
 */
package local;

import app.item.TrainedModel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public interface ModelDAO {
    ArrayList<TrainedModel> getModelsToTrain();

    void setModelToTrain(TrainedModel tm);

    String getModelNameFromModelID(String modelID);

    void updateRMSE(Double rmse, int modelId, int assetTypeId);
    // TrainedModel createTrainedModelFromResultSet(ResultSet rs, boolean withModel) throws SQLException;
    TrainedModel getModelsByAssetTypeID(String assetTypeID, int statusID);

    TrainedModel createTrainedModelFromResultSet(ResultSet rs) throws SQLException;
}
