package Utilities;


import external.AssetDAOImpl;
import external.AssetTypeDAO;
import external.AssetTypeDAOImpl;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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


    private static UIUtilities uiUtilities = new UIUtilities();
    private static TextConstants textConstants;
    private Button btn;
    private Pane root;
    private Rectangle bg;
    private Button cancelBtn;

    public CustomDialog(String header, String content, MouseEvent mouseEvent) {
        root = new Pane();

        initStyle(StageStyle.TRANSPARENT);
        initModality(Modality.APPLICATION_MODAL);

        bg = new Rectangle(500, 200, Color.WHITESMOKE);
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

    public static void systemTypeInfoControllerDialog(MouseEvent mouseEvent, String systemID) {
        AssetTypeDAOImpl assetTypeDAO = new AssetTypeDAOImpl();
        CustomDialog dialog = new CustomDialog(textConstants.ALERT_HEADER, textConstants.ALERT_CONTENT, mouseEvent);
        dialog.getOkButton().setOnAction(e -> {
            assetTypeDAO.deleteAssetTypeByID(systemID);
            uiUtilities.changeScene(mouseEvent, TextConstants.ASSETS_SCENE, dialog.getScene());
            dialog.closeDialog();
        });
        dialog.openDialog();
    }

    public static void systemInfoController(MouseEvent mouseEvent, int systemID) {
        AssetDAOImpl assetDAOImpl = new AssetDAOImpl();
        CustomDialog dialog = new CustomDialog(textConstants.ALERT_HEADER, textConstants.ALERT_CONTENT, mouseEvent);
        //Set the functionality of the btn
        dialog.getOkButton().setOnAction(e -> {
            assetDAOImpl.deleteAssetByID(systemID);
            uiUtilities.changeScene(mouseEvent, TextConstants.ASSETS_SCENE, dialog.getScene());
            dialog.closeDialog();
        });
        dialog.openDialog();
    }

    public static void addSystemControllerSaveDialog(MouseEvent mouseEvent) {
        CustomDialog dialog = new CustomDialog(textConstants.SAVE_DIALOG, textConstants.SAVE_HEADER, mouseEvent);
        dialog.getRoot().getChildren().remove(dialog.getCancelBtn());
        dialog.getOkButton().setOnAction(e -> {
            uiUtilities.changeScene(mouseEvent, TextConstants.ASSETS_SCENE, dialog.getScene());
            dialog.closeDialog();
        });
        dialog.openDialog();

    }

    public static void addSystemControllerErrorDialog(MouseEvent mouseEvent) {
        CustomDialog dialog = new CustomDialog(textConstants.ERROR_DIALOG, textConstants.ERROR_HEADER, mouseEvent);
        dialog.getRoot().getChildren().remove(dialog.getCancelBtn());
        dialog.getOkButton().setOnAction(e -> dialog.closeDialog());
        dialog.openDialog();

    }
}
