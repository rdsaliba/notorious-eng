/*
  This Controller is responsible for handling the addition of asset types.
  @author Jeff, Paul, Najim
  @last_edit 02/7/2020
 */
package Controllers;

import Utilities.TextConstants;
import Utilities.UIUtilities;
import app.item.AssetType;
import app.item.AssetTypeParameter;
import external.AssetTypeDAOImpl;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class AddAssetTypeController implements Initializable {

    @FXML
    private Button assetMenuBtn;
    @FXML
    private Button assetTypeMenuBtn;
    @FXML
    private Button cancelBtn;
    @FXML
    private Button saveBtn;
    @FXML
    private TextField assetTypeName;
    @FXML
    private TextArea assetTypeDescription;
    @FXML
    private TextField thresholdOKValue;
    @FXML
    private TextField thresholdAdvisoryValue;
    @FXML
    private TextField thresholdCautionValue;
    @FXML
    private TextField thresholdWarningValue;
    @FXML
    private TextField thresholdFailedValue;

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
        assetMenuBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, TextConstants.ASSET_TYPE_LIST_SCENE));
        //Attach link to assetTypeMenuBtn to go to Utilities.AssetTypeList.fxml
        assetTypeMenuBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, TextConstants.ASSET_TYPE_LIST_SCENE));
        // Change scenes to Assets.fxml
        cancelBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, TextConstants.ASSET_TYPE_LIST_SCENE));
        saveBtn.setOnMouseClicked(mouseEvent -> {
            if (saveAssetType(assembleAssetType()))
                uiUtilities.changeScene(mouseEvent, TextConstants.ASSET_TYPE_LIST_SCENE);
        });


        thresholdOKValue.setTextFormatter(new TextFormatter<>(c -> UIUtilities.checkFormat(TextConstants.ThresholdValueFormat, c)));
        thresholdAdvisoryValue.setTextFormatter(new TextFormatter<>(c -> UIUtilities.checkFormat(TextConstants.ThresholdValueFormat, c)));
        thresholdCautionValue.setTextFormatter(new TextFormatter<>(c -> UIUtilities.checkFormat(TextConstants.ThresholdValueFormat, c)));
        thresholdWarningValue.setTextFormatter(new TextFormatter<>(c -> UIUtilities.checkFormat(TextConstants.ThresholdValueFormat, c)));
        thresholdFailedValue.setTextFormatter(new TextFormatter<>(c -> UIUtilities.checkFormat(TextConstants.ThresholdValueFormat, c)));
    }


    /**
     * Create an asset type object from the user's input
     *
     * @author Jeff , Paul
     */
    public AssetType assembleAssetType() {
        if (assetTypeName.getText().length() > 0 && assetTypeDescription.getText().length() > 0) {
            AssetType assetType = new AssetType(assetTypeName.getText());
            assetType.setDescription(assetTypeDescription.getText());

            Double okValue = thresholdOKValue.getText().isEmpty() ? null : Double.parseDouble(thresholdOKValue.getText());
            assetTypeParameters.add(new AssetTypeParameter(TextConstants.OK_THRESHOLD, okValue));

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
}
