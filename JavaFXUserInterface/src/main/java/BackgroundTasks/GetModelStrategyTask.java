package BackgroundTasks;

import external.ModelDAOImpl;
import javafx.concurrent.Task;
import javafx.scene.input.MouseEvent;
import rul.models.ModelStrategy;
import utilities.CustomDialog;

import java.util.Objects;

public class GetModelStrategyTask extends Task<ModelStrategy> {
    private int modelID;
    private int assetTypeID;
    private int trainAssets;
    private int testAssets;
    private MouseEvent mouseEvent;
    ModelStrategy modelStrategy;

    public void setMouseEvent(MouseEvent mosueEvent){this.mouseEvent=mosueEvent;}
    public void setModelID(int modelID){this.modelID=modelID;}
    public void setAssetTypeID(int assetTypeID){this.assetTypeID=assetTypeID;}
    public void setTrainAssets(int trainAssets){this.trainAssets=trainAssets;}
    public void setTestAssets(int testAssets){this.testAssets=testAssets;}
    @Override
    protected ModelStrategy call() throws Exception {
        ModelDAOImpl modelDAO= new ModelDAOImpl();
        modelStrategy = modelDAO.getModelStrategy(modelID,assetTypeID);
        if(!Objects.isNull(modelStrategy)){
        modelStrategy.setTrainAssets(trainAssets);
        modelStrategy.setTestAssets(testAssets);
        modelDAO.updateModelStrategy(modelStrategy,modelID,assetTypeID);}
        else{
            CustomDialog.nullModelAlert(mouseEvent);
        }
        return modelStrategy;
    }
}