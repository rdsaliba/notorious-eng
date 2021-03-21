/*
  Interface for the ModelDAO object

  @author Paul Micu
  @version 1.0
  @last_edit 12/27/2020
 */
package external;

import app.item.Model;
import rul.models.ModelStrategy;

import java.sql.SQLException;
import java.util.List;

public interface ModelDAO {

    String getModelNameAssociatedWithAssetType(String assetTypeId);

    int getModelIDAssociatedWithAssetType(String assetTypeID);

    List<Model> getAllModelsForEvaluation(int assetTypeID);

    void updateModelAssociatedWithAssetType(int modelID, String assetTypeID);

    void setModelToTrain(String assetTypeID);

    ModelStrategy getModelStrategy(int modelID, int assetTypeID) throws SQLException;

    void updateModelStrategy(ModelStrategy modelStrategy, int modelID, int assetTypeID);

    double getLatestRMSE(int modelID, int assetTypeID);
}