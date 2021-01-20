package com.cbms.ui.controller;

import com.cbms.SystemList;
import com.cbms.app.item.AssetTypeParameter;
import com.cbms.source.local.AssetDAOImpl;
import com.cbms.source.local.AssetTypeDAOImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.nd4j.shade.jackson.databind.deser.impl.PropertyValue;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class SystemTypeController implements Initializable {

    //Configure the table and columns
    @FXML private TableView<SystemList> tableView;
    @FXML private TableColumn<SystemList,String> columnName;
    @FXML private TableColumn<SystemList,Integer> columnAssociatedAssets;
    @FXML private TableColumn<SystemList,Double> columnOk;
    @FXML private TableColumn<SystemList,Double> columnAdvisory;
    @FXML private TableColumn<SystemList,Double> columnCaution;
    @FXML private TableColumn<SystemList,Double> columnWarning;
    @FXML private TableColumn<SystemList,Double> columnFailed;

    //Configure buttons
    @FXML private Button systemMenuBtn;
    @FXML private Button systemListBtn;
    @FXML private Button exitMenuBtn;
    @FXML private Button addTypeBtn;


    private UIUtilities uiUtilities;
    private ArrayList<AssetTypeParameter> assetTypeParameters;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        uiUtilities = new UIUtilities();
        attachEvents();
        //set up the columns in the table
        columnName.setCellValueFactory(new PropertyValueFactory<SystemList,String>("name"));
        columnAssociatedAssets.setCellValueFactory(new PropertyValueFactory<SystemList,Integer>("associated_assets"));
        columnOk.setCellValueFactory(new PropertyValueFactory<SystemList,Double>("value_ok"));
        columnAdvisory.setCellValueFactory(new PropertyValueFactory<SystemList,Double>("value_advisory"));
        columnCaution.setCellValueFactory(new PropertyValueFactory<SystemList,Double>("value_caution"));
        columnWarning.setCellValueFactory(new PropertyValueFactory<SystemList,Double>("value_warning"));
        columnFailed.setCellValueFactory(new PropertyValueFactory<SystemList,Double>("value_failed"));

        //Fill table with database information
        tableView.setItems(getSystemList());
    }

    /**
     * This method will return an ObservableList of SystemList objects
     *
     * @author Shirwa
     */
    private ObservableList<SystemList> getSystemList() {
        ObservableList<SystemList> systemlist = FXCollections.observableArrayList();

        AssetTypeDAOImpl assetTypeDOA = new AssetTypeDAOImpl();
        ArrayList<String> systemlistname = assetTypeDOA.getAssetTypeList();
        ArrayList<Integer> systemlistcount = assetTypeDOA.getAssetTypeIdCount();

        for(int i=0;i<systemlistname.size();i++){
            //For loop to define columns
            double fail_value = 0,warning_value=0,caution_value=0,advisory_value=0,ok_value=0;


            fail_value = assetTypeDOA.getAssetTypeBoundary(i+ 1, 0);
            warning_value = assetTypeDOA.getAssetTypeBoundary(i + 1, 1);
            caution_value = assetTypeDOA.getAssetTypeBoundary(i + 1, 2);
            advisory_value = assetTypeDOA.getAssetTypeBoundary(i + 1, 3);
            ok_value = assetTypeDOA.getAssetTypeBoundary(i + 1, 4);

            systemlist.add(new SystemList(systemlistname.get(i),systemlistcount.get(i),ok_value,advisory_value,caution_value,warning_value,fail_value));
        }

        return systemlist;
    }

    /**
     * Adds mouse events to all the buttons
     *
     * @author Shirwa
     */
    public void attachEvents() {
        //Attach link to systemMenuButton to go to Systems.fxml
        systemMenuBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                uiUtilities.changeScene(mouseEvent, "/Systems");
            }
        });

        //Attach link to addTypeBtn to go to AddSystemType.fxml
        addTypeBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                uiUtilities.changeScene(mouseEvent, "/AddSystemType");
            }
        });

        //Attach link to systemListBtn to go to SystemTypeList.fxml
        systemListBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                uiUtilities.changeScene(mouseEvent, "/SystemTypeList");
            }
        });

    }
}
