/* Second strategy design pattern and implementation of Model Strategy
 *
 * @author Khaled
 * @last_edit 03/11/2021
 */

package rul.models;

import app.item.parameter.*;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.weights.WeightInit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.classifiers.Classifier;
import weka.classifiers.functions.Dl4jMlpClassifier;
import weka.core.Instances;
import weka.dl4j.NeuralNetConfiguration;
import weka.dl4j.activations.ActivationRReLU;
import weka.dl4j.activations.ActivationSwish;
import weka.dl4j.enums.CacheMode;
import weka.dl4j.layers.LSTM;
import weka.dl4j.layers.OutputLayer;
import weka.dl4j.lossfunctions.LossMSE;
import weka.dl4j.updater.Adam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LSTMModelImpl extends ModelStrategy
{
    //Default Parameters
    private static final boolean RESUME_PARAM_DEFAULT = false;
    private static final boolean FILTER_MODE_PARAM_DEFAULT = false;
    private static final boolean DO_NOT_CLEAR_FILE_SYSTEM_CACHE_PARAM_DEFAULT = false;
    private static final boolean MINIMIZE_OBJECTIVE_PARA_DEFAULT = true;

    private static final int NUMBER_OF_EPOCH_PARAM_DEFAULT = 10;
    private static final int QUEUE_SIZE_PARAM_DEFAULT = 0;
    private static final int NUMBER_OF_GPU_PARAM_DEFAULT = 1;
    private static final int AVG_FREQUENCY_PARAM_DEFAULT = 10;

    private static final String BATCH_SIZE_PARAM_DEFAULT = "100";
    private static final String CACHE_MODE_PARAM_DEFAULT = "MEMORY";
    private static final String OPTIMIZATION_ALGO_PARAM_DEFAULT = "STOCHASTIC_GRADIENT_DESCENT";
    private static final String WEIGHT_INIT_PARAM_DEFAULT = "XAVIER";

    private static final float LEARNING_RATE_PARAM_DEFAULT = 0.001F;

    //Parameter objects
    private BoolParameter resumePara;
    private BoolParameter filterModePara;
    private BoolParameter doNotClearFileSystemCachePara;
    private BoolParameter minimizeObjectivePara;

    private IntParameter numberOfEpochPara;
    private IntParameter numberOfGPUPara;
    private IntParameter avgFrequencyPara;
    private IntParameter queueSizePara;

    private ListParameter cacheModePara; //options: NONE, MEMORY, FILESYSTEM
    private ListParameter optimizationAlgoPara; //options: STOCHASTIC_GRADIENT_DESCENT, LINE_GRADIENT_DESCENT,
    private ListParameter weightInitPara;     //options: XAVIER, RELU, IDENTITY, NORMAL, UNIFORM, ZERO, DISTRIBUTION
    private StringParameter batchSizePara;

    private FloatParameter learningRatePara;

    private Dl4jMlpClassifier dl4jMlpClassifier;
    private NeuralNetConfiguration neuralNetConfiguration;

    public LSTMModelImpl()
    {
        resumePara = new BoolParameter("Resume", RESUME_PARAM_DEFAULT);
        filterModePara = new BoolParameter("Filter Mode", FILTER_MODE_PARAM_DEFAULT);
        doNotClearFileSystemCachePara = new BoolParameter("Do not Clear File System Cache", DO_NOT_CLEAR_FILE_SYSTEM_CACHE_PARAM_DEFAULT);
        minimizeObjectivePara = new BoolParameter("Minimize Objective", MINIMIZE_OBJECTIVE_PARA_DEFAULT);

        numberOfEpochPara = new IntParameter("Number of Epoch", NUMBER_OF_EPOCH_PARAM_DEFAULT);
        queueSizePara = new IntParameter("Queue Size", QUEUE_SIZE_PARAM_DEFAULT);
        numberOfGPUPara = new IntParameter("Number of GPU", NUMBER_OF_GPU_PARAM_DEFAULT);
        avgFrequencyPara = new IntParameter("Average Frequency", AVG_FREQUENCY_PARAM_DEFAULT);

        optimizationAlgoPara = new ListParameter("Optimization Algorithm", new ArrayList<>(Arrays.asList(OPTIMIZATION_ALGO_PARAM_DEFAULT, "LINE_GRADIENT_DESCENT")), OPTIMIZATION_ALGO_PARAM_DEFAULT);
        weightInitPara = new ListParameter("Weight Initializer", new ArrayList<>(Arrays.asList(WEIGHT_INIT_PARAM_DEFAULT, "RELU", "IDENTITY", "NORMAL", "UNIFORM", "ZERO", "DISTRIBUTION")), WEIGHT_INIT_PARAM_DEFAULT);
        cacheModePara = new ListParameter("Cache Mode", new ArrayList<>(Arrays.asList("NONE", CACHE_MODE_PARAM_DEFAULT, "FILESYSTEM")), CACHE_MODE_PARAM_DEFAULT);
        batchSizePara = new StringParameter("Batch Size", BATCH_SIZE_PARAM_DEFAULT);

        learningRatePara = new FloatParameter("Learning Rate", LEARNING_RATE_PARAM_DEFAULT);

        addParameter(resumePara);
        addParameter(filterModePara);
        addParameter(doNotClearFileSystemCachePara);
        addParameter(minimizeObjectivePara);

        addParameter(numberOfEpochPara);
        addParameter(queueSizePara);
        addParameter(numberOfGPUPara);
        addParameter(avgFrequencyPara);

        addParameter(optimizationAlgoPara);
        addParameter(weightInitPara);
        addParameter(cacheModePara);
        addParameter(batchSizePara);

        addParameter(learningRatePara);
    }

    static Logger logger = LoggerFactory.getLogger(LSTMModelImpl.class);

    /**
     * This function takes the filtered training dataset, builds a Neural Network using Weka's Deep Learning 4 Java plugin
     * and trains and returns an LSTM model.
     *
     * @author Khaled
     */
    @Override
    public Classifier trainModel(Instances firstTrain)
    {
        firstTrain.setClassIndex(firstTrain.numAttributes() - 1);

        Instances trainDataset = LinearRegressionModelImpl.removeInstances(firstTrain);
        trainDataset.setClassIndex(trainDataset.numAttributes() - 1);

        //DL4J Recurrent Neural Network (RNN)
        dl4jMlpClassifier = new Dl4jMlpClassifier();
        neuralNetConfiguration = new NeuralNetConfiguration();

        try
        {
            //Setting Parameters for the model
            dl4jMlpClassifier.setResume(((BoolParameter) getParameters().get(resumePara.getParamName())).getBoolValue());
            dl4jMlpClassifier.setFilterMode(((BoolParameter) getParameters().get(filterModePara.getParamName())).getBoolValue());
            dl4jMlpClassifier.setDoNotClearFilesystemCache(((BoolParameter) getParameters().get(doNotClearFileSystemCachePara.getParamName())).getBoolValue());
            neuralNetConfiguration.setMinimize(((BoolParameter) getParameters().get(minimizeObjectivePara.getParamName())).getBoolValue());

            dl4jMlpClassifier.setNumEpochs(((IntParameter) getParameters().get(numberOfEpochPara.getParamName())).getIntValue());
            dl4jMlpClassifier.setQueueSize(((IntParameter) getParameters().get(queueSizePara.getParamName())).getIntValue());
            dl4jMlpClassifier.setNumGPUs(((IntParameter) getParameters().get(numberOfGPUPara.getParamName())).getIntValue());
            dl4jMlpClassifier.setParameterAveragingFrequency(((IntParameter) getParameters().get(avgFrequencyPara.getParamName())).getIntValue());

            neuralNetConfiguration.setOptimizationAlgo(OptimizationAlgorithm.valueOf(((ListParameter) getParameters().get(optimizationAlgoPara.getParamName())).getSelectedValue()));
            dl4jMlpClassifier.setCacheMode(CacheMode.valueOf(((ListParameter) getParameters().get(cacheModePara.getParamName())).getSelectedValue()));
            neuralNetConfiguration.setWeightInit(WeightInit.valueOf((((ListParameter) getParameters().get(weightInitPara.getParamName())).getSelectedValue())));
            dl4jMlpClassifier.setBatchSize(((StringParameter) getParameters().get(batchSizePara.getParamName())).getStringValue());

            Adam adam = new Adam();
            neuralNetConfiguration.setUpdater(adam);
            adam.setLearningRate(((FloatParameter) getParameters().get(learningRatePara.getParamName())).getFloatValue());
            dl4jMlpClassifier.setNeuralNetConfiguration(neuralNetConfiguration);

            //Set Layers
            LSTM lstmLayer1 = new LSTM();
            lstmLayer1.setNOut(200);                        //First layer outputs (input for 2nd layer)

            //Activation and Gate functions:
            lstmLayer1.setActivationFunction(new ActivationRReLU());
            lstmLayer1.setGateActivationFn(new ActivationRReLU());

            //Configure output layer
            OutputLayer outLayer = new OutputLayer();           //Last layer must always be the last layer
            outLayer.setLossFn(new LossMSE());                  //Loss function is Mean Square Error
            outLayer.setActivationFunction(new ActivationSwish());      //Activation function
            outLayer.setNOut(1);                                        //Single output
            dl4jMlpClassifier.setLayers(lstmLayer1, outLayer);          //Two layers for now, LSTM and the output

            //train with the DL4J classifier
            dl4jMlpClassifier.buildClassifier(trainDataset);
        }

        catch(Exception e)
        {
            logger.error("Exception: ", e);
        }

        setClassifier(dl4jMlpClassifier);
        return dl4jMlpClassifier;
    }

    @Override
    public Map<String, Parameter> getDefaultParameters()
    {
        BoolParameter resumeParaDefault                    = new BoolParameter("Resume", RESUME_PARAM_DEFAULT);
        BoolParameter filterModeParaDefault                = new BoolParameter("Filter Mode", FILTER_MODE_PARAM_DEFAULT);
        BoolParameter doNotClearFileSystemCacheParaDefault = new BoolParameter("Do not Clear File System Cache", DO_NOT_CLEAR_FILE_SYSTEM_CACHE_PARAM_DEFAULT);
        BoolParameter minimizeObjectiveParaDefault         = new BoolParameter("Minimize Objective", MINIMIZE_OBJECTIVE_PARA_DEFAULT);

        IntParameter numberOfGPUParaDefault   = new IntParameter("Number of GPU", NUMBER_OF_GPU_PARAM_DEFAULT);
        IntParameter avgFrequencyParaDefault  = new IntParameter("Average Frequency", AVG_FREQUENCY_PARAM_DEFAULT);
        IntParameter numberOfEpochParaDefault = new IntParameter("Number of Epoch", NUMBER_OF_EPOCH_PARAM_DEFAULT);
        IntParameter queueSizeParaDefault     = new IntParameter("Queue Size", QUEUE_SIZE_PARAM_DEFAULT);

        ListParameter   cacheModeParaDefault        = new ListParameter("Cache Mode", new ArrayList<>(Arrays.asList("NONE", CACHE_MODE_PARAM_DEFAULT, "FILESYSTEM")), CACHE_MODE_PARAM_DEFAULT);
        ListParameter   optimizationAlgoParaDefault = new ListParameter("Optimization Algorithm", new ArrayList<>(Arrays.asList(OPTIMIZATION_ALGO_PARAM_DEFAULT, "LINE_GRADIENT_DESCENT")), OPTIMIZATION_ALGO_PARAM_DEFAULT);
        ListParameter   weightInitParaDefault       = new ListParameter("Weight Initializer", new ArrayList<>(Arrays.asList(WEIGHT_INIT_PARAM_DEFAULT, "RELU", "IDENTITY", "NORMAL", "UNIFORM", "ZERO", "DISTRIBUTION")), WEIGHT_INIT_PARAM_DEFAULT);
        StringParameter batchSizeParaDefault        = new StringParameter("Batch Size", BATCH_SIZE_PARAM_DEFAULT);

        FloatParameter learningRateParaDefault = new FloatParameter("Learning Rate", LEARNING_RATE_PARAM_DEFAULT);

        Map<String, Parameter> parameters = new HashMap<>();
        parameters.put(resumeParaDefault.getParamName(), resumeParaDefault);
        parameters.put(filterModeParaDefault.getParamName(), filterModeParaDefault);
        parameters.put(doNotClearFileSystemCacheParaDefault.getParamName(), doNotClearFileSystemCacheParaDefault);
        parameters.put(minimizeObjectiveParaDefault.getParamName(), minimizeObjectiveParaDefault);

        parameters.put(numberOfGPUParaDefault.getParamName(), numberOfGPUParaDefault);
        parameters.put(avgFrequencyParaDefault.getParamName(), avgFrequencyParaDefault);
        parameters.put(numberOfEpochParaDefault.getParamName(), numberOfEpochParaDefault);
        parameters.put(queueSizeParaDefault.getParamName(), queueSizeParaDefault);

        parameters.put(cacheModeParaDefault.getParamName(), cacheModeParaDefault);
        parameters.put(optimizationAlgoParaDefault.getParamName(), optimizationAlgoParaDefault);
        parameters.put(weightInitParaDefault.getParamName(), weightInitParaDefault);
        parameters.put(batchSizeParaDefault.getParamName(), batchSizeParaDefault);

        parameters.put(learningRateParaDefault.getParamName(), learningRateParaDefault);

        return parameters;
    }

    public Dl4jMlpClassifier getLSTMObject()
    {
        return this.dl4jMlpClassifier;
    }

    public NeuralNetConfiguration getLSTMNeuralObject()
    {
        return this.neuralNetConfiguration;
    }
}
