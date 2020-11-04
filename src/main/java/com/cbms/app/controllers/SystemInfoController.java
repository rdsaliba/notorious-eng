package com.cbms.app.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import com.cbms.app.Engine;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class SystemInfoController implements Initializable {

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


    private Engine system;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    void initData(Engine system) {
        this.system = system;
        systemName.setText(system.getName());
        systemType.setText(system.getType());
        serialNumber.setText(system.getSerialNumber());
        manufacturer.setText(system.getManufacturer());
        systemLocation.setText(system.getLocation());
        linearRUL.setText(String.valueOf(system.getLinearRUL()));
        lstmRUL.setText(String.valueOf(system.getLstmRUL()));
    }
}
