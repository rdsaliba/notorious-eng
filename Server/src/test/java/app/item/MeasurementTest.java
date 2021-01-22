package app.item;

import org.junit.*;

import static org.junit.Assert.*;

public class MeasurementTest {
    private Measurement measurementA;
    private Measurement measurementB;

    @Before
    public void setUp() {
        measurementA = new Measurement();
        measurementB = new Measurement(1,2);
    }

    @After
    public void tearDown() {
        measurementA = null;
        measurementB = null;
    }

    @Test
    public void defaultConst() {
        Measurement temp = new Measurement();
        assertNotNull("New measurement should be created",temp);
    }

    @Test
    public void getTime() {
        assertEquals("Time of the measurement B should be 1",1,measurementB.getTime());
    }

    @Test
    public void getValue() {
        assertEquals("Time of the measurement B should be 2",2,measurementB.getValue(),1);
    }

    @Test
    public void constWithAllParams() {
        Measurement temp = new Measurement(5, 10);
        assertNotNull("New measurement should be created",temp);
        assertEquals("Time of the measurement should be 5",5,temp.getTime());
        assertEquals("Value of the measurement should be 5",10,temp.getValue(),1);
    }

    @Test
    public void setTime(){
        measurementA.setTime(10);
        assertEquals("Time of the measurement A should be set to 10",10, measurementA.getTime());
    }

    @Test
    public void setValue() {
        measurementA.setValue(100);
        assertEquals("Value of the measurement A should be set to 100",100, measurementA.getValue(),1);
    }

    @Test
    public void measurementToString() {
        assertEquals("Measurement{time=1, measurement value=2.0}",measurementB.toString());
    }
}
