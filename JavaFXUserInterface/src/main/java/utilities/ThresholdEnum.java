package utilities;

public enum ThresholdEnum {
    OK_THRESHOLD(TextConstants.OK_THRESHOLD),
    ADVISORY_THRESHOLD(TextConstants.ADVISORY_THRESHOLD),
    CAUTION_THRESHOLD(TextConstants.CAUTION_THRESHOLD),
    WARNING_THRESHOLD(TextConstants.WARNING_THRESHOLD),
    FAILED_THRESHOLD(TextConstants.FAILED_THRESHOLD);

    private final String value;

    ThresholdEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
