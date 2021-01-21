
import app.item.Asset;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableRow;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class UIUtilities {

    /**
     * Changes scenes once an element is clicked.
     *
     * @param mouseEvent
     * @param fxmlFileName
     *
     * @author Jeff
     */
    public void changeScene(MouseEvent mouseEvent, String fxmlFileName) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource(fxmlFileName + ".fxml"));
            Parent systemsParent = loader.load();
            Scene systemInfo = new Scene(systemsParent);

            Stage window = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();

            window.setScene(systemInfo);
            window.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Changes scenes once an element is clicked, and
     * sends an asset to the new scene's controller.
     *
     * @param mouseEvent
     * @param fxmlFileName
     *
     * @author Jeff
     */
    public void changeScene(MouseEvent mouseEvent, TableRow<Asset> row, String fxmlFileName, Asset asset) {
        Stage primaryStage = (Stage) row.getScene().getWindow();
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/SystemInfo.fxml"));
            Parent systemsParent = loader.load();
            Scene systemInfo = new Scene(systemsParent);

            Stage window = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
            window.setScene(systemInfo);
            SystemInfoController controller = loader.getController();
            controller.initData(asset);
            window.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Changes scenes once an element is clicked, and
     * sends an asset type to the new scene's controller.
     *
     * @param mouseEvent
     * @param fxmlFileName
     * @param row
     * @param assetType
     * @author Najim
     */
    public void changeScene(MouseEvent mouseEvent, TableRow<SystemTypeList> row, String fxmlFileName, SystemTypeList assetType) {
        Stage primaryStage = (Stage) row.getScene().getWindow();
        try {
            if (!row.isEmpty()) {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource(fxmlFileName + ".fxml"));
                Parent systemTypeParent = loader.load();
                Scene systemTypeInfo = new Scene(systemTypeParent);

                Stage window = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
                window.setScene(systemTypeInfo);
                SystemTypeInfoController controller = loader.getController();
                controller.initData(assetType);
                controller.setImage(assetType.getName());
                window.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Changes scenes once the Edit button is clicked
     * from the system type info or when the Cancel button is clicked
     * from the system type edit page.
     *
     * @param mouseEvent
     * @param fxmlFileName
     * @param assetType
     * @author Najim
     */
    public void changeScene(MouseEvent mouseEvent, String fxmlFileName, SystemTypeList assetType) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource(fxmlFileName + ".fxml"));
            Parent systemTypeParent = loader.load();
            Scene systemType = new Scene(systemTypeParent);

            Stage window = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
            window.setScene(systemType);
            if (fxmlFileName.equals("/SystemTypeEdit")) {
                SystemTypeEditController controller = loader.getController();
                controller.initData(assetType);
                controller.setImage(assetType.getName());
            }
            if (fxmlFileName.equals("/SystemTypeInfo")) {
                SystemTypeInfoController controller = loader.getController();
                controller.initData(assetType);
                controller.setImage(assetType.getName());
            }
            window.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
