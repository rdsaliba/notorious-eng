package Controllers;

import Utilities.TextConstants;
import Utilities.UIUtilities;
import app.ModelController;
import app.item.Asset;
import external.AssetTypeDAOImpl;
import external.ModelDAOImpl;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Duration;
import rul.assessment.AssessmentController;

import java.net.URL;
import java.util.ResourceBundle;

public class SystemsController implements Initializable {
    private static final String SORT_DEFAULT = "Default";
    private static final String SORT_RUL_ASC = "Ascending RUL";
    private static final String SORT_RUL_DESC = "Descending RUL";
    private final AssetTypeDAOImpl assetTypeDAO;
    private final ModelDAOImpl modelDAO;
    @FXML
    private Button systemMenuBtn;
    @FXML
    private Button systemTypeMenuBtn;
    @FXML
    private Button addSystemBtn;
    @FXML
    private FlowPane systemsThumbPane;
    @FXML
    private AnchorPane systemsListPane;
    @FXML
    private Tab thumbnailTab;
    @FXML
    private Tab listTab;
    @FXML
    private ChoiceBox<String> sortSystem;
    private UIUtilities uiUtilities;
    private ObservableList<Asset> systems;
    private Timeline rulTimeline;
    private TableView<Asset> table;

    public SystemsController() {
        assetTypeDAO = new AssetTypeDAOImpl();
        modelDAO = new ModelDAOImpl();
        table = new TableView<>();

        try {
            systems = FXCollections.observableArrayList(ModelController.getInstance().getAllLiveAssets());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialize runs before the scene is displayed.
     * It initializes elements and data in the scene.
     *
     * @param url            url to be used
     * @param resourceBundle resourceBundle to be used
     * @author Jeff
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        uiUtilities = new UIUtilities();

        attachEvents();
        updateRULs();
        generateThumbnails();
    }

    /**
     * Updates the RUL values of all systems.
     *
     * @author Jeff
     */
    public void updateRULs() {
        rulTimeline =
                new Timeline(new KeyFrame(Duration.millis(3000), e ->
                {
                    for (Asset asset : systems) {
                        asset.setRul(String.valueOf(TextConstants.RULValueFormat.format(AssessmentController.getLatestEstimate(asset.getId()))));
                    }
                    table.refresh();
                }));

        rulTimeline.setCycleCount(Animation.INDEFINITE); // loop forever
        rulTimeline.play();
    }

    /**
     * Adds mouse events to all the buttons
     *
     * @author Jeff
     */
    public void attachEvents() {
        thumbnailTab.setOnSelectionChanged(event -> {
            systemsThumbPane.getChildren().clear();
            generateThumbnails();
        });

        listTab.setOnSelectionChanged(event -> {
            systemsListPane.getChildren().clear();
            generateList();
        });

        //Attach link to systemMenuButton to go to Systems.fxml
        systemMenuBtn.setOnMouseClicked(mouseEvent -> {
            closeTimeline();
            uiUtilities.changeScene(mouseEvent, "/Systems");
        });

        //Attach link to systemTypeMenuBtn to go to Utilities.SystemTypeList.fxml
        systemTypeMenuBtn.setOnMouseClicked(mouseEvent -> {
            closeTimeline();
            uiUtilities.changeScene(mouseEvent, "/SystemTypeList");
        });

        //Attach link to addSystemButton to go to AddSystem.fxml
        addSystemBtn.setOnMouseClicked(mouseEvent -> {
            closeTimeline();
            uiUtilities.changeScene(mouseEvent, "/AddSystem");
        });

        //Adding items to the choiceBox (drop down list)
        sortSystem.getItems().add(SORT_DEFAULT);
        sortSystem.getItems().add(SORT_RUL_ASC);
        sortSystem.getItems().add(SORT_RUL_DESC);
        //Default Value
        sortSystem.setValue(SORT_DEFAULT);
        //Listener on the sort ChoiceBox. Depending on the sort selected, all systems panes are cleared and generated again
        //with the appropriate sort applied.
        sortSystem.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                if (newValue.equals(SORT_DEFAULT)) {
                    FXCollections.sort(systems, (asset, t1) -> asset.getId() - t1.getId());
                } else if (newValue.equals(SORT_RUL_ASC)) {
                    FXCollections.sort(systems, (asset, t1) -> Double.valueOf(asset.getRul().getValue()).compareTo(Double.valueOf(t1.getRul().getValue())));
                } else if (newValue.equals(SORT_RUL_DESC)) {
                    FXCollections.sort(systems, (asset, t1) -> Double.valueOf(t1.getRul().getValue()).compareTo(Double.valueOf(asset.getRul().getValue())));
                }
                systemsThumbPane.getChildren().clear();
                generateThumbnails();

            }
        });
    }

    /**
     * Creates elements that are in the scene so the data can be displayed.
     *
     * @author Jeff
     */
    public void generateThumbnails() {
        ObservableList<Pane> boxes = FXCollections.observableArrayList();

        for (Asset system : systems) {

            Pane pane = new Pane();
            pane.setOnMouseClicked(event -> uiUtilities.changeScene(event, "/SystemInfo", system));

            pane.getStyleClass().add("systemPane");
            Text systemName = new Text(system.getSerialNo());
            Text systemType = new Text(system.getAssetTypeName());
            // UI String constants
            String RECOMMENDATION = "Recommendation: ";
            String LINEAR_RUL = "Linear RUL: ";
            Text linearLabel = new Text(LINEAR_RUL);
            Text recommendationLabel = new Text(RECOMMENDATION);
            Text recommendation = new Text(system.getRecommendation());

            Text linearRUL = new Text();
            SimpleStringProperty s = system.getRul();
            linearRUL.textProperty().bind(s);

            systemName.setId("systemName");
            systemType.setId("systemType");
            linearLabel.setId("linearLabel");
            linearRUL.setId("linearRUL");
            recommendationLabel.setId("recommendationLabel");
            recommendation.setId("recommendation");

            systemName.setLayoutX(14.0);
            systemName.setLayoutY(28.0);
            systemType.setLayoutX(14.0);
            systemType.setLayoutY(60.0);
            recommendationLabel.setLayoutX(14.0);
            recommendationLabel.setLayoutY(100.0);
            recommendation.setLayoutX(230.0);
            recommendation.setLayoutY(100.0);
            linearLabel.setLayoutX(14.0);
            linearLabel.setLayoutY(121.0);
            linearRUL.setLayoutX(230.0);
            linearRUL.setLayoutY(120.0);

            pane.getChildren().add(systemName);
            pane.getChildren().add(systemType);
            pane.getChildren().add(linearLabel);
            pane.getChildren().add(linearRUL);
            pane.getChildren().add(recommendationLabel);
            pane.getChildren().add(recommendation);

            boxes.add(pane);
        }

        systemsThumbPane.getChildren().addAll(boxes);
    }


    /**
     * Creates a table element to list all the assets.
     *
     * @author Jeff
     */
    public void generateList() {

        // When TableRow is clicked, send data to SystemInfo scene.
        table.setRowFactory(tv -> {
            TableRow<Asset> row = new TableRow<>();
            row.setOnMouseClicked(event -> uiUtilities.changeScene(event, row, "/SystemInfo", row.getItem()));
            return row;
        });

        String TYPE_COL = "Type";
        TableColumn<Asset, String> systemTypeCol = new TableColumn<>(TYPE_COL);
        systemTypeCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getAssetTypeName()));


        String SERIAL_NO_COL = "Serial No.";
        TableColumn<Asset, String> serialNoCol = new TableColumn<>(SERIAL_NO_COL);
        serialNoCol.setCellValueFactory(
                new PropertyValueFactory<>("serialNo"));

        String MODEL_COL = "Model";
        TableColumn<Asset, String> modelCol = new TableColumn<>(MODEL_COL);
        modelCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                modelDAO.getModelNameFromAssetTypeID(cellData.getValue().getAssetTypeID())));

        String RUL_COL = "RUL";
        TableColumn<Asset, Number> modelRULCol = new TableColumn<>(RUL_COL);
        modelRULCol.setCellValueFactory(cellData -> new SimpleDoubleProperty(
                Double.parseDouble(TextConstants.ThresholdValueFormat.format(Double.parseDouble(cellData.getValue().getRul().getValue())))));

        String RECOMMENDATION_COL = "Recommendation";
        TableColumn<Asset, String> recommendationCol = new TableColumn<>(RECOMMENDATION_COL);
        recommendationCol.setCellValueFactory(
                new PropertyValueFactory<>("recommendation"));

        String LOCATION_COL = "Location";
        TableColumn<Asset, String> locationCol = new TableColumn<>(LOCATION_COL);
        locationCol.setCellValueFactory(
                new PropertyValueFactory<>("location"));

        String MANUFACTURER_COL = "Manufacturer";
        TableColumn<Asset, String> manufacturerCol = new TableColumn<>(MANUFACTURER_COL);
        manufacturerCol.setCellValueFactory(
                new PropertyValueFactory<>("manufacturer"));

        String CATEGORY_COL = "Category";
        TableColumn<Asset, String> categoryCol = new TableColumn<>(CATEGORY_COL);
        categoryCol.setCellValueFactory(
                new PropertyValueFactory<>("category"));

        String SITE_COL = "Site";
        TableColumn<Asset, String> siteCol = new TableColumn<>(SITE_COL);
        siteCol.setCellValueFactory(
                new PropertyValueFactory<>("site"));

        String DESCRIPTION_COL = "Description";
        TableColumn<Asset, String> descriptionCol = new TableColumn<>(DESCRIPTION_COL);
        descriptionCol.setCellValueFactory(
                new PropertyValueFactory<>("description"));

        table.setItems(systems);
        table.setId("listTable");
        table.getColumns().addAll(systemTypeCol, serialNoCol, modelCol, modelRULCol, recommendationCol, locationCol, siteCol, categoryCol, manufacturerCol, descriptionCol);
        AnchorPane.setBottomAnchor(table, 0.0);
        AnchorPane.setTopAnchor(table, 5.0);
        AnchorPane.setRightAnchor(table, 0.0);
        AnchorPane.setLeftAnchor(table, 0.0);
        systemsListPane.getChildren().addAll(table);
    }

    /**
     * Stops the timeline
     */
    private void closeTimeline() {
        if (rulTimeline != null)
            rulTimeline.stop();
    }
}
