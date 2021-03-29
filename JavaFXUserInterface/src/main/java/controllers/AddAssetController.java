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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utilities.CustomDialog;
import utilities.FormInputValidation;
import utilities.UIUtilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static utilities.TextConstants.ASSETS_SCENE;

public class AddAssetController extends Controller implements Initializable {

    @FXML
    private Button uploadBtn;
    @FXML
    private Button cancelBtn;
    @FXML
    private Button saveBtn;
    @FXML
    private Button backBtn;
    @FXML
    private ChoiceBox<AssetType> assetTypeChoiceBox;
    @FXML
    private TextField assetNameInput;
    @FXML
    private TextArea assetDescriptionInput;
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
    @FXML
    private ImageView imageView;
    private AnchorPane addAssetInformationAnchorPane;
    private AssetDAOImpl assetDAOImpl;
    private AssetTypeDAOImpl assetTypeDAOImpl;
    private UIUtilities uiUtilities;
    private AssetType selectedAssetType;
    private String imageName = "";
    private boolean overrideImage = false;
    FileInputStream fileInputStream = null;
    private int imageId = 0;

    Logger logger = LoggerFactory.getLogger(AddAssetController.class);

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
        assetDescriptionInput.setWrapText(true);
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
            imageValidation();
            Asset newAsset = assembleAsset();
            if (FormInputValidation.assetFormInputValidation(addAssetInformationAnchorPane, assetNameInput, assetDescriptionInput, serialNumberInput, manufacturerInput, categoryInput, siteInput, locationInput) && !isAssetEmpty(newAsset)) {
                saveAsset(newAsset);
                CustomDialog.saveNewAssetInformationDialogShowAndWait();
            }
        });

        uploadBtn.setOnAction(e-> openImageFile());

        // Change scenes to Assets.fxml
        backBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(ASSETS_SCENE, backBtn.getScene()));
        cancelBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(ASSETS_SCENE, cancelBtn.getScene()));
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
        newAsset.setDescription(assetDescriptionInput.getText());
        newAsset.setSerialNo(serialNumberInput.getText());
        newAsset.setManufacturer(manufacturerInput.getText());
        newAsset.setCategory(categoryInput.getText());
        newAsset.setSite(siteInput.getText());
        newAsset.setLocation(locationInput.getText());
        setImage(newAsset);

        return newAsset;
    }

    private void setImage(Asset newAsset) {
        //Case 1: User want to override the default image
        if (overrideImage) {
            newAsset.setImageId(assetDAOImpl.findImageIdByName(imageName));
        }

        //Case 2: User selected an image that already exists
        if(imageId != 0) {
            newAsset.setImageId(imageId);
        }
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

    private void imageValidation() {
        imageId = assetDAOImpl.findImageIdByName(imageName);

        if (imageId == 0 && !imageName.isEmpty()) {
            uploadImage(fileInputStream);
        }
    }

    private void openImageFile() {
        FileChooser fileChooser = new FileChooser();
        configureFileChooser(fileChooser);
        File file = fileChooser.showOpenDialog(uploadBtn.getScene().getWindow());

        if (file!=null) {
            try {
                fileInputStream = new FileInputStream(file);
                imageName = file.getName();
                Image image = new Image(fileInputStream);
                imageView.setImage(image);
            } catch (IOException e) {
                logger.error("openImageFile() : " , e);
            }
        }
    }

    private void uploadImage(FileInputStream fileInputStream) {
        assetDAOImpl.storeImage(fileInputStream, imageName);
        overrideImage = true;
    }

    private void configureFileChooser(FileChooser fileChooser){
        fileChooser.setTitle("View Images");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Images", "*.*"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG", "*.png")
        );
    }
}
