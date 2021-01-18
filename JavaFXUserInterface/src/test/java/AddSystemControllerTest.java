import app.item.Asset;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AddSystemControllerTest {

    private Asset fullAsset;
    private Asset emptyAsset;
    private AddSystemController addSystemController;


    @Before
    public void setup() {
        addSystemController = new AddSystemController();

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

    @Test
    public void isAssetEmptyTrue() {
        assertTrue(addSystemController.isAssetEmpty(emptyAsset));
    }

    @Test
    public void isAssetEmptyFalse() {
        assertFalse(addSystemController.isAssetEmpty(fullAsset));
    }
}