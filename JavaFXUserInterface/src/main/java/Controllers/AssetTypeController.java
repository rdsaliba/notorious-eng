/*
  This Controller is responsible for generating the list of asset types.
  @author Najim, Shirwa, Paul
  @last_edit 02/7/2020
 */
package Controllers;

import Utilities.AssetTypeList;
import Utilities.TextConstants;
import Utilities.UIUtilities;
import app.item.AssetType;
import external.AssetTypeDAOImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class AssetTypeController implements Initializable {

    //Configure the table and columns
    @FXML
    private TableView<AssetTypeList> tableView;
    @FXML
    private TableColumn<AssetTypeList, String> columnName;
    @FXML
    private TableColumn<AssetTypeList, Integer> columnLiveAssets;
    @FXML
    private TableColumn<AssetTypeList, Integer> columnArchivedAssets;
    @FXML
    private TableColumn<AssetTypeList, Double> columnOk;
    @FXML
    private TableColumn<AssetTypeList, Double> columnAdvisory;
    @FXML
    private TableColumn<AssetTypeList, Double> columnCaution;
    @FXML
    private TableColumn<AssetTypeList, Double> columnWarning;
    @FXML
    private TableColumn<AssetTypeList, Double> columnFailed;



    //Configure buttons
    @FXML
    private Button assetMenuBtn;
    @FXML
    private Button assetTypeMenuBtn;
    @FXML
    private Button addTypeBtn;

    private UIUtilities uiUtilities;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        uiUtilities = new UIUtilities();
        attachEvents();
        fillAssetTypeTable();
    }

    /**
     * Fill table with database information
     *
     * @author Shirwa
     */
    public void fillAssetTypeTable() {
        tableView.setItems(getAssetList());
        // When TableRow is clicked, send data to AssetTypeInfo scene.
        tableView.setRowFactory(tv -> {
            TableRow<AssetTypeList> row = new TableRow<>();
            row.setOnMouseClicked(event -> uiUtilities.changeScene(event, row, "/AssetTypeInfo", row.getItem()));
            return row;
        });
        UIUtilities.autoResizeColumns(tableView);
    }

    /**
     * This method will return an ObservableList of AssetList objects
     *
     * @author Shirwa, Paul
     * edit: There was an issue where the getAssetTypeIdCount() would not match in size to the assetTypeList()
     * and it would crash the asset if there was an asset type with no assets associated to it
     * so this methode was rewrote
     */
    private ObservableList<AssetTypeList> getAssetList() {
        ObservableList<AssetTypeList> assettypelist = FXCollections.observableArrayList();

        AssetTypeDAOImpl assetTypeDOA = new AssetTypeDAOImpl();

        ArrayList<AssetType> assetTypeList = assetTypeDOA.getAssetTypeList();

        for (AssetType assetType : assetTypeList) {
            assettypelist.add(new AssetTypeList(
                    assetType,
                    assetTypeDOA.getAssetTypeIdCount(assetType.getId(), true),
                    assetTypeDOA.getAssetTypeIdCount(assetType.getId(), false),
                    assetTypeDOA.getAssetTypeThreshold(assetType.getId(), TextConstants.OK_THRESHOLD),
                    assetTypeDOA.getAssetTypeThreshold(assetType.getId(), TextConstants.CAUTION_THRESHOLD),
                    assetTypeDOA.getAssetTypeThreshold(assetType.getId(), TextConstants.ADVISORY_THRESHOLD),
                    assetTypeDOA.getAssetTypeThreshold(assetType.getId(), TextConstants.WARNING_THRESHOLD),
                    assetTypeDOA.getAssetTypeThreshold(assetType.getId(), TextConstants.FAILED_THRESHOLD)

            ));
        }
        return assettypelist;
    }

    /**
     * Adds mouse events to all the buttons and allows columns to be filled with
     * give information
     *
     * @author Shirwa
     */
    public void attachEvents() {
        //set up the columns in the table
        attachColumnEvents();

        //Attach link to assetMenuButton to go to Assets.fxml
        assetMenuBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, TextConstants.ASSETS_SCENE));

        //Attach link to assetTypeMenuBtn to go to Utilities.AssetTypeList.fxml
        assetTypeMenuBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, TextConstants.ASSET_TYPE_LIST_SCENE));

        //Attach link to addTypeBtn to go to AddAssetType.fxml
        addTypeBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, "/AddAssetType"));
    }

    /**
     * add the column events so they get filled with the correct information
     *
     * @author Paul
     */
    public void attachColumnEvents() {
        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnLiveAssets.setCellValueFactory(new PropertyValueFactory<>("liveAssets"));
        columnArchivedAssets.setCellValueFactory(new PropertyValueFactory<>("archivedAssets"));
        columnOk.setCellValueFactory(new PropertyValueFactory<>("valueOk"));
        columnAdvisory.setCellValueFactory(new PropertyValueFactory<>("valueAdvisory"));
        columnCaution.setCellValueFactory(new PropertyValueFactory<>("valueCaution"));
        columnWarning.setCellValueFactory(new PropertyValueFactory<>("valueWarning"));
        columnFailed.setCellValueFactory(new PropertyValueFactory<>("valueFailed"));
    }
}
