package com.cbms.app.controllers;

import com.cbms.app.Engine;
import com.cbms.app.Sensor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class SystemsController implements Initializable {

    @FXML
    private FlowPane systemsPane;

    private ObservableList<Pane> boxes = FXCollections.observableArrayList();

    private ArrayList<Engine> systems;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        createFakeListOfSystems();
        for (Engine system: systems) {
            Pane pane = new Pane();
            pane.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    Stage primaryStage = (Stage) pane.getScene().getWindow();
                    try {

                        //Parent newRoot = FXMLLoader.load(getClass().getResource("/SystemInfo.fxml"));
                        //primaryStage.getScene().setRoot(newRoot);

                        FXMLLoader loader = new FXMLLoader();
                        loader.setLocation(getClass().getResource("/SystemInfo.fxml"));
                        Parent systemsParent = loader.load();
                        Scene systemInfo = new Scene(systemsParent);

                        Stage window = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
                        window.setScene(systemInfo);
                        SystemInfoController controller = loader.getController();
                        controller.initData(system);
                        window.show();


                        /*Stage st = new Stage();
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/SystemInfo.fxml"));
                        Region root = (Region) loader.load();
                        Scene scene = new Scene(root);
                        st.setScene(scene);
                        SystemInfoController controller = loader.getController();
                        controller.initData(system);
                        st.show();*/


                        /*FXMLLoader loader = new FXMLLoader(getClass().getResource("/SystemInfo.fxml"));
                        Stage stage = new Stage(StageStyle.DECORATED);
                        stage.setScene(new Scene(loader.load()));
                        SystemInfoController controller = loader.getController();
                        controller.initData(system);
                        stage.show();*/

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            pane.getStyleClass().add("systemPane");
            //pane.setPrefHeight(231.0);
            //pane.setPrefWidth(298.0);

            Text systemName = new Text(system.getName());
            Text systemType = new Text("Type");
            Text linearLabel = new Text("Linear Regression RUL:");
            Text linearRUL = new Text(String.valueOf(system.getLinearRUL()));
            Text lstmLabel = new Text("LSTM RUL:");
            Text lstmRUL = new Text(String.valueOf(system.getLstmRUL()));

            systemName.setId("systemName");
            systemType.setId("systemType");
            linearLabel.setId("linearLabel");
            linearRUL.setId("linearRUL");
            lstmLabel.setId(("lstmLabel"));
            lstmRUL.setId("lstmRUL");

            systemName.setLayoutX(14.0);
            systemName.setLayoutY(28.0);
            systemType.setLayoutX(14.0);
            systemType.setLayoutY(60.0);
            linearLabel.setLayoutX(14.0);
            linearLabel.setLayoutY(121.0);
            linearRUL.setLayoutX(250.0);
            linearRUL.setLayoutY(120.0);
            lstmLabel.setLayoutX(14.0);
            lstmLabel.setLayoutY(190.0);
            lstmRUL.setLayoutX(250.0);
            lstmRUL.setLayoutY(190.0);

            pane.getChildren().add(systemName);
            pane.getChildren().add(systemType);
            pane.getChildren().add(linearLabel);
            pane.getChildren().add(linearRUL);
            pane.getChildren().add(lstmLabel);
            pane.getChildren().add(lstmRUL);

            boxes.add(pane);
        }

        systemsPane.getChildren().addAll(boxes);
    }

    /**
     * To be deleted
     */
    public void createFakeListOfSystems() {
        systems = new ArrayList<Engine>();
        for(int i = 1; i < 10; i++) {

            Sensor sensorA = new Sensor(123, new double[]{123,123,132,123,123},
                    new double[]{123,123,132,123,123}, "location", "type");
            Sensor sensorB = new Sensor(123, new double[]{123,123,132,123,123},
                    new double[]{123,123,132,123,123}, "location", "type");
            Sensor sensorC = new Sensor(123, new double[]{123,123,132,123,123},
                    new double[]{123,123,132,123,123}, "location", "type");
            Engine system = new Engine(123+i, 123+i, "System " + i, "Some Type", "34589ghg9", "Jeff Manufacturer",
                    "The back room", new Sensor[]{sensorA, sensorB, sensorC});
            systems.add(system);

        }
    }
}
