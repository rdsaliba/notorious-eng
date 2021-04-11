/*
  This class contains all constants which are often reused.

  @author
  @last_edit 02/7/2020
 */
package utilities;

import java.text.DecimalFormat;

public class TextConstants {

    // Scene Text Constants
    public static final String ASSETS_SCENE = "/Assets";
    public static final String ADD_ASSETS_SCENE = "/AddAsset";
    public static final String ASSET_TYPE_LIST_SCENE = "/AssetTypeList";
    public static final String ASSET_TYPE_INFO_SCENE = "/AssetTypeInfo";
    public static final String FXML = ".fxml";

    // Asset Type Thresholds Text Constants
    public static final String OK_THRESHOLD = "Ok";
    public static final String ADVISORY_THRESHOLD = "Advisory";
    public static final String CAUTION_THRESHOLD = "Caution";
    public static final String WARNING_THRESHOLD = "Warning";
    public static final String FAILED_THRESHOLD = "Failed";

    // Asset Type Thumbnail Text Constants
    public static final String NB_OF_ASSETS = "nb of Assets";

    // Custom Dialog Text Constants
    public static final String DELETE_ASSET_DIALOG_HEADER = "Confirmation of asset deletion";
    public static final String DELETE_ASSET_DIALOG_CONTENT = "Are you sure you want to delete this asset?";

    public static final String DELETE_ASSET_TYPE_DIALOG_HEADER = "Confirmation of asset type deletion";
    public static final String DELETE_ASSET_TYPE_DIALOG_CONTENT = "Are you sure you want to delete this asset type?\n" +
            "(This includes deleting all assets belonging to that asset type)";

    public static final String NEW_ASSET_SAVED_DIALOG_HEADER = "Save Dialog";
    public static final String NEW_ASSET_SAVED_DIALOG_CONTENT = "Asset has been saved to the database.";

    public static final String ARCHIVE_DIALOG_HEADER = "Archive asset confirmation";
    public static final String ARCHIVE_DIALOG_CONTENT = "Please select the last time cycle of the asset's useful life";

    public static final String NO_MODEL_ALERT_DIALOG_HEADER = "Error No Model Available";
    public static final String NO_MODEL_ALERT_DIALOG_CONTENT = "You should run server before you evaluate";

    public static final DecimalFormat RULValueFormat = new DecimalFormat("#.##");
    public static final DecimalFormat RMSEValueFormat = new DecimalFormat("#.##");
    public static final DecimalFormat ThresholdValueFormat = new DecimalFormat("#.00");

    public static final String floatRegex = "[-]?([0-9]*[.])?[0-9]*";
    public static final String intRegex = "[-]?\\d+";

    //Input Validation Constants
    public static final String EMPTY_FIELD_ERROR = "Please enter a value";
    public static final String MAX_20_CHARACTERS_ERROR = "Number of characters has to not exceed 20";
    public static final String MAX_50_CHARACTERS_ERROR = "Number of characters has to not exceed 50";
    public static final String MAX_300_CHARACTERS_ERROR = "Number of characters has to not exceed 300";
    public static final String WORD_HYPHEN_ERROR = "Value can only contain letters, numbers or hyphens";
    public static final String LETTER_NUMBER_ERROR = "Value can only contain letters or numbers";
    public static final String ADVISORY_CAUTION = "The Advisory Threshold needs to be larger \nthan the Caution Threshold";
    public static final String ADVISORY_WARNING = "The Advisory Threshold needs to be larger \nthan the Warning Threshold";
    public static final String CAUTION_WARNING = "The Caution Threshold needs to be larger \nthan the Warning Threshold";
    public static final String ADVISORY_FAILED = "The Advisory Threshold needs to be larger \nthan the Failed Threshold";
    public static final String CAUTION_FAILED = "The Caution Threshold needs to be larger \nthan the Failed Threshold";
    public static final String WARNING_FAILED = "The Warning Threshold needs to be larger \nthan the Failed Threshold";

    private TextConstants() {
        throw new IllegalStateException("Utility class");
    }
}
