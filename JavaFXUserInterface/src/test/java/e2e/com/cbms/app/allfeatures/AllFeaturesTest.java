package e2e.com.cbms.app.allfeatures;

import controllers.AssetsController;
import javafx.fxml.FXMLLoader;
import javafx.geometry.VerticalDirection;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class AllFeaturesTest extends ApplicationTest
{
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

    /*
    F4 - The system shall determine the remaining useful life using different models.
     */
    @Test
    public void f4RULModelsTest() {

    }

    /*
    F8 - The system shall display the selected RUL model parameters.
     */
    @Test
    public void f8DisplayParametersTest() {

    }

    /*
    F9 - The system shall provide the ability to edit a selected RUL model parameters.
     */
    @Test
    public void f9EditParametersTest() {

    }

    /*
    F10 - The system shall display a selected RUL model performance (RMSE).
     */
    @Test
    public void f10ModelPerformanceTest() {

    }

    /*
    F11 - The system shall provide the ability to add and remove a asset type
     */
    @Test
    public void f11AddRemoveAssetTypeTest() {

    }

    /*
    F12 - The system shall provide the ability to associate a asset type with RUL model
     */
    @Test
    public void f12AssociateAssetTypeWithModelTest() {

    }

    /*
    F13 - The system shall display a monitored asset in RUL order (lowest RUL first)
     */
    @Test
    public void f13OrderRULTest() {
        scroll(20, VerticalDirection.UP);
        clickOn("#sortAsset");
        type(KeyCode.DOWN);
        type(KeyCode.ENTER);
        moveBy(-30, 50);
        scroll(120, VerticalDirection.UP);
    }

    /*
     F14 - The system shall provide the ability to select a asset
     */
    @Test
    public void f14SelectAssetTest() {

    }

    /*
    F15 - The system shall display a selected asset raw data
     */
    @Test
    public void f15DisplayRawDataTest() {
        clickOn("#thumbnailTab");
        moveBy(20, 50);
        scroll(50, VerticalDirection.UP);
        clickOn();
        sleep(10);
        clickOn("#rawDataTab");
        TableView rootNode = (TableView) scene.getRoot().lookup("#RawDataTable");
        assertTrue(rootNode.getItems().size() > 0);
    }

    /*
    F16 - The system shall display a selected asset information
     */
    @Test
    public void f16DisplayAssetInfoTest() {
        clickOn("#thumbnailTab");
        moveBy(20, 50);
        scroll(50, VerticalDirection.UP);
        clickOn();
        sleep(10);
        clickOn("#informationTab");
//        FlowPane rootNode = (FlowPane) scene.getRoot().lookup("#attributeFlowPane");
//        assertTrue(rootNode.getChildren().size() > 0);

        AnchorPane rootNode = (AnchorPane) scene.getRoot().lookup("#assetInfoPane");
        assertTrue(rootNode.getChildren().size() > 0);
    }

    /*
    F17 - The system shall provide the ability to add or remove a asset.
     */
    @Test
    public void f17AddRemoveAssetTest() {

    }
}
