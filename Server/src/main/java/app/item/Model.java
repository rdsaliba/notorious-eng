package app.item;

import javafx.beans.property.SimpleStringProperty;

public class Model {
    private String modelName;
    private int modelID;
    private String description;
    private SimpleStringProperty rmse;

    public Model() {
        rmse = new SimpleStringProperty();
    }

    public Model(String modelName, int modelID, String description) {
        this.modelName = modelName;
        this.modelID = modelID;
        this.description = description;
    }

    public SimpleStringProperty getRMSE() {
        return rmse;
    }

    public void setRMSE(String rmse) {
        this.rmse.setValue(rmse);
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public int getModelID() {
        return modelID;
    }

    public void setModelID(int modelID) {
        this.modelID = modelID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Model{" +
                "modelID=" + modelID +
                ", model name=" + modelName +
                ", description=" + description +
                '}';
    }
}