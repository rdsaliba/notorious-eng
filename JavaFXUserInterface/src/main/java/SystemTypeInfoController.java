import app.item.AssetType;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
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

    private UIUtilities uiUtilities;

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
    void initData(AssetType assetType) {

    }

    /**
     * Attaches events to elements in the scene.
     *
     * @author Najim
     */
    public void attachEvents() {
        // Change scenes to Systems.fxml
        systemMenuBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                uiUtilities.changeScene(mouseEvent, "/Systems");
            }
        });
        //Attach link to systemTypeMenuBtn to go to SystemTypeList.fxml
        systemTypeMenuBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                uiUtilities.changeScene(mouseEvent, "/SystemTypeList");
            }
        });
        //Attach link to infoEditBtn to go to SystemTypeEdit.fxml
        infoEditBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                uiUtilities.changeScene(mouseEvent, "/SystemTypeEdit");
            }
        });
//        //Attach function to deleteBtn to delete an asset type
//        infoDeleteBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
//            @Override
//            public void handle(MouseEvent mouseEvent) {}
//        });
//        //Attach function to deleteBtn to delete an asset type
//        modelDeleteBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
//            @Override
//            public void handle(MouseEvent mouseEvent) {}
//        });
    }
}
