/* Second strategy design pattern and implementation of Model Strategy
 *
 * @author Khaled
 * @last_edit 12/28/2020
 */

package rul.models;

import org.deeplearning4j.nn.weights.WeightInit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import static org.deeplearning4j.nn.api.OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT;

public class LSTMModelImpl extends ModelStrategy {

    static Logger logger = LoggerFactory.getLogger(LSTMModelImpl.class);

    /**
     * This function takes the filtered training dataset, builds a Neural Network using Weka's Deep Learning 4 Java plugin
     * and trains and returns an LSTM model.
     *
     * @author Khaled
     */
    @Override
    public Classifier trainModel(Instances firstTrain) {
        firstTrain.setClassIndex(firstTrain.numAttributes() - 1);

        Instances trainDataset = LinearRegressionModelImpl.removeInstances(firstTrain);
        trainDataset.setClassIndex(trainDataset.numAttributes() - 1);

        //DL4J Recurrent Neural Network (RNN)
        Classifier network = new Dl4jMlpClassifier();

        try {
            //Setting Parameters for the model
            ((Dl4jMlpClassifier) network).setNumEpochs(1);                //Bigger the better but also takes more time
            //network.setEarlyStopping(new EarlyStopping()); //Set a stopping to make it stop if no progress
            ((Dl4jMlpClassifier) network).setBatchSize("100");

            //Network configurations
            NeuralNetConfiguration networkConfig = new NeuralNetConfiguration();
            networkConfig.setOptimizationAlgo(STOCHASTIC_GRADIENT_DESCENT);
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

        } catch (Exception e) {
            logger.error("Exception: ", e);
        }

        //return Neural network Classifier for LSTM
        return network;
    }

}
