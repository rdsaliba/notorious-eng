package UnitTests.app.item;

import app.item.Model;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ModelTest {
    private Model model;

    @Before
    public void setUp() {
        model = new Model();
    }

    @After
    public void tearDown() {
        model = null;
    }

    @Test
    public void settersAndGettersForModel() {
        model.setModelName("testModel");
        model.setModelID("testModelID");
        model.setDescription("testModelDescription");
        assertEquals("testModel", model.getModelName());
        assertEquals("testModelID", model.getModelID());
        assertEquals("testModelDescription", model.getDescription());
    }

    @Test
    public void modelToString() {
        assertEquals("Model{modelID=null, model name=null, description=null}", model.toString());
    }
}