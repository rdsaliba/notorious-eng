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
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class AssetsControllerTest extends ApplicationTest {

    private Scene scene;

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(AssetsController.class.getResource("/Assets.fxml"));
        scene = new Scene(root);
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
    public void hasThumbnails() {
        FlowPane rootNode = (FlowPane) scene.getRoot().lookup("#assetsThumbPane");
        assertTrue(rootNode.getChildren().size() > 0);
    }

    @Test
    public void hasList() {
        clickOn("#listTab");
        TableView rootNode = (TableView) scene.getRoot().lookup("#listTable");
        assertTrue(rootNode.getItems().size() > 0);
    }

    @Test
    public void hasAddAssetButton() {
        AnchorPane rootNode = (AnchorPane) scene.getRoot();
        Button button = from(rootNode).lookup("#addAssetBtn").query();
        assertEquals("Add Asset", button.getText());
    }

    @Test
    public void hasAssetMenuButton() {
        AnchorPane rootNode = (AnchorPane) scene.getRoot();
        Button button = from(rootNode).lookup("#assetMenuBtn").query();
        assertEquals("Assets", button.getText());
    }

    @Test
    public void hasAssetTypeMenuButton() {
        AnchorPane rootNode = (AnchorPane) scene.getRoot();
        Button button = from(rootNode).lookup("#assetTypeMenuBtn").query();
        assertEquals("Asset\nTypes", button.getText());
    }

    @Test
    public void hasExitMenuButton() {
        AnchorPane rootNode = (AnchorPane) scene.getRoot();
        Button button = from(rootNode).lookup("#exitMenuBtn").query();
        assertEquals("Exit", button.getText());
    }

    @Test
    public void testAddAssetButtonClick() {
        clickOn("#addAssetBtn");
        Node addAssetScene = lookup("#addAssetTitle").query();
        from(addAssetScene).lookup((Text t) -> t.getText().startsWith("Add Asset"));
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
    public void testSortAscendingClick() {
        clickOn("#sortAsset");
        type(KeyCode.DOWN);
        type(KeyCode.ENTER);
    }

//    @Test
//    public void testSortDescendingClick() {
//        clickOn("#sortAsset");
//        type(KeyCode.DOWN);
//        type(KeyCode.DOWN);
//        type(KeyCode.ENTER);
//    }
}