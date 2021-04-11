package utilities.services;

import external.AssetTypeDAOImpl;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import utilities.UIUtilities;

import static utilities.TextConstants.ASSET_TYPE_LIST_SCENE;

public class DeleteAssetTypeService extends Service<String> {

    private final IntegerProperty assetID = new SimpleIntegerProperty();
    private Scene scene;
    public final void setAssetId(int assetID) { this.assetID.set(assetID); }
    public final int getAssetID() { return assetID.get(); }
    public final void setScene(Scene scene) { this.scene = scene; }
    public final Scene getScene() { return scene; }

    public DeleteAssetTypeService(Scene scene, int assetID) {
        setAssetId(assetID);
        setScene(scene);
    }

    @Override
    protected Task createTask() {
        return new Task<>() {
            AssetTypeDAOImpl assetDAOImpl = new AssetTypeDAOImpl();
            final int assetId = getAssetID();
            final Scene scene = getScene();
            UIUtilities uiUtilities = new UIUtilities();

            @Override
            protected Object call() throws Exception {
                assetDAOImpl.deleteAssetTypeByID(String.valueOf(assetId));
                uiUtilities.changeScene(ASSET_TYPE_LIST_SCENE, scene);
                return null;
            }


        };
    }
}