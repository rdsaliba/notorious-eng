import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import local.AssetTypeDAOImpl;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.Optional;
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
     * @author Najim, Paul
     */
    public void attachEvents() {
        // Change scenes to Systems.fxml
        systemMenuBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, "/Systems"));
        //Attach link to systemTypeMenuBtn to go to SystemTypeList.fxml
        systemTypeMenuBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, "/SystemTypeList"));
        //Attach link to infoEditBtn to go to SystemTypeEdit.fxml
        infoEditBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, "/SystemTypeEdit", assetType));
        //Attach the asset type delete function
        infoDeleteBtn.setOnMouseClicked(this::deleteDialog);
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
        String ALERT_HEADER = "Confirmation of system type deletion";
        alert.setHeaderText(ALERT_HEADER);
        String ALERT_CONTENT = "Are you sure you want to delete this system type? \n " +
                "this will delete all the assets of this type";
        alert.setContentText(ALERT_CONTENT);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            deleteAssetType();
            uiUtilities.changeScene(mouseEvent, "/SystemTypeList");
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
