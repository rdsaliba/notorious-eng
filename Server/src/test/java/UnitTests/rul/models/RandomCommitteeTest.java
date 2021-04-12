package UnitTests.rul.models;

import app.item.parameter.IntParameter;
import app.item.parameter.Parameter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import rul.models.ModelsController;
import rul.models.RandomCommitteeModelImpl;
import weka.classifiers.Classifier;
import weka.classifiers.meta.RandomCommittee;
import weka.core.Instances;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class RandomCommitteeTest {
    private ModelsController modelsController;
    private IntParameter numExecutionSlotsPara;
    private IntParameter numIterationsPara;
    private IntParameter batchSizePara;
    private Map<String, Parameter> parameters;

    @Before
    public void setUp() {
        modelsController = new ModelsController(new RandomCommitteeModelImpl());
        numExecutionSlotsPara = new IntParameter("Number of Execution Slots", 2);
        numIterationsPara = new IntParameter("Number of Iterations", 5);
        batchSizePara = new IntParameter("Batch Size", 123);

        parameters = new HashMap();
        parameters.put(batchSizePara.getParamName(), batchSizePara);
        parameters.put(numExecutionSlotsPara.getParamName(), numExecutionSlotsPara);
        parameters.put(numIterationsPara.getParamName(), numIterationsPara);
    }

    @After
    public void tearDown() {
        modelsController = null;
    }

    @Test
    public void trainModel() throws Exception {
        FileReader trainFile = new FileReader("src/test/resources/FD01_Train_RUL.arff");
        Instances trainData = new Instances(trainFile);
        trainData.setClassIndex(trainData.numAttributes() - 1);

        Classifier randomCommittee = new RandomCommittee();
        assertEquals("Should Return Random Committee Model", randomCommittee.getClass(), modelsController.trainModel(trainData).getClass());
    }

    @Test
    public void updateParam() throws Exception {
        FileReader trainFile = new FileReader("src/test/resources/FD01_Train_RUL.arff");
        Instances trainData = new Instances(trainFile);
        trainData.setClassIndex(trainData.numAttributes() - 1);

        modelsController.setParameters(parameters);
        modelsController.trainModel(trainData);

        assertEquals("Asserting the BatchSize parameter was changed", ((RandomCommitteeModelImpl) modelsController.getModelStrategy()).getRandomCommitteeObject().getBatchSize(), String.valueOf(batchSizePara.getIntValue()));
        assertEquals("Asserting the NumExecutionSlots parameter was changed", ((RandomCommitteeModelImpl) modelsController.getModelStrategy()).getRandomCommitteeObject().getNumExecutionSlots(), numExecutionSlotsPara.getIntValue());
        assertEquals("Asserting the NumIterations parameter was changed", ((RandomCommitteeModelImpl) modelsController.getModelStrategy()).getRandomCommitteeObject().getNumIterations(), numIterationsPara.getIntValue());
    }

    @Test
    public void getDefaultParameters()  {
        assertNotNull("DefaultParameters exist", modelsController.getModelStrategy().getDefaultParameters());
        assertTrue("Should contain 3 default parameters", (modelsController.getModelStrategy()).getDefaultParameters().size() == 3);

    }

}
