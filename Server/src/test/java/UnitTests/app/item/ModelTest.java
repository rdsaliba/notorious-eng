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
    public void emptyConstructorTest() {
        model = new Model();
        assertEquals("Model{modelID=0, model name=null, description=null}", model.toString());
    }

    @Test
    public void parameterizedConstructorTest() {
        model = new Model("model 1", 1, "new model");
        assertEquals("Model{modelID=1, model name=model 1, description=new model}", model.toString());
    }

    @Test
    public void settersAndGettersForModel() {
        model.setModelName("testModel");
        model.setModelID(1);
        model.setDescription("testModelDescription");
        assertEquals("testModel", model.getModelName());
        assertEquals(1, model.getModelID());
        assertEquals("testModelDescription", model.getDescription());
    }


}