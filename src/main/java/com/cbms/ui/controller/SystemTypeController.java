package com.cbms.ui.controller;

import com.cbms.app.item.AssetTypeParameter;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class SystemTypeController implements Initializable {

    @FXML
    private Button systemMenuBtn;
    @FXML
    private Button cancelBtn;
    @FXML
    private Button addThresholdBtn;
    @FXML
    private AnchorPane systemTypeInformation;
    @FXML
    private Button saveBtn;
    @FXML
    private TextField systemTypeName;
    @FXML
    private TextArea systemTypeDesc;
    @FXML
    private Button editBtn;
    @FXML
    private Button deleteBtn;
    @FXML
    private Button addTypeBtn;

    private UIUtilities uiUtilities;
    private ArrayList<AssetTypeParameter> assetTypeParameters;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
