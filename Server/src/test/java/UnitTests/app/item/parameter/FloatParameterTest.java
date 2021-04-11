package UnitTests.app.item.parameter;

import app.item.parameter.FloatParameter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FloatParameterTest {
    private FloatParameter floatParameter;

    @Before
    public void setUp() {
        floatParameter = new FloatParameter("TestFloatParameter", 1.0f);
    }

    @After
    public void tearDown() throws Exception {
        floatParameter = null;
    }

    @Test
    public void testGetAndSetFloatValue() {
        floatParameter.setFloatValue(2.5f);
        assertEquals(2.5f, floatParameter.getFloatValue(), 0.01f);
    }
}