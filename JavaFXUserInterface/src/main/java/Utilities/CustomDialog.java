package Utilities;

import Controllers.AddSystemController;
import Controllers.SystemInfoController;
import Utilities.UIUtilities;
import Controllers.SystemTypeInfoController;
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
    private static final String SYSTEM_TYPE_LIST = "/SystemTypeList";
    private static final String SYSTEMS = "/Systems";

    private static UIUtilities uiUtilities = new UIUtilities();
    Button btn;
    Pane root;
    Rectangle bg;
    Button cancelBtn;

    CustomDialog(String header,String content,MouseEvent mouseEvent){
        root = new Pane();

        initStyle(StageStyle.TRANSPARENT);
        initModality(Modality.APPLICATION_MODAL);

        bg =  new Rectangle(500,200, Color.WHITESMOKE);
        bg.setStroke( Color.BLACK);
        bg.setStrokeWidth(1.5);

        Text headerText = new Text(header);
        headerText.setFont(Font.font(20));

        Text contentText = new Text(content);
        contentText.setFont(Font.font(16));

        VBox box = new VBox(10, headerText, contentText);
        box.setPadding(new Insets(15));

        btn = new Button("OK");
        btn.setTranslateX(bg.getWidth()-50);
        btn.setTranslateY(bg.getHeight()-50);

        cancelBtn = new Button("CANCEL");
        cancelBtn.setTranslateX(bg.getWidth()-130);
        cancelBtn.setTranslateY(bg.getHeight()-50);
        cancelBtn.setOnAction(e->closeDialog());

        root.getChildren().addAll(bg,box,btn,cancelBtn);
        setScene(new Scene(root,null));
    }
    Pane getRoot(){ return root;}
    Button getCancelBtn(){return cancelBtn;}
    Button getOkButton(){
        return btn;
    }
    void openDialog(){
        show();
    }
    void closeDialog(){
        close();
    }

    public static void systemTypeInfoControllerDialog(MouseEvent mouseEvent, String systemID){
        AssetTypeDAOImpl assetTypeDAO = new AssetTypeDAOImpl();
        String ALERT_HEADER = "Confirmation of system type deletion";
        String ALERT_CONTENT = "Are you sure you want to delete this system type? \n " +
                "this will delete all the assets of this type";

        CustomDialog dialog = new CustomDialog(ALERT_HEADER,ALERT_CONTENT,mouseEvent);
        dialog.getOkButton().setOnAction(e->{
            assetTypeDAO.deleteAssetTypeByID(systemID);
            uiUtilities.changeScene(mouseEvent, SYSTEM_TYPE_LIST);
            dialog.closeDialog();
        });
        dialog.openDialog();
    }

    public static void systemInfoController(MouseEvent mouseEvent, int systemID){
        AssetDAOImpl assetDAOImpl = new AssetDAOImpl();
        String ALERT_HEADER = "Confirmation of system deletion";
        String ALERT_CONTENT = "Are you sure you want to delete this system?";
        CustomDialog dialog = new CustomDialog(ALERT_HEADER,ALERT_CONTENT,mouseEvent);
        //Set the functionality of the btn
        dialog.getOkButton().setOnAction(e->{
            assetDAOImpl.deleteAssetByID(systemID);
            uiUtilities.changeScene(mouseEvent, "/Systems");
            dialog.closeDialog();
        });
        dialog.openDialog();
    }

    public static void addSystemControllerSaveDialog(MouseEvent mouseEvent){
        String SAVE_DIALOG = "Save Dialog";
        String SAVE_HEADER = "Asset has been saved to the database.";
        CustomDialog dialog = new CustomDialog(SAVE_DIALOG,SAVE_HEADER,mouseEvent);
        dialog.getRoot().getChildren().remove(dialog.getCancelBtn());
        dialog.getOkButton().setOnAction(e->{
            uiUtilities.changeScene(mouseEvent, SYSTEMS);
            dialog.closeDialog();
        });
        dialog.openDialog();

    }

    public static void addSystemControllerErrorDialog(MouseEvent mouseEvent){
        String ERROR_DIALOG = "Error Dialog";
        String ERROR_HEADER = "Please enter values for all text fields.";
        CustomDialog dialog = new CustomDialog(ERROR_DIALOG,ERROR_HEADER,mouseEvent);
        dialog.getRoot().getChildren().remove(dialog.getCancelBtn());
        dialog.getOkButton().setOnAction(e-> dialog.closeDialog());
        dialog.openDialog();

    }
}
