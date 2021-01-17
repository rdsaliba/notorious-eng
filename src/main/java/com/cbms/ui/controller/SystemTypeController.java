package com.cbms.ui.controller;

import com.cbms.SystemList;
import com.cbms.app.item.AssetTypeParameter;
import com.cbms.source.local.AssetDAOImpl;
import com.cbms.source.local.AssetTypeDAOImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import org.nd4j.shade.jackson.databind.deser.impl.PropertyValue;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class SystemTypeController implements Initializable {

    //Configure the table
    @FXML private TableView<SystemList> tableView;
    @FXML private TableColumn<SystemList,String> columnName;
    @FXML private TableColumn<SystemList,Integer> columnAssociatedAssets;

    private UIUtilities uiUtilities;
    private ArrayList<AssetTypeParameter> assetTypeParameters;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //set up the columns in the table
        columnName.setCellValueFactory(new PropertyValueFactory<SystemList,String>("name"));
        columnAssociatedAssets.setCellValueFactory(new PropertyValueFactory<SystemList,Integer>("associated_assets"));

        tableView.setItems(getSystemList());
    }

    //This method will return an ObservableList of People objects
    private ObservableList<SystemList> getSystemList() {
        ObservableList<SystemList> systemlist = FXCollections.observableArrayList();

        AssetTypeDAOImpl assetTypeDOA = new AssetTypeDAOImpl();
        ArrayList<String> systemlistname = assetTypeDOA.getAssetTypeList();

        AssetDAOImpl assetDAO = new AssetDAOImpl();
        ArrayList<Integer> systemlistcount = assetDAO.getAssetTypeIdCount();

        for(int i=0;i<systemlistname.size();i++){
            systemlist.add(new SystemList(systemlistname.get(i),systemlistcount.get(i)));
        }

        return systemlist;
    }
}
