import Controllers.AddAssetController;
import app.item.Asset;
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
import org.junit.Before;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import static org.junit.Assert.*;

public class AddAssetControllerTest extends ApplicationTest {

    private Asset fullAsset;
    private Asset emptyAsset;
    private AddAssetController addAssetController;
    private Scene scene;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(AddAssetController.class.getResource("/AddAsset.fxml"));
        Parent root = fxmlLoader.load();
        scene = new Scene(root);
        addAssetController = (AddAssetController) fxmlLoader.getController();
        stage.setTitle("CBMS");
        stage.setScene(scene);
        stage.show();
    }

    @Before
    public void setup() {
        fullAsset = new Asset();
        fullAsset.setName("Engine");
        fullAsset.setAssetTypeID("123");
        fullAsset.setDescription("Test Description");
        fullAsset.setSerialNo("5634");
        fullAsset.setManufacturer("Test Manufacturer");
        fullAsset.setCategory("Test Category");
        fullAsset.setSite("Test Site");
        fullAsset.setLocation("Test Location");

        emptyAsset = new Asset();
        emptyAsset.setName("");
        emptyAsset.setAssetTypeID("");
        emptyAsset.setDescription("");
        emptyAsset.setSerialNo("");
        emptyAsset.setManufacturer("");
        emptyAsset.setCategory("");
        emptyAsset.setSite("");
        emptyAsset.setLocation("");
    }

    @After
    public void tearDown () throws Exception {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }

    @Test
    public void isAssetEmptyTrue() {
        assertTrue(addAssetController.isAssetEmpty(emptyAsset));
    }

    @Test
    public void isAssetEmptyFalse() {
        assertFalse(addAssetController.isAssetEmpty(fullAsset));
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
        Node addAssetScene = lookup("#assetsTitle").query();
        from(addAssetScene).lookup((Text t) -> t.getText().startsWith("Assets"));
    }

    @Test
    public void testAssetTypeButtonClick() {
        clickOn("#assetTypeMenuBtn");
        Node addAssetScene = lookup("#assetTypesTitle").query();
        from(addAssetScene).lookup((Text t) -> t.getText().startsWith("Asset Types"));
    }

    @Test
    public void testAssembleAssetType() {
        clickOn("#assetNameInput").write("Name");
        clickOn("#assetDescriptionTextArea").write("Description");
        clickOn("#serialNumberInput").write("34543");
        clickOn("#manufacturerInput").write("Manu");
        clickOn("#categoryInput").write("cat");
        clickOn("#siteInput").write("site");
        clickOn("#locationInput").write("location");
        Asset asset = addAssetController.assembleAsset();
        assertNotNull(asset);
    }

    @Test
    public void testErrorDialogPopup() {
        clickOn("#saveBtn");
        Node dialogPane = lookup(".dialog-pane").query();
        from(dialogPane).lookup((Text t) -> t.getText().startsWith("Please"));
    }
}