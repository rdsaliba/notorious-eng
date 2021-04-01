package e2e.com.cbms.app.allfeatures;

import javafx.fxml.FXMLLoader;
import javafx.geometry.VerticalDirection;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import utilities.TextConstants;

import java.util.Random;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class AllFeaturesTest extends ApplicationTest
{
    private Scene scene;

    @Override
    public void start(Stage stage) throws Exception
    {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(TextConstants.ASSETS_SCENE + TextConstants.FXML));
        Parent root  = loader.load();
        scene = new Scene(root);
        scene.setUserData(loader);
        stage.setTitle("Minerva");
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
        clickOn(root.getChildren().get(0)).sleep(1000);

        clickOn("#modelOutput").sleep(3000);
        Text firstModel = (Text) scene.getRoot().lookup("#modelOutput");
        String modelBefore = firstModel.getText();

        //Go to asset type info
        clickOn("#assetTypeMenuBtn").sleep(2000);

        //select first asset type
        Node node = lookup("#columnName").nth(1).query();
        clickOn(node).sleep(1000);
        clickOn("#associatedModelLabel").sleep(1000);

        //go to model tab
        clickOn(scene.getRoot().lookup("#modelTab")).sleep(1000);
        FlowPane models = (FlowPane) scene.getRoot().lookup("#modelsThumbPane");

        //change to LSTM
        clickOn(models.getChildren().get(1)).sleep(1000);
        clickOn("#modelSaveBtn").sleep(1000);

        //Go back to first asset to check the model again
        clickOn("#assetMenuBtn").sleep(1000);
        clickOn("#thumbnailTab").sleep(1000);
        root = (FlowPane) scene.getRoot().lookup("#assetsThumbPane");

        //Check that the model of first asset changed
        clickOn(root.getChildren().get(0)).sleep(1000);
        clickOn("#modelOutput").sleep(3000);
        Text secondModel = (Text) scene.getRoot().lookup("#modelOutput");
        String modelAfter = secondModel.getText();

        assertNotSame("Models should be different after updating", modelBefore, modelAfter);
        clickOn("#backBtn").sleep(2000);
    }

    /*
    F8 - The system shall display the selected RUL model parameters.
     */
    @Test
    public void f8DisplayParametersTest()
    {
        clickOn("#assetTypeMenuBtn").sleep(1000);

        //select first asset type
        Node node = lookup("#columnName").nth(1).query();
        clickOn(node).sleep(1000);

        //go to model tab
        clickOn(scene.getRoot().lookup("#modelTab")).sleep(1000);

        FlowPane models = (FlowPane) scene.getRoot().lookup("#modelsThumbPane");
        VBox parameters = (VBox) scene.getRoot().lookup("#modelParameters");

        int i;
        //Iterate through all the models and their parameters
        for(int j = 0; j < models.getChildren().size(); j++)
        {
            i = 0;
            clickOn(models.getChildren().get(j));

            if(j == 4)
            {
                scroll(90, VerticalDirection.UP);
            }

            while(i < parameters.getChildren().size())
            {
                clickOn(parameters.getChildren().get(i++)).sleep(500);

                if(i == 10)
                {
                    scroll(60, VerticalDirection.UP);
                }
            }
        }

        assertTrue("There should be 8 models", models.getChildren().size() == 8);
        assertNotNull("Parameters exist", parameters);
    }

    /*
    F9 - The system shall provide the ability to edit a selected RUL model parameters.
     */
    @Test
    public void f9EditParametersTest()
    {
        clickOn("#assetTypeMenuBtn").sleep(1000);

        //select 1st asset type
        Node node = lookup("#columnName").nth(1).query();
        clickOn(node).sleep(1000);

        //go to model tab
        clickOn(scene.getRoot().lookup("#modelTab")).sleep(1000);
        FlowPane allModels = (FlowPane) scene.getRoot().lookup("#modelsThumbPane");
        Pane selectedModel = (Pane) allModels.getChildren().get(0);
        clickOn(selectedModel).sleep(1000);

        //Changing Parameters
        VBox paraBox = (VBox) scene.getRoot().lookup("#modelParameters");
        Pane batchSizePara = (Pane) paraBox.getChildren().get(0);
        TextField batchSizeVal = (TextField) batchSizePara.getChildren().get(1);
        clickOn(batchSizeVal).type(KeyCode.BACK_SPACE).sleep(500).type(KeyCode.BACK_SPACE).sleep(500).write("60").sleep(1500);

        Pane qrPara = (Pane) paraBox.getChildren().get(1);
        CheckBox qrVal = (CheckBox) qrPara.getChildren().get(1);
        clickOn(qrVal).sleep(1500);

        Pane coliniearPara = (Pane) paraBox.getChildren().get(2);
        CheckBox colinearVal = (CheckBox) coliniearPara.getChildren().get(1);
        clickOn(colinearVal).sleep(1500);

        //Parameter after changing it from the default value should be 160
        assertTrue("Batch Size value is a now 160", batchSizeVal.getText().equals("160"));

        //reset to default
        clickOn(scene.getRoot().lookup("#modelDefaultBtn")).sleep(1000);
        clickOn(paraBox.getChildren().get(0)).sleep(1500);

        //Now go to the last model (Multilayer Perceptron)
        selectedModel = (Pane) allModels.getChildren().get(allModels.getChildren().size() - 1);
        clickOn(scene.getRoot().lookup("#modelTab")).sleep(1000);

        moveBy(0, 200).scroll(65, VerticalDirection.UP);
        clickOn(selectedModel).sleep(1000);
        clickOn(scene.getRoot().lookup("#modelDefaultBtn")).sleep(2000);

        //Change parameters
        Pane autoBuildPara = (Pane) paraBox.getChildren().get(0);
        CheckBox autoBuildVal = (CheckBox) autoBuildPara.getChildren().get(1);
        clickOn(autoBuildVal).sleep(1500);

        Pane showGUIPara = (Pane) paraBox.getChildren().get(2);
        CheckBox showGUIVal = (CheckBox) showGUIPara.getChildren().get(1);
        clickOn(showGUIVal).sleep(1500);

        Pane momentumPara = (Pane) paraBox.getChildren().get(3);
        TextField momentumVal = (TextField) momentumPara.getChildren().get(1);
        clickOn(momentumVal).type(KeyCode.BACK_SPACE).sleep(500).write("1").sleep(1500);

        //Momentum after changing shouldn't be the default 0.2 value anymore
        assertFalse("Momentum is default 0.2", momentumVal.getText().equals("0.2"));

        Pane decayPara = (Pane) paraBox.getChildren().get(9);
        CheckBox decayVal = (CheckBox) decayPara.getChildren().get(1);
        clickOn(decayVal).sleep(1500);

//        //Go to RandomForest Model
        selectedModel = (Pane) allModels.getChildren().get(2);
        clickOn(selectedModel).sleep(1000);
        clickOn(scene.getRoot().lookup("#modelDefaultBtn")).sleep(2000);

//        //Change parameters
        Pane execPara = (Pane) paraBox.getChildren().get(1);
        TextField execVal = (TextField) execPara.getChildren().get(2);
        clickOn(execVal).sleep(1500);
//
        Pane calcBagPara = (Pane) paraBox.getChildren().get(3);
        CheckBox calcBagVal = (CheckBox) calcBagPara.getChildren().get(1);
        clickOn(calcBagVal).sleep(1500);
//
        Pane bagSizePara = (Pane) paraBox.getChildren().get(6);
        TextField bagSizeVal = (TextField) bagSizePara.getChildren().get(1);
        clickOn(bagSizeVal).type(KeyCode.BACK_SPACE).sleep(500).type(KeyCode.BACK_SPACE).sleep(500).write("0").sleep(1500);

        //Bag Size after changing shouldn't be the default 100 value anymore
        assertFalse("Bag size is default 100", bagSizeVal.getText().equals("100"));
        //Have parameters VBox
        assertNotNull("Parameters box for the model exists", paraBox);

        clickOn(scene.getRoot().lookup("#modelDefaultBtn")).sleep(2000);
        clickOn(paraBox.getChildren().get(0)).sleep(2000);
    }

    /*
    F10 - The system shall display a selected RUL model performance (RMSE).
     */
    @Test
    public void f10ModelPerformanceTest()
    {
        clickOn("#assetTypeMenuBtn").sleep(1000);

        //select first asset type
        Node node = lookup("#columnName").nth(1).query();
        clickOn(node).sleep(1000);

        //go to model tab
        clickOn(scene.getRoot().lookup("#modelTab")).sleep(1000);

        //Modfiy sliders
        Slider trainSlider = (Slider) scene.getRoot().lookup("#trainSlider");
        clickOn(trainSlider);
        type(KeyCode.TAB).type(KeyCode.RIGHT);
        type(KeyCode.TAB).type(KeyCode.RIGHT);
        moveBy(0, 10).clickOn().sleep(500).moveBy(0, 100).scroll(80, VerticalDirection.UP);

        FlowPane models = (FlowPane) scene.getRoot().lookup("#modelsThumbPane");
        Pane additiveRegModel = (Pane) models.getChildren().get(5);
        Button evalButton = (Button) additiveRegModel.getChildren().get(additiveRegModel.getChildren().size() - 1);

        Text rmse = (Text) additiveRegModel.getChildren().get(additiveRegModel.getChildren().size() - 2);
        clickOn(rmse).sleep(3000);
        String oldRmseVal = rmse.getText();
        int i = 0;

        clickOn(evalButton);
        //incrementally check for 30 seconds to see if rmse has been updated
        do
        {
            if(rmse.getText().equals(oldRmseVal))
            {
                sleep(2000);
            }
            else
            {
                break;
            }
        }while(++i < 15);

        clickOn(rmse).sleep(3000);

        assertNotNull("Evaluate button exists", evalButton);
        assertNotNull("RMSE exists", rmse);
        assertTrue("Assert current RMSE is different than old RMSE", !rmse.getText().equals(oldRmseVal));
    }

    /*
    F11 - The system shall provide the ability to add and remove a asset type
     */
    @Test
    public void f11AddRemoveAssetTypeTest()
    {
        clickOn("#assetTypeMenuBtn").sleep(1000).moveBy(100, 0);
        clickOn("#assetName").moveBy(40, 200);

        TableView assetTypes = (TableView) scene.getRoot().lookup("#tableView");
        int assetTypesDefaultSize = assetTypes.getItems().size();       //default size should be 4

        assetTypes.scrollTo(assetTypesDefaultSize);
        Button button = (Button) scene.getRoot().lookup("#addAssetTypeBtn");
        sleep(2000).clickOn(button).sleep(2000);

        //Enter info for new asset type
        clickOn("#assetTypeName").write("My New Engine").sleep(1000);
        clickOn("#assetTypeDescription").write("Adding a new asset for system testing.").sleep(1000);
        clickOn("#thresholdAdvisoryValue").write("300").sleep(1000);
        clickOn("#thresholdCautionValue").write("200").sleep(1000);
        clickOn("#thresholdWarningValue").write("100").sleep(1000);
        clickOn("#thresholdFailedValue").write("50").sleep(1000);

        clickOn("#saveBtn").sleep(1000);

        assetTypes = (TableView) scene.getRoot().lookup("#tableView");
        clickOn("#assetName").sleep(1000);

        assertTrue("Asset list contains an additional asset", assetTypes.getItems().size() == (assetTypesDefaultSize + 1));      //should now have default size + 1 item

        //Select last row
        Node node = lookup("#columnName").nth(assetTypes.getItems().size()).query();
        clickOn(node).sleep(1000);
        //Delete the new asset type
        clickOn("#assetTypeName").sleep(1000);
        clickOn("#deleteBtn").sleep(500);
        type(KeyCode.SPACE).sleep(500);

        assetTypes = (TableView) scene.getRoot().lookup("#tableView");
        moveBy(0, 200);
        assetTypes.scrollTo(assetTypesDefaultSize);         //back to original table
        sleep(3000);

        //Table should return to original size (of rows) after deleting the newly added asset.
        assertTrue("Asset list contains the same number of assets as before adding and deleting.", assetTypes.getItems().size() == assetTypesDefaultSize);
    }

    /*
    F12 - The system shall provide the ability to associate a asset type with RUL model
     */
    @Test
    public void f12AssociateAssetTypeWithModelTest()
    {
        clickOn("#assetTypeMenuBtn").sleep(2000);

        //select second asset type
        Node node = lookup("#columnName").nth(2).query();
        clickOn(node).sleep(1000);

        clickOn("#associatedModelLabel").sleep(1000);
        Label label = (Label) scene.getRoot().lookup("#associatedModelLabel");
        String modelBefore = label.getText();

        //go to model tab
        clickOn(scene.getRoot().lookup("#modelTab")).sleep(1000);
        FlowPane models = (FlowPane) scene.getRoot().lookup("#modelsThumbPane");

        //change to RandomForest
        clickOn(models.getChildren().get(2)).sleep(1000);
        clickOn("#modelSaveBtn").sleep(1000);

        node = lookup("#columnName").nth(2).query();
        clickOn(node);

        label = (Label) scene.getRoot().lookup("#associatedModelLabel");
        String modelAfter = label.getText();

        clickOn("#associatedModelLabel").sleep(3000);
        //model after changing should not be the same
        assertFalse("Model after changing is the same as it was before.",modelBefore.equals(modelAfter));
    }

    /*
    F13 - The system shall display a monitored asset in RUL order (lowest RUL first)
     */
    @Test
    public void f13OrderRULTest()
    {
        clickOn("#thumbnailTab").moveBy(90, 200);
        scroll(50, VerticalDirection.UP).sleep(1000);

        scroll(50, VerticalDirection.DOWN).sleep(1000);
        clickOn("#sortAsset").type(KeyCode.DOWN).type(KeyCode.ENTER);

        sleep(1000).moveBy(-90, 200).scroll(50, VerticalDirection.UP).sleep(1000);

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

        sleep(1000).scroll(50, VerticalDirection.DOWN).sleep(1000);
        assertTrue("All RULs after sorting by ascending are ordered from smallest to largest.", isOrdered);
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

        //select 4 random assets out of the first 12 visible assets in the window
        do
        {
            clickOn("#thumbnailTab").moveBy(300, 500);
            scroll(10, VerticalDirection.UP).sleep(2000);

            assets = (FlowPane) scene.getRoot().lookup("#assetsThumbPane");
            clickOn(assets.getChildren().get(random.nextInt(12)));
            sleep(1000).clickOn("#serialNumberOutput").sleep(1000);
            clickOn("#backBtn");

        } while(++i < 4);

        assertTrue("There are selectable assets on the main page.", assets.getChildren().size() > 0);
    }

    /*
    F15 - The system shall display a selected asset raw data
     */
    @Test
    public void f15DisplayRawDataTest()
    {
        clickOn("#thumbnailTab").sleep(1000);
        FlowPane root = (FlowPane) scene.getRoot().lookup("#assetsThumbPane");

        //Go to the 2nd asset
        clickOn(root.getChildren().get(1)).sleep(2000);
        clickOn("#rawDataTab").sleep(1000);

        moveBy(90, 200).sleep(1000);
        TableView table = (TableView) scene.getRoot().lookup("#RawDataTable");

        table.scrollToColumnIndex(15);          //scroll to show the end columns
        sleep(3000).clickOn("#backBtn").sleep(1000);

        assertTrue("There is a raw data table for the selected asset.", table.getItems().size() > 0);
    }

    /*
    F16 - The system shall display a selected asset information
     */
    @Test
    public void f16DisplayAssetInfoTest()
    {
        clickOn("#thumbnailTab").sleep(1000);
        FlowPane root;
        FlowPane totalAttributes;

        boolean isValidAttributesAmount = true;
        int i = 0;

        //test the first 3 assets:
        while(i < 3)
        {
            root = (FlowPane) scene.getRoot().lookup("#assetsThumbPane");
            clickOn(root.getChildren().get(i));
            sleep(2000).clickOn("#informationTab");
            moveBy(200, 500).scroll(150, VerticalDirection.UP);

            totalAttributes = (FlowPane) scene.getRoot().lookup("#attributeFlowPane");

            //should have 26 total attributes
            if(totalAttributes.getChildren().size() != 26)
            {
                isValidAttributesAmount = false;
                break;
            }

            clickOn("#backBtn").sleep(1000);
            i++;
        }

        assertTrue("Assets have 26 attributes.", isValidAttributesAmount);
    }

    /*
    F17 - The system shall provide the ability to add or remove a asset.
     */
    @Test
    public void f17AddRemoveAssetTest()
    {
        Button button   = (Button) scene.getRoot().lookup("#addAssetBtn");

        assertNotNull("Button to add asset exists", button);
        assertEquals("Add Asset", button.getText());

        clickOn(button).sleep(1000);
        //Enter info for new asset
        clickOn("#assetNameInput").write("My New Asset").sleep(1000);
        clickOn("#assetTypeChoiceBox").type(KeyCode.DOWN).sleep(1000).type(KeyCode.ENTER).sleep(1000);
        clickOn("#assetDescriptionTextArea").write("Adding a new asset for system testing").sleep(1000);
        clickOn("#serialNumberInput").write("192837465").sleep(1000);
        clickOn("#manufacturerInput").write("Asset in good condition").sleep(1000);
        clickOn("#categoryInput").write("Category 2").sleep(1000);
        clickOn("#siteInput").write("Regularly checked").sleep(1000);
        clickOn("#locationInput").write("Montreal Qc").sleep(1000);

        // TODO: Complete the test (save button) once Add Asset bug is fixed.
        clickOn(scene.getRoot().lookup("#cancelBtn")).sleep(3000);
    }

}