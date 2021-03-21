package UnitTests.external;

import app.item.Model;
import external.ModelDAOImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ModelDAOTest {
    private ModelDAOImpl modelDAO;
    private List<Model> modelArrayList;

    @Before
    public void setUp() {
        modelDAO = new ModelDAOImpl();
        modelArrayList = new ArrayList<>();
    }

    @After
    public void tearDown() {
        modelDAO = null;
        modelArrayList = null;
    }

    @Test
    public void getAllModelsTest() {
        modelArrayList = modelDAO.getAllModelsForEvaluation(1);
        assertEquals(8, modelArrayList.size());
        assertEquals(1, modelArrayList.get(0).getModelID());
        assertEquals("Linear", modelArrayList.get(0).getModelName());
        assertEquals("lorem ipsum", modelArrayList.get(0).getDescription());
    }

    @Test
    public void getModelFromAssetTypeTest() {
        int modelID = modelDAO.getModelIDAssociatedWithAssetType("4");
        String modelName = modelDAO.getModelNameAssociatedWithAssetType("4");
        assertEquals(1, modelID);
        assertEquals("Linear", modelName);
    }

    @Test
    public void getModelEvaluationTest() {
        double RMSE = modelDAO.getLatestRMSE(8, 4);
        assertEquals(-1000000,RMSE,0.5);
    }
}