package UnitTests.rul.models;

import app.item.parameter.BoolParameter;
import app.item.parameter.IntParameter;
import app.item.parameter.Parameter;
import app.item.parameter.StringParameter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import rul.models.ModelsController;
import rul.models.RandomForestModelImpl;
import weka.classifiers.Classifier;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class RandomForestTest {
    private ModelsController modelsController;
    private BoolParameter storeOutOfBagPredictionsPara;
    private BoolParameter breakTiesRandomlyPara;
    private BoolParameter calcOutBagPara;
    private BoolParameter computeAttributeImportancePara;

    private IntParameter bagSizePercentPara;
    private IntParameter maxDepthPara;
    private IntParameter numExecutionSlotsPara;
    private IntParameter numFeaturesPara;
    private IntParameter numIterationsPara;

    private StringParameter batchSizePara;

    private Map<String, Parameter> parameters;

    @Before
    public void setUp() {
        modelsController = new ModelsController(new RandomForestModelImpl());

        storeOutOfBagPredictionsPara = new BoolParameter("Store out of Bag Predictions", true);
        breakTiesRandomlyPara = new BoolParameter("Break Ties Randomly", true);
        calcOutBagPara = new BoolParameter("Calc out of Bag", true);
        computeAttributeImportancePara = new BoolParameter("Compute Attribute Importance", true);

        bagSizePercentPara = new IntParameter("Bag Size Percent", 50);
        maxDepthPara = new IntParameter("Max Depth", 1);
        numExecutionSlotsPara = new IntParameter("Number of Execution Slots", 2);
        numFeaturesPara = new IntParameter("Number of Features", 1);
        numIterationsPara = new IntParameter("Number of Iterations", 50);

        batchSizePara = new StringParameter("Batch Size", "125");

        parameters = new HashMap();
        parameters.put(storeOutOfBagPredictionsPara.getParamName(), storeOutOfBagPredictionsPara);
        parameters.put(breakTiesRandomlyPara.getParamName(), breakTiesRandomlyPara);
        parameters.put(calcOutBagPara.getParamName(), calcOutBagPara);
        parameters.put(computeAttributeImportancePara.getParamName(), computeAttributeImportancePara);

        parameters.put(bagSizePercentPara.getParamName(), bagSizePercentPara);
        parameters.put(maxDepthPara.getParamName(), maxDepthPara);
        parameters.put(numExecutionSlotsPara.getParamName(), numExecutionSlotsPara);
        parameters.put(numFeaturesPara.getParamName(), numFeaturesPara);
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
        Instances trainData  = new Instances(trainFile);
        trainData.setClassIndex(trainData.numAttributes() - 1);

        Classifier randomForest = new RandomForest();
        assertEquals("Should Return Random Forest Model", randomForest.getClass(), modelsController.trainModel(trainData).getClass());
    }


    @Test
    public void updateParam() throws Exception
    {
        FileReader trainFile = new FileReader("src/test/resources/FD01_Train_RUL.arff");
        Instances  trainData = new Instances(trainFile);
        trainData.setClassIndex(trainData.numAttributes() - 1);

        modelsController.setParameters(parameters);
        modelsController.trainModel(trainData);

        assertEquals("Asserting the BatchSize parameter was changed", ((RandomForestModelImpl) modelsController.getModelStrategy()).getRandomForestObject().getBatchSize(), batchSizePara.getStringValue());
        assertEquals("Asserting the NumIterations parameter was changed",((RandomForestModelImpl) modelsController.getModelStrategy()).getRandomForestObject().getNumIterations(), numIterationsPara.getIntValue());
        assertEquals("Asserting the MaxDepth parameter was changed",((RandomForestModelImpl) modelsController.getModelStrategy()).getRandomForestObject().getMaxDepth(), maxDepthPara.getIntValue());
        assertEquals("Asserting the BreakTiesRandomly parameter was changed",((RandomForestModelImpl) modelsController.getModelStrategy()).getRandomForestObject().getBreakTiesRandomly(), breakTiesRandomlyPara.getBoolValue());
        assertEquals("Asserting the NumberOfFeatures parameter was changed",((RandomForestModelImpl) modelsController.getModelStrategy()).getRandomForestObject().getNumFeatures(), numFeaturesPara.getIntValue());
    }
}
