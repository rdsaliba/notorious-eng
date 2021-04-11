package UnitTests.Controllers;

import controllers.AssetsController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.TextInputControlMatchers;

import static org.junit.Assert.*;
import static org.testfx.api.FxAssert.verifyThat;

public class AssetsControllerTest extends ApplicationTest {

    private AssetsController assetController;
    private Scene scene;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(AssetsController.class.getResource("/Assets.fxml"));
        Parent root = fxmlLoader.load();
        scene = new Scene(root);
        assetController = fxmlLoader.getController();
        stage.setTitle("Minerva");
        stage.setScene(scene);
        stage.show();
    }

    @After
    public void tearDown() throws Exception {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }

    @Test
    public void validSearchTest() {
        clickOn("#search").write("189");
        FlowPane rootNode = (FlowPane) scene.getRoot().lookup("#assetsThumbPane");
        verifyThat("#search", TextInputControlMatchers.hasText("189"));
        assertTrue(assetController.setAssetListToDisplay().size() > 0);
    }

    @Test
    public void invalidSearchTest() {
        clickOn("#search").write("This is an invalid search");
        FlowPane rootNode = (FlowPane) scene.getRoot().lookup("#assetsThumbPane");
        verifyThat("#search", TextInputControlMatchers.hasText("This is an invalid search"));
        interact(() -> assertNotNull(assetController.setAssetListToDisplay()));
    }

    @Test
    public void noSearchTest() {
        clickOn("#search").write("no search");
        clickOn("#search").eraseText(9);
        FlowPane rootNode = (FlowPane) scene.getRoot().lookup("#assetsThumbPane");
        verifyThat("#search", TextInputControlMatchers.hasText(""));
        interact(() -> assertNull(assetController.setAssetListToDisplay()));
    }
}
