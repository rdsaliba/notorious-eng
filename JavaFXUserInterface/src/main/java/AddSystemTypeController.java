import app.item.AssetType;
import app.item.AssetTypeParameter;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import local.AssetTypeDAOImpl;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class AddSystemTypeController implements Initializable {

    @FXML
    private Button systemMenuBtn;
    @FXML
    private Button cancelBtn;
    @FXML
    private Button addThresholdBtn;
    @FXML
    private AnchorPane systemTypeInformation;
    @FXML
    private Button saveBtn;
    @FXML
    private TextField systemTypeName;

    private int thresholdCount = 1;
    private double spacing = 40.0;
    private UIUtilities uiUtilities;
    private ArrayList<TextField> thresholdValues;
    private AssetTypeDAOImpl db;
    private ArrayList<AssetTypeParameter> assetTypeParameters;

    /**
     * Initialize runs before the scene is displayed.
     * It initializes elements and data in the scene.
     *
     * @param url
     * @param resourceBundle
     *
     * @author Jeff
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        thresholdValues = new ArrayList<>();
        uiUtilities = new UIUtilities();
        db = new AssetTypeDAOImpl();
        assetTypeParameters = new ArrayList<>();
        assetTypeParameters.add(new AssetTypeParameter());
        attachEvents();
    }

    /**
     * Attaches events to elements in the scene.
     *
     * @author Jeff
     */
    public void attachEvents() {
        // Change scenes to Systems.fxml
        systemMenuBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                uiUtilities.changeScene(mouseEvent, "/Systems");
            }
        });
        // Change scenes to Systems.fxml
        cancelBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                uiUtilities.changeScene(mouseEvent, "/Systems");
            }
        });
        addThresholdBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                addThresholdForm();
            }
        });
        saveBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                saveAssetType(assembleSystemType());
            }
        });
    }

    public void addThresholdForm() {
        Text thresholdNameLabel = new Text("Threshold Name");
        thresholdNameLabel.setFill(Paint.valueOf("#0c072e"));
        thresholdNameLabel.setLayoutX(79.0);
        thresholdNameLabel.setLayoutY(193.0 + (spacing * thresholdCount));
        thresholdNameLabel.setStrokeType(StrokeType.OUTSIDE);
        thresholdNameLabel.setFont(Font.font("Segoe UI Bold", 14));

        TextField thresholdName = new TextField();
        thresholdName.setLayoutX(200.0);
        thresholdName.setLayoutY(175.0 + (spacing * thresholdCount));
        thresholdName.setPrefHeight(25.0);
        thresholdName.setPrefWidth(190);

        Text thresholdValueLabel = new Text("Value");
        thresholdValueLabel.setFill(Paint.valueOf("#0c072e"));
        thresholdValueLabel.setLayoutX(405.0);
        thresholdValueLabel.setLayoutY(193.0 + (spacing * thresholdCount));
        thresholdValueLabel.setStrokeType(StrokeType.OUTSIDE);
        thresholdValueLabel.setFont(Font.font("Segoe UI Bold", 14));

        TextField thresholdValue = new TextField();
        thresholdValue.setLayoutX(453.0);
        thresholdValue.setLayoutY(175.0 + (spacing * thresholdCount));
        thresholdValue.setPrefHeight(25.0);
        thresholdValue.setPrefWidth(97.0);

        thresholdCount++;
        thresholdName.setId("thresholdName" + thresholdCount);
        thresholdValue.setId(("thresholdValue" + thresholdCount));

        systemTypeInformation.getChildren().addAll(thresholdNameLabel, thresholdName, thresholdValueLabel, thresholdValue);
        assetTypeParameters.add(new AssetTypeParameter());
    }

    public AssetType assembleSystemType() {
        AssetType assetType = new AssetType(systemTypeName.getText());
        for (Node node : systemTypeInformation.getChildren()) {
            if (node instanceof TextField) {
                if(node.getId().contains("thresholdName")) {
                    assetTypeParameters.get(thresholdCount - 1).setName(((TextField) node).getText());
                }
                else if(node.getId().contains("thresholdValue")) {
                    assetTypeParameters.get(thresholdCount - 1).setValue(Double.parseDouble(((TextField) node).getText()));
                    thresholdCount--;
                }
            }
        }
        assetType.setThresholdList(assetTypeParameters);
        return assetType;
    }

    public void saveAssetType(AssetType assetType) {
        db.insertAssetType(assetType);
    }
}
