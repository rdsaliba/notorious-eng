package Utilities;


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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.concurrent.atomic.AtomicInteger;

public class CustomDialog extends Stage {


    private static final UIUtilities uiUtilities = new UIUtilities();
    private static TextConstants textConstants;
    private final Button btn;
    private final Pane root;
    private final Rectangle bg;
    private final GridPane grid;
    private final Button cancelBtn;

    public CustomDialog(String header, String content, MouseEvent mouseEvent) {
        root = new Pane();

        initStyle(StageStyle.TRANSPARENT);
        initModality(Modality.APPLICATION_MODAL);

        bg = new Rectangle(500, 200, Color.WHITESMOKE);
        bg.setStroke(Color.BLACK);
        bg.setStrokeWidth(1.5);

        grid = new GridPane();
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
        CustomDialog dialog = new CustomDialog(TextConstants.ALERT_HEADER, TextConstants.ALERT_CONTENT, mouseEvent);
        dialog.getOkButton().setOnAction(e -> {
            assetTypeDAO.deleteAssetTypeByID(systemID);
            uiUtilities.changeScene(mouseEvent, TextConstants.ASSETS_SCENE, dialog.getScene());
            dialog.closeDialog();
        });
        dialog.openDialog();
    }

    public static void systemInfoController(MouseEvent mouseEvent, int systemID) {
        AssetDAOImpl assetDAOImpl = new AssetDAOImpl();
        CustomDialog dialog = new CustomDialog(TextConstants.ALERT_HEADER, TextConstants.ALERT_CONTENT, mouseEvent);
        //Set the functionality of the btn
        dialog.getOkButton().setOnAction(e -> {
            assetDAOImpl.deleteAssetByID(systemID);
            uiUtilities.changeScene(mouseEvent, TextConstants.ASSETS_SCENE, dialog.getScene());
            dialog.closeDialog();
        });
        dialog.openDialog();
    }

    public static void addSystemControllerSaveDialog(MouseEvent mouseEvent) {
        CustomDialog dialog = new CustomDialog(TextConstants.SAVE_DIALOG, TextConstants.SAVE_HEADER, mouseEvent);
        dialog.getRoot().getChildren().remove(dialog.getCancelBtn());
        dialog.getOkButton().setOnAction(e -> {
            uiUtilities.changeScene(mouseEvent, TextConstants.ASSETS_SCENE, dialog.getScene());
            dialog.closeDialog();
        });
        dialog.openDialog();

    }

    public static void addSystemControllerErrorDialog(MouseEvent mouseEvent) {
        CustomDialog dialog = new CustomDialog(TextConstants.ERROR_DIALOG, TextConstants.ERROR_HEADER, mouseEvent);
        dialog.getRoot().getChildren().remove(dialog.getCancelBtn());
        dialog.getOkButton().setOnAction(e -> dialog.closeDialog());
        dialog.openDialog();
    }

    public static void archiveAssetDialogShow(MouseEvent mouseEvent, Asset asset) {
        AssetDAOImpl assetDAO = new AssetDAOImpl();
        AtomicInteger selectedCycle = new AtomicInteger();
        CustomDialog dialog = new CustomDialog(TextConstants.ARCHIVE_DIALOG_HEADER, TextConstants.ARCHIVE_DIALOG_CONTENT, mouseEvent);
        Label timeCycleLabel = new Label("Time Cycle:");
        timeCycleLabel.setLayoutX(50);
        timeCycleLabel.setLayoutY(105);
        ComboBox<Integer> timeCycleComboBox = new ComboBox<>();
        timeCycleComboBox.setLayoutX(150);
        timeCycleComboBox.setLayoutY(100);
        timeCycleComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != 0)
                selectedCycle.set(newVal);
        });
        ObservableList<Integer> timeCycles;
        timeCycles = FXCollections.observableArrayList(asset.getAssetInfo().getAssetAttributes().get(0).getTimeCyclesList());
        timeCycleComboBox.setItems(timeCycles);
        timeCycleComboBox.setValue(timeCycleComboBox.getItems().get(0));
        dialog.getRoot().getChildren().addAll(timeCycleLabel, timeCycleComboBox);
//        timeCycleChoiceBox.setConverter(new StringConverter<>() {
//            @Override
//            public String toString(AssetType assetType) {
//                return assetType.getName();
//            }
//
//            @Override
//            public AssetType fromString(String s) {
//                return assetTypeChoiceBox.getItems().stream().filter(ap ->
//                        ap.getName().equals(s)).findFirst().orElse(null);
//            }
//        });
        dialog.getOkButton().setText("Save");
        dialog.getOkButton().setOnAction(e -> {
            uiUtilities.changeScene(mouseEvent, TextConstants.ASSETS_SCENE, dialog.getScene());
            dialog.closeDialog();
        });
        dialog.openDialog();
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
