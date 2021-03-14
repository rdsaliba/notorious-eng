/*
  This Controller is responsible for handling the assets view.
  It generates the thumbnails and the list of assets. It also
  handles the sorting of assets.

  @author Jeff, Paul, Najim
  @last_edit 02/7/2020
 */
package controllers;

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
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Duration;
import rul.assessment.AssessmentController;
import utilities.TextConstants;
import utilities.UIUtilities;

import java.net.URL;
import java.util.Comparator;
import java.util.ResourceBundle;

public class AssetsController implements Initializable {
    private static final String SORT_DEFAULT = "Order";
    private static final String SORT_RUL_ASC = "Ascending RUL";
    private static final String SORT_RUL_DESC = "Descending RUL";
    private static final String SORT_CRITICAL_ASC = "Less Critical";
    private static final String SORT_CRITICAL_DESC = "Most Critical";
    private static final String RECOMMENDATION = "Recommendation";
    private static final String LINEAR_RUL = "Linear RUL";
    private static final String TYPE_COL = "Type";
    private static final String SERIAL_NO_COL = "Serial No.";
    private static final String MODEL_COL = "Model";
    private static final String RUL_COL = "RUL";
    private static final String LOCATION_COL = "Location";
    private static final String MANUFACTURER_COL = "Manufacturer";
    private static final String CATEGORY_COL = "Category";
    private static final String SITE_COL = "Site";
    private static final String DESCRIPTION_COL = "Description";
    private final AssetTypeDAOImpl assetTypeDAO;
    private final ModelDAOImpl modelDAO;
    Logger logger = LoggerFactory.getLogger(AssetsController.class);
    @FXML
    private Button assetMenuBtn;
    @FXML
    private Button assetTypeMenuBtn;
    @FXML
    private Button addAssetBtn;
    @FXML
    private FlowPane assetsThumbPane;
    @FXML
    private AnchorPane assetsListPane;
    @FXML
    private Tab thumbnailTab;
    @FXML
    private Tab listTab;
    @FXML
    private ChoiceBox<String> sortAsset;
    @FXML
    private ChoiceBox<String> sortRecommendation;
    private UIUtilities uiUtilities;
    private ObservableList<Asset> assets;
    private Timeline rulTimeline;
    private final TableView<Asset> table;

    public AssetsController() {
        assetTypeDAO = new AssetTypeDAOImpl();
        modelDAO = new ModelDAOImpl();
        table = new TableView<>();

        try {
            assets = FXCollections.observableArrayList(ModelController.getInstance().getAllLiveAssets());
        } catch (Exception e) {
            logger.error("Exception: ", e);
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
        for (Asset asset : assets) {
            asset.setRul(String.valueOf(TextConstants.RULValueFormat.format(AssessmentController.getLatestEstimate(asset.getId()))));
        }
        rulTimeline =
                new Timeline(new KeyFrame(Duration.millis(3000), e ->
                {
                    for (Asset asset : assets) {
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
            assetsThumbPane.getChildren().clear();
            generateThumbnails();
        });

        listTab.setOnSelectionChanged(event -> {
            assetsListPane.getChildren().clear();
            generateList();
        });

        //Attach link to addAssetButton to go to AddAsset.fxml
        addAssetBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(rulTimeline, mouseEvent, TextConstants.ADD_ASSETS, addAssetBtn.getScene()));

        sortingSetUp();
    }

    private void sortingSetUp() {
        //Adding items to the choiceBox (drop down list)
        sortAsset.getItems().add(SORT_DEFAULT);
        sortAsset.getItems().add(SORT_RUL_ASC);
        sortAsset.getItems().add(SORT_RUL_DESC);
        sortAsset.getItems().add(SORT_CRITICAL_ASC);
        sortAsset.getItems().add(SORT_CRITICAL_DESC);

        //Default Value
        sortAsset.setValue(SORT_DEFAULT);
        //Listener on the sort ChoiceBox. Depending on the sort selected, all systems panes are cleared and generated again
        //with the appropriate sort applied.
        sortAsset.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                if (newValue.equals(SORT_DEFAULT)) {
                    FXCollections.sort(assets, (asset, t1) -> asset.getId() - t1.getId());
                } else if (newValue.equals(SORT_RUL_ASC)) {
                    FXCollections.sort(assets, (asset, t1) -> Double.valueOf(asset.getRul().getValue()).compareTo(Double.valueOf(t1.getRul().getValue())));
                } else if (newValue.equals(SORT_RUL_DESC)) {
                    FXCollections.sort(assets, (asset, t1) -> Double.valueOf(t1.getRul().getValue()).compareTo(Double.valueOf(asset.getRul().getValue())));
                } else if (newValue.equals(SORT_CRITICAL_ASC)) {
                    FXCollections.sort(assets, Comparator.comparingInt(Asset::mapCriticality));
                } else if (newValue.equals(SORT_CRITICAL_DESC)) {
                    FXCollections.sort(assets, (asset, t1) -> Integer.compare(t1.mapCriticality(), asset.mapCriticality()));
                }

                assetsThumbPane.getChildren().clear();
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

        for (Asset asset : assets) {

            Pane pane = new Pane();
            pane.setOnMouseClicked(event -> uiUtilities.changeScene(event, "/AssetInfo", asset, pane.getScene()));
            pane.getStyleClass().add("assetPane");

            Pane imagePlaceholder = new Pane();
            imagePlaceholder.getStyleClass().add("imagePlaceholder");

            HBox rulPane = new HBox();
            rulPane.getStyleClass().add("rulPane");

            HBox statusPane = new HBox();
            statusPane.getStyleClass().add("statusPane");

            Text assetName = new Text(asset.getSerialNo());
            Text assetType = new Text(assetTypeDAO.getNameFromID(asset.getAssetTypeID()));

            Text rulLabel = new Text("RUL");
            Text recommendationLabel = new Text("Status");
            Text recommendation = new Text(asset.getRecommendation());

            Text rulValue = new Text();
            SimpleStringProperty s = asset.getRul();
            rulValue.textProperty().bind(s);

            assetName.getStyleClass().add("assetName");
            assetType.getStyleClass().add("assetType");
            rulLabel.getStyleClass().add("rulLabel");
            rulValue.getStyleClass().add("rulValue");
            recommendationLabel.getStyleClass().add("statusLabel");
            recommendation.getStyleClass().add("statusValue");
            statusPane.getStyleClass().add("statusPane");
            if (asset.getRecommendation().equals("Ok"))
                statusPane.getStyleClass().add("ok");
            else if (asset.getRecommendation().equals("Advisory"))
                statusPane.getStyleClass().add("advisory");
            else if (asset.getRecommendation().equals("Caution"))
                statusPane.getStyleClass().add("caution");
            else if (asset.getRecommendation().equals("Warning"))
                statusPane.getStyleClass().add("warning");
            else if (asset.getRecommendation().equals("Failed"))
                statusPane.getStyleClass().add("failed");
            else
                statusPane.getStyleClass().add("none");

            statusPane.setAlignment(Pos.CENTER);
            rulPane.setAlignment(Pos.CENTER);

            assetName.setLayoutX(15.0);
            assetName.setLayoutY(35.0);
            assetType.setLayoutX(15.0);
            assetType.setLayoutY(63.0);
            imagePlaceholder.setLayoutX(15.0);
            imagePlaceholder.setLayoutY(80.0);
            rulLabel.setLayoutX(52.0);
            rulLabel.setLayoutY(239.0);
            rulPane.setLayoutX(15.0);
            rulPane.setLayoutY(243.0);
            rulValue.setLayoutY(21.0);
            recommendationLabel.setLayoutX(164.0);
            recommendationLabel.setLayoutY(238.0);
            statusPane.setLayoutX(133.0);
            statusPane.setLayoutY(243.0);
            recommendation.setLayoutY(21.0);

            pane.getChildren().add(assetName);
            pane.getChildren().add(assetType);
            pane.getChildren().add(rulLabel);
            rulPane.getChildren().add(rulValue);
            pane.getChildren().add(rulPane);
            pane.getChildren().add(recommendationLabel);
            statusPane.getChildren().add(recommendation);
            pane.getChildren().add(statusPane);
            pane.getChildren().add(imagePlaceholder);

            boxes.add(pane);
        }

        assetsThumbPane.getChildren().addAll(boxes);
    }


    /**
     * Creates a table element to list all the assets.
     *
     * @author Jeff
     */
    public void generateList() {

        // When TableRow is clicked, send data to AssetInfo scene.
        table.setRowFactory(tv -> {
            TableRow<Asset> row = new TableRow<>();
            row.setOnMouseClicked(event -> uiUtilities.changeScene(event, row, "/AssetInfo", row.getItem(), row.getScene()));
            return row;
        });

        TableColumn<Asset, String> assetTypeCol = new TableColumn<>(TYPE_COL);
        assetTypeCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getAssetTypeName()));

        TableColumn<Asset, String> serialNoCol = new TableColumn<>(SERIAL_NO_COL);
        serialNoCol.setCellValueFactory(
                new PropertyValueFactory<>("serialNo"));

        TableColumn<Asset, String> modelCol = new TableColumn<>(MODEL_COL);
        modelCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                modelDAO.getModelNameFromAssetTypeID(cellData.getValue().getAssetTypeID())));

        TableColumn<Asset, Number> modelRULCol = new TableColumn<>(RUL_COL);
        modelRULCol.setCellValueFactory(cellData -> new SimpleDoubleProperty(
                Double.parseDouble(TextConstants.ThresholdValueFormat.format(Double.parseDouble(cellData.getValue().getRul().getValue())))));

        TableColumn<Asset, String> recommendationCol = new TableColumn<>(RECOMMENDATION);
        recommendationCol.setCellValueFactory(
                new PropertyValueFactory<>("recommendation"));

        TableColumn<Asset, String> locationCol = new TableColumn<>(LOCATION_COL);
        locationCol.setCellValueFactory(
                new PropertyValueFactory<>("location"));

        TableColumn<Asset, String> manufacturerCol = new TableColumn<>(MANUFACTURER_COL);
        manufacturerCol.setCellValueFactory(
                new PropertyValueFactory<>("manufacturer"));

        TableColumn<Asset, String> categoryCol = new TableColumn<>(CATEGORY_COL);
        categoryCol.setCellValueFactory(
                new PropertyValueFactory<>("category"));

        TableColumn<Asset, String> siteCol = new TableColumn<>(SITE_COL);
        siteCol.setCellValueFactory(
                new PropertyValueFactory<>("site"));

        TableColumn<Asset, String> descriptionCol = new TableColumn<>(DESCRIPTION_COL);
        descriptionCol.setCellValueFactory(
                new PropertyValueFactory<>("description"));

        table.setItems(assets);
        table.setId("listTable");
        table.getColumns().addAll(assetTypeCol, serialNoCol, modelCol, modelRULCol, recommendationCol, locationCol, siteCol, categoryCol, manufacturerCol, descriptionCol);
        AnchorPane.setBottomAnchor(table, 0.0);
        AnchorPane.setTopAnchor(table, 5.0);
        AnchorPane.setRightAnchor(table, 0.0);
        AnchorPane.setLeftAnchor(table, 0.0);
        assetsListPane.getChildren().addAll(table);
    }

}
