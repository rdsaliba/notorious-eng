package UnitTests.app.item.parameter;

import app.item.parameter.IntParameter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class IntParameterTest {
    private IntParameter intParameter;

    @Before
    public void setUp() {
        intParameter = new IntParameter("TestIntParameter", 1);
    }

    @After
    public void tearDown() throws Exception {
        intParameter = null;
    }

    @Test
    public void testGetAndSetFloatValue() {
        intParameter.setIntValue(2);
        assertEquals(2, intParameter.getIntValue());
    }
}