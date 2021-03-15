package BackgroundTasks;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.input.MouseEvent;
import rul.models.ModelStrategy;

public class GetModelStrategyService extends Service<ModelStrategy> {
    private int modelID;
    private int assetTypeID;
    private int trainAssets;
    private int testAssets;
    private MouseEvent mouseEvent;

    public void setMouseEvent(MouseEvent mosueEvent){this.mouseEvent=mouseEvent;}
    public void setModelID(int modelID){this.modelID=modelID;}
    public void setAssetTypeID(int assetTypeID){this.assetTypeID=assetTypeID;}
    public void setTrainAssets(int trainAssets){this.trainAssets=trainAssets;}
    public void setTestAssets(int testAssets){this.testAssets=testAssets;}
    @Override
    protected Task<ModelStrategy> createTask() {
        GetModelStrategyTask task = new GetModelStrategyTask();
        task.setAssetTypeID(assetTypeID);
        task.setModelID(modelID);
        task.setTrainAssets(trainAssets);
        task.setTestAssets(testAssets);
        return task;
    }
}