import app.item.AssetType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import local.AssetTypeDAOImpl;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class SystemTypeController implements Initializable {

    //Configure the table and columns
    @FXML private TableView<SystemTypeList> tableView;
    @FXML private TableColumn<SystemTypeList,String> columnName;
    @FXML private TableColumn<SystemTypeList,Integer> columnAssociatedAssets;
    @FXML private TableColumn<SystemTypeList,Double> columnOk;
    @FXML private TableColumn<SystemTypeList,Double> columnAdvisory;
    @FXML private TableColumn<SystemTypeList,Double> columnCaution;
    @FXML private TableColumn<SystemTypeList,Double> columnWarning;
    @FXML private TableColumn<SystemTypeList,Double> columnFailed;

    //Configure buttons
    @FXML private Button systemMenuBtn;
    @FXML private Button systemTypeMenuBtn;
    @FXML private Button exitMenuBtn;
    @FXML private Button addTypeBtn;

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
    }

    /**
     * This method will return an ObservableList of SystemList objects
     *
     * @author Shirwa
     */
    private ObservableList<SystemTypeList> getSystemList() {
        ObservableList<SystemTypeList> systemtypelist = FXCollections.observableArrayList();

        AssetTypeDAOImpl assetTypeDOA = new AssetTypeDAOImpl();

        ArrayList<AssetType> systemtypelistname = assetTypeDOA.getAssetTypeList();
        ArrayList<Integer> systemtypelistcount = assetTypeDOA.getAssetTypeIdCount();

        for(int i=0;i<systemtypelistname.size();i++){
            //For loop to define columns
            double fail_value = 0,warning_value=0,caution_value=0,advisory_value=0,ok_value=0;

            /*
            * Each boundary type corresponds to the given number
            * 0 - Failed
            * 1 - Warning
            * 2 - Caution
            * 3 - Advisory
            * 4 - Ok
            * */
            fail_value = assetTypeDOA.getAssetTypeBoundary(i+ 1, 0);
            warning_value = assetTypeDOA.getAssetTypeBoundary(i + 1, 1);
            caution_value = assetTypeDOA.getAssetTypeBoundary(i + 1, 2);
            advisory_value = assetTypeDOA.getAssetTypeBoundary(i + 1, 3);
            ok_value = assetTypeDOA.getAssetTypeBoundary(i + 1, 4);

            systemtypelist.add(new SystemTypeList(systemtypelistname.get(i).getName(),systemtypelistcount.get(i),ok_value,advisory_value,caution_value,warning_value,fail_value));
        }

        return systemtypelist;
    }

    /**
     * Adds mouse events to all the buttons and allows columns to be filled with
     * give information
     *
     * @author Shirwa
     */
    public void attachEvents() {
        //set up the columns in the table
        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnAssociatedAssets.setCellValueFactory(new PropertyValueFactory<>("associated_assets"));
        columnOk.setCellValueFactory(new PropertyValueFactory<>("value_ok"));
        columnAdvisory.setCellValueFactory(new PropertyValueFactory<>("value_advisory"));
        columnCaution.setCellValueFactory(new PropertyValueFactory<>("value_caution"));
        columnWarning.setCellValueFactory(new PropertyValueFactory<>("value_warning"));
        columnFailed.setCellValueFactory(new PropertyValueFactory<>("value_failed"));

        //Attach link to systemMenuButton to go to Systems.fxml
        systemMenuBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, "/Systems"));

        //Attach link to systemTypeMenuBtn to go to SystemTypeList.fxml
        systemTypeMenuBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, "/SystemTypeList"));

        //Attach link to addTypeBtn to go to AddSystemType.fxml
        addTypeBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, "/AddSystemType"));
    }
}
