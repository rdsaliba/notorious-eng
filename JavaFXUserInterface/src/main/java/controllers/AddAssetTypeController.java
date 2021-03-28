/*
  This Controller is responsible for handling the addition of asset types.
  @author Jeff, Paul, Najim
  @last_edit 02/7/2020
 */
package controllers;

import app.item.AssetType;
import app.item.AssetTypeParameter;
import external.AssetTypeDAOImpl;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import utilities.TextConstants;
import utilities.UIUtilities;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class AddAssetTypeController extends Controller implements Initializable {

    private final Text[] errorMessages = new Text[7];
    private final boolean[] validInput = new boolean[7];
    boolean validForm = true;
    @FXML
    private Button cancelBtn;
    @FXML
    private Button saveBtn;
    @FXML
    private Button backBtn;
    @FXML
    private TextField assetTypeName;
    @FXML
    private TextArea assetTypeDescription;
    @FXML
    private TextField thresholdAdvisoryValue;
    @FXML
    private TextField thresholdCautionValue;
    @FXML
    private TextField thresholdWarningValue;
    @FXML
    private TextField thresholdFailedValue;
    @FXML
    private AnchorPane inputError;
    private UIUtilities uiUtilities;
    private AssetTypeDAOImpl db;
    private ArrayList<AssetTypeParameter> assetTypeParameters;

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
        uiUtilities = new UIUtilities();
        db = new AssetTypeDAOImpl();
        assetTypeParameters = new ArrayList<>();
        attachEvents();
    }

    /**
     * Attaches events to elements in the scene.
     *
     * @author Jeff , Paul
     */
    public void attachEvents() {
        // Change scenes to Assets.fxml
        backBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(TextConstants.ASSET_TYPE_LIST_SCENE, backBtn.getScene()));
        // Change scenes to Assets.fxml
        cancelBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(TextConstants.ASSET_TYPE_LIST_SCENE, cancelBtn.getScene()));
        saveBtn.setOnMouseClicked(mouseEvent -> {
            if (assetTypeFormInputValidation() && saveAssetType(assembleAssetType())) {
                uiUtilities.changeScene(TextConstants.ASSET_TYPE_LIST_SCENE, saveBtn.getScene());
            }
        });

        thresholdAdvisoryValue.setTextFormatter(new TextFormatter<>(c -> UIUtilities.checkFormat(TextConstants.ThresholdValueFormat, c)));
        thresholdCautionValue.setTextFormatter(new TextFormatter<>(c -> UIUtilities.checkFormat(TextConstants.ThresholdValueFormat, c)));
        thresholdWarningValue.setTextFormatter(new TextFormatter<>(c -> UIUtilities.checkFormat(TextConstants.ThresholdValueFormat, c)));
        thresholdFailedValue.setTextFormatter(new TextFormatter<>(c -> UIUtilities.checkFormat(TextConstants.ThresholdValueFormat, c)));
    }


    /**
     * Create an asset type object from the user's input
     *
     * @author Jeff , Paul, Najim
     */
    public AssetType assembleAssetType() {
        if (assetTypeName.getText().length() > 0) {
            AssetType assetType = new AssetType(assetTypeName.getText());
            if (assetTypeDescription.getText().length() > 0) {
                assetType.setDescription(assetTypeDescription.getText());
            } else {
                assetType.setDescription(null);
            }

            Double advisoryValue = thresholdAdvisoryValue.getText().isEmpty() ? null : Double.parseDouble(thresholdAdvisoryValue.getText());
            assetTypeParameters.add(new AssetTypeParameter(TextConstants.ADVISORY_THRESHOLD, advisoryValue));

            Double cautionValue = thresholdCautionValue.getText().isEmpty() ? null : Double.parseDouble(thresholdCautionValue.getText());
            assetTypeParameters.add(new AssetTypeParameter(TextConstants.CAUTION_THRESHOLD, cautionValue));

            Double warningValue = thresholdWarningValue.getText().isEmpty() ? null : Double.parseDouble(thresholdWarningValue.getText());
            assetTypeParameters.add(new AssetTypeParameter(TextConstants.WARNING_THRESHOLD, warningValue));

            Double failedValue = thresholdFailedValue.getText().isEmpty() ? null : Double.parseDouble(thresholdFailedValue.getText());
            assetTypeParameters.add(new AssetTypeParameter(TextConstants.FAILED_THRESHOLD, failedValue));

            assetType.setThresholdList(assetTypeParameters);
            return assetType;
        }
        return null;
    }

    /**
     * save the asset type to the database
     *
     * @author Jeff , Paul
     */
    public boolean saveAssetType(AssetType assetType) {
        if (assetType != null) {
            db.insertAssetType(assetType);
            return true;
        }
        return false;
    }

    /**
     * Displays an error for a field when the validation criteria are not respected.
     *
     * @author Najim
     */
    public boolean assetTypeFormInputValidation() {
        String assetTypeNameValue = assetTypeName.getText();
        String assetTypeDescValue = assetTypeDescription.getText();

        assetTypeNameValidation(assetTypeNameValue);
        assetTypeDescValidation(assetTypeDescValue);

        if (UIUtilities.compareThresholds(thresholdAdvisoryValue, thresholdCautionValue)) {
            validInput[3] = true;
            validInput[4] = true;
            UIUtilities.removeInputError(inputError, errorMessages, validInput, thresholdAdvisoryValue, 3);
            UIUtilities.removeInputError(inputError, errorMessages, validInput, thresholdCautionValue, 4);
        } else {
            validForm = false;
            validInput[3] = false;
            validInput[4] = false;
            UIUtilities.createInputError(inputError, errorMessages, thresholdAdvisoryValue, TextConstants.ADVISORY_CAUTION, 245.0, 0, 3);
            UIUtilities.createInputError(inputError, errorMessages, thresholdCautionValue, "", 0, 0, 4);
        }

        if (UIUtilities.compareThresholds(thresholdAdvisoryValue, thresholdWarningValue)) {
            validInput[3] = true;
            validInput[5] = true;
            UIUtilities.removeInputError(inputError, errorMessages, validInput, thresholdAdvisoryValue, 3);
            UIUtilities.removeInputError(inputError, errorMessages, validInput, thresholdWarningValue, 5);
        } else {
            validForm = false;
            validInput[3] = false;
            validInput[5] = false;
            UIUtilities.createInputError(inputError, errorMessages, thresholdAdvisoryValue, TextConstants.ADVISORY_WARNING, 245.0, 0, 3);
            UIUtilities.createInputError(inputError, errorMessages, thresholdWarningValue, "", 0, 0, 5);
        }

        if (UIUtilities.compareThresholds(thresholdCautionValue, thresholdWarningValue)) {
            validInput[4] = true;
            validInput[5] = true;
            UIUtilities.removeInputError(inputError, errorMessages, validInput, thresholdCautionValue, 4);
            UIUtilities.removeInputError(inputError, errorMessages, validInput, thresholdWarningValue, 5);
        } else {
            validForm = false;
            validInput[4] = false;
            validInput[5] = false;
            UIUtilities.createInputError(inputError, errorMessages, thresholdCautionValue, TextConstants.CAUTION_WARNING, 275.0, 0, 4);
            UIUtilities.createInputError(inputError, errorMessages, thresholdWarningValue, "", 0, 0, 5);
        }

        if (UIUtilities.compareThresholds(thresholdAdvisoryValue, thresholdFailedValue)) {
            validInput[3] = true;
            validInput[6] = true;
            UIUtilities.removeInputError(inputError, errorMessages, validInput, thresholdAdvisoryValue, 3);
            UIUtilities.removeInputError(inputError, errorMessages, validInput, thresholdFailedValue, 6);
        } else {
            validForm = false;
            validInput[3] = false;
            validInput[6] = false;
            UIUtilities.createInputError(inputError, errorMessages, thresholdAdvisoryValue, TextConstants.ADVISORY_FAILED, 245.0, 0, 3);
            UIUtilities.createInputError(inputError, errorMessages, thresholdFailedValue, "", 0, 0, 6);
        }
        if (UIUtilities.compareThresholds(thresholdCautionValue, thresholdFailedValue)) {
            validInput[4] = true;
            validInput[6] = true;
            UIUtilities.removeInputError(inputError, errorMessages, validInput, thresholdCautionValue, 4);
            UIUtilities.removeInputError(inputError, errorMessages, validInput, thresholdFailedValue, 6);
        } else {
            validForm = false;
            validInput[4] = false;
            validInput[6] = false;
            UIUtilities.createInputError(inputError, errorMessages, thresholdCautionValue, TextConstants.CAUTION_FAILED, 275.0, 0, 4);
            UIUtilities.createInputError(inputError, errorMessages, thresholdFailedValue, "", 0, 0, 6);
        }
        if (UIUtilities.compareThresholds(thresholdWarningValue, thresholdFailedValue)) {
            validInput[5] = true;
            validInput[6] = true;
            UIUtilities.removeInputError(inputError, errorMessages, validInput, thresholdWarningValue, 5);
            UIUtilities.removeInputError(inputError, errorMessages, validInput, thresholdFailedValue, 6);
        } else {
            validForm = false;
            validInput[5] = false;
            validInput[6] = false;
            UIUtilities.createInputError(inputError, errorMessages, thresholdWarningValue, TextConstants.WARNING_FAILED, 305.0, 0, 5);
            UIUtilities.createInputError(inputError, errorMessages, thresholdFailedValue, "", 0, 0, 6);
        }
        return validForm;
    }

    private void assetTypeDescValidation(String assetTypeDescValue) {
        if (assetTypeDescValue.length() > 300) {
            validForm = false;
            validInput[1] = false;
            UIUtilities.createInputError(inputError, errorMessages, assetTypeDescription, TextConstants.MAX_300_CHARACTERS_ERROR, 127.0, 374.0, 1);
        } else {
            validInput[1] = true;
            UIUtilities.removeInputError(inputError, errorMessages, validInput, assetTypeDescription, 1);
        }
    }

    private void assetTypeNameValidation(String assetTypeNameValue) {
        validForm = true;
        if (assetTypeNameValue.trim().isEmpty()) {
            validForm = false;
            validInput[0] = false;
            UIUtilities.createInputError(inputError, errorMessages, assetTypeName, TextConstants.EMPTY_FIELD_ERROR, 72.0, 374.0, 0);
        } else if (assetTypeNameValue.length() > 50) {
            validForm = false;
            validInput[0] = false;
            UIUtilities.createInputError(inputError, errorMessages, assetTypeName, TextConstants.MAX_50_CHARACTERS_ERROR, 72.0, 374.0, 0);
        } else {
            validInput[0] = true;
            UIUtilities.removeInputError(inputError, errorMessages, validInput, assetTypeName, 0);
        }
    }
}
