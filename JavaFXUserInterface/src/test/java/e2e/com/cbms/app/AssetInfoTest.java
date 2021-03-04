package e2e.com.cbms.app;

import controllers.AssetInfoController;
import controllers.AssetsController;
import app.item.Asset;
import external.AssetDAOImpl;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Test;
import org.testfx.api.FxAssert;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AssetInfoTest extends ApplicationTest {

    private Scene scene;
    private static AssetInfoController AssetInfoController;

    @Override
    public void start (Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(AssetsController.class.getResource("/AssetInfo.fxml"));
        Parent root = fxmlLoader.load();
        scene = new Scene(root);
        AssetInfoController = fxmlLoader.getController();
//        Asset asset = new Asset();
//        asset.setId(123);
//        asset.setLocation("location");
//        asset.setAssetTypeID("2");
//        asset.setSite("site");
//        asset.setManufacturer("manu");
//        asset.setCategory("cat");
//        asset.setName("namename");
//        asset.setDescription("this is a description");
//        asset.setSerialNo("3ewff4");
//        asset.setRecommendation("OK");
//        AssetAttribute assetAttribute1 = new AssetAttribute();
//        assetAttribute1.addMeasurement(1, 1.0);
//        AssetAttribute assetAttribute2 = new AssetAttribute();
//        assetAttribute2.addMeasurement(2, 2.0);
//        AssetAttribute assetAttribute3 = new AssetAttribute();
//        assetAttribute3.addMeasurement(3, 3.0);
//        ArrayList<AssetAttribute> attributes = new ArrayList<>();
//        attributes.add(assetAttribute1);
//        attributes.add(assetAttribute2);
//        attributes.add(assetAttribute3);
//        AssetInfo assetInfo = new AssetInfo();
//        assetInfo.setAssetAttributes(attributes);
//        asset.setAssetInfo(assetInfo);
        AssetDAOImpl assetDAOImpl = new AssetDAOImpl();
        ArrayList<Asset> assets = assetDAOImpl.getAllLiveAssets();
        AssetInfoController.initData(assets.get(0));
        stage.setTitle("CBMS");
        stage.setScene(scene);
        stage.show();
    }

    @After
    public void tearDown () throws Exception {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }

    @Test
    public void hasExitMenuButton() {
        AnchorPane rootNode = (AnchorPane) scene.getRoot();
        Button button = from(rootNode).lookup("#exitMenuBtn").query();
        assertEquals("Exit", button.getText());
    }

    @Test
    public void testAssetsButtonClick() {
        clickOn("#AssetMenuBtn");
        Node addAssetScene = lookup("#AssetsTitle").query();
        from(addAssetScene).lookup((Text t) -> t.getText().startsWith("Assets"));
    }

    @Test
    public void testAssetTypeButtonClick() {
        clickOn("#AssetTypeMenuBtn");
        Node addAssetScene = lookup("#AssetTypesTitle").query();
        from(addAssetScene).lookup((Text t) -> t.getText().startsWith("Asset Types"));
    }

    @Test
    public void testInformationTabClick() {
        clickOn("#informationTab");
        FlowPane rootNode = (FlowPane) scene.getRoot().lookup("#sensorFlowPane");
        assertTrue(rootNode.getChildren().size() > 0);
    }

    @Test
    public void hasRawDataList() {
        clickOn("#rawDataTab");
        TableView rootNode = (TableView) scene.getRoot().lookup("#RawDataTable");
        assertTrue(rootNode.getItems().size() > 0);
    }

    @Test
    public void testDeleteButtonClick() {
        clickOn("#deleteBtn");
        FxAssert.verifyThat("OK", NodeMatchers.isVisible());
        FxAssert.verifyThat("Cancel", NodeMatchers.isVisible());
    }

}