package UnitTests.app.item.parameter;

import app.item.parameter.BoolParameter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;

public class BoolParameterTest {
    private BoolParameter boolParameter;

    @Before
    public void setUp() throws Exception {
        boolParameter = new BoolParameter("TestBoolParameter", true);
    }

    @After
    public void tearDown() throws Exception {
        boolParameter = null;
    }

    @Test
    public void testGetAndSetBoolValue() {
        boolParameter.setBoolValue(false);
        assertFalse(boolParameter.getBoolValue());
    }
}