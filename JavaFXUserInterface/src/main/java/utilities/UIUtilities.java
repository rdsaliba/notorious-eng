/*
/*
  This class contains methods which are often reused withing the UI.

  @author
  @last_edit 02/7/2020
 */
package utilities;

import app.item.Asset;
import controllers.AssetInfoController;
import controllers.AssetTypeInfoController;
import controllers.Controller;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParsePosition;

public class UIUtilities {

    private static final String FXML = ".fxml";
    private static final String ERROR_MESSAGE = "error-message";
    private static final String INPUT_ERROR = "input-error";
    Logger logger = LoggerFactory.getLogger(UIUtilities.class);

    /**
     * Given a tableView this function will set the width to fit the largest content
     *
     * @param table is the table view object that will be resized
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
     * @param format is the decimal format to be applied to the field
     * @param c      is the text formatter change
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
     * This function validates an input of a change on a text field to only allow the change if it fits the DecimalFormat
     *
     * @param regex is the decimal format to be applied to the field
     * @param c     is the text formatter change
     * @author Paul
     */
    public static TextFormatter.Change checkFormat(String regex, TextFormatter.Change c) {
        if (c.getControlNewText().matches(regex))
            return c;
        return null;
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
            errorMessages[i].getStyleClass().add(ERROR_MESSAGE);

            inputError.getChildren().add(errorMessages[i]);
            field.getStyleClass().add(INPUT_ERROR);
        } else if (errorMessages[i].getText().equals("")) {
            errorMessages[i].getStyleClass().remove(ERROR_MESSAGE);
            field.getStyleClass().remove(INPUT_ERROR);

            errorMessages[i] = new Text(msg);
            errorMessages[i].setLayoutY(verticalPosition);
            errorMessages[i].setLayoutX(horizontalPosition);
            errorMessages[i].getStyleClass().add(ERROR_MESSAGE);

            inputError.getChildren().add(errorMessages[i]);
            field.getStyleClass().add(INPUT_ERROR);
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
            field.getStyleClass().remove(INPUT_ERROR);
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

    /**
     * Changes scenes once an element is clicked.
     *
     * @param fxmlFileName is the name of the resource file for the scene
     * @param scene        is the screen that will app will be changed to
     * @author Jeff
     */
    public void changeScene(String fxmlFileName, Scene scene) {
        try {
            ((Controller) ((FXMLLoader) scene.getUserData()).getController()).getTimelines().forEach(Timeline::stop);
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource(fxmlFileName + FXML));
            Parent parent = loader.load();
            scene.setRoot(parent);
            scene.setUserData(loader);
        } catch (IOException e) {
            logger.error("Exception in changeScene(): ", e);
        }
    }

    /**
     * Changes scenes once an element is clicked, and
     * sends an asset to the new scene's controller.
     *
     * @param fxmlFileName the name of the fxml file that will be loaded for the scene
     * @author Jeff
     */
    public void changeScene(TableRow<Asset> row, String fxmlFileName, Asset asset, Scene scene) {
        row.getScene().getWindow();
        if (!row.isEmpty()) {
            changeScene(fxmlFileName, asset, scene);
        }
    }

    /**
     * Changes scenes once an element is clicked, and
     * sends an asset type to the new scene's controller.
     *
     * @param fxmlFileName the name of the fxml file that will be loaded for the scene
     * @param row          is the table row of an asset type
     * @param assetType    is the asset type being observed
     * @author Najim, Jeff
     */
    public void changeScene(TableRow<AssetTypeList> row, String fxmlFileName, AssetTypeList assetType, Scene scene) {
        try {
            if (!row.isEmpty()) {
                ((Controller) ((FXMLLoader) scene.getUserData()).getController()).getTimelines().forEach(Timeline::stop);
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource(fxmlFileName + FXML));
                Parent parent = loader.load();
                scene.setRoot(parent);
                scene.setUserData(loader);
                AssetTypeInfoController controller = loader.getController();
                controller.initData(assetType);
            }
        } catch (IOException e) {
            logger.error("Exception in changeScene 2: ", e);
        }
    }

    /**
     * Changes scenes once an element is clicked, and
     * sends an asset to the new scene's controller.
     *
     * @param fxmlFileName the name of the fxml file that will be loaded for the scene
     * @param asset        the asset we want to sent to the other page
     * @author Paul, Jeff
     */
    public void changeScene(String fxmlFileName, Asset asset, Scene scene) {
        try {
            ((Controller) ((FXMLLoader) scene.getUserData()).getController()).getTimelines().forEach(Timeline::stop);
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource(fxmlFileName + FXML));
            Parent parent = loader.load();
            scene.setRoot(parent);
            scene.setUserData(loader);
            AssetInfoController controller = loader.getController();
            controller.initData(asset);

        } catch (IOException e) {
            logger.error("Exception in changeScene 3: ", e);
        }
    }
}
