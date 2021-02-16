package Controllers;

import Utilities.UIUtilities;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Duration;
import rul.assessment.AssessmentController;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;

public class AssetInfoController implements Initializable {

    @FXML
    private Button AssetMenuBtn;
    @FXML
    private Button AssetTypeMenuBtn;
    @FXML
    private Button deleteBtn;
    @FXML
    private Text AssetName;
    @FXML
    private Text AssetNameOutput;
    @FXML
    private FlowPane sensorFlowPane;
    @FXML
    private Text AssetTypeOutput;
    @FXML
    private Text serialNumberOutput;
    @FXML
    private Text manufacturerOutput;
    @FXML
    private Text locationOutput;
    @FXML
    private Text siteOutput;
    @FXML
    private Text modelOutput;
    @FXML
    private Text rulOutput;
    @FXML
    private Text recommendationOutput;
    @FXML
    private Text categoryOutput;
    @FXML
    private Text descriptionOutput;
    @FXML
    private Tab rawDataTab;
    @FXML
    private AnchorPane rawDataListPane;

    private Asset Asset;
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
        attachEvents();
    }

    /**
     * initData receives the Engine data that was selected from Asset.FXML
     * Then, uses that data to populate the text fields in the scene.
     *
     * @param Asset is an asset object that will get initialized
     * @author Jeff
     */
    public void initData(Asset Asset) {
        this.Asset = Asset;
        String AssetTypeName = assetTypeDAOImpl.getNameFromID(Asset.getAssetTypeID());
        AssetName.setText(AssetTypeName + " - " + Asset.getSerialNo());
        AssetNameOutput.setText(Asset.getName());
        AssetTypeOutput.setText(AssetTypeName);
        serialNumberOutput.setText(Asset.getSerialNo());
        manufacturerOutput.setText(Asset.getManufacturer());
        locationOutput.setText(Asset.getLocation());
        siteOutput.setText(Asset.getSite());
        modelOutput.setText(modelDAO.getModelNameFromModelID(modelDAO.getModelsByAssetTypeID(Asset.getAssetTypeID()).getModelID()));
        categoryOutput.setText(Asset.getCategory());

        rulOutput.setText(new DecimalFormat("#.##").format(AssessmentController.getLatestEstimate(Asset.getId())));
        recommendationOutput.setText(Asset.getRecommendation());

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1000), e -> rulOutput.setText(String.valueOf(new DecimalFormat("#.##").format(AssessmentController.getLatestEstimate(Asset.getId()))))));

        timeline.setCycleCount(Animation.INDEFINITE); // loop forever
        timeline.play();


        descriptionOutput.setText(Asset.getDescription());
        constructSensorPanes();
    }

    /**
     * Constructs the sensor panes to be able to display data in a nice format.
     *
     * @author Jeff
     */
    public void constructSensorPanes() {
        for (AssetAttribute sensor : Asset.getAssetInfo().getAssetAttributes()) {
            Pane pane = new Pane();
            pane.getStyleClass().add("sensorPane");
            final CategoryAxis xAxis = new CategoryAxis();
            final CategoryAxis yAxis = new CategoryAxis();
            // UI String constants
            String CYCLE = "Cycle";
            xAxis.setLabel(CYCLE);
            xAxis.setAnimated(false);
            final LineChart<String, String> sensorChart =
                    new LineChart<>(xAxis, yAxis);
            String SENSOR_VALUES = "Sensor Values";
            sensorChart.setTitle(SENSOR_VALUES);
            XYChart.Series<String, String> series = new XYChart.Series<>();
            sensorChart.setAnimated(false);

            Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1000), e -> {
                ArrayList<Measurement> measurements = attributeDAOImpl.getLastXMeasurementsByAssetIDAndAttributeID(Integer.toString(Asset.getId()), Integer.toString(sensor.getId()), 5);
                if (!sensorChart.getXAxis().isValueOnAxis(Integer.toString(measurements.get(0).getTime())))
                    series.getData().add(new XYChart.Data<>(Integer.toString(measurements.get(0).getTime()), Double.toString(measurements.get(0).getValue())));
                else if (series.getData().size() != measurements.size()) {
                    for (int i = measurements.size() - 1; i >= 0; i--) {
                        series.getData().add(new XYChart.Data<>(Integer.toString(measurements.get(i).getTime()), Double.toString(measurements.get(i).getValue())));
                    }
                }
                if (series.getData().size() > 5)
                    series.getData().remove(0);
            }));
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.play();

            sensorChart.getData().add(series);
            sensorChart.setPrefWidth(275.0);
            sensorChart.setPrefHeight(163.0);
            sensorChart.setLayoutX(12.0);
            sensorChart.setLayoutY(50.0);
            pane.getChildren().add(sensorChart);
            Text sensorName = new Text(sensor.getName());
            sensorName.setId("sensorType");
            sensorName.setLayoutX(35.0);
            sensorName.setLayoutY(30.0);
            pane.getChildren().add(sensorName);

            sensorFlowPane.getChildren().add(pane);
        }
    }

    /**
     * Attaches events to elements in the scene.
     *
     * @author Jeff
     */
    public void attachEvents() {
        AssetMenuBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, "/Assets"));
        //Attach link to AssetTypeMenuBtn to go to Utilities.AssetTypeList.fxml
        AssetTypeMenuBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, "/AssetTypeList"));
        deleteBtn.setOnMouseClicked(this::deleteDialog);

        rawDataTab.setOnSelectionChanged(event -> {
            rawDataListPane.getChildren().clear();
            generateRawDataTable();
        });
    }

    /**
     * Send the asset ID to the Database class in order for it to be deleted.
     *
     * @author Jeff
     */
    public void deleteAsset() {
        assetDAOImpl.deleteAssetByID(Asset.getId());
    }

    /**
     * Creates a dialog box that asks user if they want to delete an asset.
     *
     * @param mouseEvent is an event trigger for this delete dialog
     */
    void deleteDialog(MouseEvent mouseEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        String ALERT_TITLE = "Confirmation Dialog";
        alert.setTitle(ALERT_TITLE);
        String ALERT_HEADER = "Confirmation of Asset deletion";
        alert.setHeaderText(ALERT_HEADER);
        String ALERT_CONTENT = "Are you sure you want to delete this Asset?";
        alert.setContentText(ALERT_CONTENT);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            deleteAsset();
            uiUtilities.changeScene(mouseEvent, "/Assets");
        }
    }

    public void generateRawDataTable() {
        TableView<ObservableList<String>> table = new TableView<>();
        table.getItems().clear();
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(3000), e -> {
            ObservableList<AssetAttribute> attributes = FXCollections.observableArrayList(assetDAOImpl.createAssetInfo(Asset.getId()).getAssetAttributes());

            int columnIndex = 1;
            TableColumn<ObservableList<String>, String>[] tableColumns = new TableColumn[attributes.size() + 1];

            ArrayList<List<Measurement>> data = new ArrayList<>();

            tableColumns[0] = new TableColumn<>("Cycle");
            data.add(attributes.get(0).getMeasurements());
            setCellValue(0, tableColumns[0]);

            for (AssetAttribute attribute : attributes) {
                data.add(attribute.getMeasurements());
                tableColumns[columnIndex] = new TableColumn<>(attribute.getName());
                setCellValue(columnIndex, tableColumns[columnIndex]);
                columnIndex++;
            }
            table.getColumns().addAll(tableColumns);

            ObservableList<ObservableList<String>> dataPerColumn = FXCollections.observableArrayList();

            int outCounter = 0;
            for (List<Measurement> dataList : data) {
                int counter = 0;
                for (Measurement measurement : dataList) {
                    if (outCounter < dataList.size()) {
                        ObservableList<String> list = FXCollections.observableArrayList();
                        dataPerColumn.add(list);
                        dataPerColumn.get(outCounter).add(String.valueOf(measurement.getTime()));
                        outCounter++;
                    } else {
                        dataPerColumn.get(counter).add(String.valueOf(measurement.getValue()));
                        counter++;
                    }
                }
            }
            Collections.reverse(dataPerColumn);
            table.setItems(dataPerColumn);

        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        table.setId("RawDataTable");
        AnchorPane.setBottomAnchor(table, 0.0);
        AnchorPane.setTopAnchor(table, 5.0);
        AnchorPane.setRightAnchor(table, 0.0);
        AnchorPane.setLeftAnchor(table, 0.0);
        rawDataListPane.getChildren().addAll(table);
    }

    public void setCellValue(int index, TableColumn<ObservableList<String>, String> tableColumn) {
        tableColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(index)));
    }
}