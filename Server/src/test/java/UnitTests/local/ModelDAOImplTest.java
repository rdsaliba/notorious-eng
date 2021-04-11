package UnitTests.local;

import app.item.TrainedModel;
import local.ModelDAOImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class ModelDAOImplTest {
    private ModelDAOImpl modelDAO;
    @Before
    public void setUp() {
        modelDAO = new ModelDAOImpl();
    }

    @After
    public void tearDown() {
        modelDAO = null;
    }

    @Test
    public void convertToByteString() {
    }

    @Test
    public void convertFromByteString() {
    }

    @Test
    public void getModelNameFromModelID() {
        String name = modelDAO.getModelNameFromModelID("3");
        assertEquals("RandomForest",name);
    }

    @Test
    public void getModelsToTrain() {
        ArrayList<TrainedModel> trainedModels = modelDAO.getModelsToTrain();
        assertEquals(3, trainedModels.size());
    }

    @Test
    public void setModelToTrain() {
    }

    @Test
    public void getModelsByAssetTypeID() {
        //Continue with
        TrainedModel tm = modelDAO.getModelsByAssetTypeID("1",2);
    }

    @Test
    public void createTrainedModelFromResultSet() {
    }

    @Test
    public void updateEvaluationRMSE() {
    }
}