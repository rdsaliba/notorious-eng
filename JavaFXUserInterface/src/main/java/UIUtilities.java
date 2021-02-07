
import app.item.Asset;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParsePosition;

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
            Parent assetsParent = loader.load();
            Scene assetInfo = new Scene(assetsParent);

            Stage window = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();

            window.setScene(assetInfo);
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
            if(!row.isEmpty()) {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/AssetInfo.fxml"));
                Parent assetsParent = loader.load();
                Scene assetInfo = new Scene(assetsParent);

                Stage window = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
                window.setScene(assetInfo);
                AssetInfoController controller = loader.getController();
                controller.initData(asset);
                window.show();
            }
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
    public void changeScene(MouseEvent mouseEvent, TableRow<AssetTypeList> row, String fxmlFileName, AssetTypeList assetType) {
        Stage primaryStage = (Stage) row.getScene().getWindow();
        try {
            if (!row.isEmpty()) {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource(fxmlFileName + ".fxml"));
                Parent assetTypeParent = loader.load();
                Scene assetTypeInfo = new Scene(assetTypeParent);

                Stage window = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
                window.setScene(assetTypeInfo);
                AssetTypeInfoController controller = loader.getController();
                controller.initData(assetType);
                controller.setImage(assetType.getAssetType().getName());
                window.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Given a tableView this function will set the width to fit the largest content
     *
     * @author  Paul
     */
    public static void autoResizeColumns( TableView<?> table )
    {
        table.setColumnResizePolicy( TableView.UNCONSTRAINED_RESIZE_POLICY);
        table.getColumns().stream().forEach( (column) ->
        {
            Text t = new Text( column.getText() );
            double max = t.getLayoutBounds().getWidth();
            for ( int i = 0; i < table.getItems().size(); i++ )
            {
                if ( column.getCellData( i ) != null )
                {
                    t = new Text( column.getCellData( i ).toString() );
                    double calcwidth = t.getLayoutBounds().getWidth();
                    if ( calcwidth > max )
                    {
                        max = calcwidth;
                    }
                }
            }
            column.setPrefWidth( max + 35.0d );
        } );
    }

    /**
     * This function validates an input of a change on a textfield to only allow the change if it fits the DecimalFormat
     *
     * @author Paul
     */
    public static TextFormatter.Change checkFormat(DecimalFormat format, TextFormatter.Change c) {
        if ( c.getControlNewText().isEmpty() )
            return c;
        ParsePosition parsePosition = new ParsePosition( 0 );
        if ( format.parse( c.getControlNewText(), parsePosition ) == null || parsePosition.getIndex() < c.getControlNewText().length() )
            return null;
        return c;
    }

}
