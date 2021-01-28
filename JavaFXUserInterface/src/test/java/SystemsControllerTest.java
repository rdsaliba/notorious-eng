import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.testfx.framework.junit.ApplicationTest;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class SystemsControllerTest extends ApplicationTest {

    private Scene scene;

    @Override
    public void start (Stage stage) throws Exception {
        Parent root = FXMLLoader.load(SystemsController.class.getResource("/Systems.fxml"));
        scene = new Scene(root);
        stage.setTitle("CBMS");
        stage.setScene(scene);
        stage.show();
    }

    @Test
    public void hasThumbnails() {
        FlowPane rootNode = (FlowPane) scene.getRoot().lookup("#systemsThumbPane");
        assertTrue(rootNode.getChildren().size() > 0);
    }

    @Test
    public void hasList() {
        clickOn("#listTab");
        AnchorPane rootNode = (AnchorPane) scene.getRoot().lookup("#listTable");
        assertTrue(rootNode.getChildren().size() > 0);
    }
}