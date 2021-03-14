/*
  This class contains methods which are often reused withing the UI.

  @author
  @last_edit 02/7/2020
 */
package Utilities;

import Controllers.AssetInfoController;
import Controllers.AssetTypeInfoController;
import app.item.Asset;
import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.util.ArrayList;

public class UIUtilities {

    /**
     * Given a tableView this function will set the width to fit the largest content
     *
     * @author Paul
     */
    public static void autoResizeColumns(TableView<?> table) {
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        table.getColumns().forEach(column ->
        {
            Text t = new Text(column.getText());
            double max = t.getLayoutBounds().getWidth();
            for (int i = 0; i < table.getItems().size(); i++) {
                if (column.getCellData(i) != null) {
                    t = new Text(column.getCellData(i).toString());
                    double calcWidth = t.getLayoutBounds().getWidth();
                    if (calcWidth > max) {
                        max = calcWidth;
                    }
                }
            }
            column.setPrefWidth(max + 35.0d);
        });
    }

    /**
     * This function validates an input of a change on a text field to only allow the change if it fits the DecimalFormat
     *
     * @author Paul
     */
    public static TextFormatter.Change checkFormat(DecimalFormat format, TextFormatter.Change c) {
        if (c.getControlNewText().isEmpty())
            return c;
        ParsePosition parsePosition = new ParsePosition(0);
        if (format.parse(c.getControlNewText(), parsePosition) == null || parsePosition.getIndex() < c.getControlNewText().length())
            return null;
        return c;
    }

    /**
     * Changes scenes once an element is clicked.
     *
     * @param rootPane
     * @param mouseEvent
     * @param fxmlFileName
     * @author Jeff
     */
    public void changeScene(AnchorPane rootPane, MouseEvent mouseEvent, String fxmlFileName) {
        FadeTransition ft = fadeOut(rootPane);
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
        ft.play();
    }

    /**
     * Changes scenes once an element is clicked. Specifically used for changing scenes
     * via the CustomDialog object.
     *
     * @param mouseEvent
     * @param fxmlFileName
     * @author Najim
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
     * @param rootPane
     * @param mouseEvent
     * @param fxmlFileName
     * @author Jeff
     */
    public void changeScene(AnchorPane rootPane, MouseEvent mouseEvent, TableRow<Asset> row, String fxmlFileName, Asset asset) {
        row.getScene().getWindow();
        if (!row.isEmpty()) {
            changeScene(rootPane, mouseEvent, fxmlFileName, asset);
        }
    }

    /**
     * Changes scenes once an element is clicked, and
     * sends an asset type to the new scene's controller.
     *
     * @param rootPane
     * @param mouseEvent
     * @param fxmlFileName
     * @param row
     * @param assetType
     * @author Najim
     */
    public void changeScene(AnchorPane rootPane, MouseEvent mouseEvent, TableRow<AssetTypeList> row, String fxmlFileName, AssetTypeList assetType) {
        FadeTransition ft = fadeOut(rootPane);
        row.getScene().getWindow();
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
        ft.play();
    }

    /**
     * Changes scenes once an element is clicked, and
     * sends an asset to the new scene's controller.
     *
     * @param rootPane
     * @param mouseEvent   the mouse click event
     * @param fxmlFileName the name of the fxml file we want to navigate to
     * @param asset        the asset we want to sent to the other page
     * @author Paul
     */
    public void changeScene(AnchorPane rootPane, MouseEvent mouseEvent, String fxmlFileName, Asset asset) {
        FadeTransition ft = fadeOut(rootPane);
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource(fxmlFileName + ".fxml"));
            Parent assetTypeParent = loader.load();
            Scene assetTypeInfo = new Scene(assetTypeParent);

            Stage window = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
            window.setScene(assetTypeInfo);
            AssetInfoController controller = loader.getController();
            controller.initData(asset);
            window.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
        ft.play();
    }

    public void changeScene(AnchorPane rootPane, ArrayList<Timeline> timelines, MouseEvent mouseEvent, String s) {
        timelines.forEach(Timeline::stop);
        changeScene(rootPane, mouseEvent, s);
    }

    /**
     * Creates an error message to be displayed next to the TextField or TextArea
     * and makes the border of the TextField red
     *
     * @param inputError         The AnchorPane where error messages will be displayed in
     * @param errorMessages      An array keeping track of error messages
     * @param field              The field being validated
     * @param msg                The error message
     * @param verticalPosition   The vertical position of the error message
     * @param horizontalPosition The horizontal position of the message
     * @param i                  The field number/position (starting from 0)
     * @author Najim
     */
    public static void createInputError(AnchorPane inputError, Text[] errorMessages, TextInputControl field, String msg, double verticalPosition, double horizontalPosition, int i) {
        if (errorMessages[i] == null) {
            errorMessages[i] = new Text(msg);
            errorMessages[i].setLayoutY(verticalPosition);
            errorMessages[i].setLayoutX(horizontalPosition);
            errorMessages[i].getStyleClass().add("error-message");

            inputError.getChildren().add(errorMessages[i]);
            field.getStyleClass().add("input-error");
        } else if (errorMessages[i].getText().equals("")) {
            errorMessages[i].getStyleClass().remove("error-message");
            field.getStyleClass().remove("input-error");

            errorMessages[i] = new Text(msg);
            errorMessages[i].setLayoutY(verticalPosition);
            errorMessages[i].setLayoutX(horizontalPosition);
            errorMessages[i].getStyleClass().add("error-message");

            inputError.getChildren().add(errorMessages[i]);
            field.getStyleClass().add("input-error");
        }
    }

    /**
     * Removes the error message from the AnchorPane and the styling added on the field being validated.
     *
     * @param inputError    The AnchorPane where error messages will be displayed in
     * @param errorMessages An array keeping track of error messages
     * @param validInput    An array keeping track of fields which are valid or invalid
     * @param field         The field being validated
     * @param i             The field number/position (starting from 0)
     * @author Najim
     */
    public static void removeInputError(AnchorPane inputError, Text[] errorMessages, boolean[] validInput, TextInputControl field, int i) {
        if (errorMessages[i] != null && validInput[i]) {
            field.getStyleClass().remove("input-error");
            inputError.getChildren().remove(errorMessages[i]);
            errorMessages[i] = null;
        }
    }

    /**
     * Compares two thresholds and determines if the previous threshold is larger than the next.
     *
     * @param previousThreshold The Threshold preceding
     * @param nextThreshold     The Threshold succeeding
     * @author Najim
     */
    public static boolean compareThresholds(TextField previousThreshold, TextField nextThreshold) {
        boolean valid = true;
        if (!previousThreshold.getText().isEmpty() && !nextThreshold.getText().isEmpty()) {
            double previousThresholdValue = Double.parseDouble(previousThreshold.getText());
            double nextThresholdValue = Double.parseDouble(nextThreshold.getText());
            if (previousThresholdValue <= nextThresholdValue) {
                valid = false;
            }
        }
        return valid;
    }

    public void changeScene(AnchorPane rootPane, Timeline timeline, MouseEvent mouseEvent, String s) {
        timeline.stop();
        changeScene(rootPane, mouseEvent, s);
    }

    /**
     * Creates a fade out animation
     *
     * @param rootPane Root AnchorPane of the view/page
     * @return FadeTransition object
     * @author Najim
     */
    public FadeTransition fadeOut(AnchorPane rootPane) {
        FadeTransition ft = new FadeTransition();
        ft.setDuration(Duration.millis(700));
        ft.setNode(rootPane);
        ft.setFromValue(1);
        ft.setToValue(0);

        return ft;
    }

    /**
     * Creates a fade in animation
     *
     * @param rootPane Root AnchorPane of the view/page
     * @author Najim
     */
    public void fadeInTransition(AnchorPane rootPane) {
        FadeTransition ft = new FadeTransition();
        ft.setDuration(Duration.millis(700));
        ft.setNode(rootPane);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }
}
