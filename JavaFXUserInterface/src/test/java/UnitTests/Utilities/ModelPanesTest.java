package UnitTests.Utilities;

import app.item.Model;
import app.item.TrainedModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utilities.ModelPanes;

import static org.junit.Assert.*;

public class ModelPanesTest {
    private ModelPanes modelPanes;
    private FlowPane flowPane;
    private ObservableList<Pane> modelPanesObservableList;
    private TrainedModel model1;
    private TrainedModel model2;
    private Pane pane1;
    private Pane pane2;

    @Before
    public void setUp() {
        modelPanes = new ModelPanes();
        flowPane = new FlowPane();
        flowPane.setHgap(10);
        modelPanesObservableList = FXCollections.observableArrayList();
        model1 = new TrainedModel("Model 1 Name", 1, "Model 1 Description");
        model2 = new TrainedModel("Model 2 Name", 2, "Model 2 Description");
        pane1 = new Pane();
        pane2 = new Pane();
        modelPanesObservableList.addAll(pane1, pane2);
    }

    @After
    public void tearDown() {
        modelPanes = null;
        flowPane.getChildren().removeAll();
        flowPane = null;
        modelPanesObservableList.removeAll();
        modelPanesObservableList = null;
        model1 = null;
        model2 = null;
        pane1 = null;
        pane2 = null;
    }

    @Test
    public void setAndGetSelectedModelTest() {
        modelPanes.setSelectedModel(model1);
        Model returnedModel = modelPanes.getSelectedModel();
        assertEquals("Model 1 Name", returnedModel.getModelName());
        assertEquals(1, returnedModel.getModelID());
        assertEquals("Model 1 Description", returnedModel.getDescription());
    }

    @Test
    public void setModelThumbnailsContainerPaneTest() {
        modelPanes.setModelThumbnailsContainerPane(modelPanesObservableList, flowPane);
        int modelPanesSize = modelPanesObservableList.size();
        assertEquals(2, modelPanesSize);
        assertNotNull(flowPane.getOnMouseClicked());
        assertEquals(38, flowPane.getPrefWidth(), 0.5);
    }

    @Test
    public void highlightAssociatedModelTest() {
        modelPanes.highlightAssociatedModel(modelPanesObservableList, 2);
        assertEquals("", modelPanesObservableList.get(0).getStyle());
        assertEquals("-fx-border-color: #226B9E", modelPanesObservableList.get(1).getStyle());
    }

    @Test
    public void handleModelSelectionTest() {
        modelPanes.handleModelSelection(model1, pane1);
        assertEquals("-fx-border-color: #226B9E", pane1.getStyle());
        assertEquals("", pane2.getStyle());
        assertEquals(model1, modelPanes.getSelectedModel());
        assertNotSame(model2, modelPanes.getSelectedModel());
    }

    @Test
    public void handleModelSelectionChangeTest() {
        modelPanes.handleModelSelectionChange(modelPanesObservableList, 2);
        assertEquals("-fx-border-color: transparent;", modelPanesObservableList.get(0).getStyle());
        assertEquals("", modelPanesObservableList.get(1).getStyle());
    }
}