/* Second strategy design pattern and implementation of Model Strategy
 *
 * @author Khaled
 * @last_edit 12/28/2020
 */

package rul.models;

import app.item.parameter.*;
import org.deeplearning4j.nn.weights.WeightInit;
import weka.classifiers.Classifier;
import weka.classifiers.functions.Dl4jMlpClassifier;
import weka.core.Instances;
import weka.dl4j.NeuralNetConfiguration;
import weka.dl4j.activations.ActivationRReLU;
import weka.dl4j.activations.ActivationSwish;
import weka.dl4j.layers.LSTM;
import weka.dl4j.layers.OutputLayer;
import weka.dl4j.lossfunctions.LossMSE;
import weka.dl4j.updater.Adam;
import weka.dl4j.updater.Sgd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import static org.deeplearning4j.nn.api.OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT;

public class LSTMModelImpl extends ModelStrategy
{

    private final String BATCH_SIZE_PARAM_DEFAULT = "100";
    private final int NUMBER_OF_EPOCH_PARAM_DEFAULT = 10;
    private final String CACHE_MODE_PARAM_DEFAULT = "MEMORY";
    private final int QUEUE_SIZE_PARAM_DEFAULT = 0;
    private final boolean RESUME_PARAM_DEFAULT = false;
    private final boolean FILTER_MODE_PARAM_DEFAULT = false;
    private final boolean DO_NOT_CLEAR_FILE_SYSTEM_CACHE_PARAM_DEFAULT = false;
    private final int NUMBER_OF_GPU_PARAM_DEFAULT = 1;
    private final String OPTIMIZATION_ALGO_PARAM_DEFAULT = "STOCHASTIC_GRADIENT_DESCENT";
    private final String WEIGHT_INIT_PARAM_DEFAULT = "XAVIER";
    private final int AVG_FREQUENCY_PARAM_DEFAULT = 10;
    private final boolean MINIMIZE_OBJECTIVE_PARA_DEFAULT = true;


    private IntParameter numberOfEpochPara;
    private ListParameter cacheModePara; //options: NONE, MEMORY, FILESYSTEM
    private IntParameter queueSizePara;
    private BoolParameter resumePara;
    private BoolParameter filterModePara;
    private BoolParameter doNotClearFileSystemCachePara;
    private IntParameter numberOfGPUPara;
    private IntParameter avgFrequencyPara;
    private StringParameter batchSizePara;
    private ListParameter optimizationAlgoPara; //options: STOCHASTIC_GRADIENT_DESCENT, LINE_GRADIENT_DESCENT,
    private ListParameter weightInitPara;     //options: XAVIER, RELU, IDENTITY, NORMAL, UNIFORM, ZERO, DISTRIBUTION
    private BoolParameter minimizeObjectivePara;


    public LSTMModelImpl()
    {
        numberOfEpochPara = new IntParameter("numberOfEpoch", NUMBER_OF_EPOCH_PARAM_DEFAULT);
        cacheModePara = new ListParameter("cacheMode", new ArrayList<String>(Arrays.asList("NONE", "MEMORY", "FILESYSTEM")), CACHE_MODE_PARAM_DEFAULT);
        queueSizePara = new IntParameter("queueSize", QUEUE_SIZE_PARAM_DEFAULT);
        resumePara = new BoolParameter("resume", RESUME_PARAM_DEFAULT);
        filterModePara = new BoolParameter("filterMode", FILTER_MODE_PARAM_DEFAULT);
        doNotClearFileSystemCachePara = new BoolParameter("doNotClearFileSystemCache", DO_NOT_CLEAR_FILE_SYSTEM_CACHE_PARAM_DEFAULT);
        numberOfGPUPara = new IntParameter("numberOfGPU", NUMBER_OF_GPU_PARAM_DEFAULT);
        avgFrequencyPara = new IntParameter("averageFrequency", AVG_FREQUENCY_PARAM_DEFAULT);
        batchSizePara = new StringParameter("batchSize", BATCH_SIZE_PARAM_DEFAULT);
        optimizationAlgoPara = new ListParameter("optimizationAlgo", new ArrayList<String>(Arrays.asList("STOCHASTIC_GRADIENT_DESCENT", "LINE_GRADIENT_DESCENT")), OPTIMIZATION_ALGO_PARAM_DEFAULT);
        weightInitPara = new ListParameter("weightInit", new ArrayList<String>(Arrays.asList("XAVIER", "RELU", "IDENTITY", "NORMAL", "UNIFORM", "ZERO", "DISTRIBUTION")), WEIGHT_INIT_PARAM_DEFAULT);
        minimizeObjectivePara = new BoolParameter("minimizeObjective", MINIMIZE_OBJECTIVE_PARA_DEFAULT);
    }

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
        Classifier network = new Dl4jMlpClassifier();

        try
        {
            //Setting Parameters for the model
            ((Dl4jMlpClassifier) network).setNumEpochs(1);                //Bigger the better but also takes more time
            //network.setEarlyStopping(new EarlyStopping()); //TODO: set a stopping to make it stop if no progress
            ((Dl4jMlpClassifier) network).setBatchSize("100");
            //network.setSeed(124564);                            //to ensure randomness
            //network.setNumDecimalPlaces(2);


            //Network configurations
            NeuralNetConfiguration networkConfig = new NeuralNetConfiguration();
            networkConfig.setOptimizationAlgo(STOCHASTIC_GRADIENT_DESCENT);
            networkConfig.getOptimizationAlgo();
            networkConfig.setWeightInit(WeightInit.XAVIER);
            networkConfig.setUpdater(new Adam());
            networkConfig.setBiasUpdater(new Sgd());
            ((Dl4jMlpClassifier) network).setNeuralNetConfiguration(networkConfig);


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
            ((Dl4jMlpClassifier) network).setLayers(lstmLayer1, outLayer);                //Two layers for now, LSTM and the output

            //train with the DL4J classifier
            network.buildClassifier(trainDataset);

        }

        catch(Exception ex)
        {
            ex.printStackTrace();
        }

        setClassifier(network);
        return network;
    }

    @Override
    public Map<String, Parameter> getDefaultParameters()
    {
        return null;
    }


}
