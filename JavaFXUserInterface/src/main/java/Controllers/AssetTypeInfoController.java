/*
  This Controller is responsible for handling the information view
  of an asset type. It handles the editing of asset types
  and saving them to the database.

  @author Jeff, Paul, Roy, Najim
  @last_edit 02/7/2020
 */
package Controllers;

import Utilities.AssetTypeList;
import Utilities.TextConstants;
import Utilities.UIUtilities;
import external.AssetTypeDAOImpl;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class AssetTypeInfoController implements Initializable {
    private static final String ALERT_HEADER = "Confirmation of asset type deletion";
    private static final String ALERT_CONTENT = "Are you sure you want to delete this asset type? \n " +
            "this will delete all the assets of this type";

    @FXML
    private Button assetMenuBtn;
    @FXML
    private Button assetTypeMenuBtn;
    @FXML
    private Button infoSaveBtn;
    @FXML
    private Button infoDeleteBtn;
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
    @FXML
    private AnchorPane inputError;

    private UIUtilities uiUtilities;
    private AssetTypeList assetType;
    private AssetTypeList originalAssetType;
    private AssetTypeDAOImpl assetTypeDAO;
    private Text[] errorMessages = new Text[7];
    private boolean[] validInput = new boolean[7];

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        uiUtilities = new UIUtilities();
        assetTypeDAO = new AssetTypeDAOImpl();
        attachEvents();
    }

    /**
     * initData receives the Asset Type data that was selected from Utilities.AssetTypeList.FXML
     * Then, uses that data to populate the text fields in the scene.
     *
     * @param assetType represents the asset type we want to get info on
     * @author Najim, Paul
     */
    public void initData(AssetTypeList assetType) {
        this.assetType = assetType;
        this.originalAssetType = new AssetTypeList(assetType);
        assetTypeName.setText(assetType.getAssetType().getName());
        assetTypeDesc.setText(assetType.getAssetType().getDescription());
        try {
            thresholdOK.setText(TextConstants.ThresholdValueFormat.format(Double.parseDouble(assetType.getValueOk())));
            thresholdAdvisory.setText(TextConstants.ThresholdValueFormat.format(Double.parseDouble(assetType.getValueAdvisory())));
            thresholdCaution.setText(TextConstants.ThresholdValueFormat.format(Double.parseDouble(assetType.getValueCaution())));
            thresholdWarning.setText(TextConstants.ThresholdValueFormat.format(Double.parseDouble(assetType.getValueWarning())));
            thresholdFailed.setText(TextConstants.ThresholdValueFormat.format(Double.parseDouble(assetType.getValueFailed())));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    /**
     * Attaches events to elements in the scene.
     *
     * @author Najim, Paul
     * Edit: added all the text proprety listeners and text formaters for all the fields
     */
    public void attachEvents() {
        // Change scenes to Assets.fxml
        assetMenuBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, TextConstants.ASSETS_SCENE));
        //Attach link to assetTypeMenuBtn to go to Utilities.AssetTypeList.fxml
        assetTypeMenuBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, TextConstants.ASSET_TYPE_LIST_SCENE));
        infoDeleteBtn.setOnMouseClicked(this::deleteDialog);

        infoSaveBtn.setDisable(true);
        infoSaveBtn.setOnMouseClicked(mouseEvent -> {
            if (formInputValidation()) {
                assetTypeDAO.updateAssetType(assetType.toAssetType());
                uiUtilities.changeScene(mouseEvent, TextConstants.ASSET_TYPE_LIST_SCENE);
            }
        });

        assetTypeName.textProperty().addListener((obs, oldText, newText) -> {
            if (handleTextChange(newText, originalAssetType.getName()))
                assetType.getAssetType().setName(newText);
        });
        assetTypeDesc.textProperty().addListener((obs, oldText, newText) -> {
            if (handleTextChange(newText, originalAssetType.getDescription()))
                assetType.getAssetType().setDescription(newText);
        });

        thresholdOK.textProperty().addListener((obs, oldText, newText) -> {
            if (handleTextChange(newText, originalAssetType.getValueOk()))
                assetType.setValueOk(newText);
        });
        thresholdOK.setTextFormatter(new TextFormatter<>(c -> UIUtilities.checkFormat(TextConstants.ThresholdValueFormat, c)));

        thresholdAdvisory.textProperty().addListener((obs, oldText, newText) -> {
            if (handleTextChange(newText, originalAssetType.getValueAdvisory()))
                assetType.setValueAdvisory(newText);
        });
        thresholdAdvisory.setTextFormatter(new TextFormatter<>(c -> UIUtilities.checkFormat(TextConstants.ThresholdValueFormat, c)));

        thresholdCaution.textProperty().addListener((obs, oldText, newText) -> {
            if (handleTextChange(newText, originalAssetType.getValueCaution()))
                assetType.setValueCaution(newText);
        });
        thresholdCaution.setTextFormatter(new TextFormatter<>(c -> UIUtilities.checkFormat(TextConstants.ThresholdValueFormat, c)));

        thresholdWarning.textProperty().addListener((obs, oldText, newText) -> {
            if (handleTextChange(newText, originalAssetType.getValueWarning()))
                assetType.setValueWarning(newText);
        });
        thresholdWarning.setTextFormatter(new TextFormatter<>(c -> UIUtilities.checkFormat(TextConstants.ThresholdValueFormat, c)));

        thresholdFailed.textProperty().addListener((obs, oldText, newText) -> {
            if (handleTextChange(newText, originalAssetType.getValueFailed()))
                assetType.setValueFailed(newText);
        });
        thresholdFailed.setTextFormatter(new TextFormatter<>(c -> UIUtilities.checkFormat(TextConstants.ThresholdValueFormat, c)));

    }

    /**
     * Handle the text change of the user fields to turn on or off the save functionality
     *
     * @author Paul
     */
    private boolean handleTextChange(String newText, String field) {
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
        } else if (!newText.isEmpty() && !field.equals("-") && Double.parseDouble(newText) == Double.parseDouble(field)) {
            infoSaveBtn.setDisable(true);
            infoSaveBtn.getStyleClass().clear();
            infoSaveBtn.getStyleClass().add("infoSaveFalse");
            return false;
        } else {
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
        alert.setTitle(TextConstants.ALERT_TITLE_DIALOG);
        alert.setHeaderText(ALERT_HEADER);
        alert.setContentText(ALERT_CONTENT);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            deleteAssetType();
            uiUtilities.changeScene(mouseEvent, TextConstants.ASSET_TYPE_LIST_SCENE);
        }
    }

    /**
     * Send the asset ID to the Database class in order for it to be deleted.
     *
     * @author Paul
     */
    private void deleteAssetType() {
        assetTypeDAO.deleteAssetTypeByID(assetType.getId());
    }

    /**
     * Displays an error for a field when the validation criteria are not respected.
     *
     * @author Najim
     */
    public boolean formInputValidation() {
        String assetTypeNameValue = assetTypeName.getText();
        String assetTypeDescValue = assetTypeDesc.getText();
        double thresholdAdvisoryNumber;
        double thresholdCautionNumber;
        double thresholdWarningNumber;
        double thresholdFailedNumber;
        double horizontalPosition = 0;
        boolean validForm = true;

        if (assetTypeNameValue.trim().isEmpty() || assetTypeNameValue.length() > 50) {
            validForm = false;
            validInput[0] = false;
            UIUtilities.createInputError(inputError, errorMessages, assetTypeName, TextConstants.EMPTY_FIELD_ERROR + " and/or \n" + TextConstants.MAX_50_CHARACTERS_ERROR, 27.0, horizontalPosition, 0);
        } else {
            validInput[0] = true;
            UIUtilities.removeInputError(inputError, errorMessages, validInput, assetTypeName, 0);
        }
        if (assetTypeDescValue.length() > 300) {
            validForm = false;
            validInput[1] = false;
            UIUtilities.createInputError(inputError, errorMessages, assetTypeDesc, TextConstants.MAX_300_CHARACTERS_ERROR, 85.0, horizontalPosition, 1);
        } else {
            validInput[1] = true;
            UIUtilities.removeInputError(inputError, errorMessages, validInput, assetTypeDesc, 1);
        }

        if (!thresholdAdvisory.getText().isEmpty() && !thresholdCaution.getText().isEmpty()) {
            thresholdAdvisoryNumber = Double.parseDouble(thresholdAdvisory.getText());
            thresholdCautionNumber = Double.parseDouble(thresholdCaution.getText());

            if (thresholdAdvisoryNumber <= thresholdCautionNumber) {
                validForm = false;
                validInput[3] = false;
                validInput[4] = false;
                UIUtilities.createInputError(inputError, errorMessages, thresholdAdvisory, TextConstants.ADIVSORY_CAUTION, 178.0, horizontalPosition, 3);
                UIUtilities.createInputError(inputError, errorMessages, thresholdCaution, "", 0, horizontalPosition, 4);
            } else {
                validInput[3] = true;
                validInput[4] = true;
                UIUtilities.removeInputError(inputError, errorMessages, validInput, thresholdAdvisory, 3);
                UIUtilities.removeInputError(inputError, errorMessages, validInput, thresholdCaution, 4);
            }
        }
        if (!thresholdCaution.getText().isEmpty() && !thresholdWarning.getText().isEmpty()) {
            thresholdCautionNumber = Double.parseDouble(thresholdCaution.getText());
            thresholdWarningNumber = Double.parseDouble(thresholdWarning.getText());

            if (thresholdCautionNumber <= thresholdWarningNumber) {
                validForm = false;
                validInput[4] = false;
                validInput[5] = false;
                UIUtilities.createInputError(inputError, errorMessages, thresholdCaution, TextConstants.CAUTION_WARNING, 218.0, horizontalPosition, 4);
                UIUtilities.createInputError(inputError, errorMessages, thresholdWarning, "", 0, horizontalPosition, 5);
            } else {
                validInput[4] = true;
                validInput[5] = true;
                UIUtilities.removeInputError(inputError, errorMessages, validInput, thresholdCaution, 4);
                UIUtilities.removeInputError(inputError, errorMessages, validInput, thresholdWarning, 5);
            }
        }
        if (!thresholdWarning.getText().isEmpty() && !thresholdFailed.getText().isEmpty()) {
            thresholdWarningNumber = Double.parseDouble(thresholdWarning.getText());
            thresholdFailedNumber = Double.parseDouble(thresholdFailed.getText());

            if (thresholdWarningNumber <= thresholdFailedNumber) {
                validForm = false;
                validInput[5] = false;
                validInput[6] = false;
                UIUtilities.createInputError(inputError, errorMessages, thresholdWarning, TextConstants.WARNING_FAILED, 258.0, horizontalPosition, 5);
                UIUtilities.createInputError(inputError, errorMessages, thresholdFailed, "", 0, horizontalPosition, 6);
            } else {
                validInput[5] = true;
                validInput[6] = true;
                UIUtilities.removeInputError(inputError, errorMessages, validInput, thresholdWarning, 5);
                UIUtilities.removeInputError(inputError, errorMessages, validInput, thresholdFailed, 6);
            }
        }
        return validForm;
    }
}
