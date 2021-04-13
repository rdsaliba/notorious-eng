package UnitTests.external;

import app.item.Measurement;
import external.AttributeDAOImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class AttributeDAOImplTest {
    private AttributeDAOImpl attributeDAO;
    private List<Measurement> measurementArrayList;

    @Before
    public void setUp() {
        attributeDAO = new AttributeDAOImpl();
        measurementArrayList = new ArrayList<>();
    }

    @After
    public void tearDown() {
        attributeDAO = null;
        measurementArrayList = null;
    }

    @Test
    public void getLastXMeasurementsByAssetIDAndAttributeIDTest() {
        measurementArrayList = attributeDAO.getLastXMeasurementsByAssetIDAndAttributeID("1", "1", 1);
        assertEquals(1, measurementArrayList.size());
        assertEquals(192, measurementArrayList.get(0).getTime());
    }
}