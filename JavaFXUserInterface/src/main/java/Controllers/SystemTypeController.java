package Controllers;

import Utilities.SystemTypeList;
import Utilities.TextConstants;
import Utilities.UIUtilities;
import app.item.AssetType;
import external.AssetTypeDAOImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class SystemTypeController implements Initializable {

    //Configure the table and columns
    @FXML
    private TableView<SystemTypeList> tableView;
    @FXML
    private TableColumn<SystemTypeList, String> columnName;
    @FXML
    private TableColumn<SystemTypeList, Integer> columnLiveAssets;
    @FXML
    private TableColumn<SystemTypeList, Integer> columnArchivedAssets;
    @FXML
    private TableColumn<SystemTypeList, Double> columnOk;
    @FXML
    private TableColumn<SystemTypeList, Double> columnAdvisory;
    @FXML
    private TableColumn<SystemTypeList, Double> columnCaution;
    @FXML
    private TableColumn<SystemTypeList, Double> columnWarning;
    @FXML
    private TableColumn<SystemTypeList, Double> columnFailed;

    //Configure buttons
    @FXML
    private Button systemMenuBtn;
    @FXML
    private Button systemTypeMenuBtn;
    @FXML
    private Button addTypeBtn;

    private UIUtilities uiUtilities;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        uiUtilities = new UIUtilities();
        attachEvents();
        fillSystemTypeTable();
    }

    /**
     * Fill table with database information
     *
     * @author Shirwa
     */
    public void fillSystemTypeTable() {
        tableView.setItems(getSystemList());
        // When TableRow is clicked, send data to SystemTypeInfo scene.
        tableView.setRowFactory(tv -> {
            TableRow<SystemTypeList> row = new TableRow<>();
            row.setOnMouseClicked(event -> uiUtilities.changeScene(event, row, "/SystemTypeInfo", row.getItem()));
            return row;
        });
        UIUtilities.autoResizeColumns(tableView);
    }

    /**
     * This method will return an ObservableList of SystemList objects
     *
     * @author Shirwa, Paul
     * edit: There was an issue where the getAssetTypeIdCount() would not match in size to the assetTypeList()
     * and it would crash the system if there was an asset type with no assets associated to it
     * so this methode was rewrote
     */
    private ObservableList<SystemTypeList> getSystemList() {
        ObservableList<SystemTypeList> systemTypeList = FXCollections.observableArrayList();

        AssetTypeDAOImpl assetTypeDOA = new AssetTypeDAOImpl();

        ArrayList<AssetType> assetTypeList = assetTypeDOA.getAssetTypeList();

        for (AssetType assetType : assetTypeList) {
            systemTypeList.add(new SystemTypeList(
                    assetType,
                    assetTypeDOA.getAssetTypeIdCount(assetType.getId(), true),
                    assetTypeDOA.getAssetTypeIdCount(assetType.getId(), false),
                    assetTypeDOA.getAssetTypeBoundary(assetType.getId(), TextConstants.OK_THRESHOLD),
                    assetTypeDOA.getAssetTypeBoundaryCount(assetType.getId(), TextConstants.OK_THRESHOLD),
                    assetTypeDOA.getAssetTypeBoundary(assetType.getId(), TextConstants.CAUTION_THRESHOLD),
                    assetTypeDOA.getAssetTypeBoundaryCount(assetType.getId(), TextConstants.CAUTION_THRESHOLD),
                    assetTypeDOA.getAssetTypeBoundary(assetType.getId(), TextConstants.ADVISORY_THRESHOLD),
                    assetTypeDOA.getAssetTypeBoundaryCount(assetType.getId(), TextConstants.ADVISORY_THRESHOLD),
                    assetTypeDOA.getAssetTypeBoundary(assetType.getId(), TextConstants.WARNING_THRESHOLD),
                    assetTypeDOA.getAssetTypeBoundaryCount(assetType.getId(), TextConstants.WARNING_THRESHOLD),
                    assetTypeDOA.getAssetTypeBoundary(assetType.getId(), TextConstants.FAILED_THRESHOLD),
                    assetTypeDOA.getAssetTypeBoundaryCount(assetType.getId(), TextConstants.FAILED_THRESHOLD)

            ));
        }
        return systemTypeList;
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

        //Attach link to systemMenuButton to go to Systems.fxml
        systemMenuBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, "/Systems"));

        //Attach link to systemTypeMenuBtn to go to Utilities.SystemTypeList.fxml
        systemTypeMenuBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, "/SystemTypeList"));

        //Attach link to addTypeBtn to go to AddSystemType.fxml
        addTypeBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, "/AddSystemType"));
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
        columnOk.setCellValueFactory(new PropertyValueFactory<>("countOk"));
        columnAdvisory.setCellValueFactory(new PropertyValueFactory<>("countAdvisory"));
        columnCaution.setCellValueFactory(new PropertyValueFactory<>("countCaution"));
        columnWarning.setCellValueFactory(new PropertyValueFactory<>("countWarning"));
        columnFailed.setCellValueFactory(new PropertyValueFactory<>("countFailed"));
    }
}
