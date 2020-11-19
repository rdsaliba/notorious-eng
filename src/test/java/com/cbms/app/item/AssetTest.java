package com.cbms.app.item;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AssetTest {
    private Asset asset;

    @Before
    public void setUp() throws Exception {
        asset = new Asset("SerialNo","AssetType","location","description", new AssetInfo());
    }

    @Test
    public void defaultConst(){
        Asset temp = new Asset();
        assertNotNull("new asset should be created", temp);
    }

    @Test
    public void paramConst(){
        Asset temp = new Asset("SerialNo","AssetType","location","description");
        assertNotNull("new asset should be created", temp);
    }


    @Test
    public void getSerialNo() {
        assertEquals("default serial number should be 'SerialNo'", "SerialNo",asset.getSerialNo());
    }

    @Test
    public void setSerialNo() {
        asset.setSerialNo("newSerialNo");
        assertEquals("new serial number should be 'newSerialNo'", "newSerialNo",asset.getSerialNo());
    }

    @Test
    public void getAssetType() {
        assertEquals("default assetType should be 'AssetType'", "AssetType",asset.getAssetType());
    }

    @Test
    public void setAssetType() {
        asset.setAssetType("newAssetType");
        assertEquals("new assetType should be 'newAssetType'", "newAssetType",asset.getAssetType());
    }

    @Test
    public void getLocation() {
        assertEquals("default Location should be 'location'", "location",asset.getLocation());
    }

    @Test
    public void setLocation() {
        asset.setLocation("newLocation");
        assertEquals("new Location should be 'newLocation'", "newLocation",asset.getLocation());
    }

    @Test
    public void getDescription() {
        assertEquals("default description should be 'description'", "description",asset.getDescription());
    }

    @Test
    public void setDescription() {
        asset.setDescription("newDescription");
        assertEquals("new description should be 'newDescription'", "newDescription",asset.getDescription());
    }

    @Test
    public void getAssetInfo() {
        assertNotNull("current asset info should not be null",asset.getAssetInfo());
    }

    @Test
    public void setAssetInfo() {
        asset.setAssetInfo(null);
        assertNull("asset type should be nulled after set",asset.getAssetInfo());
    }

}