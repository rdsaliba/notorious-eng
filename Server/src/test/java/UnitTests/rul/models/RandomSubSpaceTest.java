package UnitTests.rul.models;

import app.item.parameter.FloatParameter;
import app.item.parameter.IntParameter;
import app.item.parameter.Parameter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import rul.models.ModelsController;
import rul.models.RandomSubSpaceModelImpl;
import weka.classifiers.Classifier;
import weka.classifiers.meta.RandomSubSpace;
import weka.core.Instances;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class RandomSubSpaceTest {
    private ModelsController modelsController;
    private FloatParameter subSpaceSizePara;
    private IntParameter numExecutionSlotsPara;
    private IntParameter numIterationsPara;
    private IntParameter batchSizePara;
    private Map<String, Parameter> parameters;

    @Before
    public void setUp() {
        modelsController = new ModelsController(new RandomSubSpaceModelImpl());
        subSpaceSizePara = new FloatParameter("SubSpace Size", 0.4F);
        numExecutionSlotsPara = new IntParameter("Number of Execution Slots", 2);
        numIterationsPara = new IntParameter("Number of Iterations", 15);
        batchSizePara = new IntParameter("Batch Size", 333);

        parameters = new HashMap();
        parameters.put(subSpaceSizePara.getParamName(), subSpaceSizePara);
        parameters.put(numExecutionSlotsPara.getParamName(), numExecutionSlotsPara);
        parameters.put(numIterationsPara.getParamName(), numIterationsPara);
        parameters.put(batchSizePara.getParamName(), batchSizePara);
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

        Classifier randomSubSpace = new RandomSubSpace();
        assertEquals("Should Return Random SubSpace Model", randomSubSpace.getClass(),
                modelsController.trainModel(trainData).getClass());
    }

    @Test
    public void updateParam() throws Exception {
        FileReader trainFile = new FileReader("src/test/resources/FD01_Train_RUL.arff");
        Instances trainData = new Instances(trainFile);
        trainData.setClassIndex(trainData.numAttributes() - 1);

        modelsController.setParameters(parameters);
        modelsController.trainModel(trainData);

        assertEquals("Asserting the BatchSize parameter was changed", ((RandomSubSpaceModelImpl) modelsController.getModelStrategy()).getRandomSubSpaceObject().getBatchSize(), String.valueOf(batchSizePara.getIntValue()));
        assertEquals("Asserting the NumExecutionSlots parameter was changed", ((RandomSubSpaceModelImpl) modelsController.getModelStrategy()).getRandomSubSpaceObject().getNumExecutionSlots(), numExecutionSlotsPara.getIntValue());
        assertEquals("Asserting the NumIterations parameter was changed", ((RandomSubSpaceModelImpl) modelsController.getModelStrategy()).getRandomSubSpaceObject().getNumIterations(), numIterationsPara.getIntValue());
        assertEquals("Asserting the SubSpaceSize parameter was changed", ((RandomSubSpaceModelImpl) modelsController.getModelStrategy()).getRandomSubSpaceObject().getSubSpaceSize(), subSpaceSizePara.getFloatValue(), 0.1);
    }
}