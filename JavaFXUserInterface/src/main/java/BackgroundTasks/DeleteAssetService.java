package BackgroundTasks;

import Utilities.CustomDialog;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import javafx.scene.input.MouseEvent;

public class DeleteAssetService extends Service<CustomDialog> {
    private MouseEvent mouseEvent;
    private int assetID;

    public void setMouseEvent(MouseEvent mouseEvent){this.mouseEvent=mouseEvent;}
    public void setAssetID(int assetID){this.assetID=assetID;}
    @Override
    protected Task<CustomDialog> createTask() {
        DeleteAssetTask deleteAssetTask=new DeleteAssetTask();
        deleteAssetTask.setAssetID(assetID);
        deleteAssetTask.setMouseEvent(mouseEvent);
        return deleteAssetTask;
    }
}
