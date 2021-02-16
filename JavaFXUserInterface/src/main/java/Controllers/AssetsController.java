package Controllers;

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
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.ResourceBundle;

public class AssetsController implements Initializable {
    private final AssetTypeDAOImpl assetTypeDAO;
    private final ModelDAOImpl modelDAO;
    @FXML
    private Button AssetMenuBtn;
    @FXML
    private Button AssetTypeMenuBtn;
    @FXML
    private Button addAssetBtn;
    @FXML
    private FlowPane AssetsThumbPane;
    @FXML
    private AnchorPane AssetsListPane;
    @FXML
    private Tab thumbnailTab;
    @FXML
    private Tab listTab;
    @FXML
    private ChoiceBox<String> sortAsset;
    private UIUtilities uiUtilities;
    private ObservableList<Asset> Assets;

    public AssetsController() {
        assetTypeDAO = new AssetTypeDAOImpl();
        modelDAO = new ModelDAOImpl();

        try {
            Assets = FXCollections.observableArrayList(ModelController.getInstance().getAllLiveAssets());
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
        generateThumbnails();

    }

    /**
     * Adds mouse events to all the buttons
     *
     * @author Jeff
     */
    public void attachEvents() {
        thumbnailTab.setOnSelectionChanged(event -> {
            AssetsThumbPane.getChildren().clear();
            generateThumbnails();
        });

        listTab.setOnSelectionChanged(event -> {
            AssetsListPane.getChildren().clear();
            generateList();
        });

        //Attach link to AssetMenuButton to go to Assets.fxml
        AssetMenuBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, "/Assets"));

        //Attach link to AssetTypeMenuBtn to go to Utilities.AssetTypeList.fxml
        AssetTypeMenuBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, "/AssetTypeList"));

        //Attach link to addAssetButton to go to AddAsset.fxml
        addAssetBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, "/AddAsset"));

        //Adding items to the choiceBox (drop down list)
        sortAsset.getItems().add("Default");
        sortAsset.getItems().add("Ascending RUL");
        sortAsset.getItems().add("Descending RUL");
        //Default Value
        sortAsset.setValue("Default");
        //Listener on the sort ChoiceBox. Depending on the sort selected, all Assets panes are cleared and generated again
        //with the appropriate sort applied.
        sortAsset.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            switch (newValue) {
                case "Ascending RUL":
                    if (thumbnailTab.isSelected()) {
                        AssetsThumbPane.getChildren().clear();
                        Assets = FXCollections.observableArrayList(ModelController.getInstance().getAllLiveAssetsDes());
                        Collections.reverse(Assets);
                        generateThumbnails();
                    }
                    break;
                case "Descending RUL":
                    if (thumbnailTab.isSelected()) {
                        AssetsThumbPane.getChildren().clear();
                        Assets = FXCollections.observableArrayList(ModelController.getInstance().getAllLiveAssetsDes());
                        generateThumbnails();
                    }
                    break;
                default:
                    if (thumbnailTab.isSelected()) {
                        AssetsThumbPane.getChildren().clear();
                        Assets = FXCollections.observableArrayList(ModelController.getInstance().getAllLiveAssets());
                        generateThumbnails();
                    }
                    break;
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

        for (Asset Asset : Assets) {

            Pane pane = new Pane();
            pane.setOnMouseClicked(event -> uiUtilities.changeScene(event, "/AssetInfo", Asset));

            pane.getStyleClass().add("AssetPane");
            Text AssetName = new Text(Asset.getSerialNo());
            Text AssetType = new Text(assetTypeDAO.getNameFromID(Asset.getAssetTypeID()));
            // UI String constants
            String RECOMMENDATION = "Recommendation: ";
            String LINEAR_RUL = "Linear RUL: ";
            Text linearLabel = new Text(LINEAR_RUL);
            Text linearRUL = new Text(String.valueOf(new DecimalFormat("#.##").format(AssessmentController.getLatestEstimate(Asset.getId()))));
            Text recommendationLabel = new Text(RECOMMENDATION);
            Text recommendation = new Text(Asset.getRecommendation());

            Timeline timeline =
                    new Timeline(new KeyFrame(Duration.millis(1000), e -> linearRUL.setText(String.valueOf(new DecimalFormat("#.##").format(AssessmentController.getLatestEstimate(Asset.getId()))))));

            timeline.setCycleCount(Animation.INDEFINITE); // loop forever
            timeline.play();


            AssetName.setId("AssetName");
            AssetType.setId("AssetType");
            linearLabel.setId("linearLabel");
            linearRUL.setId("linearRUL");
            recommendationLabel.setId("recommendationLabel");
            recommendation.setId("recommendation");

            AssetName.setLayoutX(14.0);
            AssetName.setLayoutY(28.0);
            AssetType.setLayoutX(14.0);
            AssetType.setLayoutY(60.0);
            recommendationLabel.setLayoutX(14.0);
            recommendationLabel.setLayoutY(100.0);
            recommendation.setLayoutX(230.0);
            recommendation.setLayoutY(100.0);
            linearLabel.setLayoutX(14.0);
            linearLabel.setLayoutY(121.0);
            linearRUL.setLayoutX(230.0);
            linearRUL.setLayoutY(120.0);

            pane.getChildren().add(AssetName);
            pane.getChildren().add(AssetType);
            pane.getChildren().add(linearLabel);
            pane.getChildren().add(linearRUL);
            pane.getChildren().add(recommendationLabel);
            pane.getChildren().add(recommendation);

            boxes.add(pane);
        }

        AssetsThumbPane.getChildren().addAll(boxes);

    }


    /**
     * Creates a table element to list all the assets.
     *
     * @author Jeff
     */
    public void generateList() {
        TableView<Asset> table = new TableView<>();

        // When TableRow is clicked, send data to AssetInfo scene.
        table.setRowFactory(tv -> {
            TableRow<Asset> row = new TableRow<>();
            row.setOnMouseClicked(event -> uiUtilities.changeScene(event, row, "/AssetInfo", row.getItem()));
            return row;
        });

        String TYPE_COL = "Type";
        TableColumn<Asset, String> AssetTypeCol = new TableColumn<>(TYPE_COL);
        AssetTypeCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                assetTypeDAO.getNameFromID(cellData.getValue().getAssetTypeID())));


        String SERIAL_NO_COL = "Serial No.";
        TableColumn<Asset, String> serialNoCol = new TableColumn<>(SERIAL_NO_COL);
        serialNoCol.setCellValueFactory(
                new PropertyValueFactory<>("serialNo"));

        String MODEL_COL = "Model";
        TableColumn<Asset, String> modelCol = new TableColumn<>(MODEL_COL);
        modelCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                modelDAO.getModelNameFromModelID(modelDAO.getModelsByAssetTypeID(cellData.getValue().getAssetTypeID()).getModelID())));

        String RUL_COL = "RUL";
        TableColumn<Asset, Double> modelRULCol = new TableColumn<>(RUL_COL);
        modelRULCol.setCellValueFactory(cellData -> new SimpleDoubleProperty(
                Double.parseDouble(new DecimalFormat("#.##").format(AssessmentController.getLatestEstimate(cellData.getValue().getId())))).asObject());

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

        table.setItems(Assets);
        table.setId("listTable");
        table.getColumns().addAll(AssetTypeCol, serialNoCol, modelCol, modelRULCol, recommendationCol, locationCol, siteCol, categoryCol, manufacturerCol, descriptionCol);
        AnchorPane.setBottomAnchor(table, 0.0);
        AnchorPane.setTopAnchor(table, 5.0);
        AnchorPane.setRightAnchor(table, 0.0);
        AnchorPane.setLeftAnchor(table, 0.0);
        AssetsListPane.getChildren().addAll(table);

    }

}
