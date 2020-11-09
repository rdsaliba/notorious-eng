package com.cbms.ui.controller;

import com.cbms.app.ModelController;
import com.cbms.app.item.Asset;
import com.cbms.app.item.AssetAttribute;
import com.cbms.app.item.AssetInfo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class SystemsController implements Initializable {

    @FXML
    private Button systemMenuButton;
    @FXML
    private Button addSystemButton;
    @FXML
    private FlowPane systemsPane;

    private ObservableList<Pane> boxes = FXCollections.observableArrayList();

    //private ArrayList<Engine> systems;
    private ArrayList<Asset> systems;

    public SystemsController() {

    }

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

        try {
                systems = ModelController.getInstance().estimate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        constructElements();
    }

    /**
     * Creates elements that are in the scene so the data can be displayed.
     *
     * @author Jeff
     */
    public void constructElements() {
        for (Asset system: systems) {
            Pane pane = new Pane();

            //Attach link to systemMenuButton to go to Systems.fxml
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

            //Attach link to addSystemButton to go to AddSystem.fxml
            addSystemButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    try {
                        FXMLLoader loader = new FXMLLoader();
                        loader.setLocation(getClass().getResource("/AddSystem.fxml"));
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

            //When clicked on a system, open SystemInfo.FXML for that system.
            pane.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    Stage primaryStage = (Stage) pane.getScene().getWindow();
                    try {

                        FXMLLoader loader = new FXMLLoader();
                        loader.setLocation(getClass().getResource("/SystemInfo.fxml"));
                        Parent systemsParent = loader.load();
                        Scene systemInfo = new Scene(systemsParent);

                        Stage window = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
                        window.setScene(systemInfo);
                        SystemInfoController controller = loader.getController();
                        controller.initData(system);
                        window.show();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            pane.getStyleClass().add("systemPane");
            Text systemName = new Text(system.getSerialNo());
            Text systemType = new Text(system.getAssetType());
            Text linearLabel = new Text("Linear Regression RUL:");
            Text linearRUL = new Text(String.valueOf(new DecimalFormat("#.##").format(system.getAssetInfo().getRULMeasurement())));
            //Text lstmLabel = new Text("LSTM RUL:");
            //Text lstmRUL = new Text(String.valueOf(system.getLstmRUL()));

            systemName.setId("systemName");
            systemType.setId("systemType");
            linearLabel.setId("linearLabel");
            linearRUL.setId("linearRUL");
            //lstmLabel.setId(("lstmLabel"));
            //lstmRUL.setId("lstmRUL");

            systemName.setLayoutX(14.0);
            systemName.setLayoutY(28.0);
            systemType.setLayoutX(14.0);
            systemType.setLayoutY(60.0);
            linearLabel.setLayoutX(14.0);
            linearLabel.setLayoutY(121.0);
            linearRUL.setLayoutX(230.0);
            linearRUL.setLayoutY(120.0);
            //lstmLabel.setLayoutX(14.0);
            //lstmLabel.setLayoutY(190.0);
            //lstmRUL.setLayoutX(250.0);
            //lstmRUL.setLayoutY(190.0);

            pane.getChildren().add(systemName);
            pane.getChildren().add(systemType);
            pane.getChildren().add(linearLabel);
            pane.getChildren().add(linearRUL);
            //pane.getChildren().add(lstmLabel);
            //pane.getChildren().add(lstmRUL);

            boxes.add(pane);
        }

        systemsPane.getChildren().addAll(boxes);
    }
}
