package BackgroundTasks;

import app.item.Asset;
import app.item.AssetType;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.input.MouseEvent;

public class AddAssetTypeService extends Service<AssetType> {
    private AssetType newAssetType;


    public void setAssetType(AssetType newAssetType){this.newAssetType=newAssetType;}

    @Override
    protected Task<AssetType> createTask() {
        AddAssetTypeTask assetTypeTask=new AddAssetTypeTask();
        assetTypeTask.setAssetType(newAssetType);
        return assetTypeTask;
    }
}
