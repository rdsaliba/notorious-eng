package BackgroundTasks;

import app.item.Asset;
import external.AssetDAOImpl;
import javafx.concurrent.Task;

public class AddAssetTask extends Task<Asset> {
    private Asset newAsset;

    public void setAsset(Asset newAsset){this.newAsset=newAsset;}
    @Override
    protected Asset call() throws Exception {
        AssetDAOImpl assetDAOImpl = new AssetDAOImpl();
        assetDAOImpl.insertAsset(newAsset);
        return null;
    }
}