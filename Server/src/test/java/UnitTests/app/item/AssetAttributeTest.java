package UnitTests.app.item;

import app.item.AssetAttribute;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AssetAttributeTest {
    private AssetAttribute assetAttribute;

    @Before
    public void setUp() {
        assetAttribute = new AssetAttribute();
    }

    @After
    public void tearDown() {
        assetAttribute = null;
    }

    @Test
    public void addMeasurement() {
        assertTrue("empty measurements list", assetAttribute.getMeasurements().isEmpty());
        assetAttribute.addMeasurement(1, 0.5);
        assertEquals("one element in the measurement map", 1, assetAttribute.getMeasurements().size());
    }

    @Test
    public void getAllMeasurements() {
        assertTrue("empty measurements list", assetAttribute.getMeasurements().isEmpty());
    }

    @Test
    public void getOneMeasurement() {
        assetAttribute.addMeasurement(1, 0.5);
        assertEquals("Asserting specific measurement is returned ", 0.5, assetAttribute.getMeasurements(1), 0.0);
        assertNull("Trying to get the measurement at a time that isn't valid should return null", assetAttribute.getMeasurements(10));
    }

    @Test
    public void getId() {
        assertEquals("Default int value of get int should be 0", 0, assetAttribute.getId());
    }

    @Test
    public void setId() {
        assetAttribute.setId(123);
        assertEquals("New id should be 123", 123, assetAttribute.getId());
    }

    @Test
    public void getName() {
        assertNull("Default name value should be null", assetAttribute.getName());
    }

    @Test
    public void setName() {
        assetAttribute.setName("foo");
        assertEquals("New name should be foo", "foo", assetAttribute.getName());
    }

    @Test
    public void assetAttributeToString() {
        assertEquals("AssetAttribute{id=0, name='null', measurements=[]}", assetAttribute.toString());
        assetAttribute.setId(7);
        assetAttribute.setName("assetAttribute");
        assertEquals("AssetAttribute{id=7, name='assetAttribute', measurements=[]}", assetAttribute.toString());
    }
}