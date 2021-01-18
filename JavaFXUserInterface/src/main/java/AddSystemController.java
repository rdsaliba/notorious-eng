import app.item.Asset;
import app.item.AssetType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.util.StringConverter;
import local.AssetDAOImpl;
import local.AssetTypeDAOImpl;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class AddSystemController implements Initializable {

    @FXML
    public Button systemMenuBtn;
    @FXML
    private Button cancelBtn;
    @FXML
    private Button saveBtn;
    @FXML
    private ChoiceBox<AssetType> systemTypeChoiceBox;
    @FXML
    private TextField systemNameInput;
    @FXML
    private TextArea systemDescriptionTextArea;
    @FXML
    private TextField serialNumberInput;
    @FXML
    private TextField manufacturerInput;
    @FXML
    private TextField categoryInput;
    @FXML
    private TextField siteInput;
    @FXML
    private TextField locationInput;

    private static ObservableList<AssetType> assetTypeNamesList;
    private AssetDAOImpl assetDAOImpl;
    private AssetTypeDAOImpl assetTypeDAOImpl;
    private UIUtilities uiUtilities;
    private AssetType selectedAssetType;
    private final String SAVE_DIALOG = "Save Dialog";
    private final String SAVE_HEADER = "Asset has been saved to the database.";
    private final String ERROR_DIALOG = "Error Dialog";
    private final String ERROR_HEADER = "Please enter values for all text fields.";

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
        assetDAOImpl = new AssetDAOImpl();
        assetTypeDAOImpl = new AssetTypeDAOImpl();
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
        systemTypeChoiceBox.valueProperty().addListener((obs, oldval, newval) -> {
            if(newval != null)
                selectedAssetType = newval;
        });
        saveBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                Asset newAsset = assembleAsset();
                if(!isAssetEmpty(newAsset)) {
                    saveAsset(newAsset);
                    saveDialog(mouseEvent);
                }
                else {
                    errorDialog(mouseEvent);
                }
            }
        });
        // Change scenes to Systems.fxml
        systemMenuBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, "/Systems"));
        // Change scenes to Systems.fxml
        cancelBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, "/Systems"));
    }

    /**
     * Initializes the default and possible values for all fields that can accept user input. For example,
     * it establishes the possible dropdown values for the system type selection.
     */
    /**
     * Initializes the default and possible values for all fields that can accept user input. For example,
     * it establishes the possible dropdown values for the system type selection.
     */
    public void initializeFieldValues() {
        // Establishes the asset types available for selection in the dropdown
        assetTypeNamesList = FXCollections.observableArrayList(assetTypeDAOImpl.getAssetTypeList());
        systemTypeChoiceBox.setItems(assetTypeNamesList);
        systemTypeChoiceBox.setValue(systemTypeChoiceBox.getItems().get(0));
        systemTypeChoiceBox.setConverter(new StringConverter<AssetType>() {
            @Override
            public String toString(AssetType assetType) {
                return assetType.getName();
            }

            @Override
            public AssetType fromString(String s) {
                return systemTypeChoiceBox.getItems().stream().filter(ap ->
                        ap.getName().equals(s)).findFirst().orElse(null);
            }
        });
    }

    /**
     * Assembles all the TextField data and create an Asset object.
     *
     * @return
     */
    public Asset assembleAsset() {
        Asset newAsset = new Asset();
        newAsset.setName(systemNameInput.getText());
        newAsset.setAssetTypeID(selectedAssetType.getId());
        newAsset.setDescription(systemDescriptionTextArea.getText());
        newAsset.setSerialNo(serialNumberInput.getText());
        newAsset.setManufacturer(manufacturerInput.getText());
        newAsset.setCategory(categoryInput.getText());
        newAsset.setSite(siteInput.getText());
        newAsset.setLocation(locationInput.getText());
        return newAsset;
    }

    /**
     * Creates an ObservableList of asset type names.
     *
     * @param assetTypeList
     * @return
     */
    public ObservableList<String> getAssetTypeNameList(ObservableList<AssetType> assetTypeList) {
        ObservableList<String> assetTypeNames = FXCollections.observableArrayList();
        for (AssetType assetType:assetTypeList) {
            assetTypeNames.add(assetType.getName());
        }
        return assetTypeNames;
    }

    /**
     * Sends the new asset to be inserted in the database
     *
     * @param newAsset
     */
    public void saveAsset(Asset newAsset) {
        assetDAOImpl.insertAsset(newAsset);
    }

    /**
     * Creates a dialog to alert the user that an asset was saved to the database
     *
     * @param mouseEvent
     */
    void saveDialog(MouseEvent mouseEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(SAVE_DIALOG);
        alert.setHeaderText(SAVE_HEADER);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            uiUtilities.changeScene(mouseEvent, "/Systems");
        }
    }

    /**
     * Creates a dialog to
     *
     * @param mouseEvent
     */
    void errorDialog(MouseEvent mouseEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(ERROR_DIALOG);
        alert.setHeaderText(ERROR_HEADER);

        Optional<ButtonType> result = alert.showAndWait();
    }

    /**
     *  Checks to see if values of the asset are filled.
     *
     * @param asset
     * @return
     */
    boolean isAssetEmpty(Asset asset) {
        if(asset.getName().equals("") || asset.getAssetTypeID().equals("") || asset.getDescription().equals("") ||
                asset.getSerialNo().equals("") || asset.getManufacturer().equals("") || asset.getCategory().equals("") || asset.getSite().equals("") || asset.getLocation().equals(""))
            return true;
        else
            return false;
    }
}
