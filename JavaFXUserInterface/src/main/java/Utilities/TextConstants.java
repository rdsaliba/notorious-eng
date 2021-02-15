package Utilities;

import java.text.DecimalFormat;

public class TextConstants {
    public static final String OK_THRESHOLD = "Ok";
    public static final String ADVISORY_THRESHOLD = "Advisory";
    public static final String CAUTION_THRESHOLD = "Caution";
    public static final String WARNING_THRESHOLD = "Warning";
    public static final String FAILED_THRESHOLD = "Failed";

    public static final String ALERT_HEADER = "Confirmation of system deletion";
    public static final String ALERT_CONTENT = "Are you sure you want to delete this system?";

    public static final String SAVE_DIALOG = "Save Dialog";
    public static final String SAVE_HEADER = "Asset has been saved to the database.";

    public static final String ERROR_DIALOG = "Error Dialog";
    public static final String ERROR_HEADER = "Please enter values for all text fields.";



    public static final DecimalFormat ThresholdValueFormat = new DecimalFormat("#.00");

    private TextConstants() {
        throw new IllegalStateException("Utility class");
    }
}
