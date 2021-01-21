package app.item;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MeasurementTest {
    private Measurement measurement;

    @Before
    public void setUp() {
        measurement = new Measurement();
    }

    @Test
    public void DefaultConst() {
        Measurement temp = new Measurement();
        assertNotNull("New asset should be created",temp);
    }

}
