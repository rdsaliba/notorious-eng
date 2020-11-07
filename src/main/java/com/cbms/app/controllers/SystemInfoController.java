package com.cbms.app.controllers;

import com.cbms.app.Sensor;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import com.cbms.app.Engine;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SystemInfoController implements Initializable {

    @FXML
    private Button systemMenuButton;
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


    private Engine system;


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
    void initData(Engine system) {
        this.system = system;
        systemName.setText(system.getName());
        systemType.setText(system.getType());
        serialNumber.setText(system.getSerialNumber());
        manufacturer.setText(system.getManufacturer());
        systemLocation.setText(system.getLocation());
        linearRUL.setText(String.valueOf(system.getLinearRUL()));
        lstmRUL.setText(String.valueOf(system.getLstmRUL()));
        constructSensorPanes();
    }

    /**
     * Constructs the sensor panes to be able to display data in a nice format.
     *
     * @author Jeff
     */
    public void constructSensorPanes() {
        for (Sensor sensor: system.getSensors()) {
            Pane pane = new Pane();
            pane.getStyleClass().add("sensorPane");
            //LineChart sensorChat = new LineChart();
            Text sensorType = new Text(sensor.getType());
            Text sensorLocation = new Text(sensor.getLocation());
            sensorType.setId("sensorType");
            sensorType.setLayoutX(35.0);
            sensorType.setLayoutY(191.0);

            sensorLocation.setId("sensorLocation");
            sensorLocation.setLayoutX(35.0);
            sensorLocation.setLayoutY(211.0);

            pane.getChildren().add(sensorType);
            pane.getChildren().add(sensorLocation);

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
                try {
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("/Systems.fxml"));
                    Parent systemsParent = loader.load();
                    Scene systemInfo = new Scene(systemsParent);

                    Stage window = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
                    window.setScene(systemInfo);
                    window.show();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
