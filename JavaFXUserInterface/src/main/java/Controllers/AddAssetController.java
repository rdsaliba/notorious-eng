/*
  This Controller is responsible for handling the addition of assets.
  @author
  @last_edit 02/7/2020
 */
package Controllers;

import Utilities.CustomDialog;
import Utilities.TextConstants;
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
import javafx.util.StringConverter;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class AddAssetController implements Initializable {

    private static final String SAVE_DIALOG = "Save Dialog";
    private static final String SAVE_HEADER = "Asset has been saved to the database.";
    private static final String ERROR_DIALOG = "Error Dialog";
    private static final String ERROR_HEADER = "Please enter values for all text fields.";

    @FXML
    public Button assetMenuBtn;
    @FXML
    private Button assetTypeMenuBtn;
    @FXML
    private Button cancelBtn;
    @FXML
    private Button saveBtn;
    @FXML
    private ChoiceBox<AssetType> assetTypeChoiceBox;
    @FXML
    private TextField assetNameInput;
    @FXML
    private TextArea assetDescriptionTextArea;
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
        assetDescriptionTextArea.setWrapText(true);
    }

    /**
     * Attaches events to elements in the scene.
     *
     * @author Jeff
     */
    public void attachEvents() {
        assetTypeChoiceBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null)
                selectedAssetType = newVal;
        });

        saveBtn.setOnMouseClicked(mouseEvent -> {
            Asset newAsset = assembleAsset();
            if (!isAssetEmpty(newAsset)) {
                saveAsset(newAsset);
                CustomDialog.addSystemControllerSaveDialog(mouseEvent);
            } else {
                CustomDialog.addSystemControllerErrorDialog(mouseEvent);
            }
        });
        // Change scenes to Assets.fxml
        assetMenuBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, TextConstants.ASSETS_SCENE));
        //Attach link to assetTypeMenuBtn to go to Utilities.AssetTypeList.fxml
        assetTypeMenuBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, TextConstants.ASSET_TYPE_LIST_SCENE));
        // Change scenes to Assets.fxml
        cancelBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, TextConstants.ASSETS_SCENE));
    }


    /**
     * Initializes the default and possible values for all fields that can accept user input. For example,
     * it establishes the possible dropdown values for the asset type selection.
     */
    public void initializeFieldValues() {
        // Establishes the asset types available for selection in the dropdown
        ObservableList<AssetType> assetTypeNamesList;
        assetTypeNamesList = FXCollections.observableArrayList(assetTypeDAOImpl.getAssetTypeList());
        assetTypeChoiceBox.setItems(assetTypeNamesList);
        assetTypeChoiceBox.setValue(assetTypeChoiceBox.getItems().get(0));
        assetTypeChoiceBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(AssetType assetType) {
                return assetType.getName();
            }

            @Override
            public AssetType fromString(String s) {
                return assetTypeChoiceBox.getItems().stream().filter(ap ->
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
        newAsset.setName(assetNameInput.getText());
        newAsset.setAssetTypeID(selectedAssetType.getId());
        newAsset.setDescription(assetDescriptionTextArea.getText());
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