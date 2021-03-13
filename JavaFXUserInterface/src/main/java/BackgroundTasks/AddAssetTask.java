package BackgroundTasks;

import app.item.Asset;
import external.AssetDAO;
import external.AssetDAOImpl;
import javafx.concurrent.Task;
import javafx.scene.input.MouseEvent;

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
