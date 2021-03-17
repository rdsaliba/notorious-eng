package UnitTests;

import controllers.AddAssetController;
import app.item.Asset;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import static org.junit.Assert.*;

public class AddAssetControllerTest extends ApplicationTest {

    private Asset fullAsset;
    private Asset emptyAsset;
    private AddAssetController addAssetController;
    private Scene scene;

    @Override
    public void start (Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(AddAssetController.class.getResource("/AddAsset.fxml"));
        Parent root = fxmlLoader.load();
        scene = new Scene(root);
        addAssetController = (AddAssetController) fxmlLoader.getController();
        stage.setTitle("CBMS");
        stage.setScene(scene);
        stage.show();
    }

    @Before
    public void setup() {
        fullAsset = new Asset();
        fullAsset.setName("Engine");
        fullAsset.setAssetTypeID("123");
        fullAsset.setDescription("Test Description");
        fullAsset.setSerialNo("5634");
        fullAsset.setManufacturer("Test Manufacturer");
        fullAsset.setCategory("Test Category");
        fullAsset.setSite("Test Site");
        fullAsset.setLocation("Test Location");

        emptyAsset = new Asset();
        emptyAsset.setName("");
        emptyAsset.setAssetTypeID("");
        emptyAsset.setDescription("");
        emptyAsset.setSerialNo("");
        emptyAsset.setManufacturer("");
        emptyAsset.setCategory("");
        emptyAsset.setSite("");
        emptyAsset.setLocation("");
    }

    @After
    public void tearDown () throws Exception {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }

    @Test
    public void isAssetEmptyTrue() {
        assertTrue(addAssetController.isAssetEmpty(emptyAsset));
    }

    @Test
    public void isAssetEmptyFalse() {
        assertFalse(addAssetController.isAssetEmpty(fullAsset));
    }

}