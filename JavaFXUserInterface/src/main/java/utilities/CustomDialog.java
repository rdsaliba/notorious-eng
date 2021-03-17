package utilities;


import app.item.Asset;
import external.AssetDAOImpl;
import external.AssetTypeDAOImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class CustomDialog extends Stage {

    private static final String SAVE_LABEL = "Save";
    private static final String TIME_CYCLE_LABEL = "Time cycle:";
    private static final UIUtilities uiUtilities = new UIUtilities();
    private final Button btn;
    private final Pane root;
    private final Button cancelBtn;

    public CustomDialog(String header, String content) {
        root = new Pane();

        initStyle(StageStyle.TRANSPARENT);
        initModality(Modality.APPLICATION_MODAL);

        Rectangle bg = new Rectangle(500, 200, Color.WHITESMOKE);
        bg.setStroke(Color.BLACK);
        bg.setStrokeWidth(1.5);

        Text headerText = new Text(header);
        headerText.setFont(Font.font(20));

        Text contentText = new Text(content);
        contentText.setFont(Font.font(16));

        VBox box = new VBox(10, headerText, contentText);
        box.setPadding(new Insets(15));

        btn = new Button("Ok");
        btn.setTranslateX(bg.getWidth() - 50);
        btn.setTranslateY(bg.getHeight() - 50);

        cancelBtn = new Button("Cancel");
        cancelBtn.setTranslateX(bg.getWidth() - 120);
        cancelBtn.setTranslateY(bg.getHeight() - 50);
        cancelBtn.setOnAction(e -> closeDialog());

        root.getChildren().addAll(bg, box, btn, cancelBtn);
        setScene(new Scene(root, null));
    }

    public static void systemTypeInfoControllerDialog(MouseEvent mouseEvent, String systemID) {
        AssetTypeDAOImpl assetTypeDAO = new AssetTypeDAOImpl();
        CustomDialog dialog = new CustomDialog(TextConstants.ALERT_HEADER, TextConstants.ALERT_CONTENT);
        dialog.getOkButton().setOnAction(e -> {
            assetTypeDAO.deleteAssetTypeByID(systemID);
            uiUtilities.changeScene(mouseEvent, TextConstants.ASSETS_SCENE, dialog.getScene());
            dialog.closeDialog();
        });
        dialog.openDialog();
    }
    public static void nullModelAlert(MouseEvent mouseEvent){
        CustomDialog dialog = new CustomDialog(TextConstants.NO_MODEL_ALERT_HEADER, TextConstants.NO_MODEL_ALERT_CONTENT);
        dialog.getOkButton().setOnAction(e -> {
            uiUtilities.changeScene(mouseEvent, TextConstants.ASSET_TYPE_INFO_SCENE, dialog.getScene());
            dialog.closeDialog();
        });
        dialog.getRoot().getChildren().remove(3);
        dialog.openDialog();
    }

    public static void systemInfoController(MouseEvent mouseEvent, int systemID) {
        AssetDAOImpl assetDAOImpl = new AssetDAOImpl();
        CustomDialog dialog = new CustomDialog(TextConstants.ALERT_HEADER, TextConstants.ALERT_CONTENT);
        //Set the functionality of the btn
        dialog.getOkButton().setOnAction(e -> {
            assetDAOImpl.deleteAssetByID(systemID);
            uiUtilities.changeScene(mouseEvent, TextConstants.ASSETS_SCENE, dialog.getScene());
            dialog.closeDialog();
        });
        dialog.openDialog();
    }

    public static void addSystemControllerSaveDialog(MouseEvent mouseEvent) {
        CustomDialog dialog = new CustomDialog(TextConstants.SAVE_DIALOG, TextConstants.SAVE_HEADER);
        dialog.getRoot().getChildren().remove(dialog.getCancelBtn());
        dialog.getOkButton().setOnAction(e -> {
            uiUtilities.changeScene(mouseEvent, TextConstants.ASSETS_SCENE, dialog.getScene());
            dialog.closeDialog();
        });
        dialog.openDialog();

    }

    /**
     * This static functions creates and shows a custom dialog meant for archiving assets. It gets shown whenever
     * the archive button is pressed. The user will be able to determine the last RUL time cycle for the asset
     * specified. Saving the selection will delete all time cycles after the selected one and will archive the
     * asset.
     *
     * @param mouseEvent     click mouse event
     * @param asset          is the asset to be archived
     * @param parentSceneBtn is a button from the parent scene of the custom dialog window
     * @author Jeremie
     */
    public static void archiveAssetDialogShow(MouseEvent mouseEvent, Asset asset, Button parentSceneBtn) {
        CustomDialog dialog = new CustomDialog(TextConstants.ARCHIVE_DIALOG_HEADER, TextConstants.ARCHIVE_DIALOG_CONTENT);
        final Integer[] selectedCycle = new Integer[1];
        AssetDAOImpl assetDAO = new AssetDAOImpl();

        generateTimeCycleSelection(asset, dialog, selectedCycle);

        dialog.getOkButton().setText(SAVE_LABEL);
        dialog.getOkButton().setOnAction(e -> {
            assetDAO.deleteAssetMeasurementsAfterTimeCycle(asset.getId(), selectedCycle[0]);
            assetDAO.setAssetToBeArchived(asset.getId());
            uiUtilities.changeScene(mouseEvent, TextConstants.ASSETS_SCENE, parentSceneBtn.getScene());
            dialog.closeDialog();
        });
        dialog.openDialog();
    }

    /**
     * This static functions generates the ComboBox and Label needed for the time cycle selection. It will use
     * the given asset to return the list of time cycles with attribute values available for that asset. This
     * functions allows to keep track of the selected time cycle.
     *
     * @param asset         is the asset to be archived
     * @param dialog        is the parent custom dialog holding the label and ComboBox
     * @param selectedCycle is the selected option in the ComboBox list
     * @author Jeremie
     */
    private static void generateTimeCycleSelection(Asset asset, CustomDialog dialog, Integer[] selectedCycle) {
        // Time Cycle Label Creation and Configuration
        Label timeCycleLabel = new Label(TIME_CYCLE_LABEL);
        timeCycleLabel.setLayoutX(50);
        timeCycleLabel.setLayoutY(105);

        // Time Cycle ComboBox Creation and Configuration
        ComboBox<Integer> timeCycleComboBox = new ComboBox<>();
        timeCycleComboBox.setLayoutX(150);
        timeCycleComboBox.setLayoutY(100);
        timeCycleComboBox.valueProperty().addListener((observableValue, integer, selectedInt) -> selectedCycle[0] = selectedInt);
        ObservableList<Integer> timeCycles;
        timeCycles = FXCollections.observableArrayList(asset.getAssetInfo().getAssetAttributes().get(0).getTimeCyclesList());
        timeCycleComboBox.setItems(timeCycles);
        timeCycleComboBox.setValue(timeCycleComboBox.getItems().get(0));

        // Adding the ComboBox and Label to the dialog stage
        dialog.getRoot().getChildren().addAll(timeCycleLabel, timeCycleComboBox);
    }

    public Pane getRoot() {
        return root;
    }

    public Button getCancelBtn() {
        return cancelBtn;
    }

    public Button getOkButton() {
        return btn;
    }

    public void openDialog() {
        show();
    }

    public void closeDialog() {
        close();
    }
}
