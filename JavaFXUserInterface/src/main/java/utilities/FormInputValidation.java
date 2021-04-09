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

import static local.DAO.logger;
import static utilities.TextConstants.*;

public class FormInputValidation {
    private final static int INPUT_VALIDATION_H_GAP = 10;
    private final static int MAX_NB_TEXT_INPUT_CONTROL = 7;
    private final static Text[] INPUT_VALIDATION_MESSAGES = new Text[MAX_NB_TEXT_INPUT_CONTROL];
    private static final String ERROR_MESSAGE = "error-message";
    private static final String INPUT_ERROR = "input-error";
    private static boolean assetValidForm = true;

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
     * This function validates an input of a change on a text field to only allow the change if it satisfies the regex rule
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
     * @param inputValidationAnchorPane The AnchorPane where error messages will be displayed in
     * @param errorMessages             An array keeping track of error messages
     * @param field                     The field being validated
     * @param msg                       The error message
     * @param i                         The field number/position (starting from 0)
     * @author Najim
     */
    public static void createInputError(AnchorPane inputValidationAnchorPane, Text[] errorMessages, TextInputControl field, String msg, int i) {
        double inputValidationVGap = field.getHeight() / 2;
        if (errorMessages[i] == null) {
            errorMessages[i] = new Text(msg);
            errorMessages[i].setLayoutY(field.getLayoutY() + inputValidationVGap);
            errorMessages[i].setLayoutX(field.getLayoutX() + field.getWidth() + INPUT_VALIDATION_H_GAP);
            errorMessages[i].getStyleClass().add(ERROR_MESSAGE);

            inputValidationAnchorPane.getChildren().add(errorMessages[i]);
            field.getStyleClass().add(INPUT_ERROR);
        }
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
            errorMessages[i].getStyleClass().remove(ERROR_MESSAGE);
            inputValidationAnchorPane.getChildren().remove(errorMessages[i]);
            errorMessages[i] = null;
        }
    }

    /**
     * This function validates the user input for asset Type input forms. Whenever an input is invalid,
     * it creates and displays an error message for the specific invalid input field.
     *
     * @param textInputControlAnchorPane The Anchor pane of the input form
     * @param assetTypeName              TextInputControl for the asset Type name
     * @param assetTypeDescription       TextInputControl for the asset Type description
     * @param thresholdAdvisory          TextInputControl for the advisory threshold
     * @param thresholdCaution           TextInputControl for the caution threshold
     * @param thresholdWarning           TextInputControl for the warning threshold
     * @param thresholdFailed            TextInputControl for the failed threshold
     * @author Najim, Jeremie
     */
    public static boolean assetTypeFormInputValidation(AnchorPane textInputControlAnchorPane, TextField assetTypeName, TextArea assetTypeDescription, TextField thresholdAdvisory, TextField thresholdCaution, TextField thresholdWarning, TextField thresholdFailed) {
        boolean validForm = true;
        String assetTypeNameValue = assetTypeName.getText();
        String assetTypeDescValue = assetTypeDescription.getText();
        ArrayList<TextInputControl> textInputControls = new ArrayList<>();

        // Initializing the form input validation
        initializeFormInputValidation(textInputControlAnchorPane, textInputControls);

        // Asset type name input validation
        if (assetTypeNameValue.trim().isEmpty()) {
            validForm = false;
            createInputError(textInputControlAnchorPane, INPUT_VALIDATION_MESSAGES, assetTypeName, EMPTY_FIELD_ERROR, textInputControls.indexOf(assetTypeName));
        } else if (assetTypeNameValue.length() > 50) {
            validForm = false;
            createInputError(textInputControlAnchorPane, INPUT_VALIDATION_MESSAGES, assetTypeName, MAX_50_CHARACTERS_ERROR, textInputControls.indexOf(assetTypeName));
        }

        // Asset type description input validation
        if (assetTypeDescValue.length() > 300) {
            validForm = false;
            createInputError(textInputControlAnchorPane, INPUT_VALIDATION_MESSAGES, assetTypeDescription, MAX_300_CHARACTERS_ERROR, textInputControls.indexOf(assetTypeDescription));
        }

        // Advisory threshold input validation
        if (!compareThresholds(thresholdAdvisory, thresholdCaution)) {
            validForm = false;
            createInputError(textInputControlAnchorPane, INPUT_VALIDATION_MESSAGES, thresholdAdvisory, ADVISORY_CAUTION, textInputControls.indexOf(thresholdAdvisory));
        } else if (!compareThresholds(thresholdAdvisory, thresholdWarning)) {
            validForm = false;
            createInputError(textInputControlAnchorPane, INPUT_VALIDATION_MESSAGES, thresholdAdvisory, ADVISORY_WARNING, textInputControls.indexOf(thresholdAdvisory));
        } else if (!compareThresholds(thresholdAdvisory, thresholdFailed)) {
            validForm = false;
            createInputError(textInputControlAnchorPane, INPUT_VALIDATION_MESSAGES, thresholdAdvisory, ADVISORY_FAILED, textInputControls.indexOf(thresholdAdvisory));
        }

        // Caution Threshold input validation
        if (!compareThresholds(thresholdCaution, thresholdWarning)) {
            validForm = false;
            createInputError(textInputControlAnchorPane, INPUT_VALIDATION_MESSAGES, thresholdCaution, CAUTION_WARNING, textInputControls.indexOf(thresholdCaution));
        } else if (!compareThresholds(thresholdCaution, thresholdFailed)) {
            validForm = false;
            createInputError(textInputControlAnchorPane, INPUT_VALIDATION_MESSAGES, thresholdCaution, CAUTION_FAILED, textInputControls.indexOf(thresholdCaution));
        }

        // Warning Threshold input validation
        if (!compareThresholds(thresholdWarning, thresholdFailed)) {
            validForm = false;
            createInputError(textInputControlAnchorPane, INPUT_VALIDATION_MESSAGES, thresholdWarning, WARNING_FAILED, textInputControls.indexOf(thresholdWarning));
        }
        return validForm;
    }

    /**
     * This function validates the user input for asset input forms. Whenever an input is invalid,
     * it creates and displays an error message for the specific invalid input field.
     *
     * @param textInputControlAnchorPane The Anchor pane of the input form
     * @param assetNameInput             TextInputControl for the asset name
     * @param assetDescriptionInput      TextInputControl for the asset description
     * @param serialNumberInput          TextInputControl for the asset serial number
     * @param manufacturerInput          TextInputControl for the asset manufacturer
     * @param categoryInput              TextInputControl for the asset category
     * @param siteInput                  TextInputControl for the asset site
     * @param locationInput              TextInputControl for the asset location
     * @author Najim, Jeremie
     */
    public static boolean assetFormInputValidation(AnchorPane textInputControlAnchorPane, TextField assetNameInput, TextArea assetDescriptionInput, TextField serialNumberInput, TextField manufacturerInput, TextField categoryInput, TextField siteInput, TextField locationInput) {
        String regexWordAndHyphen = "(?=\\S*[-])([a-zA-Z0-9-]*)|([a-zA-Z0-9]*)"; //Any word containing letters, numbers and hyphens
        String regexLettersAndHyphen = "(?=\\S*[-])([a-zA-Z-]*)|([a-zA-Z]*)"; //Any word containing letters and hyphens
        assetValidForm = true;
        ArrayList<TextInputControl> textInputControls = new ArrayList<>();

        // Initializing the form input validation
        initializeFormInputValidation(textInputControlAnchorPane, textInputControls);

        logger.info("Start - formInputValidation() -> The form is : {}", assetValidForm);

        // Input validation on the different asset input fields
        assetNameValidation(textInputControlAnchorPane, assetNameInput, textInputControls.indexOf(assetNameInput));
        assetDescriptionValidation(textInputControlAnchorPane, assetDescriptionInput, textInputControls.indexOf(assetDescriptionInput));
        serialNumberValidation(textInputControlAnchorPane, serialNumberInput, regexWordAndHyphen, textInputControls.indexOf(serialNumberInput));
        manufacturerValidation(textInputControlAnchorPane, manufacturerInput, regexWordAndHyphen, textInputControls.indexOf(manufacturerInput));
        categoryValidation(textInputControlAnchorPane, categoryInput, regexLettersAndHyphen, textInputControls.indexOf(categoryInput));
        siteValidation(textInputControlAnchorPane, siteInput, regexWordAndHyphen, textInputControls.indexOf(siteInput));
        locationValidation(textInputControlAnchorPane, locationInput, regexWordAndHyphen, textInputControls.indexOf(locationInput));

        logger.info("End - formInputValidation() -> The form is : {}", assetValidForm);

        return assetValidForm;
    }

    /**
     * This function validates the user input for asset input forms when editing an asset.
     * Whenever an input is invalid, it creates and displays an error message for the specific invalid input field.
     *
     * @param textInputControlAnchorPane The Anchor pane of the input form
     * @param assetNameInput             TextInputControl for the asset name
     * @param assetDescriptionInput      TextInputControl for the asset description
     * @param manufacturerInput          TextInputControl for the asset manufacturer
     * @param categoryInput              TextInputControl for the asset category
     * @param siteInput                  TextInputControl for the asset site
     * @param locationInput              TextInputControl for the asset location
     * @author Paul
     */
    public static boolean assetEditFormInputValidation(AnchorPane textInputControlAnchorPane, TextField assetNameInput, TextArea assetDescriptionInput, TextField manufacturerInput, TextField categoryInput, TextField siteInput, TextField locationInput) {
        String regexWordAndHyphen = "(?=\\S*[-])([a-zA-Z0-9-]*)|([a-zA-Z0-9]*)"; //Any word containing letters, numbers and hyphens
        String regexLettersAndHyphen = "(?=\\S*[-])([a-zA-Z-]*)|([a-zA-Z]*)"; //Any word containing letters and hyphens
        assetValidForm = true;
        ArrayList<TextInputControl> textInputControls = new ArrayList<>();

        // Initializing the form input validation
        initializeFormInputValidation(textInputControlAnchorPane, textInputControls);

        logger.info("Start - formInputValidation() -> The form is : {}", assetValidForm);

        // Input validation on the different asset input fields
        assetNameValidation(textInputControlAnchorPane, assetNameInput, textInputControls.indexOf(assetNameInput));
        assetDescriptionValidation(textInputControlAnchorPane, assetDescriptionInput, textInputControls.indexOf(assetDescriptionInput));
        manufacturerValidation(textInputControlAnchorPane, manufacturerInput, regexWordAndHyphen, textInputControls.indexOf(manufacturerInput));
        categoryValidation(textInputControlAnchorPane, categoryInput, regexLettersAndHyphen, textInputControls.indexOf(categoryInput));
        siteValidation(textInputControlAnchorPane, siteInput, regexWordAndHyphen, textInputControls.indexOf(siteInput));
        locationValidation(textInputControlAnchorPane, locationInput, regexWordAndHyphen, textInputControls.indexOf(locationInput));

        logger.info("End - formInputValidation() -> The form is : {}", assetValidForm);

        return assetValidForm;
    }

    /**
     * Initializes the form for input validation by setting up the list of TextInputControl components to validate input on as well as removing
     * any previous states of input validation errors.
     *
     * @param textInputControlAnchorPane The Anchor pane of the input form
     * @param textInputControls          The list of textInputControls (text fields, text areas, etc) that are contained in the input form
     * @author Najim, Jeremie
     */
    private static void initializeFormInputValidation(AnchorPane textInputControlAnchorPane, ArrayList<TextInputControl> textInputControls) {
        // Creating an ArrayList of the TextInputControl components included in the form
        for (Node node : textInputControlAnchorPane.getChildren()) {
            if (node instanceof TextInputControl) {
                textInputControls.add((TextInputControl) node);
            }
        }

        // Clearing all previous existing errors if any
        for (int i = 0; i < textInputControls.size(); i++) {
            removeInputError(textInputControlAnchorPane, INPUT_VALIDATION_MESSAGES, textInputControls.get(i), i);
        }
    }

    private static void locationValidation(AnchorPane textInputControlAnchorPane, TextInputControl locationInput, String regexWordAndHyphen, int index) {
        if (locationInput.getText().length() > 20) {
            assetValidForm = false;
            createInputError(textInputControlAnchorPane, FormInputValidation.INPUT_VALIDATION_MESSAGES, locationInput, MAX_20_CHARACTERS_ERROR, index);
        } else if (!locationInput.getText().trim().matches(regexWordAndHyphen)) {
            assetValidForm = false;
            createInputError(textInputControlAnchorPane, FormInputValidation.INPUT_VALIDATION_MESSAGES, locationInput, WORD_HYPHEN_ERROR, index);
        }
    }

    private static void siteValidation(AnchorPane textInputControlAnchorPane, TextInputControl siteInput, String regexWordAndHyphen, int index) {
        if (siteInput.getText().length() > 20 || !siteInput.getText().trim().matches(regexWordAndHyphen)) {
            assetValidForm = false;
            createInputError(textInputControlAnchorPane, FormInputValidation.INPUT_VALIDATION_MESSAGES, siteInput, MAX_20_CHARACTERS_ERROR, index);
        } else if (!siteInput.getText().trim().matches(regexWordAndHyphen)) {
            assetValidForm = false;
            createInputError(textInputControlAnchorPane, FormInputValidation.INPUT_VALIDATION_MESSAGES, siteInput, WORD_HYPHEN_ERROR, index);
        }
    }

    private static void categoryValidation(AnchorPane textInputControlAnchorPane, TextInputControl categoryInput, String regexLettersAndHyphen, int index) {
        if (categoryInput.getText().length() > 20) {
            assetValidForm = false;
            createInputError(textInputControlAnchorPane, FormInputValidation.INPUT_VALIDATION_MESSAGES, categoryInput, MAX_20_CHARACTERS_ERROR, index);
        } else if (!categoryInput.getText().trim().matches(regexLettersAndHyphen)) {
            assetValidForm = false;
            createInputError(textInputControlAnchorPane, FormInputValidation.INPUT_VALIDATION_MESSAGES, categoryInput, LETTER_NUMBER_ERROR, index);
        }
    }

    private static void manufacturerValidation(AnchorPane textInputControlAnchorPane, TextInputControl manufacturerInput, String regexWordAndHyphen, int index) {
        if (manufacturerInput.getText().length() > 20) {
            assetValidForm = false;
            createInputError(textInputControlAnchorPane, FormInputValidation.INPUT_VALIDATION_MESSAGES, manufacturerInput, MAX_20_CHARACTERS_ERROR, index);
        } else if (!manufacturerInput.getText().trim().matches(regexWordAndHyphen)) {
            assetValidForm = false;
            createInputError(textInputControlAnchorPane, FormInputValidation.INPUT_VALIDATION_MESSAGES, manufacturerInput, WORD_HYPHEN_ERROR, index);
        }
    }

    private static void serialNumberValidation(AnchorPane textInputControlAnchorPane, TextInputControl serialNumberInput, String regexWordAndHyphen, int index) {
        if (serialNumberInput.getText().trim().isEmpty()) {
            assetValidForm = false;
            createInputError(textInputControlAnchorPane, FormInputValidation.INPUT_VALIDATION_MESSAGES, serialNumberInput, EMPTY_FIELD_ERROR, index);
        } else if (serialNumberInput.getText().length() > 20) {
            assetValidForm = false;
            createInputError(textInputControlAnchorPane, FormInputValidation.INPUT_VALIDATION_MESSAGES, serialNumberInput, MAX_20_CHARACTERS_ERROR, index);
        } else if (!serialNumberInput.getText().trim().matches(regexWordAndHyphen)) {
            assetValidForm = false;
            createInputError(textInputControlAnchorPane, FormInputValidation.INPUT_VALIDATION_MESSAGES, serialNumberInput, WORD_HYPHEN_ERROR, index);
        }
    }

    private static void assetDescriptionValidation(AnchorPane textInputControlAnchorPane, TextInputControl assetDescriptionInput, int index) {
        if (assetDescriptionInput.getText().length() > 300) {
            assetValidForm = false;
            createInputError(textInputControlAnchorPane, FormInputValidation.INPUT_VALIDATION_MESSAGES, assetDescriptionInput, MAX_300_CHARACTERS_ERROR, index);
        }
    }

    private static void assetNameValidation(AnchorPane textInputControlAnchorPane, TextInputControl assetNameInput, int index) {
        if (assetNameInput.getText().trim().isEmpty()) {
            assetValidForm = false;
            createInputError(textInputControlAnchorPane, FormInputValidation.INPUT_VALIDATION_MESSAGES, assetNameInput, EMPTY_FIELD_ERROR, index);
        } else if (assetNameInput.getText().length() > 50) {
            assetValidForm = false;
            createInputError(textInputControlAnchorPane, FormInputValidation.INPUT_VALIDATION_MESSAGES, assetNameInput, MAX_50_CHARACTERS_ERROR, index);
        }
    }

    /**
     * Compares two thresholds and determines if the previous threshold is larger than the next. If the the previous threshold is larger than
     * the next, the threshold difference is considered invalid and returns false.
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
