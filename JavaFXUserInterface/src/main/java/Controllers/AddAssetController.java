package Controllers;

import Utilities.UIUtilities;
import app.item.Asset;
import app.item.AssetType;
import external.AssetDAOImpl;
import external.AssetTypeDAOImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class AddAssetController implements Initializable {

    private static final String SAVE_DIALOG = "Save Dialog";
    private static final String SAVE_HEADER = "Asset has been saved to the database.";
    private static final String ERROR_DIALOG = "Error Dialog";
    private static final String ERROR_HEADER = "Please enter values for all text fields.";
    private static final String AssetS = "/Assets";
    @FXML
    public Button AssetMenuBtn;
    @FXML
    private Button AssetTypeMenuBtn;
    @FXML
    private Button cancelBtn;
    @FXML
    private Button saveBtn;
    @FXML
    private ChoiceBox<AssetType> AssetTypeChoiceBox;
    @FXML
    private TextField AssetNameInput;
    @FXML
    private TextArea AssetDescriptionTextArea;
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
        AssetDescriptionTextArea.setWrapText(true);
    }

    /**
     * Attaches events to elements in the scene.
     *
     * @author Jeff
     */
    public void attachEvents() {
        AssetTypeChoiceBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null)
                selectedAssetType = newVal;
        });

        saveBtn.setOnMouseClicked(mouseEvent -> {
            Asset newAsset = assembleAsset();
            if (!isAssetEmpty(newAsset)) {
                saveAsset(newAsset);
                saveDialog(mouseEvent);
            } else {
                errorDialog();
            }
        });
        // Change scenes to Assets.fxml
        AssetMenuBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, AssetS));
        //Attach link to AssetTypeMenuBtn to go to Utilities.AssetTypeList.fxml
        AssetTypeMenuBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, "/AssetTypeList"));
        // Change scenes to Assets.fxml
        cancelBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, AssetS));
    }


    /**
     * Initializes the default and possible values for all fields that can accept user input. For example,
     * it establishes the possible dropdown values for the Asset type selection.
     */
    public void initializeFieldValues() {
        // Establishes the asset types available for selection in the dropdown
        ObservableList<AssetType> assetTypeNamesList;
        assetTypeNamesList = FXCollections.observableArrayList(assetTypeDAOImpl.getAssetTypeList());
        AssetTypeChoiceBox.setItems(assetTypeNamesList);
        AssetTypeChoiceBox.setValue(AssetTypeChoiceBox.getItems().get(0));
        AssetTypeChoiceBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(AssetType assetType) {
                return assetType.getName();
            }

            @Override
            public AssetType fromString(String s) {
                return AssetTypeChoiceBox.getItems().stream().filter(ap ->
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
        newAsset.setName(AssetNameInput.getText());
        newAsset.setAssetTypeID(selectedAssetType.getId());
        newAsset.setDescription(AssetDescriptionTextArea.getText());
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
            uiUtilities.changeScene(mouseEvent, AssetS);
        }
    }

    /**
     * Creates a dialog to inform the user that there was an error in the user input
     *
     */
    void errorDialog() {
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