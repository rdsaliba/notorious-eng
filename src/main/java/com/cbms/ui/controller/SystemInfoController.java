package com.cbms.ui.controller;

import com.cbms.app.item.Asset;
import com.cbms.app.item.AssetAttribute;
import com.cbms.source.local.Database;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

public class SystemInfoController implements Initializable {

    @FXML
    private Button systemMenuButton;
    @FXML
    private Button deletebtn;
    @FXML
    private AnchorPane systemInfoPane;
    @FXML
    private Text systemName;
    @FXML
    private Text systemType;
    @FXML
    private Text serialNumber;
    @FXML
    private Text manufacturer;
    @FXML
    private Text systemLocation;
    @FXML
    private Text linearRUL;
    @FXML
    private Text lstmRUL;
    @FXML
    private FlowPane sensorFlowPane;


    private Asset system;
    private Database db;
    private UIUtilities uiUtilities;


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
        db = new Database();
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
        systemName.setText(system.getAssetTypeID() + system.getSerialNo());
        systemType.setText(system.getAssetTypeID());
        serialNumber.setText(system.getSerialNo());
        manufacturer.setText("");
        systemLocation.setText("Location: ");
        linearRUL.setText("Linear RUL: " + new DecimalFormat("#.##").format(system.getAssetInfo().getRULMeasurement()));
        lstmRUL.setText("Description: ");
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
            xAxis.setLabel("Cycle");
            final LineChart<Number, Number> sensorChart =
                    new LineChart<>(xAxis, yAxis);
            sensorChart.setTitle("Sensor Values");
            XYChart.Series series = new XYChart.Series();
            Map<Integer, Double> measurements = sensor.getMeasurements();
            series.getData().add(new XYChart.Data(1, measurements.get(1)));
            series.getData().add(new XYChart.Data(2, measurements.get(2)));
            series.getData().add(new XYChart.Data(3, measurements.get(3)));
            series.getData().add(new XYChart.Data(4, measurements.get(4)));
            series.getData().add(new XYChart.Data(5, measurements.get(5)));
            sensorChart.getData().add(series);
            sensorChart.setPrefWidth(275.0);
            sensorChart.setPrefHeight(163.0);
            sensorChart.setLayoutX(12.0);
            sensorChart.setLayoutY(12.0);
            pane.getChildren().add(sensorChart);


            Text sensorName = new Text(sensor.getName());
            //Text sensorLocation = new Text(sensor.getLocation());
            sensorName.setId("sensorType");
            sensorName.setLayoutX(35.0);
            sensorName.setLayoutY(191.0);

            //sensorLocation.setId("sensorLocation");
            //sensorLocation.setLayoutX(35.0);
            //sensorLocation.setLayoutY(211.0);

            pane.getChildren().add(sensorName);
            //pane.getChildren().add(sensorLocation);

            sensorFlowPane.getChildren().add(pane);
        }
    }


    /**
     * Attaches events to elements in the scene.
     *
     * @author Jeff
     */
    public void attachEvents() {
        systemMenuButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                uiUtilities.changeScene(mouseEvent, "/Systems");
            }
        });

        deletebtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation Dialog");
                alert.setHeaderText("Confirmation of system deletion");
                alert.setContentText("Are you sure you want to delete this system?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK){
                    deleteAsset();
                    uiUtilities.changeScene(mouseEvent, "/Systems");
                }
            }
        });
    }

    /**
     * Send the asset ID to the Database class in order for it to be deleted.
     *
     * @author Jeff
     */
    public void deleteAsset() {
        db.deleteAssetByID(system.getId());
    }
}
