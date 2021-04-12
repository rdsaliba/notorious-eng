package UnitTests.app.item.parameter;

import app.item.parameter.Parameter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ParameterTest {
    private Parameter parameter;

    @Before
    public void setUp() {
        parameter = new Parameter("TestParameter");
    }

    @After
    public void tearDown() {
        parameter = null;
    }

    @Test
    public void testGetAndSetParamName() {
        parameter.setParamName("newParameter");
        assertEquals("newParameter", parameter.getParamName());
    }
}