package e2e.com.cbms.app;

import Controllers.AssetTypeInfoController;
import Utilities.AssetTypeList;
import Utilities.TextConstants;
import app.item.AssetType;
import app.item.AssetTypeParameter;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class AssetTypeInfoTest extends ApplicationTest {

    private Scene scene;
    private static AssetTypeInfoController AssetTypeInfoController;

    @Override
    public void start (Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(AssetTypeInfoController.class.getResource("/AssetTypeInfo.fxml"));
        Parent root = fxmlLoader.load();
        scene = new Scene(root);
        AssetTypeInfoController = fxmlLoader.getController();

        AssetType assetType = new AssetType();
        ArrayList<AssetTypeParameter> parameters = new ArrayList<>();
        parameters.add(new AssetTypeParameter(TextConstants.OK_THRESHOLD, 10.0));
        parameters.add(new AssetTypeParameter(TextConstants.ADVISORY_THRESHOLD, 8.0));
        parameters.add(new AssetTypeParameter(TextConstants.CAUTION_THRESHOLD, 6.0));
        parameters.add(new AssetTypeParameter(TextConstants.WARNING_THRESHOLD, 3.0));
        parameters.add(new AssetTypeParameter(TextConstants.FAILED_THRESHOLD, 1.0));
        assetType.setThresholdList(parameters);
        assetType.setId("1");
        assetType.setDescription("This is a description");
        assetType.setName("Asset Type Name");

        AssetTypeList assetTypeList = new AssetTypeList(assetType, 34, 67,
                "Ok",0, "Caution",0, "Advisory",0, "Warning",0, "Failed",0);

        AssetTypeInfoController.initData(assetTypeList);

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
        Node rootNode = lookup("#AssetsTitle").query();
        from(rootNode).lookup((Text t) -> t.getText().startsWith("Assets"));
    }

    @Test
    public void testAssetTypeButtonClick() {
        clickOn("#AssetTypeMenuBtn");
        Node rootNode = lookup("#AssetTypesTitle").query();
        from(rootNode).lookup((Text t) -> t.getText().startsWith("Asset Types"));
    }
}