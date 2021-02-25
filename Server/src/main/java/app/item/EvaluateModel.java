/*
  This object holds the information regarding a trained model, including a copy of the trained Classifier object

  @author Paul Micu
  @last_edit 12/27/2020
 */
package app.item;

import weka.classifiers.Classifier;

public class EvaluateModel {
    private int modelID;
    private String modelName;
    private int assetTypeID;
    private Classifier modelClassifier;
    private int from;
    private int to;

    public int getFrom(){return from;}

    public void setFrom(int from){this.from = from;}

    public int getTom(){return to;}

    public void setTo(int to){this.to = to;}

    public String getModelName(){return modelName;}

    public void setModelName(String modelName){ this.modelName = modelName;}

    public int getModelID() {
        return modelID;
    }

    public void setModelID(int modelID) {
        this.modelID = modelID;
    }

    public int getAssetTypeID() {
        return assetTypeID;
    }

    public void setAssetTypeID(int assetTypeID) {
        this.assetTypeID = assetTypeID;
    }

    public Classifier getModelClassifier() {
        return modelClassifier;
    }

    public void setModelClassifier(Classifier modelClassifier) {
        this.modelClassifier = modelClassifier;
    }

    @Override
    public String toString() {
        return "TrainedModel{" +
                "modelID=" + modelID +
                "modelName=" + modelName +
                ", assetTypeID=" + assetTypeID +
                ", modelClassifier=" + modelClassifier +
                '}';
    }
}

