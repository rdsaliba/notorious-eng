/*
  This object holds the information regarding a trained model, including a copy of the trained Classifier object

  @author Paul Micu
  @version 1.0
  @last_edit 12/27/2020
 */
package com.cbms.app;

import weka.classifiers.Classifier;

public class TrainedModel {
    private int modelID;
    private int assetTypeID;
    private boolean retrain;
    private Classifier modelClassifier;

    public TrainedModel() {
    }

    public TrainedModel(int modelID, int assetTypeID, boolean retrain, Classifier modelClassifier) {
        this.modelID = modelID;
        this.assetTypeID = assetTypeID;
        this.retrain = retrain;
        this.modelClassifier = modelClassifier;
    }

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

    public boolean isRetrain() {
        return retrain;
    }

    public void setRetrain(boolean retrain) {
        this.retrain = retrain;
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
                ", assetTypeID=" + assetTypeID +
                ", retrain=" + retrain +
                ", modelClassifier=" + modelClassifier +
                '}';
    }
}
