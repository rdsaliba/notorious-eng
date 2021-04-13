/*
  This Controller is responsible for handling the information view
  of an asset. It constructs attribute panes, generates the raw data table
  and handles the deletion of an asset.
  @author Jeff, Paul, Roy
  @last_edit 02/7/2020
 */
package controllers;

import app.item.Asset;
import app.item.AssetAttribute;
import app.item.Measurement;
import external.AssetDAOImpl;
import external.AssetTypeDAOImpl;
import external.AttributeDAOImpl;
import external.ModelDAOImpl;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rul.assessment.AssessmentController;
import utilities.CustomDialog;
import utilities.FormInputValidation;
import utilities.UIUtilities;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

import static utilities.TextConstants.*;

public class AssetInfoController extends Controller implements Initializable {
    private static final String CYCLE = "Cycle";

    private static final int ATTRIBUTE_GRAPH_SIZE = 5;
    private static final int THUMBNAIL_WIDTH = 247;
    private static final int PADDING = 54;
    static Logger logger = LoggerFactory.getLogger(AssetInfoController.class);
    Image image;
    @FXML
    private Button deleteBtn;
    @FXML
    private Button archiveBtn;
    @FXML
    private Button assetSaveBtn;
    @FXML
    private Button backBtn;
    @FXML
    private Text assetName;
    @FXML
    private TextField assetNameOutput;
    @FXML
    private FlowPane attributeFlowPane;
    @FXML
    private Text assetTypeOutput;
    @FXML
    private Text serialNumberOutput;
    @FXML
    private TextField manufacturerOutput;
    @FXML
    private TextField locationOutput;
    @FXML
    private TextField siteOutput;
    @FXML
    private Text modelOutput;
    @FXML
    private Text rulOutput;
    @FXML
    private Text recommendationOutput;
    @FXML
    private TextField categoryOutput;
    @FXML
    private TextArea descriptionOutput;
    @FXML
    private Tab rawDataTab;
    @FXML
    private AnchorPane rawDataListPane;
    @FXML
    private AnchorPane assetInfoPane;
    @FXML
    private ImageView imageView;
    @FXML
    private AnchorPane root;
    private Asset asset;
    private AssetDAOImpl assetDAOImpl;
    private AssetTypeDAOImpl assetTypeDAOImpl;
    private AttributeDAOImpl attributeDAOImpl;
    private ModelDAOImpl modelDAO;
    private UIUtilities uiUtilities;

    /**
     * Initialize runs before the scene is displayed.
     * It initializes elements and data in the scene.
     *
     * @param url            url to be used
     * @param resourceBundle url to be used
     * @author Jeff
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        assetDAOImpl = new AssetDAOImpl();
        assetTypeDAOImpl = new AssetTypeDAOImpl();
        modelDAO = new ModelDAOImpl();
        attributeDAOImpl = new AttributeDAOImpl();
        uiUtilities = new UIUtilities();
        root.setOpacity(0);
        uiUtilities.fadeInTransition(root);
        attachEvents();
    }

    /**
     * initData receives the Engine data that was selected from Asset.FXML
     * Then, uses that data to populate the text fields in the scene.
     * It also binds the text fields to the asset proprieties to be able to save the information
     *
     * @param asset is an asset object that will get initialized
     * @author Jeff, Paul
     */
    public void initData(Asset asset) {
        this.asset = asset;
        this.asset.setAssetInfo(assetDAOImpl.createAssetInfo(asset.getId()));
        String assetTypeName = assetTypeDAOImpl.getNameFromID(asset.getAssetTypeID());
        assetName.setText(assetTypeName + " - " + asset.getSerialNo());
        assetTypeOutput.setText(assetTypeName);
        serialNumberOutput.setText(asset.getSerialNo());
        modelOutput.setText(modelDAO.getModelNameAssociatedWithAssetType(asset.getAssetTypeID()));
        categoryOutput.setText(asset.getCategory());

        if (asset.getImageId() != 0) {
            image = assetDAOImpl.findImageById(asset.getImageId());
            imageView.setImage(image);
        } else {
            //Set default image
            image = new Image("file:JavaFXUserInterface/src/main/resources/imgs/default.png");
            imageView.setImage(image);
            imageView.setCache(true);
        }

        rulOutput.setText(new DecimalFormat("#.##").format(AssessmentController.getLatestEstimate(asset.getId())));
        recommendationOutput.setText(String.valueOf(asset.getRecommendation()));

        assetSaveBtn.setDisable(true);
        assetNameOutput.setText(String.valueOf(asset.getName()));
        assetNameOutput.textProperty().addListener((obs, oldText, newText) -> assetSaveBtn.setDisable(false));
        asset.nameProperty().bind(assetNameOutput.textProperty());

        manufacturerOutput.setText(String.valueOf(asset.getManufacturer()));
        manufacturerOutput.textProperty().addListener((obs, oldText, newText) -> assetSaveBtn.setDisable(false));
        asset.manufacturerProperty().bind(manufacturerOutput.textProperty());

        locationOutput.setText(String.valueOf(asset.getLocation()));
        locationOutput.textProperty().addListener((obs, oldText, newText) -> assetSaveBtn.setDisable(false));
        asset.locationProperty().bind(locationOutput.textProperty());

        siteOutput.setText(String.valueOf(asset.getSite()));
        siteOutput.textProperty().addListener((obs, oldText, newText) -> assetSaveBtn.setDisable(false));
        asset.siteProperty().bind(siteOutput.textProperty());

        categoryOutput.setText(String.valueOf(asset.getCategory()));
        categoryOutput.textProperty().addListener((obs, oldText, newText) -> assetSaveBtn.setDisable(false));
        asset.categoryProperty().bind(categoryOutput.textProperty());

        descriptionOutput.setText(String.valueOf(asset.getDescription()));
        descriptionOutput.textProperty().addListener((obs, oldText, newText) -> assetSaveBtn.setDisable(false));
        descriptionOutput.setWrapText(true);
        asset.descriptionProperty().bind(descriptionOutput.textProperty());


        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1000), e -> rulOutput.setText(String.valueOf(new DecimalFormat("#.##").format(AssessmentController.getLatestEstimate(asset.getId()))))));

        timeline.setCycleCount(Animation.INDEFINITE); // loop forever
        timeline.play();
        addTimeline(timeline);

        constructAttributePanes();
    }

    /**
     * Constructs the attribute panes to be able to display data in a nice format.
     *
     * @author Jeff, Paul
     */
    public void constructAttributePanes() {
        if (asset.getAssetInfo().getAssetAttributes().isEmpty()) {
            Text noSensorsText = new Text("No sensor readings to display");
            noSensorsText.setFont(new Font("Segoe UI Bold", 14));
            attributeFlowPane.getChildren().add(noSensorsText);

        }
        for (AssetAttribute attribute : asset.getAssetInfo().getAssetAttributes()) {
            Pane pane = new Pane();
            pane.getStyleClass().add(ATTRIBUTE_PANE_STYLE_CLASS);
            final CategoryAxis xAxis = new CategoryAxis();
            final CategoryAxis yAxis = new CategoryAxis();
            xAxis.setLabel(CYCLE);
            xAxis.setAnimated(false);
            final LineChart<String, String> attributeChart = new LineChart<>(xAxis, yAxis);
            attributeChart.setAnimated(false);

            XYChart.Series<String, String> series = new XYChart.Series<>();
            ObservableList<XYChart.Data<String, String>> data = FXCollections.observableArrayList();

            ArrayList<Measurement> initialMeasurements = attributeDAOImpl.getLastXMeasurementsByAssetIDAndAttributeID(Integer.toString(asset.getId()), Integer.toString(attribute.getId()), ATTRIBUTE_GRAPH_SIZE);
            initialMeasurements.stream()
                    .sorted(Collections.reverseOrder((t, measurement) -> measurement.getTime() - t.getTime()))
                    .forEach(d -> data.add(new XYChart.Data<>(Integer.toString(d.getTime()), Double.toString(d.getValue()))));

            Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1000), e -> {
                ArrayList<Measurement> measurements = attributeDAOImpl.getLastXMeasurementsByAssetIDAndAttributeID(Integer.toString(asset.getId()), Integer.toString(attribute.getId()), ATTRIBUTE_GRAPH_SIZE);
                measurements.stream()
                        .sorted(Collections.reverseOrder((t, measurement) -> measurement.getTime() - t.getTime()))
                        .filter(m -> !attributeChart.getXAxis().isValueOnAxis(Integer.toString(m.getTime())))
                        .forEach(d -> data.add(new XYChart.Data<>(Integer.toString(d.getTime()), Double.toString(d.getValue()))));

                if (data.size() > ATTRIBUTE_GRAPH_SIZE)
                    data.remove(0, data.size() - ATTRIBUTE_GRAPH_SIZE);
            }));
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.play();
            addTimeline(timeline);

            series.setData(data);
            attributeChart.getData().add(series);
            attributeChart.setPrefWidth(217.0);
            attributeChart.setPrefHeight(133.0);
            attributeChart.setLayoutX(5.0);
            attributeChart.setLayoutY(50.0);
            pane.getChildren().add(attributeChart);
            Text attributeName = new Text(attribute.getName());
            attributeName.getStyleClass().add(ATTRIBUTE_NAME_STYLE_CLASS);
            attributeName.setLayoutX(16.0);
            attributeName.setLayoutY(30.0);
            pane.getChildren().add(attributeName);

            attributeFlowPane.getChildren().add(pane);
        }
    }

    /**
     * Attaches events to elements in the scene.
     *
     * @author Jeff
     */
    public void attachEvents() {
        // Change scenes to Assets.fxml
        backBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(ASSETS_SCENE, backBtn.getScene()));
        deleteBtn.setOnMouseClicked(mouseEvent -> CustomDialog.deleteAssetConfirmationDialogShowAndWait(asset.getId(), backBtn.getScene()));
        archiveBtn.setOnMouseClicked(mouseEvent -> CustomDialog.archiveAssetConfirmationDialogShowAndWait(asset, archiveBtn.getScene()));

        rawDataTab.setOnSelectionChanged(event -> {
            rawDataListPane.getChildren().clear();
            generateRawDataTable();
        });

        assetSaveBtn.setOnMouseClicked(mouseEvent -> {
            if (FormInputValidation.assetEditFormInputValidation(assetInfoPane, assetNameOutput, descriptionOutput, manufacturerOutput, categoryOutput, siteOutput, locationOutput)) {
                updateAsset(asset);
                uiUtilities.changeScene(ASSETS_SCENE, assetSaveBtn.getScene());
            }
        });

        // As the window expands or shrinks, attribute panes will adjust to the window size accordingly
        attributeFlowPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            double attributeFlowWidth = (double) newVal - PADDING;
            if (attributeFlowPane.getChildren().size() < 6) {
                int nbOfPanes = attributeFlowPane.getChildren().size();
                attributeFlowWidth = attributeFlowWidth - (THUMBNAIL_WIDTH * nbOfPanes);
                attributeFlowWidth = attributeFlowWidth / (nbOfPanes - 1);
                attributeFlowPane.setHgap(attributeFlowWidth);
            } else {
                int nbOfPanes = (int) (attributeFlowWidth / THUMBNAIL_WIDTH);
                attributeFlowWidth = (attributeFlowWidth % THUMBNAIL_WIDTH);
                attributeFlowWidth = attributeFlowWidth / (nbOfPanes - 1);
                attributeFlowPane.setHgap(attributeFlowWidth);
            }
        });
    }

    private void updateAsset(Asset asset) {
        assetDAOImpl.updateAsset(asset);
    }

    /**
     * Fill the raw data table with the current measurement for the asset and update the list every
     * second with new measurement as they come in
     *
     * @author Paul
     */
    public void generateRawDataTable() {
        TableView<ObservableList<String>> tableview = new TableView<>();
        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
        AtomicInteger lastCycle = new AtomicInteger();
        try (ResultSet resultSet = assetDAOImpl.createMeasurementsFromAssetIdAndTime(asset.getId(), lastCycle.get())) {
            for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
                final int j = i;
                TableColumn<ObservableList<String>, String> col = new TableColumn<>(resultSet.getMetaData().getColumnName(i + 1));
                col.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(j)));
                col.setComparator(Comparator.comparingDouble(Double::parseDouble));
                tableview.getColumns().addAll(col);
            }

            updateRawTableView(data, lastCycle);

            Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1000), e -> updateRawTableView(data, lastCycle)));

            timeline.setCycleCount(Animation.INDEFINITE); // loop forever
            timeline.play();

            addTimeline(timeline);

            tableview.setItems(data);
        } catch (SQLException e) {
            logger.error("Error on Building Data");
            logger.error("Exception: ", e);
        }


        tableview.setId("RawDataTable");
        AnchorPane.setBottomAnchor(tableview, 0.0);
        AnchorPane.setTopAnchor(tableview, 5.0);
        AnchorPane.setRightAnchor(tableview, 0.0);
        AnchorPane.setLeftAnchor(tableview, 0.0);
        rawDataListPane.getChildren().addAll(tableview);
    }

    /**
     * this function fills the row of the raw table view given the table object and
     * what from what cycle do we want to get the information
     * ex: if given lastCycle 5 for asset 1 it will get all measurements for asset 1
     * and for time cycle over 5 and insert them in the table while making use of the
     * observable list from javafx
     *
     * @author Paul
     */
    private void updateRawTableView(ObservableList<ObservableList<String>> data, AtomicInteger lastCycle) {
        try (ResultSet rs = assetDAOImpl.createMeasurementsFromAssetIdAndTime(asset.getId(), lastCycle.get())) {
            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    row.add(rs.getString(i));
                }
                data.add(0, row);
            }
        } catch (SQLException e) {
            logger.error("Error on Building Data: ", e);
        }
        lastCycle.set(data.size());
    }
}
