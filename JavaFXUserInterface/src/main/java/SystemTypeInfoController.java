import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import local.AssetTypeDAOImpl;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

public class SystemTypeInfoController implements Initializable {

    @FXML
    private Button systemMenuBtn;
    @FXML
    private Button systemTypeMenuBtn;
    @FXML
    private Button infoSaveBtn;
    @FXML
    private Button infoDeleteBtn;
    @FXML
    private Button modelEditBtn;
    @FXML
    private Button modelDeleteBtn;
    @FXML
    private AnchorPane systemTypeInformation;
    @FXML
    private TextField systemTypeName;
    @FXML
    private TextArea systemTypeDesc;
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
    private ImageView systemTypeImageView;

    private UIUtilities uiUtilities;
    private SystemTypeList assetType;
    private SystemTypeList originalAssetType;
    private AssetTypeDAOImpl assetTypeDAO;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        uiUtilities = new UIUtilities();
        assetTypeDAO = new AssetTypeDAOImpl();
        attachEvents();
    }

    /**
     * initData receives the System Type data that was selected from SystemTypeList.FXML
     * Then, uses that data to populate the text fields in the scene.
     *
     * @param assetType
     * @author Najim, Paul
     */
    void initData(SystemTypeList assetType) {
        this.assetType = assetType;
        this.originalAssetType = new SystemTypeList(assetType);
        systemTypeName.setText(assetType.getAssetType().getName());
        systemTypeDesc.setText(assetType.getAssetType().getDescription());
        try { thresholdOK.setText(new DecimalFormat("#.##").format(Double.parseDouble(assetType.getValueOk()))); } catch (NumberFormatException e){ }
        try { thresholdAdvisory.setText(new DecimalFormat("#.##").format(Double.parseDouble(assetType.getValueAdvisory()))); } catch (NumberFormatException e){ }
        try { thresholdCaution.setText(new DecimalFormat("#.##").format(Double.parseDouble(assetType.getValueCaution()))); } catch (NumberFormatException e){ }
        try { thresholdWarning.setText(new DecimalFormat("#.##").format(Double.parseDouble(assetType.getValueWarning()))); } catch (NumberFormatException e){ }
        try { thresholdFailed.setText(new DecimalFormat("#.##").format(Double.parseDouble(assetType.getValueFailed()))); } catch (NumberFormatException e){ }


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
        // Change scenes to Systems.fxml
        systemMenuBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, "/Systems"));
        //Attach link to systemTypeMenuBtn to go to SystemTypeList.fxml
        systemTypeMenuBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, "/SystemTypeList"));

        infoSaveBtn.setDisable(true);
        infoSaveBtn.setOnMouseClicked(mouseEvent -> {
            assetTypeDAO.updateAssetType(assetType.toAssetType());
            uiUtilities.changeScene(mouseEvent, "/SystemTypeList");
        });

        systemTypeName.textProperty().addListener((obs, oldText, newText) -> {
            if( handleTextChange(newText,originalAssetType.getName()))
                assetType.getAssetType().setName(newText);
        });
        systemTypeDesc.textProperty().addListener((obs, oldText, newText) -> {
            if( handleTextChange(newText,originalAssetType.getDescription()))
                assetType.getAssetType().setDescription(newText);
        });

        thresholdOK.textProperty().addListener((obs, oldText, newText) -> {
            if(handleTextChange(newText, originalAssetType.getValueOk()))
                assetType.setValueOk(newText);
        });
        thresholdOK.setTextFormatter( new TextFormatter<>(c -> UIUtilities.checkFormat(TextConstants.ThresholdValueFormat,c)));

        thresholdAdvisory.textProperty().addListener((obs, oldText, newText) -> {
            if(handleTextChange(newText, originalAssetType.getValueAdvisory()))
                assetType.setValueAdvisory(newText);
        });
        thresholdAdvisory.setTextFormatter( new TextFormatter<>(c -> UIUtilities.checkFormat(TextConstants.ThresholdValueFormat,c)));

        thresholdCaution.textProperty().addListener((obs, oldText, newText) -> {
            if(handleTextChange(newText, originalAssetType.getValueCaution()))
                assetType.setValueCaution(newText);
        });
        thresholdCaution.setTextFormatter( new TextFormatter<>(c -> UIUtilities.checkFormat(TextConstants.ThresholdValueFormat,c)));

        thresholdWarning.textProperty().addListener((obs, oldText, newText) -> {
            if(handleTextChange(newText, originalAssetType.getValueWarning()))
                assetType.setValueWarning(newText);
        });
        thresholdWarning.setTextFormatter( new TextFormatter<>(c -> UIUtilities.checkFormat(TextConstants.ThresholdValueFormat,c)));

        thresholdFailed.textProperty().addListener((obs, oldText, newText) -> {
            if(handleTextChange(newText, originalAssetType.getValueFailed()))
                assetType.setValueFailed(newText);
        });
        thresholdFailed.setTextFormatter( new TextFormatter<>(c -> UIUtilities.checkFormat(TextConstants.ThresholdValueFormat,c)));

    }

    /**
     *  Handle the text change of the user fields to turn on or off the save functionality
     *
     * @author  Paul
     */
    private boolean handleTextChange(String newText, String field) {
        if (field.equals(originalAssetType.getName()) || field.equals(originalAssetType.getDescription()) && newText.equals(field))
        {
            infoSaveBtn.setDisable(true);
            infoSaveBtn.getStyleClass().clear();
            infoSaveBtn.getStyleClass().add("infoSaveFalse");
            return false;
        }
        else if (!newText.isEmpty() && Double.parseDouble(newText) == Double.parseDouble(field))
        {
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
     * Changes the Image depending on the System Type.
     *
     * @author Najim
     */
    public void setImage(String typeName) {
        if (typeName.contains("Engine")) {
            systemTypeImageView.setImage(new Image("imgs/system_type_engine.png"));
        } else {
            systemTypeImageView.setImage(new Image("imgs/unknown_system_type.png"));
        }
    }
}
