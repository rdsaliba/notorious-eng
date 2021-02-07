import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import local.AssetTypeDAOImpl;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.Optional;
import java.util.ResourceBundle;

public class AssetTypeInfoController implements Initializable {

    @FXML
    private Button assetMenuBtn;
    @FXML
    private Button assetTypeMenuBtn;
    @FXML
    private Button infoSaveBtn;
    @FXML
    private Button infoDeleteBtn;
    @FXML
    private Button modelEditBtn;
    @FXML
    private Button modelDeleteBtn;
    @FXML
    private AnchorPane assetTypeInformation;
    @FXML
    private TextField assetTypeName;
    @FXML
    private TextArea assetTypeDesc;
    @FXML
    private TextField thresholdOK;
    @FXML
    private TextField thresholdAdvisory;
    @FXML
    private TextField thresholdCaution;
    @FXML
    private TextField thresholdWarning;
    @FXML
    private TextField thresholdFailed;
    @FXML
    private ImageView assetTypeImageView;

    private UIUtilities uiUtilities;
    private AssetTypeList assetType;
    private AssetTypeList originalAssetType;
    private AssetTypeDAOImpl assetTypeDAO;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        uiUtilities = new UIUtilities();
        assetTypeDAO = new AssetTypeDAOImpl();
        attachEvents();
    }

    /**
     * initData receives the Asset Type data that was selected from AssetTypeList.FXML
     * Then, uses that data to populate the text fields in the scene.
     *
     * @param assetType
     * @author Najim, Paul
     */
    void initData(AssetTypeList assetType) {
        this.assetType = assetType;
        this.originalAssetType = new AssetTypeList(assetType);
        assetTypeName.setText(assetType.getAssetType().getName());
        assetTypeDesc.setText(assetType.getAssetType().getDescription());
        try {
            thresholdOK.setText(new DecimalFormat("#.##").format(Double.parseDouble(assetType.getValueOk())));
        } catch (NumberFormatException e) {
        }
        try {
            thresholdAdvisory.setText(new DecimalFormat("#.##").format(Double.parseDouble(assetType.getValueAdvisory())));
        } catch (NumberFormatException e) {
        }
        try {
            thresholdCaution.setText(new DecimalFormat("#.##").format(Double.parseDouble(assetType.getValueCaution())));
        } catch (NumberFormatException e) {
        }
        try {
            thresholdWarning.setText(new DecimalFormat("#.##").format(Double.parseDouble(assetType.getValueWarning())));
        } catch (NumberFormatException e) {
        }
        try {
            thresholdFailed.setText(new DecimalFormat("#.##").format(Double.parseDouble(assetType.getValueFailed())));
        } catch (NumberFormatException e) {
        }


    }

    /**
     * Attaches events to elements in the scene.
     *
     * @author Najim
     *
     * Edit: added all the text proprety listeners and text formaters for all the fields
     *
     * @author Paul
     */
    public void attachEvents() {
        // Change scenes to Assets.fxml
        assetMenuBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, "/Assets"));
        //Attach link to assetTypeMenuBtn to go to AssetTypeList.fxml
        assetTypeMenuBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, "/AssetTypeList"));
        infoDeleteBtn.setOnMouseClicked(this::deleteDialog);

        infoSaveBtn.setDisable(true);
        infoSaveBtn.setOnMouseClicked(mouseEvent -> {
            assetTypeDAO.updateAssetType(assetType.toAssetType());
            uiUtilities.changeScene(mouseEvent, "/AssetTypeList");
        });

        assetTypeName.textProperty().addListener((obs, oldText, newText) -> {
            if (handleTextChange(obs, newText, originalAssetType.getName()))
                assetType.getAssetType().setName(newText);
        });
        assetTypeDesc.textProperty().addListener((obs, oldText, newText) -> {
            if (handleTextChange(obs, newText, originalAssetType.getDescription()))
                assetType.getAssetType().setDescription(newText);
        });

        thresholdOK.textProperty().addListener((obs, oldText, newText) -> {
            if(handleTextChange(obs, newText, originalAssetType.getValueOk()))
                assetType.setValueOk(newText);
        });
        thresholdOK.setTextFormatter( new TextFormatter<>(c -> UIUtilities.checkFormat(TextConstants.ThresholdValueFormat,c)));

        thresholdAdvisory.textProperty().addListener((obs, oldText, newText) -> {
            if(handleTextChange(obs, newText, originalAssetType.getValueAdvisory()))
                assetType.setValueAdvisory(newText);
        });
        thresholdAdvisory.setTextFormatter( new TextFormatter<>(c -> UIUtilities.checkFormat(TextConstants.ThresholdValueFormat,c)));

        thresholdCaution.textProperty().addListener((obs, oldText, newText) -> {
            if(handleTextChange(obs, newText, originalAssetType.getValueCaution()))
                assetType.setValueCaution(newText);
        });
        thresholdCaution.setTextFormatter( new TextFormatter<>(c -> UIUtilities.checkFormat(TextConstants.ThresholdValueFormat,c)));

        thresholdWarning.textProperty().addListener((obs, oldText, newText) -> {
            if(handleTextChange(obs, newText, originalAssetType.getValueWarning()))
                assetType.setValueWarning(newText);
        });
        thresholdWarning.setTextFormatter( new TextFormatter<>(c -> UIUtilities.checkFormat(TextConstants.ThresholdValueFormat,c)));

        thresholdFailed.textProperty().addListener((obs, oldText, newText) -> {
            if(handleTextChange(obs, newText, originalAssetType.getValueFailed()))
                assetType.setValueFailed(newText);
        });
        thresholdFailed.setTextFormatter( new TextFormatter<>(c -> UIUtilities.checkFormat(TextConstants.ThresholdValueFormat,c)));

    }

    /**
     *  Handle the text change of the user fields to turn on or off the save functionality
     *
     * @author  Paul
     */
    private boolean handleTextChange(ObservableValue<? extends String> obs, String newText, String field) {
        if ((field).equals(originalAssetType.getName()) || field.equals(originalAssetType.getDescription())) {
            if (!newText.isEmpty() && !newText.equals(field)) {
                infoSaveBtn.setDisable(false);
                infoSaveBtn.getStyleClass().clear();
                infoSaveBtn.getStyleClass().add("infoSaveTrue");
                return true;
            } else {
                infoSaveBtn.setDisable(true);
                infoSaveBtn.getStyleClass().clear();
                infoSaveBtn.getStyleClass().add("infoSaveFalse");
                return false;
            }
        }
        else if (!newText.isEmpty() && !field.equals("-") && Double.parseDouble(newText) == Double.parseDouble(field)) {
            infoSaveBtn.setDisable(true);
            infoSaveBtn.getStyleClass().clear();
            infoSaveBtn.getStyleClass().add("infoSaveFalse");
            return false;
        }
        else {
            infoSaveBtn.setDisable(false);
            infoSaveBtn.getStyleClass().clear();
            infoSaveBtn.getStyleClass().add("infoSaveTrue");
            return true;
        }
    }

    /**
     * Changes the Image depending on the Asset Type.
     *
     * @author Najim
     */
    public void setImage(String typeName) {
        if (typeName.contains("Engine")) {
            assetTypeImageView.setImage(new Image("imgs/asset_type_engine.png"));
        } else {
            assetTypeImageView.setImage(new Image("imgs/unknown_asset_type.png"));
        }
    }

    /**
     * Creates a dialog box that asks user if they want to delete an assetType.
     *
     * @param mouseEvent is an event trigger for this delete dialog
     * @author Paul
     */
    private void deleteDialog(MouseEvent mouseEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        String ALERT_TITLE = "Confirmation Dialog";
        alert.setTitle(ALERT_TITLE);
        String ALERT_HEADER = "Confirmation of asset type deletion";
        alert.setHeaderText(ALERT_HEADER);
        String ALERT_CONTENT = "Are you sure you want to delete this asset type? \n " +
                "this will delete all the assets of this type";
        alert.setContentText(ALERT_CONTENT);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            deleteAssetType();
            uiUtilities.changeScene(mouseEvent, "/AssetTypeList");
        }
    }

    /**
     * Send the asset ID to the Database class in order for it to be deleted.
     *
     * @author Paul
     */
    private void deleteAssetType() {
        AssetTypeDAOImpl assetTypeDAO = new AssetTypeDAOImpl();
        assetTypeDAO.deleteAssetTypeByID(assetType.getId());
    }
}
