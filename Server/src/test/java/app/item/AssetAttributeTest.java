package app.item;

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
        assetAttribute.addMeasurement(2, 234.423);
        assertTrue("Asserting specific measurement is returned ", assetAttribute.getMeasurements(1) == 0.5);
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
        assertNull("Default name value should be nulled", assetAttribute.getName());
    }

    @Test
    public void setName() {
        assetAttribute.setName("foo");
        assertEquals("New name should be foo", "foo", assetAttribute.getName());
    }
}