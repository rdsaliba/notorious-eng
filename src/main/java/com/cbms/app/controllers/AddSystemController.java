package com.cbms.app.controllers;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AddSystemController implements Initializable {

    @FXML
    private Button systemMenuButton;
    @FXML
    private Button cancelButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
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
        cancelButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
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
