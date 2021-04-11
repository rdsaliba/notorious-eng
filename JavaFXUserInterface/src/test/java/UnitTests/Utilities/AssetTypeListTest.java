package UnitTests.Utilities;

import app.item.AssetType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utilities.AssetTypeList;

import static org.junit.Assert.*;

public class AssetTypeListTest {
    private AssetType assetType;
    private AssetTypeList assetTypeList;

    @Before
    public void setUp() {
        assetType = new AssetType("Test");
        assetTypeList = new AssetTypeList(assetType, 2, 3, "30", 2, "25", 0, "20", 0, "15", 0, "10", 0);
    }

    @After
    public void tearDown() {
        assetType = null;
        assetTypeList = null;
    }

    @Test
    public void testGetAndSetAssetType() {
        AssetType temp = new AssetType("temp");
        assetTypeList.setAssetType(temp);
        assertEquals("temp", assetTypeList.getAssetType().getName());
    }

    @Test
    public void testGetLiveAssets() {
        int liveAssetsNumber = assetTypeList.getLiveAssets();
        assertEquals(2, liveAssetsNumber);
    }

    @Test
    public void testGetArchivedAssets() {
        int archivedAssetsNumber = assetTypeList.getArchivedAssets();
        assertEquals(3, archivedAssetsNumber);
    }

    @Test
    public void testGetAndSetValueOk() {
        assetTypeList.setValueOk("30");
        assertEquals("30", assetTypeList.getValueOk());
    }

    @Test
    public void testGetAndSetValueCaution() {
        assetTypeList.setValueCaution("25");
        assertEquals("25", assetTypeList.getValueCaution());
    }

    @Test
    public void testGetAndSetValueAdvisory() {
        assetTypeList.setValueAdvisory("20");
        assertEquals("20", assetTypeList.getValueAdvisory());
    }

    @Test
    public void testGetAndSetValueWarning() {
        assetTypeList.setValueWarning("15");
        assertEquals("15", assetTypeList.getValueWarning());
    }

    @Test
    public void testGetAndSetValueFailed() {
        assetTypeList.setValueFailed("10");
        assertEquals("10", assetTypeList.getValueFailed());
    }

    @Test
    public void testGetId() {
        assetTypeList.getAssetType().setId("TempAssetTypeID");
        assertEquals("TempAssetTypeID", assetTypeList.getId());
    }

    @Test
    public void testGetAndSetName() {
        assetTypeList.setName("newAssetTypeName");
        assertEquals("newAssetTypeName", assetTypeList.getName());
    }

    @Test
    public void testGetDescription() {
        assertNull(assetTypeList.getDescription());
    }

    @Test
    public void testGetCountOk() {
        assertEquals(2, assetTypeList.getCountOk());
    }

    @Test
    public void testGetCountCaution() {
        assertEquals(0, assetTypeList.getCountCaution());
    }

    @Test
    public void testGetCountAdvisory() {
        assertEquals(0, assetTypeList.getCountAdvisory());
    }

    @Test
    public void testGetCountWarning() {
        assertEquals(0, assetTypeList.getCountWarning());
    }

    @Test
    public void testGetCountFailed() {
        assertEquals(0, assetTypeList.getCountFailed());
    }

    @Test
    public void testToAssetType() {
        assertNotNull(assetTypeList.toAssetType());
    }
}