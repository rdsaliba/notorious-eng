package BackgroundTasks;

import utilities.CustomDialog;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import javafx.scene.input.MouseEvent;

public class DeleteAssetService extends Service<CustomDialog> {
    private int assetID;

    public void setAssetID(int assetID){this.assetID=assetID;}
    @Override
    protected Task<CustomDialog> createTask() {
        DeleteAssetTask deleteAssetTask=new DeleteAssetTask();
        deleteAssetTask.setAssetID(assetID);
        return deleteAssetTask;
    }
}