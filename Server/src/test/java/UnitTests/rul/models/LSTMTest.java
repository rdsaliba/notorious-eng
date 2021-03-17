package UnitTests.rul.models;

import app.item.parameter.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import rul.models.LSTMModelImpl;
import rul.models.ModelsController;
import weka.classifiers.functions.Dl4jMlpClassifier;
import weka.core.Instances;
import weka.dl4j.NeuralNetConfiguration;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class LSTMTest
{
    private ModelsController modelsController;
    //BoolParameters
    private BoolParameter resumePara;
    private BoolParameter filterModePara;
    private BoolParameter doNotClearFileSystemCachePara;
    private BoolParameter minimizeObjectivePara;
    //IntParameters
    private IntParameter numberOfEpochPara;
    private IntParameter numberOfGPUPara;
    private IntParameter avgFrequencyPara;
    private IntParameter queueSizePara;
    //List & StringParameters
    private ListParameter cacheModePara; //options: NONE, MEMORY, FILESYSTEM
    private ListParameter optimizationAlgoPara; //options: STOCHASTIC_GRADIENT_DESCENT, LINE_GRADIENT_DESCENT,
    private ListParameter weightInitPara;     //options: XAVIER, RELU, IDENTITY, NORMAL, UNIFORM, ZERO, DISTRIBUTION
    private StringParameter batchSizePara;
    //FloatParameter
    private FloatParameter learningRatePara;

    private Map<String, Parameter> parameters;

    private Dl4jMlpClassifier dl4jMlpClassifier;
    private NeuralNetConfiguration neuralNetConfiguration;

    @Before
    public void setUp(){
        modelsController = new ModelsController(new LSTMModelImpl());
        resumePara = new BoolParameter("Resume", true);
        filterModePara = new BoolParameter("Filter Mode", true);
        doNotClearFileSystemCachePara = new BoolParameter("Do not Clear File System Cache", true);
        minimizeObjectivePara = new BoolParameter("Minimize Objective", false);

        numberOfEpochPara = new IntParameter("Number of Epoch", 1);
        numberOfGPUPara = new IntParameter("Number of GPU", 2);
        avgFrequencyPara = new IntParameter("Average Frequency", 5);
        queueSizePara = new IntParameter("Queue Size", 1);

        optimizationAlgoPara = new ListParameter("Optimization Algorithm", new ArrayList<>(Arrays.asList("STOCHASTIC_GRADIENT_DESCENT", "LINE_GRADIENT_DESCENT")), "LINE_GRADIENT_DESCENT");
        weightInitPara = new ListParameter("Weight Initializer", new ArrayList<>(Arrays.asList("XAVIER", "RELU", "IDENTITY", "NORMAL", "UNIFORM", "ZERO", "DISTRIBUTION")), "NORMAL");
        cacheModePara = new ListParameter("Cache Mode", new ArrayList<>(Arrays.asList("NONE", "MEMORY", "FILESYSTEM")), "NONE");
        batchSizePara = new StringParameter("Batch Size", "5");

        learningRatePara = new FloatParameter("Learning Rate", 0.002F);

        parameters = new HashMap();
        parameters.put(batchSizePara.getParamName(), batchSizePara);
        parameters.put(cacheModePara.getParamName(), cacheModePara);
        parameters.put(weightInitPara.getParamName(), weightInitPara);
        parameters.put(optimizationAlgoPara.getParamName(), optimizationAlgoPara);
        parameters.put(queueSizePara.getParamName(), queueSizePara);
        parameters.put(avgFrequencyPara.getParamName(), avgFrequencyPara);
        parameters.put(numberOfGPUPara.getParamName(), numberOfGPUPara);
        parameters.put(numberOfEpochPara.getParamName(), numberOfEpochPara);
        parameters.put(minimizeObjectivePara.getParamName(), minimizeObjectivePara);
        parameters.put(doNotClearFileSystemCachePara.getParamName(), doNotClearFileSystemCachePara);
        parameters.put(filterModePara.getParamName(), filterModePara);
        parameters.put(resumePara.getParamName(), resumePara);
        parameters.put(learningRatePara.getParamName(), learningRatePara);

    }

    @After
    public void tearDown() {
        modelsController = null;
    }

    @Test
    public void trainModel() throws Exception {
        FileReader file= new FileReader("src/test/resources/FD01_Train_RUL.arff");
        Instances set = new Instances(file);
        set.setClassIndex(set.numAttributes()-1);
        Dl4jMlpClassifier LSTM = new Dl4jMlpClassifier();
        assertEquals("Should Return Linear Regression Model", LSTM.getClass(), modelsController.trainModel(set).getClass());

    }

    @Test
    public void updateParam() throws Exception
    {
        FileReader trainFile = new FileReader("src/test/resources/FD01_Train_RUL.arff");
        Instances  trainData = new Instances(trainFile);
        trainData.setClassIndex(trainData.numAttributes() - 1);

        modelsController.setParameters(parameters);
        modelsController.trainModel(trainData);

        assertEquals("Asserting the BatchSize parameter was changed", ((LSTMModelImpl) modelsController.getModelStrategy()).getLSTMObject().getBatchSize(), batchSizePara.getStringValue());
        assertEquals("Asserting the CacheMode parameter was changed",((LSTMModelImpl) modelsController.getModelStrategy()).getLSTMObject().getCacheMode().toString(), cacheModePara.getSelectedValue());
        assertEquals("Asserting the WeightInit parameter was changed",((LSTMModelImpl) modelsController.getModelStrategy()).getLSTMNeuralObject().getWeightInit().toString(), weightInitPara.getSelectedValue());
        assertEquals("Asserting the OptimizationAlgo parameter was changed",((LSTMModelImpl) modelsController.getModelStrategy()).getLSTMNeuralObject().getOptimizationAlgo().toString(), optimizationAlgoPara.getSelectedValue());
        assertEquals("Asserting the QueueSize parameter was changed",((LSTMModelImpl) modelsController.getModelStrategy()).getLSTMObject().getQueueSize(), queueSizePara.getIntValue());
    }
}
