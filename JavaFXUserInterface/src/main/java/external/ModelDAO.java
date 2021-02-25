/*
  Interface for the ModelDAO object

  @author Paul Micu
  @version 1.0
  @last_edit 12/27/2020
 */
package external;

import app.item.Model;
import app.item.TrainedModel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface ModelDAO {

    TrainedModel getModelsByAssetTypeID(String assetTypeID);

    String getModelNameFromAssetTypeID(String assetTypeId);

    TrainedModel createTrainedModelFromResultSet(ResultSet rs, boolean withModel) throws SQLException;

    List<Model> getAllModels();

    void updateModelAssociatedWithAssetType(String modelID, String assetTypeID);

    void setModelToTrain(String assetTypeID);
}
