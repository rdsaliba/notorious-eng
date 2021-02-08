import Controllers.AddSystemTypeController;
import Controllers.SystemTypeInfoController;
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

public class AddSystemTypeControllerTest extends ApplicationTest {
    private Scene scene;
    private AddSystemTypeController addSystemTypeController;

    @Override
    public void start (Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(SystemTypeInfoController.class.getResource("/AddSystemType.fxml"));
        Parent root = fxmlLoader.load();
        scene = new Scene(root);
        addSystemTypeController = fxmlLoader.getController();
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
    public void testSystemsButtonClick() {
        clickOn("#systemMenuBtn");
        Node rootNode = lookup("#systemsTitle").query();
        from(rootNode).lookup((Text t) -> t.getText().startsWith("Systems"));
    }

    @Test
    public void testSystemTypeButtonClick() {
        clickOn("#systemTypeMenuBtn");
        Node rootNode = lookup("#systemTypesTitle").query();
        from(rootNode).lookup((Text t) -> t.getText().startsWith("System Types"));
    }

    @Test
    public void testCancelButtonClick() {
        clickOn("#cancelBtn");
        Node rootNode = lookup("#systemTypesTitle").query();
        from(rootNode).lookup((Text t) -> t.getText().startsWith("System Types"));
    }

    @Test
    public void testAssembleSystemType() {
        clickOn("#systemTypeName").write("Name");
        clickOn("#systemTypeDescription").write("Description");
        clickOn("#thresholdOKValue").write("23.0");
        clickOn("#thresholdAdvisoryValue").write("20.0");
        clickOn("#thresholdCautionValue").write("15.0");
        clickOn("#thresholdWarningValue").write("10.0");
        clickOn("#thresholdFailedValue").write("5.0");
        AssetType assetType = addSystemTypeController.assembleSystemType();
        assertNotNull(assetType);
    }
}