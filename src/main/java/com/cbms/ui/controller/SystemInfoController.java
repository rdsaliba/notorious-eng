package com.cbms.ui.controller;

import com.cbms.app.item.Asset;
import com.cbms.app.item.AssetAttribute;
import com.cbms.app.item.Measurement;
import com.cbms.rul.assessment.AssessmentController;
import com.cbms.source.local.AssetDAOImpl;
import com.cbms.source.local.AssetTypeDAOImpl;
import com.cbms.source.local.ModelDAOImpl;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class SystemInfoController implements Initializable {
    @FXML
    private Button systemMenuBtn;
    @FXML
    private Button deleteBtn;
    @FXML
    private Text systemName;
    @FXML
    private Text systemNameOutput;
    @FXML
    private FlowPane sensorFlowPane;
    @FXML
    private Text systemTypeOutput;
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
    private Text categoryOutput;
    @FXML
    private Text descriptionOutput;

    private Asset system;
    private AssetDAOImpl assetDAOImpl;
    private AssetTypeDAOImpl assetTypeDAOImpl;
    private ModelDAOImpl modelDAO;
    private UIUtilities uiUtilities;

    // UI String constants
    private final String CYCLE = "Cycle";
    private final String SENSOR_VALUES = "Sensor Values";
    private final String ALERT_TITLE = "Confirmation Dialog";
    private final String ALERT_HEADER = "Confirmation of system deletion";
    private final String ALERT_CONTENT = "Are you sure you want to delete this system?";

    /**
     * Initialize runs before the scene is displayed.
     * It initializes elements and data in the scene.
     *
     * @param url
     * @param resourceBundle
     *
     * @author Jeff
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        assetDAOImpl = new AssetDAOImpl();
        assetTypeDAOImpl = new AssetTypeDAOImpl();
        modelDAO = new ModelDAOImpl();
        uiUtilities = new UIUtilities();
        attachEvents();
    }

    /**
     * initData receives the Engine data that was selected from System.FXML
     * Then, uses that data to populate the text fields in the scene.
     *
     * @param system
     *
     * @author Jeff
     */
    void initData(Asset system) {
        this.system = system;
        String systemTypeName = assetTypeDAOImpl.getNameFromID(system.getAssetTypeID());
        systemName.setText(systemTypeName + " - " + system.getSerialNo());
        systemNameOutput.setText(system.getName());
        systemTypeOutput.setText(systemTypeName);
        serialNumberOutput.setText(system.getSerialNo());
        manufacturerOutput.setText(system.getManufacturer());
        locationOutput.setText(system.getLocation());
        siteOutput.setText(system.getSite());
        modelOutput.setText(modelDAO.getModelNameFromModelID(modelDAO.getModelsByAssetTypeID(system.getAssetTypeID()).getModelID()));
        categoryOutput.setText(system.getCategory());
        rulOutput.setText(new DecimalFormat("#.##").format(AssessmentController.getLatestEstimate(system.getId())));
        descriptionOutput.setText(system.getDescription());
        constructSensorPanes();
    }

    /**
     * Constructs the sensor panes to be able to display data in a nice format.
     *
     * @author Jeff
     */
    public void constructSensorPanes() {
        for (AssetAttribute sensor: system.getAssetInfo().getAssetAttributes()) {
            Pane pane = new Pane();
            pane.getStyleClass().add("sensorPane");
            final NumberAxis xAxis = new NumberAxis();
            final NumberAxis yAxis = new NumberAxis();
            xAxis.setLabel(CYCLE);
            final LineChart<Number, Number> sensorChart =
                    new LineChart<>(xAxis, yAxis);
            sensorChart.setTitle(SENSOR_VALUES);
            XYChart.Series series = new XYChart.Series();
            ArrayList<Measurement> measurements = sensor.getMeasurements();
            int lastCycle = sensor.getMeasurements().size() - 1;
            double lowestMeasurement = getLowestMeasurement(sensor.getMeasurements());
            double highestMeasurement = getHighestMeasurement(sensor.getMeasurements());
            setAxisBounds(sensorChart, lowestMeasurement - (lowestMeasurement * 0.002),
                    highestMeasurement + (highestMeasurement * 0.002), false);

            if(lastCycle >= 4) {
                setAxisBounds(sensorChart, lastCycle - 3, lastCycle + 1, true);
                for(int i = 4; i >= 0; i--) {
                    series.getData().add(new XYChart.Data(lastCycle + 1 - i, measurements.get(lastCycle - i).getValue()));
                }
            }
            else {
                setAxisBounds(sensorChart, 0, lastCycle, true);
                for(int i = lastCycle; i >= 0; i--) {
                    series.getData().add(new XYChart.Data(i, measurements.get(i).getValue()));
                }
            }
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
        systemMenuBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                uiUtilities.changeScene(mouseEvent, "/Systems");
            }
        });

        deleteBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                deleteDialog(mouseEvent);
            }
        });
    }

    /**
     * Send the asset ID to the Database class in order for it to be deleted.
     *
     * @author Jeff
     */
    public void deleteAsset() {
        assetDAOImpl.deleteAssetByID(system.getId());
    }

    /**
     * Creates a dialog box that asks user if they want to delete an asset.
     *
     * @param mouseEvent
     */
    void deleteDialog(MouseEvent mouseEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(ALERT_TITLE);
        alert.setHeaderText(ALERT_HEADER);
        alert.setContentText(ALERT_CONTENT);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            deleteAsset();
            uiUtilities.changeScene(mouseEvent, "/Systems");
        }
    }

    /**
     * Set the minimum and maximum values in the X or Y axis on a graph.
     *
     * @param sensorChart
     * @param min
     * @param max
     * @param isXAxis
     */
    public void setAxisBounds(LineChart<Number, Number> sensorChart, double min, double max, boolean isXAxis) {
        NumberAxis axis;
        if(isXAxis) {
            axis = (NumberAxis) sensorChart.getXAxis();
        }
        else {
            axis = (NumberAxis) sensorChart.getYAxis();
        }
        axis.setAutoRanging(false);
        axis.setLowerBound(min);
        axis.setUpperBound(max);
    }

    /**
     * Returns the lowest sensor measurement from an arrayList of measurements objects.
     *
     * @param measurements
     * @return
     */
    double getLowestMeasurement( ArrayList<Measurement> measurements) {
        double minValue = 0.0;
        if (!measurements.isEmpty()){
            minValue = measurements.get(0).getValue();
            for (Measurement m : measurements)
                if (minValue > m.getValue())
                    minValue = m.getValue();
        }
        return minValue;
    }

    /**
     * Returns the highest sensor measurement from a map.
     *
     * @param measurements
     * @return
     */
    double getHighestMeasurement(ArrayList<Measurement> measurements) {
        double maxValue = 0.0;
        if (!measurements.isEmpty()){
            maxValue = measurements.get(0).getValue();
            for (Measurement m : measurements)
                if (maxValue < m.getValue())
                    maxValue = m.getValue();
        }
        return maxValue;
    }
}
