package e2e.com.cbms.app;

import Controllers.SystemsController;
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
import org.testfx.api.FxAssert;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.base.WindowMatchers;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class SystemsTest extends ApplicationTest {

    private Scene scene;

    @Override
    public void start (Stage stage) throws Exception {
        Parent root = FXMLLoader.load(SystemsController.class.getResource("/Systems.fxml"));
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
    public void testApplicationLaunch() {
        FxAssert.verifyThat(window("CBMS"), WindowMatchers.isShowing());
    }


    @Test
    public void hasThumbnails() {
        FlowPane rootNode = (FlowPane) scene.getRoot().lookup("#systemsThumbPane");
        assertTrue(rootNode.getChildren().size() > 0);
    }

    @Test
    public void hasList() {
        clickOn("#listTab");
        TableView rootNode = (TableView) scene.getRoot().lookup("#listTable");
        assertTrue(rootNode.getItems().size() > 0);
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
        assertEquals("System\nTypes", button.getText());
    }

    @Test
    public void hasExitMenuButton() {
        AnchorPane rootNode = (AnchorPane) scene.getRoot();
        Button button = from(rootNode).lookup("#exitMenuBtn").query();
        assertEquals("Exit", button.getText());
    }

    @Test
    public void testAddSystemButtonClick() {
        clickOn("#addSystemBtn");
        Node addSystemScene = lookup("#addSystemTitle").query();
        from(addSystemScene).lookup((Text t) -> t.getText().startsWith("Add System"));
    }

    @Test
    public void testSystemsButtonClick() {
        clickOn("#systemMenuBtn");
        Node addSystemScene = lookup("#systemsTitle").query();
        from(addSystemScene).lookup((Text t) -> t.getText().startsWith("Systems"));
    }

    @Test
    public void testSystemTypeButtonClick() {
        clickOn("#systemTypeMenuBtn");
        Node addSystemScene = lookup("#systemTypesTitle").query();
        from(addSystemScene).lookup((Text t) -> t.getText().startsWith("System Types"));
    }

    @Test
    public void testSortAscendingClick() {
        clickOn("#sortSystem");
        type(KeyCode.DOWN);
        type(KeyCode.ENTER);
    }

    @Test
    public void testSortDescendingClick() {
        clickOn("#sortSystem");
        type(KeyCode.DOWN);
        type(KeyCode.DOWN);
        type(KeyCode.ENTER);
    }
}