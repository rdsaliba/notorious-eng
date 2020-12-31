package com.cbms.ui.controller;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import java.net.URL;
import java.util.ResourceBundle;

public class AddSystemTypeController implements Initializable {

    @FXML
    private Button systemMenuButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Button addThresholdBtn;
    @FXML
    private AnchorPane systemTypeInformation;
    @FXML
    private Button saveBtn;

    private int thresholdCount = 1;
    private double spacing = 40.0;
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
        uiUtilities = new UIUtilities();
        attachEvents();
    }

    /**
     * Attaches events to elements in the scene.
     *
     * @author Jeff
     */
    public void attachEvents() {
        // Change scenes to Systems.fxml
        systemMenuButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                uiUtilities.changeScene(mouseEvent, "/Systems");
            }
        });
        // Change scenes to Systems.fxml
        cancelButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                uiUtilities.changeScene(mouseEvent, "/Systems");
            }
        });
        addThresholdBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                addThresholdForm();
            }
        });
        saveBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                saveSystemType();
            }
        });
    }

    public void addThresholdForm() {
        Text thresholdLabel = new Text("Threshold");
        thresholdLabel.setFill(Paint.valueOf("#0c072e"));
        thresholdLabel.setLayoutX(86.0);
        thresholdLabel.setLayoutY(193.0 + (spacing * thresholdCount));
        thresholdLabel.setStrokeType(StrokeType.OUTSIDE);
        thresholdLabel.setFont(Font.font("Segoe UI Bold", 14));

        TextField thresholdTextField = new TextField();
        thresholdTextField.setLayoutX(181.0);
        thresholdTextField.setLayoutY(175.0 + (spacing * thresholdCount));
        thresholdTextField.setPrefHeight(25.0);
        thresholdTextField.setPrefWidth(350.0);

        systemTypeInformation.getChildren().add(thresholdLabel);
        systemTypeInformation.getChildren().add(thresholdTextField);
        thresholdCount++;
    }

    public void saveSystemType() {

    }
}
