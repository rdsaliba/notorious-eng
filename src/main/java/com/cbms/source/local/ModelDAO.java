/*
  Interface for the ModelDAO object

  @author Paul Micu
  @version 1.0
  @last_edit 12/27/2020
 */
package com.cbms.source.local;

import com.cbms.app.TrainedModel;

import java.util.ArrayList;

public interface ModelDAO {
    ArrayList<TrainedModel> getModelsToTrain();

    void setModelsToTrain(ArrayList<TrainedModel> tms);

    TrainedModel getModelsByAssetTypeID(String assetTypeID);

    String getModelNameFromModelID(int modelID);
}
