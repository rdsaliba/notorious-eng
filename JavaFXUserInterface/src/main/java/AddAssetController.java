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

public class AddAssetController implements Initializable {

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
    private final String SAVE_DIALOG = "Save Dialog";
    private final String SAVE_HEADER = "Asset has been saved to the database.";
    private final String ERROR_DIALOG = "Error Dialog";
    private final String ERROR_HEADER = "Please enter values for all text fields.";

    /**
     * Initialize runs before the scene is displayed.
     * It initializes elements and data in the scene.
     *
     * @param url url to be used
     * @param resourceBundle resource bundle to be used
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
        assetDescriptionTextArea.setWrapText(true);
    }

    /**
     * Attaches events to elements in the scene.
     *
     * @author Jeff
     */
    public void attachEvents() {
        assetTypeChoiceBox.valueProperty().addListener((obs, oldval, newval) -> {
            if (newval != null)
                selectedAssetType = newval;
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
        // Change scenes to Assets.fxml
        assetMenuBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, "/Assets"));
        //Attach link to assetTypeMenuBtn to go to AssetTypeList.fxml
        assetTypeMenuBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, "/AssetTypeList"));
        // Change scenes to Assets.fxml
        cancelBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, "/Assets"));
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
     * Creates a dialog to alert the user that an asset was saved to the database
     *
     * @param mouseEvent is the event that triggers the dialog
     */
    void saveDialog(MouseEvent mouseEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(SAVE_DIALOG);
        alert.setHeaderText(SAVE_HEADER);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK){
            uiUtilities.changeScene(mouseEvent, "/Assets");
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

        Optional<ButtonType> result = alert.showAndWait();
    }

    /**
     *  Checks to see if values of the asset are filled.
     *
     * @param asset os an asset object
     * @return whether or not the asset object passed is empty (no info or attributes) or not
     */
    boolean isAssetEmpty(Asset asset) {
        return asset.getName().equals("") || asset.getAssetTypeID().equals("") || asset.getDescription().equals("") ||
                asset.getSerialNo().equals("") || asset.getManufacturer().equals("") || asset.getCategory().equals("") || asset.getSite().equals("") || asset.getLocation().equals("");
    }
}
