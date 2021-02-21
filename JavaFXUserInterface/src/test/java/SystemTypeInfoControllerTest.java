import Controllers.SystemTypeInfoController;
import Utilities.SystemTypeList;
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

public class SystemTypeInfoControllerTest extends ApplicationTest {

    private Scene scene;
    private static SystemTypeInfoController systemTypeInfoController;

    @Override
    public void start (Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(SystemTypeInfoController.class.getResource("/SystemTypeInfo.fxml"));
        Parent root = fxmlLoader.load();
        scene = new Scene(root);
        systemTypeInfoController = fxmlLoader.getController();

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

        SystemTypeList systemTypeList = new SystemTypeList(assetType, 34, 67,
                "Ok", "Caution", "Advisory", "Warning", "Failed");

        systemTypeInfoController.initData(systemTypeList);

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
}