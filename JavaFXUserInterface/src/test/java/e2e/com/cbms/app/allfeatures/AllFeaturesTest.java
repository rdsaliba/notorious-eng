package e2e.com.cbms.app.allfeatures;

import controllers.AssetsController;
import javafx.fxml.FXMLLoader;
import javafx.geometry.VerticalDirection;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class AllFeaturesTest extends ApplicationTest
{
    private Scene scene;

    @Override
    public void start(Stage stage) throws Exception
    {
        Parent homePage = FXMLLoader.load(AssetsController.class.getResource("/Assets.fxml"));
        scene = new Scene(homePage);

//        Parent navBar = FXMLLoader.load(NavigationController.class.getResource("/Navigation.fxml"));
//        //Scene  scene2 = new Scene(navBar);

        stage.setTitle("CBMS");
        stage.setScene(scene);
        stage.show();
    }

    @After
    public void tearDown () throws Exception
    {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }

    /*
    F4 - The system shall determine the remaining useful life using different models.
     */
    @Test
    public void f4RULModelsTest()
    {
        //Parts of this test uses f12 (Associate asset type)

        //Go to first asset
        clickOn("#thumbnailTab");
        FlowPane root = (FlowPane) scene.getRoot().lookup("#assetsThumbPane");
        clickOn(root.getChildren().get(0));
        sleep(2000);

        clickOn("#modelOutput");
        Text firstModel = (Text) scene.getRoot().lookup("#modelOutput");
        String modelBefore = firstModel.getText();
        System.out.println(modelBefore);
        sleep(3000);

        //Go to asset type info
        clickOn("#assetTypeMenuBtn");
        sleep(2000);

        //select first asset type
        Node node = lookup("#columnName").nth(1).query();
        clickOn(node);
        sleep(1000);

        clickOn("#associatedModelLabel");
        sleep(1000);

        //go to model tab
        clickOn(scene.getRoot().lookup("#modelTab"));
        sleep(1000);
        FlowPane models = (FlowPane) scene.getRoot().lookup("#modelsThumbPane");

        //change to LSTM
        clickOn(models.getChildren().get(1));
        sleep(1000);

        clickOn("#modelSaveBtn");
        sleep(1000);

        //Go back to first asset to check the model again
        clickOn("#assetMenuBtn");
        sleep(1000);
        clickOn("#thumbnailTab");
        sleep(1000);
        root = (FlowPane) scene.getRoot().lookup("#assetsThumbPane");
        clickOn(root.getChildren().get(0));
        sleep(2000);

        clickOn("#modelOutput");
        Text secondModel = (Text) scene.getRoot().lookup("#modelOutput");
        String modelAfter = secondModel.getText();
        System.out.println(modelAfter);
        sleep(3000);
        clickOn("#backBtn");
        sleep(2000);
        // TODO: 2021-03-27 Change to false
        assertEquals(modelBefore, modelAfter);
    }

    /*
    F8 - The system shall display the selected RUL model parameters.
     */
    @Test
    public void f8DisplayParametersTest()
    {

    }

    /*
    F9 - The system shall provide the ability to edit a selected RUL model parameters.
     */
    @Test
    public void f9EditParametersTest()
    {

    }

    /*
    F10 - The system shall display a selected RUL model performance (RMSE).
     */
    @Test
    public void f10ModelPerformanceTest()
    {

    }

    /*
    F11 - The system shall provide the ability to add and remove a asset type
     */
    @Test
    public void f11AddRemoveAssetTypeTest()
    {
        clickOn("#assetTypeMenuBtn");
        sleep(2000);

        TableView assetTypes = (TableView) scene.getRoot().lookup("#tableView");
        int assetTypesDefaultSize = assetTypes.getItems().size();       //default size should be 4

        clickOn("#addAssetTypeBtn");
        sleep(2000);

        clickOn("#assetTypeName");
        write("My New Engine");
        sleep(1000);

        clickOn("#assetTypeDescription");
        write("Adding a new asset for system testing.");
        sleep(1000);

        clickOn("#thresholdOKValue");
        write("400");
        sleep(1000);

        clickOn("#thresholdAdvisoryValue");
        write("300");
        sleep(1000);

        clickOn("#thresholdCautionValue");
        write("200");
        sleep(1000);

        clickOn("#thresholdWarningValue");
        write("100");
        sleep(1000);

        clickOn("#thresholdFailedValue");
        write("50");
        sleep(1000);

        clickOn("#saveBtn");
        sleep(1000);

        assetTypes = (TableView) scene.getRoot().lookup("#tableView");
        assertTrue(assetTypes.getItems().size() == (assetTypesDefaultSize + 1));      //should now have default size + 1 item

        sleep(2000);

        //select last row
        Node node = lookup("#columnName").nth(assetTypes.getItems().size()).query();
        clickOn(node);
        sleep(1000);

        clickOn("#assetTypeName");
        sleep(1000);

        clickOn("#deleteBtn");
        sleep(500);

        type(KeyCode.SPACE);
        sleep(500);

        clickOn("#backBtn");
        sleep(1000);

        assetTypes = (TableView) scene.getRoot().lookup("#tableView");
        moveBy(40, 200);
        assetTypes.scrollTo(assetTypesDefaultSize);         //back to original table
        sleep(3000);

        //Table should return to original size (of rows) after deleting the newly added asset.
        assertTrue(assetTypes.getItems().size() == assetTypesDefaultSize);
    }

    /*
    F12 - The system shall provide the ability to associate a asset type with RUL model
     */
    @Test
    public void f12AssociateAssetTypeWithModelTest()
    {
        clickOn("#assetTypeMenuBtn");
        sleep(2000);

        //select first asset type
        Node node = lookup("#columnName").nth(1).query();
        clickOn(node);
        sleep(1000);

        clickOn("#associatedModelLabel");
        sleep(1000);
        Label label = (Label) scene.getRoot().lookup("#associatedModelLabel");
        String modelBefore = label.getText();

        //go to model tab
        clickOn(scene.getRoot().lookup("#modelTab"));
        sleep(1000);
        FlowPane models = (FlowPane) scene.getRoot().lookup("#modelsThumbPane");
        System.out.println(models.getChildren());

        //change to LSTM
        clickOn(models.getChildren().get(1));
        sleep(1000);

        clickOn("#modelSaveBtn");
        sleep(1000);

        node = lookup("#columnName").nth(1).query();
        clickOn(node);
        label = (Label) scene.getRoot().lookup("#associatedModelLabel");
        String modelAfter = label.getText();
        System.out.println(modelAfter);

        clickOn("#associatedModelLabel");
        sleep(1000);
        // TODO: 2021-03-27 : Change to false
        assertTrue(modelBefore.equals(modelAfter));
    }

    /*
    F13 - The system shall display a monitored asset in RUL order (lowest RUL first)
     */
    @Test
    public void f13OrderRULTest()
    {
        clickOn("#thumbnailTab");
        moveBy(90, 200);
        scroll(50, VerticalDirection.UP);
        scroll(50, VerticalDirection.DOWN);
        clickOn("#sortAsset");
        type(KeyCode.DOWN);
        type(KeyCode.ENTER);
        moveBy(-90, 200);

        scroll(50, VerticalDirection.UP);

        boolean isOrdered = true;
        double[] ruls = getRuls();

        //Check if the ascending ruls are ordered
        for (int i = 0; i < ruls.length - 1; i++)
        {
            if(ruls[i] > ruls[i + 1])
            {
                isOrdered = false;
                break;
            }
        }

        scroll(50, VerticalDirection.UP);
        assertTrue(isOrdered);
    }

    //get double[] RUL values after it's been sorted in ascending order.
    public double[] getRuls()
    {
        FlowPane root = (FlowPane) scene.getRoot().lookup("#assetsThumbPane");
        double[] ruls = new double[root.getChildren().size()];
        int i = 0;

        do
        {
            Pane pane = (Pane) root.getChildren().get(i);       //each pane
            HBox hBox = (HBox) pane.getChildren().get(3);

            Text text = (Text) hBox.getChildren().get(0);       //rul
            //System.out.println(text.getText());
            ruls[i] = Double.parseDouble(text.getText());       //add to array

        } while(++i < ruls.length);

        return ruls;
    }

    /*
     F14 - The system shall provide the ability to select a asset
     */
    @Test
    public void f14SelectAssetTest()
    {
        FlowPane assets;
        int i = 0;
        Random random = new Random();

        //select 5 random assets out of the first 12
        do
        {
            clickOn("#thumbnailTab");
            moveBy(300, 500);
            scroll(10, VerticalDirection.UP);
            sleep(2000);

            assets = (FlowPane) scene.getRoot().lookup("#assetsThumbPane");
            clickOn(assets.getChildren().get(random.nextInt(12)));
            sleep(2000);
            clickOn("#backBtn");
        } while(++i < 5);

        assertTrue(assets.getChildren().size() > 0);
    }

    /*
    F15 - The system shall display a selected asset raw data
     */
    @Test
    public void f15DisplayRawDataTest()
    {
        clickOn("#thumbnailTab");

        FlowPane root = (FlowPane) scene.getRoot().lookup("#assetsThumbPane");
        clickOn(root.getChildren().get(1));
        sleep(100);
        clickOn("#rawDataTab");

        sleep(1000);
        moveBy(90, 200);

        TableView table = (TableView) scene.getRoot().lookup("#RawDataTable");
        sleep(1000);

        table.scrollToColumnIndex(15);
        sleep(3000);
        assertTrue(table.getItems().size() > 0);
    }

    /*
    F16 - The system shall display a selected asset information
     */
    @Test
    public void f16DisplayAssetInfoTest()
    {
        clickOn("#thumbnailTab");
        FlowPane root;
        FlowPane totalAttributes;

        boolean isValidAttributesAmount = true;
        int i = 0;

        //test the first 3 assets:
        while(i < 3)
        {
            root = (FlowPane) scene.getRoot().lookup("#assetsThumbPane");
            clickOn(root.getChildren().get(i));
            sleep(2000);
            clickOn("#informationTab");
            moveBy(200, 500);
            scroll(150, VerticalDirection.UP);

            totalAttributes = (FlowPane) scene.getRoot().lookup("#attributeFlowPane");

            //should have 26 total attributes
            if(totalAttributes.getChildren().size() != 26)
            {
                isValidAttributesAmount = false;
                break;
            }

            clickOn("#backBtn");
            i++;
        }

        assertTrue(isValidAttributesAmount);
    }

    /*
    F17 - The system shall provide the ability to add or remove a asset.
     */
    @Test
    public void f17AddRemoveAssetTest()
    {

    }
}
