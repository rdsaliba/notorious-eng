package BackgroundTasks;

import external.ModelDAOImpl;
import javafx.concurrent.Task;
import rul.models.ModelStrategy;

public class GetModelStrategyTask extends Task<ModelStrategy> {
    private ModelDAOImpl modelDAO;
    private int modelID;
    private int assetTypeID;
    private int trainAssets;
    private int testAssets;
    public void setModelID(int modelID){this.modelID=modelID;}
    public void setAssetTypeID(int assetTypeID){this.assetTypeID=assetTypeID;}
    public void setTrainAssets(int trainAssets){this.trainAssets=trainAssets;}
    public void setTestAssets(int testAssets){this.testAssets=testAssets;}
    @Override
    protected ModelStrategy call() throws Exception {
        ModelStrategy modelStrategy = modelDAO.getModelStrategy(modelID,assetTypeID);
        modelStrategy.setTrainAssets(trainAssets);
        modelStrategy.setTestAssets(testAssets);
        modelDAO.updateModelStrategy(modelStrategy,modelID,assetTypeID);
        return modelStrategy;
    }
}
