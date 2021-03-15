package BackgroundTasks;

import app.item.Asset;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class AddAssetService extends Service<Asset> {
    private Asset newAsset;

    public void setAsset(Asset newAsset){this.newAsset=newAsset;}

    @Override
    protected Task<Asset> createTask() {
        AddAssetTask assetTask=new AddAssetTask();
        assetTask.setAsset(newAsset);

        return assetTask;
    }
}