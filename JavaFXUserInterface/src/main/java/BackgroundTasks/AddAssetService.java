package BackgroundTasks;

import app.item.Asset;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.input.MouseEvent;

public class AddAssetService extends Service<Asset> {
    private Asset newAsset;
    private boolean formInputValidation;
    private boolean isAssetEmpty;
    private MouseEvent mouseEvent;

    public void setAsset(Asset newAsset){this.newAsset=newAsset;}

    @Override
    protected Task<Asset> createTask() {
        AddAssetTask assetTask=new AddAssetTask();
        assetTask.setAsset(newAsset);

        return assetTask;
    }
}
