package BackgroundTasks;

import external.AssetDAOImpl;
import utilities.CustomDialog;
import javafx.concurrent.Task;

public class DeleteAssetTask extends Task<CustomDialog> {
    private int assetID;

    public void setAssetID(int assetID){this.assetID=assetID;}
    @Override
    protected CustomDialog call() throws Exception {
        AssetDAOImpl assetDAOImpl=new AssetDAOImpl();
        assetDAOImpl.deleteAssetByID(assetID);
        return null;
    }
}