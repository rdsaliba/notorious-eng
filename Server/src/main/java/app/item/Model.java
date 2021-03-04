package app.item;

public class Model {
    private String modelName;
    private int modelID;
    private String description;

    public Model() {
    }

    public Model(String modelName, int modelID, String description) {
        this.modelName = modelName;
        this.modelID = modelID;
        this.description = description;
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