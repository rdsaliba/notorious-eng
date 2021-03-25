package utilities;

import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.util.ArrayList;

import static utilities.TextConstants.*;

public class FormInputValidation {
    private final static int inputValidationVGap = 10;
    private static final String ERROR_MESSAGE = "error-message";
    private static final String INPUT_ERROR = "input-error";

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
     * Creates an error message to be displayed next to the TextField or TextArea
     * and makes the border of the TextField red
     *
     * @param inputValidationAnchorPane The AnchorPane where error messages will be displayed in
     * @param errorMessages             An array keeping track of error messages
     * @param field                     The field being validated
     * @param msg                       The error message
     * @param verticalPosition          The vertical position of the error message
     * @param horizontalPosition        The horizontal position of the message
     * @param i                         The field number/position (starting from 0)
     * @author Najim
     */
    public static void createInputError(AnchorPane inputValidationAnchorPane, Text[] errorMessages, TextInputControl field, String msg, double verticalPosition, double horizontalPosition, int i) {
        if (errorMessages[i] != null && errorMessages[i].getText().equals("")) {
            errorMessages[i].getStyleClass().remove(ERROR_MESSAGE);
            field.getStyleClass().remove(INPUT_ERROR);
        }

        errorMessages[i] = new Text(msg);
        errorMessages[i].setLayoutY(verticalPosition);
        errorMessages[i].setLayoutX(horizontalPosition);
        errorMessages[i].getStyleClass().add(ERROR_MESSAGE);

        inputValidationAnchorPane.getChildren().add(errorMessages[i]);
        field.getStyleClass().add(INPUT_ERROR);
    }

    /**
     * Removes the error message from the AnchorPane and the styling added on the field being validated.
     *
     * @param inputValidationAnchorPane The AnchorPane where error messages will be displayed in
     * @param errorMessages             An array keeping track of error messages
     * @param field                     The field being validated
     * @param i                         The field number/position (starting from 0)
     * @author Najim
     */
    public static void removeInputError(AnchorPane inputValidationAnchorPane, Text[] errorMessages, TextInputControl field, int i) {
        if (errorMessages[i] != null) {
            field.getStyleClass().remove(INPUT_ERROR);
            inputValidationAnchorPane.getChildren().remove(errorMessages[i]);
            errorMessages[i] = null;
        }
    }

    /**
     * Displays an error for a field when the validation criteria are not respected.
     *
     * @author Najim, Jeremie
     */
    public static boolean assetTypeFormInputValidation(AnchorPane assetTypeInformationAnchorPane, TextField assetTypeName, TextArea assetTypeDesc, TextField thresholdAdvisory, TextField thresholdCaution, TextField thresholdWarning, TextField thresholdFailed) {
        boolean validForm = true;
        Text[] inputValidationMessages = new Text[7];
        String assetTypeNameValue = assetTypeName.getText();
        String assetTypeDescValue = assetTypeDesc.getText();
        ArrayList<TextInputControl> textInputControls = new ArrayList<>();

        // Creating an ArrayList of the TextInputControl components included in the form
        for (Node node : assetTypeInformationAnchorPane.getChildren()) {
            if (node instanceof TextInputControl) {
                textInputControls.add((TextInputControl) node);
            }
        }
        double inputValidationXLayout = textInputControls.get(0).getWidth() + textInputControls.get(0).getLayoutX();

        // Clearing all previous existing errors if any
        removeInputError(assetTypeInformationAnchorPane, inputValidationMessages, assetTypeDesc, textInputControls.indexOf(assetTypeName));
        removeInputError(assetTypeInformationAnchorPane, inputValidationMessages, assetTypeDesc, textInputControls.indexOf(assetTypeDesc));
        removeInputError(assetTypeInformationAnchorPane, inputValidationMessages, thresholdAdvisory, textInputControls.indexOf(thresholdAdvisory));
        removeInputError(assetTypeInformationAnchorPane, inputValidationMessages, thresholdCaution, textInputControls.indexOf(thresholdCaution));
        removeInputError(assetTypeInformationAnchorPane, inputValidationMessages, thresholdWarning, textInputControls.indexOf(thresholdWarning));
        removeInputError(assetTypeInformationAnchorPane, inputValidationMessages, thresholdFailed, textInputControls.indexOf(thresholdFailed));

        // Asset type name input validation
        if (assetTypeNameValue.trim().isEmpty()) {
            validForm = false;
            createInputError(assetTypeInformationAnchorPane, inputValidationMessages, assetTypeName, EMPTY_FIELD_ERROR, assetTypeName.getLayoutY() + inputValidationVGap, inputValidationXLayout, textInputControls.indexOf(assetTypeName));
        } else if (assetTypeNameValue.length() > 50) {
            validForm = false;
            createInputError(assetTypeInformationAnchorPane, inputValidationMessages, assetTypeName, MAX_50_CHARACTERS_ERROR, assetTypeName.getLayoutY() + inputValidationVGap, inputValidationXLayout, textInputControls.indexOf(assetTypeName));
        } else {
            removeInputError(assetTypeInformationAnchorPane, inputValidationMessages, assetTypeName, textInputControls.indexOf(assetTypeName));
        }

        // Asset type description input validation
        if (assetTypeDescValue.length() > 300) {
            validForm = false;
            createInputError(assetTypeInformationAnchorPane, inputValidationMessages, assetTypeDesc, MAX_300_CHARACTERS_ERROR, assetTypeDesc.getLayoutY() + inputValidationVGap, inputValidationXLayout, textInputControls.indexOf(assetTypeDesc));
        } else {
            removeInputError(assetTypeInformationAnchorPane, inputValidationMessages, assetTypeDesc, textInputControls.indexOf(assetTypeDesc));
        }

        // Advisory threshold input validation
        if (!compareThresholds(thresholdAdvisory, thresholdCaution)) {
            validForm = false;
            createInputError(assetTypeInformationAnchorPane, inputValidationMessages, thresholdAdvisory, ADVISORY_CAUTION, thresholdAdvisory.getLayoutY() + inputValidationVGap, inputValidationXLayout, textInputControls.indexOf(thresholdAdvisory));
        } else if (!compareThresholds(thresholdAdvisory, thresholdWarning)) {
            validForm = false;
            createInputError(assetTypeInformationAnchorPane, inputValidationMessages, thresholdAdvisory, ADVISORY_WARNING, thresholdAdvisory.getLayoutY() + inputValidationVGap, inputValidationXLayout, textInputControls.indexOf(thresholdAdvisory));
        } else if (!compareThresholds(thresholdAdvisory, thresholdFailed)) {
            validForm = false;
            createInputError(assetTypeInformationAnchorPane, inputValidationMessages, thresholdAdvisory, ADVISORY_FAILED, thresholdAdvisory.getLayoutY() + inputValidationVGap, inputValidationXLayout, textInputControls.indexOf(thresholdAdvisory));
        } else {
            removeInputError(assetTypeInformationAnchorPane, inputValidationMessages, thresholdAdvisory, textInputControls.indexOf(thresholdAdvisory));
            removeInputError(assetTypeInformationAnchorPane, inputValidationMessages, thresholdCaution, textInputControls.indexOf(thresholdCaution));
            removeInputError(assetTypeInformationAnchorPane, inputValidationMessages, thresholdWarning, textInputControls.indexOf(thresholdWarning));
            removeInputError(assetTypeInformationAnchorPane, inputValidationMessages, thresholdFailed, textInputControls.indexOf(thresholdFailed));
        }

        // Caution Threshold input validation
        if (!compareThresholds(thresholdCaution, thresholdWarning)) {
            validForm = false;
            createInputError(assetTypeInformationAnchorPane, inputValidationMessages, thresholdCaution, CAUTION_WARNING, thresholdCaution.getLayoutY() + inputValidationVGap, inputValidationXLayout, textInputControls.indexOf(thresholdCaution));
        } else if (!compareThresholds(thresholdCaution, thresholdFailed)) {
            validForm = false;
            createInputError(assetTypeInformationAnchorPane, inputValidationMessages, thresholdCaution, CAUTION_FAILED, thresholdCaution.getLayoutY() + inputValidationVGap, inputValidationXLayout, textInputControls.indexOf(thresholdCaution));
        } else {
            removeInputError(assetTypeInformationAnchorPane, inputValidationMessages, thresholdCaution, textInputControls.indexOf(thresholdCaution));
            removeInputError(assetTypeInformationAnchorPane, inputValidationMessages, thresholdWarning, textInputControls.indexOf(thresholdWarning));
            removeInputError(assetTypeInformationAnchorPane, inputValidationMessages, thresholdFailed, textInputControls.indexOf(thresholdFailed));
        }

        // Warning Threshold input validation
        if (!compareThresholds(thresholdWarning, thresholdFailed)) {
            validForm = false;
            createInputError(assetTypeInformationAnchorPane, inputValidationMessages, thresholdWarning, WARNING_FAILED, thresholdWarning.getLayoutY() + inputValidationVGap, inputValidationXLayout, textInputControls.indexOf(thresholdWarning));
        } else {
            removeInputError(assetTypeInformationAnchorPane, inputValidationMessages, thresholdWarning, textInputControls.indexOf(thresholdWarning));
            removeInputError(assetTypeInformationAnchorPane, inputValidationMessages, thresholdFailed, textInputControls.indexOf(thresholdFailed));
        }
        return validForm;
    }

    /**
     * Compares two thresholds and determines if the previous threshold is larger than the next.
     *
     * @param previousThreshold The Threshold preceding
     * @param nextThreshold     The Threshold succeeding
     * @author Najim
     */
    private static boolean compareThresholds(TextField previousThreshold, TextField nextThreshold) {
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
}
