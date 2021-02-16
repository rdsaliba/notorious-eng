package UnitTests.app.item;

import app.item.AssetTypeParameter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AssetTypeParameterTest {
    private AssetTypeParameter assetTypeParameterA;
    private AssetTypeParameter assetTypeParameterB;

    @Before
    public void setup() {
        assetTypeParameterA = new AssetTypeParameter();
        assetTypeParameterB = new AssetTypeParameter("aTypeParamTest", 15.0);
    }

    @After
    public void tearDown() {
        assetTypeParameterA = null;
    }

    @Test
    public void defaultConst() {
        AssetTypeParameter temp = new AssetTypeParameter();
        assertNotNull("A new asset type parameter should have created", temp);
    }

    @Test
    public void getName() {
        assertNull("aTypeParamA", assetTypeParameterA.getName());
        assertEquals("aTypeParamB", "aTypeParamTest", assetTypeParameterB.getName());
    }

    @Test
    public void getValue() {
        assertNull("aTypeParamA", assetTypeParameterA.getValue());
        assertEquals("aTypeParamB", 15.0, assetTypeParameterB.getValue(),1);
    }

    @Test
    public void constWithAllParams() {
        AssetTypeParameter temp = new AssetTypeParameter("aTypeParamTest", 10.0);
        assertNotNull("A new asset type parameter should have created", temp);
        assertEquals("New asset type parameter name", "aTypeParamTest", temp.getName());
        assertEquals("New asset type parameter value", 10, temp.getValue(),1);
    }

    @Test
    public void setName() {
        assetTypeParameterA.setName("aTypeParamTestA");
        assertEquals("aTypeParamA", "aTypeParamTestA", assetTypeParameterA.getName());
    }

    @Test
    public void setValue() {
        assetTypeParameterA.setValue(20.0);
        assertEquals("aTypeParamA", 20, assetTypeParameterA.getValue(),1);
    }

    @Test
    public void aTypeParamToString() {
        assertEquals("AssetTypeParameter{name='null', value=null}", assetTypeParameterA.toString());
        assertEquals("AssetTypeParameter{name='aTypeParamTest', value=15.0}", assetTypeParameterB.toString());
    }
}
