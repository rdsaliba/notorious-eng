/*
  Interface for the ModelDAO object

  @author Paul Micu
  @version 1.0
  @last_edit 12/27/2020
 */
package external;

import app.item.Model;

import java.util.List;

public interface ModelDAO {

    String getModelNameFromAssetTypeID(String assetTypeId);

    int getModelIDFromAssetTypeID(String assetTypeID);

    void updateRMSE(Double rmse, int modelId, int assetTypeId);

    List<Model> getAllModels();

    void updateModelAssociatedWithAssetType(String modelID, String assetTypeID);

    void setModelToTrain(String assetTypeID);

    String getGetModelEvaluation(String modelID, String assetTypeID);
}