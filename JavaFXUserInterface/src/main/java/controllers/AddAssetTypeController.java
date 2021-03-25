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
import utilities.FormInputValidation;
import utilities.TextConstants;
import utilities.UIUtilities;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class AddAssetTypeController implements Initializable {

    private final Text[] inputValidationMessages = new Text[7];
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
    private AnchorPane addAssetTypeInformationAnchorPane;
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
            if (FormInputValidation.assetTypeFormInputValidation(addAssetTypeInformationAnchorPane, assetTypeName, assetTypeDescription, thresholdAdvisory, thresholdCaution, thresholdWarning, thresholdFailed) && saveAssetType(assembleAssetType())) {
                uiUtilities.changeScene(TextConstants.ASSET_TYPE_LIST_SCENE, saveBtn.getScene());
            }
        });


        thresholdOK.setTextFormatter(new TextFormatter<>(c -> FormInputValidation.checkFormat(TextConstants.ThresholdValueFormat, c)));
        thresholdAdvisory.setTextFormatter(new TextFormatter<>(c -> FormInputValidation.checkFormat(TextConstants.ThresholdValueFormat, c)));
        thresholdCaution.setTextFormatter(new TextFormatter<>(c -> FormInputValidation.checkFormat(TextConstants.ThresholdValueFormat, c)));
        thresholdWarning.setTextFormatter(new TextFormatter<>(c -> FormInputValidation.checkFormat(TextConstants.ThresholdValueFormat, c)));
        thresholdFailed.setTextFormatter(new TextFormatter<>(c -> FormInputValidation.checkFormat(TextConstants.ThresholdValueFormat, c)));
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
            Double okValue = thresholdOK.getText().isEmpty() ? null : Double.parseDouble(thresholdOK.getText());
            assetTypeParameters.add(new AssetTypeParameter(TextConstants.OK_THRESHOLD, okValue));

            Double advisoryValue = thresholdAdvisory.getText().isEmpty() ? null : Double.parseDouble(thresholdAdvisory.getText());
            assetTypeParameters.add(new AssetTypeParameter(TextConstants.ADVISORY_THRESHOLD, advisoryValue));

            Double cautionValue = thresholdCaution.getText().isEmpty() ? null : Double.parseDouble(thresholdCaution.getText());
            assetTypeParameters.add(new AssetTypeParameter(TextConstants.CAUTION_THRESHOLD, cautionValue));

            Double warningValue = thresholdWarning.getText().isEmpty() ? null : Double.parseDouble(thresholdWarning.getText());
            assetTypeParameters.add(new AssetTypeParameter(TextConstants.WARNING_THRESHOLD, warningValue));

            Double failedValue = thresholdFailed.getText().isEmpty() ? null : Double.parseDouble(thresholdFailed.getText());
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
}
