import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Test;
import org.testfx.api.FxAssert;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.base.WindowMatchers;

public class CBMSApplicationTest extends ApplicationTest {

    private Scene scene;

    @Override
    public void start (Stage stage) throws Exception {
        Parent root = FXMLLoader.load(CBMSApplication.class.getResource("/Assets.fxml"));
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
}