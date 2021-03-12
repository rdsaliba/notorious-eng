package utilities;

public class Constants {

    public static final String OK_THRESHOLD = "Ok";
    public static final String ADVISORY_THRESHOLD = "Advisory";
    public static final String CAUTION_THRESHOLD = "Caution";
    public static final String WARNING_THRESHOLD = "Warning";
    public static final String FAILED_THRESHOLD = "Failed";

    public static final int STATUS_LIVE = 1;
    public static final int STATUS_EVALUATION = 2;

    private Constants() {
        throw new IllegalStateException("Utility class");
    }
}

