/*
  Interface for the model generation, Part of the strategy design pattern
  the trainModel will take an Instances object containing the reduced dataset to train
  the return object will be a Classifier, either a linear regression or LSTM(for future releases)

  @author Paul Micu
  @last_edit 03/11/2021
 */
package rul.models;

import app.item.parameter.Parameter;
import weka.classifiers.Classifier;
import weka.core.Instances;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public abstract class ModelStrategy implements Serializable {
    private Classifier classifier;
    private Map<String, Parameter> parameters;
    private Instances dataToTrain;
    private int trainAssets;
    private int testAssets;


    public ModelStrategy() {
        parameters = new HashMap<>();
    }

    public int getTrainsAssets() {
        return trainAssets;
    }

    public void setTrainAssets(int trainAssets) {
        this.trainAssets = trainAssets;
    }

    public int getTestAssets() {
        return testAssets;
    }

    public void setTestAssets(int testAssets) {
        this.testAssets = testAssets;
    }

    public abstract Classifier trainModel(Instances reducedData);

    public abstract Map<String, Parameter> getDefaultParameters();

    public Classifier getClassifier() {
        return classifier;
    }

    public void setClassifier(Classifier classifier) {
        this.classifier = classifier;
    }

    public Instances getDataToTrain() {
        return dataToTrain;
    }

    public void setDataToTrain(Instances dataToTrain) {
        this.dataToTrain = dataToTrain;
    }

    public Map<String, Parameter> getParameters() {
        return this.parameters;
    }

    public void setParameters(Map<String, Parameter> parameters) {
        this.parameters = parameters;
    }

    public void addParameter(Parameter newParam) {
        this.parameters.put(newParam.getParamName(), newParam);
    }
}
