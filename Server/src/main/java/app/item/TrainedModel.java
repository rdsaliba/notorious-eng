/*
  This object holds the information regarding a trained model, including a copy of the trained Classifier object

  @author Paul Micu
  @last_edit 12/27/2020
 */
package app.item;

import app.item.parameter.Parameter;
import weka.classifiers.Classifier;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

public class TrainedModel {
    private int modelID;
    private int assetTypeID;
    private boolean retrain;
    private boolean isLive;
    private ArrayList<Parameter> parameterList;
    private Classifier modelClassifier;

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

    public ArrayList<Parameter> getParameterList() {
        return parameterList;
    }

    public ArrayList<Parameter> getDefaultParameterList() {
        return (ArrayList<Parameter>) parameterList
                .stream()
                .filter(parameter -> parameter.isDefault())
                .collect(Collectors.toList());
    }

    public ArrayList<Parameter> getLiveParameterList() {
        return (ArrayList<Parameter>) parameterList
                .stream()
                .filter(parameter -> parameter.isLive())
                .collect(Collectors.toList());
    }

    public ArrayList<Parameter> getEvalParameterList() {
        return (ArrayList<Parameter>) parameterList
                .stream()
                .filter(parameter -> !parameter.isLive())
                .collect(Collectors.toList());
    }

    public ArrayList<Parameter> getParameter(String name) {
        return (ArrayList<Parameter>) parameterList
                .stream()
                .filter(parameter -> parameter.getParamName().equals(name))
                .collect(Collectors.toList());
    }

    public void setParameterList(ArrayList<Parameter> parameterMap) {
        this.parameterList = parameterMap;
    }

    public boolean isLive() {
        return isLive;
    }

    public void setIsLive(boolean isLive) {
        this.isLive = isLive;
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
