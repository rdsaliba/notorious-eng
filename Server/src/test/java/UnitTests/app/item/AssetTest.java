package UnitTests.app.item;

import app.item.Asset;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AssetTest {
    private Asset asset;

    @Before
    public void setUp() {
        asset = new Asset();
    }

    @After
    public void tearDown() {
        asset = null;
    }

    @Test
    public void defaultConst(){
        Asset temp = new Asset();
        assertNotNull("new asset should be created", temp);
    }

    @Test
    public void setAndGetSerialNo() {
        asset.setSerialNo("newSerialNo");
        assertEquals("new serial number should be 'newSerialNo'", "newSerialNo",asset.getSerialNo());
    }

    @Test
    public void setAndGetAssetType() {
        asset.setAssetTypeID("newAssetType");
        assertEquals("new assetType should be 'newAssetType'", "newAssetType",asset.getAssetTypeID());
    }

    @Test
    public void setAndGetLocation() {
        asset.setLocation("newLocation");
        assertEquals("new Location should be 'newLocation'", "newLocation",asset.getLocation());
    }

    @Test
    public void setAndGetDescription() {
        asset.setDescription("newDescription");
        assertEquals("new description should be 'newDescription'", "newDescription",asset.getDescription());
    }

    @Test
    public void setAndGetName() {
        asset.setName("newName");
        assertEquals("new description should be 'newName'", "newName", asset.getName());
    }

    @Test
    public void setAndGetID() {
        asset.setId(10);
        assertEquals("new description should be '10'", 10, asset.getId());
    }

    @Test
    public void setAndGetManufacturer() {
        asset.setManufacturer("newManufacturer");
        assertEquals("new description should be 'newManufacturer'", "newManufacturer", asset.getManufacturer());
    }

    @Test
    public void setAndGetCategory() {
        asset.setCategory("newCategory");
        assertEquals("new description should be 'newCategory'", "newCategory", asset.getCategory());
    }

    @Test
    public void setAndGetSite() {
        asset.setSite("newSite");
        assertEquals("new description should be 'newSite'", "newSite", asset.getSite());
    }

    @Test
    public void setAndGetRecommendation() {
        asset.setRecommendation("newRecommendation");
        assertEquals("new description should be 'newRecommendation'", "newRecommendation", asset.getRecommendation());
    }

    @Test
    public void setAndGetAssetInfo() {
        asset.setAssetInfo(null);
        assertNull("asset type should be null after set",asset.getAssetInfo());
    }

    @Test
    public void assetToString() {
        assertEquals("Asset{serialNo='null', name='null', assetTypeID='null', location='null', description='null', recommendation='null', assetInfo=null, manufacturer='null', category='null', site='null'}", asset.toString());
    }
}