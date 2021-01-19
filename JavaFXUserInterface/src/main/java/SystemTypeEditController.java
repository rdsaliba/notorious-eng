import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

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

    private UIUtilities uiUtilities;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        uiUtilities = new UIUtilities();
        attachEvents();
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
//        //Attach link to infoEditBtn to go to SystemTypeEdit.fxml
//        infoSaveBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
//            @Override
//            public void handle(MouseEvent mouseEvent) {
//                uiUtilities.changeScene(mouseEvent, "/SystemTypeEdit");
//            }
//        });
        //Attach link to infoEditBtn to go to SystemTypeEdit.fxml
        infoCancelBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                uiUtilities.changeScene(mouseEvent, "/SystemTypeInfo");
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
