package BackgroundTasks;

import app.item.AssetType;
import external.AssetTypeDAOImpl;
import javafx.concurrent.Task;

public class AddAssetTypeTask extends Task<AssetType> {
    private AssetType newAssetType;

    public void setAssetType(AssetType newAssetType){this.newAssetType=newAssetType;}

    @Override
    protected AssetType call() throws Exception {
        AssetTypeDAOImpl db=new AssetTypeDAOImpl();
        db.insertAssetType(newAssetType);
        return null;
    }
}