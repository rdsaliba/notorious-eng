package app.item;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class AssetInfoTest {
    private static AssetInfo assetInfo;

    @Before
    public void setUp() {
        assetInfo = new AssetInfo();
        assetInfo.addAttribute(new AssetAttribute());
    }

    @Test
    public void defaultConst(){
        AssetInfo temp = new AssetInfo();
        assertNotNull("A new asset info object should have been created",temp);
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
    public void setAssetAttributes() {
        AssetInfo temp = new AssetInfo();
        assertEquals("There should be no asset attribute at start", 0, temp.getAssetAttributes().size());
        assertEquals("[]", temp.getAssetAttributes().toString());
        ArrayList<AssetAttribute> aAttributesList = new ArrayList<>();
        aAttributesList.add(new AssetAttribute());
        temp.setAssetAttributes(aAttributesList);
        assertEquals("There should be one asset attribute after setting", 1, temp.getAssetAttributes().size());
        assertEquals("[AssetAttribute{id=0, name='null', measurements=[]}]", temp.getAssetAttributes().toString());
    }
}