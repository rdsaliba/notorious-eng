import app.item.AssetTypeParameter;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class SystemTypeController implements Initializable {

    @FXML
    private Button systemMenuBtn;
    @FXML
    private Button systemTypeMenuBtn;
    @FXML
    private AnchorPane systemTypeInformation;
    @FXML
    private Button saveBtn;
    @FXML
    private TextField systemTypeName;
    @FXML
    private TextArea systemTypeDesc;
    @FXML
    private Button addTypeBtn;

    private UIUtilities uiUtilities;
    private ArrayList<AssetTypeParameter> assetTypeParameters;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        uiUtilities = new UIUtilities();
        attachEvents();
    }

    /**
     * Adds mouse events to all the buttons
     *
     * @author Najim
     */
    public void attachEvents() {
        //Attach link to systemMenuButton to go to Systems.fxml
        systemMenuBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, "/Systems"));
        //Attach link to systemTypeMenuBtn to go to SystemTypeList.fxml
        systemTypeMenuBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, "/SystemTypeList"));
        //Attach link to addTypeBtn to go to AddSystemType.fxml
        addTypeBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, "/AddSystemType"));
    }
}
