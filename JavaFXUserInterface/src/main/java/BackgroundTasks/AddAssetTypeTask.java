package BackgroundTasks;

import app.item.Asset;
import app.item.AssetType;
import external.AssetTypeDAOImpl;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.input.MouseEvent;

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
