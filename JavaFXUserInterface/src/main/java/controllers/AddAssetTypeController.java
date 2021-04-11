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
import utilities.FormInputValidation;
import utilities.UIUtilities;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static utilities.TextConstants.*;

public class AddAssetTypeController extends Controller implements Initializable {

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
    private AnchorPane addAssetTypeInformationAnchorPane;
    @FXML
    private AnchorPane root;

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
        root.setOpacity(0);
        uiUtilities.fadeInTransition(root);
        attachEvents();
    }

    /**
     * Attaches events to elements in the scene.
     *
     * @author Jeff , Paul
     */
    public void attachEvents() {
        // Change scenes to Assets.fxml
        backBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(ASSET_TYPE_LIST_SCENE, backBtn.getScene()));
        // Change scenes to Assets.fxml
        cancelBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(ASSET_TYPE_LIST_SCENE, cancelBtn.getScene()));
        saveBtn.setOnMouseClicked(mouseEvent -> {
            if (FormInputValidation.assetTypeFormInputValidation(addAssetTypeInformationAnchorPane, assetTypeName, assetTypeDescription, thresholdAdvisoryValue, thresholdCautionValue, thresholdWarningValue, thresholdFailedValue) && saveAssetType(assembleAssetType())) {
                uiUtilities.changeScene(ASSET_TYPE_LIST_SCENE, saveBtn.getScene());
            }
        });

        thresholdAdvisoryValue.setTextFormatter(new TextFormatter<>(c -> FormInputValidation.checkFormat(ThresholdValueFormat, c)));
        thresholdCautionValue.setTextFormatter(new TextFormatter<>(c -> FormInputValidation.checkFormat(ThresholdValueFormat, c)));
        thresholdWarningValue.setTextFormatter(new TextFormatter<>(c -> FormInputValidation.checkFormat(ThresholdValueFormat, c)));
        thresholdFailedValue.setTextFormatter(new TextFormatter<>(c -> FormInputValidation.checkFormat(ThresholdValueFormat, c)));
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
            assetTypeParameters.add(new AssetTypeParameter(ADVISORY_THRESHOLD, advisoryValue));

            Double cautionValue = thresholdCautionValue.getText().isEmpty() ? null : Double.parseDouble(thresholdCautionValue.getText());
            assetTypeParameters.add(new AssetTypeParameter(CAUTION_THRESHOLD, cautionValue));

            Double warningValue = thresholdWarningValue.getText().isEmpty() ? null : Double.parseDouble(thresholdWarningValue.getText());
            assetTypeParameters.add(new AssetTypeParameter(WARNING_THRESHOLD, warningValue));

            Double failedValue = thresholdFailedValue.getText().isEmpty() ? null : Double.parseDouble(thresholdFailedValue.getText());
            assetTypeParameters.add(new AssetTypeParameter(FAILED_THRESHOLD, failedValue));

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
