package Controllers;

import Utilities.UIUtilities;
import app.item.Asset;
import app.item.AssetType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

    private static final String SAVE_DIALOG = "Save Dialog";
    private static final String SAVE_HEADER = "Asset has been saved to the database.";
    private static final String ERROR_DIALOG = "Error Dialog";
    private static final String ERROR_HEADER = "Please enter values for all text fields.";
    private static final String SYSTEMS = "/Systems";
    @FXML
    public Button systemMenuBtn;
    @FXML
    private Button systemTypeMenuBtn;
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
    private AssetDAOImpl assetDAOImpl;
    private AssetTypeDAOImpl assetTypeDAOImpl;
    private UIUtilities uiUtilities;
    private AssetType selectedAssetType;

    /**
     * Initialize runs before the scene is displayed.
     * It initializes elements and data in the scene.
     *
     * @param url            url to be used
     * @param resourceBundle resource bundle to be used
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
        systemTypeChoiceBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null)
                selectedAssetType = newVal;
        });

        saveBtn.setOnMouseClicked(mouseEvent -> {
            Asset newAsset = assembleAsset();
            if (!isAssetEmpty(newAsset)) {
                saveAsset(newAsset);
                saveDialog(mouseEvent);
            } else {
                errorDialog(mouseEvent);
            }
        });
        // Change scenes to Systems.fxml
        systemMenuBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, SYSTEMS));
        //Attach link to systemTypeMenuBtn to go to Utilities.SystemTypeList.fxml
        systemTypeMenuBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, "/SystemTypeList"));
        // Change scenes to Systems.fxml
        cancelBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, SYSTEMS));
    }


    /**
     * Initializes the default and possible values for all fields that can accept user input. For example,
     * it establishes the possible dropdown values for the system type selection.
     */
    public void initializeFieldValues() {
        // Establishes the asset types available for selection in the dropdown
        ObservableList<AssetType> assetTypeNamesList;
        assetTypeNamesList = FXCollections.observableArrayList(assetTypeDAOImpl.getAssetTypeList());
        systemTypeChoiceBox.setItems(assetTypeNamesList);
        systemTypeChoiceBox.setValue(systemTypeChoiceBox.getItems().get(0));
        systemTypeChoiceBox.setConverter(new StringConverter<>() {
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
     * @return a new asset object created
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
     * Sends the new asset to be inserted in the database
     *
     * @param newAsset is an asset object to be added in the database
     */
    public void saveAsset(Asset newAsset) {
        assetDAOImpl.insertAsset(newAsset);
    }

    /**
     * Creates a dialog to alert the user that an asset was saved to the database
     *
     * @param mouseEvent is the event that triggers the dialog
     */
    void saveDialog(MouseEvent mouseEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(SAVE_DIALOG);
        alert.setHeaderText(SAVE_HEADER);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            uiUtilities.changeScene(mouseEvent, SYSTEMS);
        }
    }

    /**
     * Creates a dialog to inform the user that there was an error in the user input
     *
     * @param mouseEvent is the event that triggers the dialog
     */
    void errorDialog(MouseEvent mouseEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(ERROR_DIALOG);
        alert.setHeaderText(ERROR_HEADER);

        alert.showAndWait();
    }

    /**
     * Checks to see if values of the asset are filled.
     *
     * @param asset os an asset object
     * @return whether or not the asset object passed is empty (no info or attributes) or not
     */
    public boolean isAssetEmpty(Asset asset) {
        return asset.getName().equals("") || asset.getAssetTypeID().equals("") || asset.getDescription().equals("") ||
                asset.getSerialNo().equals("") || asset.getManufacturer().equals("") || asset.getCategory().equals("") || asset.getSite().equals("") || asset.getLocation().equals("");
    }
}