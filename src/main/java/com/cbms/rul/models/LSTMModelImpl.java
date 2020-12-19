package com.cbms.rul.models;

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

import static org.deeplearning4j.nn.api.OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT;

public class LSTMModelImpl implements ModelStrategy
{
    @Override
    public Classifier trainModel(Instances firstTrain) throws Exception
    {
        firstTrain.setClassIndex(firstTrain.numAttributes() - 1);

        Instances trainDataset = LinearRegressionModelImpl.removeInstances(firstTrain);
        trainDataset.setClassIndex(trainDataset.numAttributes() - 1);

        //DL4J Recurrent Neural Network (RNN)
        Dl4jMlpClassifier network = new Dl4jMlpClassifier();

        try
        {
            //Setting Parameters for the model
            network.setNumEpochs(1);                //Bigger the better but also takes more time
            //network.setEarlyStopping(new EarlyStopping()); //TODO: set a stopping to make it stop if no progress
            network.setBatchSize("100");
            network.setSeed(124564);                            //to ensure randomness
            network.setNumDecimalPlaces(2);

            //Network configurations
            NeuralNetConfiguration networkConfig = new NeuralNetConfiguration();
            networkConfig.setOptimizationAlgo(STOCHASTIC_GRADIENT_DESCENT);
            networkConfig.setWeightInit(WeightInit.XAVIER);
            networkConfig.setUpdater(new Adam());
            networkConfig.setBiasUpdater(new Sgd());
            network.setNeuralNetConfiguration(networkConfig);

            //Set Layers
            LSTM lstmLayer1 = new LSTM();
            lstmLayer1.setNOut(200);                        //First layer outputs (input for 2nd layer)

            //Activation and Gate functions:
            lstmLayer1.setActivationFunction(new ActivationRReLU());
            lstmLayer1.setGateActivationFn(new ActivationRReLU());

            // TODO: Add more layers (makes it slower doing so now)
//            LSTM lstmLayer2  = new LSTM();
//            lstmLayer2.setNOut(64);
//            lstmLayer2.setActivationFunction(new ActivationReLU());
//            lstmLayer2.setGateActivationFn(new ActivationSigmoid());

            //Configure output layer
            OutputLayer outLayer = new OutputLayer();           //Last layer must always be the last layer
            outLayer.setLossFn(new LossMSE());                  //Loss function is Mean Square Error
            outLayer.setActivationFunction(new ActivationSwish());      //Activation function
            outLayer.setNOut(1);                                        //Single output
            network.setLayers(lstmLayer1, outLayer);                //Two layers for now, LSTM and the output

            //train with the DL4J classifier
            network.buildClassifier(trainDataset);

            // TODO: Evaluate
//        Evaluation ev = new Evaluation(firstTrain);
//        ev.crossValidateModel(network, firstTrain, 10, new Random(0));
//        System.out.println(ev.toSummaryString());
        }

        catch(Exception ex)
        {
            ex.printStackTrace();
        }

        //return Neural network Classifier for LSTM
        return network;
    }

}
