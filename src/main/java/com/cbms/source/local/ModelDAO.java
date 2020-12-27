package com.cbms.source.local;

import com.cbms.app.TrainedModel;

import java.util.ArrayList;

public interface ModelDAO {
    ArrayList<TrainedModel> getModelsToTrain();

    void setModelsToTrain(ArrayList<TrainedModel> tms);

    TrainedModel getModelsByAssetTypeID(String assetTypeID);

    String getModelNameFromModelID(int modelID);
}
