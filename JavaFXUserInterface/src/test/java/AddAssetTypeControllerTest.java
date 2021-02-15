import Controllers.AddAssetTypeController;
import Controllers.AssetTypeInfoController;
import app.item.AssetType;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AddAssetTypeControllerTest extends ApplicationTest {
    private Scene scene;
    private AddAssetTypeController addAssetTypeController;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(AssetTypeInfoController.class.getResource("/AddAssetType.fxml"));
        Parent root = fxmlLoader.load();
        scene = new Scene(root);
        addAssetTypeController = fxmlLoader.getController();
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
        clickOn("#assetMenuBtn");
        Node rootNode = lookup("#assetsTitle").query();
        from(rootNode).lookup((Text t) -> t.getText().startsWith("Assets"));
    }

    @Test
    public void testAssetTypeButtonClick() {
        clickOn("#assetTypeMenuBtn");
        Node rootNode = lookup("#assetTypesTitle").query();
        from(rootNode).lookup((Text t) -> t.getText().startsWith("Asset Types"));
    }

    @Test
    public void testCancelButtonClick() {
        clickOn("#cancelBtn");
        Node rootNode = lookup("#assetTypesTitle").query();
        from(rootNode).lookup((Text t) -> t.getText().startsWith("Asset Types"));
    }

    @Test
    public void testAssembleAssetType() {
        clickOn("#assetTypeName").write("Name");
        clickOn("#assetTypeDescription").write("Description");
        clickOn("#thresholdOKValue").write("23.0");
        clickOn("#thresholdAdvisoryValue").write("20.0");
        clickOn("#thresholdCautionValue").write("15.0");
        clickOn("#thresholdWarningValue").write("10.0");
        clickOn("#thresholdFailedValue").write("5.0");
        AssetType assetType = addAssetTypeController.assembleAssetType();
        assertNotNull(assetType);
    }
}