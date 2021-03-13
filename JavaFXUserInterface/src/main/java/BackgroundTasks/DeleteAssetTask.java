package BackgroundTasks;

import Utilities.CustomDialog;
import javafx.concurrent.Task;
import javafx.scene.input.MouseEvent;

public class DeleteAssetTask extends Task<CustomDialog> {
    private MouseEvent mouseEvent;
    private int assetID;

    public void setMouseEvent(MouseEvent mouseEvent){this.mouseEvent=mouseEvent;}
    public void setAssetID(int assetID){this.assetID=assetID;}
    @Override
    protected CustomDialog call() throws Exception {
        CustomDialog.systemInfoController(mouseEvent, assetID);
        return null;
    }
}
