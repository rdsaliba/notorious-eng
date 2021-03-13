/*
  This Controller is responsible for handling the addition of assets.
  @author
  @last_edit 02/7/2020
 */
package controllers;

import app.item.Asset;
import app.item.AssetType;
import external.AssetDAOImpl;
import external.AssetTypeDAOImpl;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utilities.CustomDialog;
import utilities.TextConstants;
import utilities.UIUtilities;

import java.net.URL;
import java.util.ResourceBundle;

public class AddAssetController implements Initializable {

    private static final String AND_OR = " and/or \n";
    private final Text[] errorMessages = new Text[7];
    private final boolean[] validInput = new boolean[7];
    Logger logger = LoggerFactory.getLogger(AddAssetController.class);
    boolean validForm = true;

    @FXML
    private Button assetMenuBtn;
    @FXML
    private Button assetTypeMenuBtn;
    @FXML
    private Button cancelBtn;
    @FXML
    private Button saveBtn;
    @FXML
    private Button backBtn;
    @FXML
    private Button exitMenuBtn;
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
    @FXML
    private AnchorPane inputError;
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
            if (formInputValidation() && !isAssetEmpty(newAsset)) {
                saveAsset(newAsset);
                CustomDialog.addSystemControllerSaveDialog(mouseEvent);
            }
        });
        // Change scenes to Assets.fxml
        backBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, TextConstants.ASSETS_SCENE));
        //Attach ability to close program
        exitMenuBtn.setOnMouseClicked(mouseEvent -> Platform.exit());
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
     * Checks to see if mandatory values of the asset are filled.
     *
     * @param asset os an asset object
     * @return whether or not the asset object passed is empty (no info or attributes) or not
     */
    public boolean isAssetEmpty(Asset asset) {
        return asset.getName().equals("") || asset.getAssetTypeID().equals("") || asset.getSerialNo().equals("");
    }

    /**
     * Displays an error for a field when the validation criteria are not respected.
     *
     * @author Najim
     */
    public boolean formInputValidation() {
        String regexWordAndHyphen = "(?=\\S*[-])([a-zA-Z0-9-]*)|([a-zA-Z0-9]*)"; //Any word containing letters, numbers and hyphens
        String regexLettersAndHyphen = "(?=\\S*[-])([a-zA-Z-]*)|([a-zA-Z]*)"; //Any word containing letters and hyphens
        double horizontalPosition = 0;

        logger.info("Start - formInputValidation() -> The form is : {}", validForm);

        assetNameValidation(assetNameInput.getText(), horizontalPosition);
        assetDescriptionValidation(assetDescriptionTextArea.getText(), horizontalPosition);
        serialNumberValidation(serialNumberInput.getText(), regexWordAndHyphen, horizontalPosition);
        manufacturerValidation(manufacturerInput.getText(), regexWordAndHyphen, horizontalPosition);
        categoryValidation(categoryInput.getText(), regexLettersAndHyphen, horizontalPosition);
        siteValidation(siteInput.getText(), regexWordAndHyphen, horizontalPosition);
        locationValidation(locationInput.getText(), regexWordAndHyphen, horizontalPosition);

        logger.info("End - formInputValidation() -> The form is : {}", validForm);

        return validForm;
    }

    private void locationValidation(String locationValue, String regexWordAndHyphen, double horizontalPosition) {
        if (locationValue.length() > 20 || !locationValue.trim().matches(regexWordAndHyphen)) {
            validForm = false;
            validInput[6] = false;
            UIUtilities.createInputError(inputError, errorMessages, locationInput, TextConstants.MAX_20_CHARACTERS_ERROR + AND_OR + TextConstants.WORD_HYPHEN_ERROR, 533.0, horizontalPosition, 6);
        } else {
            validInput[6] = true;
            UIUtilities.removeInputError(inputError, errorMessages, validInput, locationInput, 6);
        }
    }

    private void siteValidation(String siteValue, String regexWordAndHyphen, double horizontalPosition) {
        if (siteValue.length() > 20 || !siteValue.trim().matches(regexWordAndHyphen)) {
            validForm = false;
            validInput[5] = false;
            UIUtilities.createInputError(inputError, errorMessages, siteInput, TextConstants.MAX_20_CHARACTERS_ERROR + AND_OR + TextConstants.WORD_HYPHEN_ERROR, 482.0, horizontalPosition, 5);
        } else {
            validInput[5] = true;
            UIUtilities.removeInputError(inputError, errorMessages, validInput, siteInput, 5);
        }
    }

    private void categoryValidation(String categoryValue, String regexLettersAndHyphen, double horizontalPosition) {
        if (categoryValue.length() > 20 || !categoryValue.trim().matches(regexLettersAndHyphen)) {
            validForm = false;
            validInput[4] = false;
            UIUtilities.createInputError(inputError, errorMessages, categoryInput, TextConstants.MAX_20_CHARACTERS_ERROR + AND_OR + TextConstants.LETTER_NUMBER_ERROR, 384.0, horizontalPosition, 4);
        } else {
            validInput[4] = true;
            UIUtilities.removeInputError(inputError, errorMessages, validInput, categoryInput, 4);
        }
    }

    private void manufacturerValidation(String manufacturerValue, String regexWordAndHyphen, double horizontalPosition) {
        if (manufacturerValue.length() > 20 || !manufacturerValue.trim().matches(regexWordAndHyphen)) {
            validForm = false;
            validInput[3] = false;
            UIUtilities.createInputError(inputError, errorMessages, manufacturerInput, TextConstants.MAX_20_CHARACTERS_ERROR + AND_OR + TextConstants.WORD_HYPHEN_ERROR, 331.0, horizontalPosition, 3);
        } else {
            validInput[3] = true;
            UIUtilities.removeInputError(inputError, errorMessages, validInput, manufacturerInput, 3);
        }
    }

    private void serialNumberValidation(String serialNumberValue, String regexWordAndHyphen, double horizontalPosition) {
        if (serialNumberValue.trim().isEmpty() || serialNumberValue.length() > 20 || !serialNumberValue.trim().matches(regexWordAndHyphen)) {
            validForm = false;
            validInput[2] = false;
            UIUtilities.createInputError(inputError, errorMessages, serialNumberInput, TextConstants.EMPTY_FIELD_ERROR + AND_OR + TextConstants.MAX_20_CHARACTERS_ERROR + AND_OR + TextConstants.WORD_HYPHEN_ERROR, 276.5, horizontalPosition, 2);
        } else {
            validInput[2] = true;
            UIUtilities.removeInputError(inputError, errorMessages, validInput, serialNumberInput, 2);
        }
    }

    private void assetDescriptionValidation(String assetDescriptionValue, double horizontalPosition) {
        if (assetDescriptionValue.length() > 300) {
            validForm = false;
            validInput[1] = false;
            UIUtilities.createInputError(inputError, errorMessages, assetDescriptionTextArea, TextConstants.MAX_300_CHARACTERS_ERROR, 210.0, horizontalPosition, 1);
        } else {
            validInput[1] = true;
            UIUtilities.removeInputError(inputError, errorMessages, validInput, assetDescriptionTextArea, 1);
        }
    }

    private void assetNameValidation(String assetNameValue, double horizontalPosition) {
        if (assetNameValue.trim().isEmpty()) {
            validForm = false;
            validInput[0] = false;
            UIUtilities.createInputError(inputError, errorMessages, assetNameInput, TextConstants.EMPTY_FIELD_ERROR, 66.0, horizontalPosition, 0);
        } else if (assetNameValue.length() > 50) {
            validForm = false;
            validInput[0] = false;
            UIUtilities.createInputError(inputError, errorMessages, assetNameInput, TextConstants.MAX_50_CHARACTERS_ERROR, 66.0, horizontalPosition, 0);
        } else {
            validForm = true;
            validInput[0] = true;
            UIUtilities.removeInputError(inputError, errorMessages, validInput, assetNameInput, 0);
        }
    }

}