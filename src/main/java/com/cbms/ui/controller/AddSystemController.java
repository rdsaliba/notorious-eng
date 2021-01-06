package com.cbms.ui.controller;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

import java.sql.ResultSet;

public class AddSystemController implements Initializable {

    @FXML
    public Button systemMenuBtn;
    @FXML
    private Button cancelBtn;
    @FXML
    private Button saveBtn;
    @FXML
    private TextArea systemDescriptionTextArea;
    @FXML
    private ChoiceBox systemTypeChoiceBox;

    private static final String GET_ASSET_TYPES = "SELECT name FROM asset_type";
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
        initializeFieldValues();
        systemDescriptionTextArea.setWrapText(true);
    }

    /**
     * Attaches events to elements in the scene.
     *
     * @author Jeff
     */
    public void attachEvents() {
        // Change scenes to Systems.fxml
        systemMenuBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, "/Systems"));
        // Change scenes to Systems.fxml
        cancelBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, "/Systems"));
    }

    /**
     * Initializes the default and possible values for all fields that can accept user input. For example,
     * it establishes the possible dropdown values for the system type selection.
     */
    public void initializeFieldValues(Connection con) {
        try (Statement stmt = con.createStatement();) {
            ResultSet rs = stmt.executeQuery(GET_ASSET_TYPES);
            while (rs.next()) {
                systemTypeChoiceBox.getItems().add(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void executeStatement(Connection con) {
        try(Statement stmt = con.createStatement();) {
            String SQL = "SELECT LastName, FirstName FROM Person.Contact ORDER BY LastName";
            ResultSet rs = stmt.executeQuery(SQL);

            while (rs.next()) {
                System.out.println(rs.getString("LastName") + ", " + rs.getString("FirstName"));
            }
        }
        // Handle any errors that may have occurred.
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    }
}
