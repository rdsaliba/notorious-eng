package Utilities;

import app.item.Model;
import javafx.collections.ObservableList;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;

public class ModelPanes {
    private Model selectedModel;

    public Model getSelectedModel() {
        return selectedModel;
    }

    public void setSelectedModel(Model selectedModel) {
        this.selectedModel = selectedModel;
    }

    /**
     * This function properly sets up the flow pane to scroll horizontally only and other
     * settings
     *
     * @param modelPanesObservableList is the observable list of panes that contain model information
     * @param flowPaneSelector         is the container pane for all the model panes
     * @author Jeremie
     */
    public void setModelThumbnailsContainerPane(ObservableList<Pane> modelPanesObservableList, FlowPane flowPaneSelector) {
        flowPaneSelector.setPrefWidth((300.0 + flowPaneSelector.getHgap()) * (modelPanesObservableList.size()) + (2 * flowPaneSelector.getHgap()));
        flowPaneSelector.getChildren().addAll(modelPanesObservableList);
        flowPaneSelector.setOnMouseClicked(mouseEvent -> handleModelSelectionChange(modelPanesObservableList, selectedModel.getModelID()));
    }

    /**
     * This function highlights the pane of the currently associated model for the asset type.
     *
     * @param modelPanes is the observable list of panes that contain model information
     * @param modelID    is the ID of the model
     * @author Jeremie
     */
    public void highlightAssociatedModel(ObservableList<Pane> modelPanes, int modelID) {
        for (int i = 0; i < modelPanes.size(); i++) {
            if ((i + 1) == modelID) {
                modelPanes.get(i).setStyle("-fx-border-color: red");
            } else if ((i + 1) != modelID) {
                //modelPanes.get(i).setStyle("-fx-background-color: #e0e0eb");
            }
        }
    }

    /**
     * This function handles the selection of a model.
     *
     * @param model is the selected model
     * @param pane  is the pane containing the selected model
     * @author Jeremie
     */
    public void handleModelSelection(Model model, Pane pane) {
        pane.setStyle("-fx-border-color: red");
        setSelectedModel(model);
    }

    /**
     * This function handles the UI display of changing the user selection from one model
     * pane to another.
     *
     * @param modelPanes is the observable list of panes that contain model information
     * @param index      is the index of the selected model
     * @author Jeremie
     */
    public void handleModelSelectionChange(ObservableList<Pane> modelPanes, int index) {
        for (int i = 0; i < modelPanes.size(); i++) {
            if ((i + 1) != index) {
                modelPanes.get(i).setStyle("-fx-border-color: transparent;");
            }
        }
    }
}