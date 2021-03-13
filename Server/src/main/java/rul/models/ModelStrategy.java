/*
  Interface for the model generation, Part of the strategy design pattern
  the trainModel will take an Instances object containing the reduced dataset to train
  the return object will be a Classifier, either a linear regression or LSTM(for future releases)

  @author Paul Micu
  @last_edit 11/01/2020
 */
package rul.models;

import app.item.Asset;
import weka.classifiers.Classifier;
import weka.core.Instances;

import java.io.Serializable;
import java.util.List;

public abstract class ModelStrategy implements Serializable {
    //    private static final Byte serialVersionUID = new Byte(9176206042562869304);
    private Classifier classifier;
    private Instances dataToTrain;
    private List<Asset> trainAssets;
    private List<Asset> testAssets;


    public List<Asset> getTrainsAssets(){return trainAssets;}

    public void setTrainAssets(List<Asset> trainAssets){this.trainAssets=trainAssets;}

    public List<Asset> getTestAssets(){return testAssets;}

    public void setTestAssets(List<Asset> testAssets){this.testAssets=testAssets;}

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
