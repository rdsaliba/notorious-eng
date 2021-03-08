package UnitTests.app.item;

import app.item.TrainedModel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TrainedModelTest {
    private TrainedModel trainedModel;

    @Before
    public void setUp() {
        trainedModel = new TrainedModel();
    }

    @After
    public void tearDown() {
        trainedModel = null;
    }

    @Test
    public void defaultConst() {
        TrainedModel temp = new TrainedModel();
        assertNotNull("new trainedModel should be created", temp);
    }

    @Test
    public void setAndGetModelID() {
        trainedModel.setModelID(999);
        assertEquals("new serial number should be '999'", 999, trainedModel.getModelID());
    }

    @Test
    public void setAndGettrainedModelType() {
        trainedModel.setAssetTypeID(888);
        assertEquals("new trainedModelType should be '888'", 888, trainedModel.getAssetTypeID());
    }

    @Test
    public void setAndGetRetrain() {
        trainedModel.setRetrain(true);
        assertTrue("new Location should be 'true'", trainedModel.isRetrain());
    }

    @Test
    public void setAndGetClassifier() {
        trainedModel.setModelClassifier(null);
        assertNull("new description should be 'null'", trainedModel.getModelClassifier());
    }


    @Test
    public void trainedModelToString() {
        assertEquals("TrainedModel{modelID=0, assetTypeID=0, retrain=false, modelClassifier=null}", trainedModel.toString());
    }
}