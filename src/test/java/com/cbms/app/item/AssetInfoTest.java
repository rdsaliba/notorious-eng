package com.cbms.app.item;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class AssetInfoTest {
    private static AssetInfo assetInfo;

    @BeforeClass
    public static void setUp() {
        assetInfo = new AssetInfo();
        assetInfo.addAttribute(new AssetAttribute());
    }

    @Test
    public void addAttribute() {
        assertEquals("There should be one asset attribute at start", 1, assetInfo.getAssetAttributes().size());
        assetInfo.addAttribute(new AssetAttribute());
        assertEquals("there should now be 2 asset attributes", 2, assetInfo.getAssetAttributes().size());
    }

    @Test
    public void getAssetAttributes() {
        assertFalse(assetInfo.getAssetAttributes().isEmpty());
    }

    @Test
    public void getLastRecorderTimeCycle() {
        assetInfo.getAssetAttributes().get(0).addMeasurement(1, 1.0);
        assetInfo.getAssetAttributes().get(0).addMeasurement(2, 1.0);
        assetInfo.getAssetAttributes().get(0).addMeasurement(3, 1.0);
        assertEquals("there should be 3 measurements", 3, assetInfo.getLastRecorderTimeCycle());
    }

    @Test
    public void addRULMeasurement() {
        assertTrue("No current measurements", assetInfo.getAllEstimates().isEmpty());
        assetInfo.addRULMeasurement(123.0);
        assertEquals("one measurement after the add", 1, assetInfo.getAllEstimates().size());
    }

    @Test
    public void getRULMeasurement() {
        assetInfo.addRULMeasurement(111.0);
        assertTrue("getting the lastest rul measurement should return 111", assetInfo.getRULMeasurement() == 111.0);

    }
}