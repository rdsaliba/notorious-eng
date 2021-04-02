package UnitTests.rul.models;

import app.item.parameter.BoolParameter;
import app.item.parameter.FloatParameter;
import app.item.parameter.IntParameter;
import app.item.parameter.Parameter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import rul.models.ModelsController;
import rul.models.MultilayerPerceptronModelImpl;
import weka.classifiers.Classifier;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Instances;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class MultilayerPerceptronTest {
    private ModelsController modelsController;
    private BoolParameter showGUIPara;
    private BoolParameter autoBuildPara;
    private BoolParameter decayPara;
    private BoolParameter nominalToBinaryFilterPara;
    private BoolParameter normalizeAttributesPara;
    private BoolParameter normalizeNumericClassPara;
    private BoolParameter resetPara;
    private BoolParameter resumePara;

    private FloatParameter learningRatePara;
    private FloatParameter momentumPara;

    private IntParameter trainingTimePara;
    private IntParameter validationSizePara;
    private IntParameter validationThresholdPara;

    private IntParameter batchSizePara;

    private Map<String, Parameter> parameters;

    @Before
    public void setUp() {
        modelsController = new ModelsController(new MultilayerPerceptronModelImpl());

        showGUIPara = new BoolParameter("Show GUI", true);
        autoBuildPara = new BoolParameter("Auto Build", false);
        decayPara = new BoolParameter("Decay", true);
        nominalToBinaryFilterPara = new BoolParameter("Nominal to Binary Filter", false);
        normalizeAttributesPara = new BoolParameter("Normalize Attributes", false);
        normalizeNumericClassPara = new BoolParameter("Normalize Numeric Class", false);
        resetPara = new BoolParameter("Reset", false);
        resumePara = new BoolParameter("Resume", true);

        learningRatePara = new FloatParameter("Learning Rate", 0.2F);
        momentumPara = new FloatParameter("Momentum", 0.1F);

        trainingTimePara = new IntParameter("Training Time", 300);
        validationSizePara = new IntParameter("Validation Size", 1);
        validationThresholdPara = new IntParameter("Validation Threshold", 10);

        batchSizePara = new IntParameter("Batch Size", 150);

        parameters = new HashMap();
        parameters.put(showGUIPara.getParamName(), showGUIPara);
        parameters.put(autoBuildPara.getParamName(), autoBuildPara);
        parameters.put(decayPara.getParamName(), decayPara);
        parameters.put(nominalToBinaryFilterPara.getParamName(), nominalToBinaryFilterPara);
        parameters.put(normalizeAttributesPara.getParamName(), normalizeAttributesPara);
        parameters.put(normalizeNumericClassPara.getParamName(), normalizeNumericClassPara);
        parameters.put(resetPara.getParamName(), resetPara);
        parameters.put(resumePara.getParamName(), resumePara);
        parameters.put(learningRatePara.getParamName(), learningRatePara);
        parameters.put(momentumPara.getParamName(), momentumPara);
        parameters.put(trainingTimePara.getParamName(), trainingTimePara);
        parameters.put(validationSizePara.getParamName(), validationSizePara);
        parameters.put(validationThresholdPara.getParamName(), validationThresholdPara);
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

        Classifier mlp = new MultilayerPerceptron();
        assertEquals("Should Return Multilayer Perceptron Model", mlp.getClass(),
                modelsController.trainModel(trainData).getClass());
    }

    @Test
    public void updateParam() throws Exception {
        FileReader trainFile = new FileReader("src/test/resources/FD01_Train_RUL.arff");
        Instances trainData = new Instances(trainFile);
        trainData.setClassIndex(trainData.numAttributes() - 1);

        modelsController.setParameters(parameters);
        modelsController.trainModel(trainData);

        assertEquals("Asserting the BatchSize parameter was changed", ((MultilayerPerceptronModelImpl) modelsController.getModelStrategy()).getMultilayerPerceptronObject().getBatchSize(), String.valueOf(batchSizePara.getIntValue()));
        assertEquals("Asserting the ValidationThreshold parameter was changed", ((MultilayerPerceptronModelImpl) modelsController.getModelStrategy()).getMultilayerPerceptronObject().getValidationThreshold(), validationThresholdPara.getIntValue());
        assertEquals("Asserting the LearningRate parameter was changed", ((MultilayerPerceptronModelImpl) modelsController.getModelStrategy()).getMultilayerPerceptronObject().getLearningRate(), learningRatePara.getFloatValue(), 0.1F);
        assertEquals("Asserting the Resume parameter was changed", ((MultilayerPerceptronModelImpl) modelsController.getModelStrategy()).getMultilayerPerceptronObject().getResume(), resumePara.getBoolValue());
        assertEquals("Asserting the ShowGUI parameter was changed", ((MultilayerPerceptronModelImpl) modelsController.getModelStrategy()).getMultilayerPerceptronObject().getGUI(), showGUIPara.getBoolValue());
    }
}
