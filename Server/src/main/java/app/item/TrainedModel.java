/*
  This object holds the information regarding a trained model, including a copy of the trained Classifier object

  @author Paul Micu
  @last_edit 12/27/2020
 */
package app.item;

import rul.models.ModelStrategy;
import weka.classifiers.Classifier;

public class TrainedModel extends Model {
    private int assetTypeID;
    private boolean retrain;
    private int statusID;
    private ModelStrategy modelStrategy;

    public TrainedModel() {
        super();
    }

    public TrainedModel(String modelName, int modelID, String description) {
        super(modelName, modelID, description);
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
        return modelStrategy.getClassifier();
    }

    public void setModelClassifier(Classifier modelClassifier) {
        modelStrategy.setClassifier(modelClassifier);
    }

    public int getStatusID() {
        return statusID;
    }

    public void setStatusID(int statusID) {
        this.statusID = statusID;
    }

    public ModelStrategy getModelStrategy() {
        return modelStrategy;
    }

    public void setModelStrategy(ModelStrategy modelStrategy) {
        this.modelStrategy = modelStrategy;
    }

    @Override
    public String toString() {
        return "TrainedModel{" +
                ", assetTypeID=" + assetTypeID +
                ", retrain=" + retrain +
                ", modelClassifier=" + modelStrategy +
                '}';
    }
}
