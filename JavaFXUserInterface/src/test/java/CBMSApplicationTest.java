import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testfx.api.FxAssert;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.base.WindowMatchers;
import static org.junit.Assert.assertEquals;

public class CBMSApplicationTest extends ApplicationTest {

    private Scene scene;

    @Override
    public void start (Stage stage) throws Exception {
        Parent root = FXMLLoader.load(CBMSApplication.class.getResource("/Systems.fxml"));
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
    public void hasAddSystemButton() {
        AnchorPane rootNode = (AnchorPane) scene.getRoot();
        Button button = from(rootNode).lookup("#addSystemBtn").query();
        assertEquals("Add System", button.getText());
    }

    @Test
    public void hasSystemMenuButton() {
        AnchorPane rootNode = (AnchorPane) scene.getRoot();
        Button button = from(rootNode).lookup("#systemMenuBtn").query();
        assertEquals("Systems", button.getText());
    }

    @Test
    public void hasSystemTypeMenuButton() {
        AnchorPane rootNode = (AnchorPane) scene.getRoot();
        Button button = from(rootNode).lookup("#systemTypeMenuBtn").query();
        assertEquals("System Types", button.getText());
    }

    @Test
    public void testApplicationLaunch() {
        FxAssert.verifyThat(window("CBMS"), WindowMatchers.isShowing());
    }
}