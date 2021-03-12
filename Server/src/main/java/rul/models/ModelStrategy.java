/*
  Interface for the model generation, Part of the strategy design pattern
  the trainModel will take an Instances object containing the reduced dataset to train
  the return object will be a Classifier, either a linear regression or LSTM(for future releases)

  @author Paul Micu
  @last_edit 11/01/2020
 */
package rul.models;

import weka.classifiers.Classifier;
import weka.core.Instances;

import java.io.Serializable;

public abstract class ModelStrategy implements Serializable {
    private Classifier classifier;
    private Instances dataToTrain;

    public abstract Classifier trainModel(Instances reducedData);

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
}
