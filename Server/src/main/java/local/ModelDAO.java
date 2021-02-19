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

    void setModelsToTrain(ArrayList<TrainedModel> tms);

    TrainedModel getModelsByAssetTypeID(String assetTypeID);

    String getModelNameFromAssetTypeID(String modelID);

    TrainedModel createTrainedModelFromResultSet(ResultSet rs, boolean withModel) throws SQLException;
}
