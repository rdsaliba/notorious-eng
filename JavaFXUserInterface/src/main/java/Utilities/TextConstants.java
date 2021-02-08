/*
  This class contains all constants which are often reused.

  @author
  @last_edit 02/7/2020
 */
package Utilities;

import java.text.DecimalFormat;

public class TextConstants {
    public static final String OK_THRESHOLD = "Ok";
    public static final String ADVISORY_THRESHOLD = "Advisory";
    public static final String CAUTION_THRESHOLD = "Caution";
    public static final String WARNING_THRESHOLD = "Warning";
    public static final String FAILED_THRESHOLD = "Failed";

    public static final String ASSETS = "/Assets";
    public static final String ASSET_TYPE_LIST = "/AssetTypeList";
    public static final String ALERT_TITLE = "Confirmation Dialog";
    public static final DecimalFormat ThresholdValueFormat = new DecimalFormat("#.0");

    private TextConstants() {
        throw new IllegalStateException("Utility class");
    }
}
