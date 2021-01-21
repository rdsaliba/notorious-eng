import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

public class SystemTypeEditController implements Initializable {

    @FXML
    private Button systemMenuBtn;
    @FXML
    private Button systemTypeMenuBtn;
    @FXML
    private Button infoSaveBtn;
    @FXML
    private Button infoCancelBtn;
    @FXML
    private TextField systemTypeName;
    @FXML
    private ImageView systemTypeImageView;

    private UIUtilities uiUtilities;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        uiUtilities = new UIUtilities();
        attachEvents();
        setImage();
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
        infoCancelBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, "/SystemTypeInfo"));
    }

    /**
     * Changes the Image depending on the System Type.
     *
     * @author Najim
     */
    public void setImage() {
        if (systemTypeName.getText().equals("Engine")) {
            systemTypeImageView.setImage(new Image("imgs/system_type_engine.png"));
        } else {
            systemTypeImageView.setImage(new Image("imgs/system_type_engine.png"));
        }
    }
}
