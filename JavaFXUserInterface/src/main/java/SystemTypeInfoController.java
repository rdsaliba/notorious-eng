import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

public class SystemTypeInfoController implements Initializable {

    @FXML
    private Button systemMenuBtn;
    @FXML
    private Button systemTypeMenuBtn;
    @FXML
    private Button infoEditBtn;
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        uiUtilities = new UIUtilities();
        attachEvents();
    }

    /**
     * initData receives the System Type data that was selected from SystemTypeList.FXML
     * Then, uses that data to populate the text fields in the scene.
     *
     * @param assetType
     * @author Najim
     */
    void initData(SystemTypeList assetType) {
        this.assetType = assetType;
        systemTypeName.setText(assetType.getName());
        thresholdOK.setText(new DecimalFormat("#.##").format(assetType.getValue_ok()));
        thresholdAdvisory.setText(new DecimalFormat("#.##").format(assetType.getValue_advisory()));
        thresholdCaution.setText(new DecimalFormat("#.##").format(assetType.getValue_caution()));
        thresholdWarning.setText(new DecimalFormat("#.##").format(assetType.getValue_warning()));
        thresholdFailed.setText(new DecimalFormat("#.##").format(assetType.getValue_failed()));
    }

    /**
     * Attaches events to elements in the scene.
     *
     * @author Najim
     */
    public void attachEvents() {
        // Change scenes to Systems.fxml
        systemMenuBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, "/Systems"));
        //Attach link to systemTypeMenuBtn to go to SystemTypeList.fxml
        systemTypeMenuBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, "/SystemTypeList"));
        //Attach link to infoEditBtn to go to SystemTypeEdit.fxml
        infoEditBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, "/SystemTypeEdit", assetType));
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
