package app.item;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class AssetTypeTest {
    private AssetType assetTypeA;
    private AssetType assetTypeB;
    private AssetType assetTypeC;
    private AssetType assetTypeD;
    private ArrayList<AssetTypeParameter> aListAssetTypeParametersA;
    private ArrayList<AssetTypeParameter> aListAssetTypeParametersB;
    private AssetTypeParameter assetTypeParameterA;

    @Before
    public void setUp() {
        assetTypeA = new AssetType();
        assetTypeB = new AssetType("assetTypeB");
        assetTypeParameterA = new AssetTypeParameter();
        aListAssetTypeParametersA = new ArrayList<>();
        aListAssetTypeParametersB = new ArrayList<>();
        aListAssetTypeParametersB.add(assetTypeParameterA);
        assetTypeC = new AssetType("assetTypeC",aListAssetTypeParametersA);
        assetTypeD = new AssetType("assetTypeD",aListAssetTypeParametersB);
    }

    @After
    public void tearDown() {
        assetTypeA = null;
        assetTypeB = null;
        assetTypeC = null;
        assetTypeD = null;
        assetTypeParameterA = null;
        aListAssetTypeParametersA = null;
        aListAssetTypeParametersB = null;
    }

    @Test
    public void defaultConst() {
        AssetType temp = new AssetType();
        assertNotNull("New asset type should be created",temp);
    }

    @Test
    public void constWith1Param() {
        AssetType temp = new AssetType("newAssetTypeName");
        assertNotNull("New asset type should be created", temp);
        assertEquals("Expected asset type name: newAssetTypeName","newAssetTypeName",temp.getName());
    }

    @Test
    public void getName() {
        assertEquals("assetTypeB",assetTypeB.getName());
    }

    @Test
    public void getThresholdList() {
        assertNull("Threshold list expected for asset type A: ", assetTypeA.getThresholdList());
        assertEquals("Threshold list expected for asset type C: ", "[]", assetTypeC.getThresholdList().toString());
        assertEquals("Threshold list expected for asset type D: ", "[AssetTypeParameter{name='null', value=null}]", assetTypeD.getThresholdList().toString());
    }

    @Test
    public void constWith2Params() {
        AssetType temp = new AssetType("newAssetTypeName",aListAssetTypeParametersB);
        assertNotNull("New asset type should be created", temp);
        assertEquals("Expected asset type name: newAssetTypeName","newAssetTypeName",temp.getName());
        assertEquals("Threshold list expected: null","[AssetTypeParameter{name='null', value=null}]",temp.getThresholdList().toString());
    }

    @Test
    public void setAndGetID() {
        assetTypeA.setId("007");
        assertEquals("Expected asset type ID: 007", "007", assetTypeA.getId());
    }

    @Test
    public void setName() {
        assetTypeA.setName("AssetTypeA");
        assertEquals("AssetTypeA", assetTypeA.getName());
    }

    @Test
    public void setThresholdList() {
        assetTypeA.setThresholdList(aListAssetTypeParametersA);
        assertEquals("[]", assetTypeA.getThresholdList().toString());
        assetTypeA.setThresholdList(aListAssetTypeParametersB);
        assertEquals("[AssetTypeParameter{name='null', value=null}]",assetTypeA.getThresholdList().toString());
    }

    @Test
    public void assetTypeToString() {
        assertEquals("To string for asset type A:", "AssetType{name='null', thresholdList=null}", assetTypeA.toString());
        assertEquals("To string for asset type B:", "AssetType{name='assetTypeB', thresholdList=[]}", assetTypeB.toString());
        assertEquals("To string for asset type C:", "AssetType{name='assetTypeC', thresholdList=[]}", assetTypeC.toString());
        assertEquals("To string for asset type D:", "AssetType{name='assetTypeD', thresholdList=[AssetTypeParameter{name='null', value=null}]}", assetTypeD.toString());
    }

}
