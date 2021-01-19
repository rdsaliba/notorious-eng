import app.ModelController;
import app.item.Asset;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import local.AssetTypeDAOImpl;
import local.ModelDAOImpl;
import rul.assessment.AssessmentController;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

public class SystemsController implements Initializable {
    @FXML
    private Button systemMenuBtn;
    @FXML
    private Button addSystemBtn;
    @FXML
    private FlowPane systemsThumbPane;
    @FXML
    private AnchorPane systemsListPane;
    @FXML
    private Tab thumbnailTab;
    @FXML
    private Tab listTab;

    private final ObservableList<Pane> boxes = FXCollections.observableArrayList();
    private UIUtilities uiUtilities;
    private ObservableList<Asset> systems;
    private AssetTypeDAOImpl assetTypeDAO;
    private ModelDAOImpl modelDAO;

    private final String RECOMMENDATION_COL = "Recommendation";

    public SystemsController() {
        assetTypeDAO = new AssetTypeDAOImpl();
        modelDAO = new ModelDAOImpl();

        try {
            systems = FXCollections.observableArrayList(ModelController.getInstance().getAllLiveAssets());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Initialize runs before the scene is displayed.
     * It initializes elements and data in the scene.
     *
     * @param url url to be used
     * @param resourceBundle resourceBundle to be used
     *
     * @author Jeff
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        uiUtilities = new UIUtilities();

        attachEvents();
        generateThumbnails();

    }

    /**
     * Adds mouse events to all the buttons
     *
     * @author Jeff
     */
    public void attachEvents() {
        thumbnailTab.setOnSelectionChanged(event -> {
            systemsThumbPane.getChildren().clear();
            generateThumbnails();
        });

        listTab.setOnSelectionChanged(event -> {
            systemsListPane.getChildren().clear();
            generateList();
        });

        //Attach link to systemMenuButton to go to Systems.fxml
        systemMenuBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, "/Systems"));

        //Attach link to addSystemButton to go to AddSystem.fxml
        addSystemBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, "/AddSystem"));

    }

    /**
     * Creates elements that are in the scene so the data can be displayed.
     *
     * @author Jeff
     */
    public void generateThumbnails() {
        for (Asset system: systems) {
            Pane pane = new Pane();

            //When clicked on a system, open SystemInfo.FXML for that system.
            pane.setOnMouseClicked(new EventHandler<>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    try {
                        FXMLLoader loader = new FXMLLoader();
                        loader.setLocation(getClass().getResource("/SystemInfo.fxml"));
                        Parent systemsParent = loader.load();
                        Scene systemInfo = new Scene(systemsParent);

                        Stage window = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
                        window.setScene(systemInfo);
                        SystemInfoController controller = loader.getController();
                        controller.initData(system);
                        window.show();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            pane.getStyleClass().add("systemPane");
            Text systemName = new Text(system.getSerialNo());
            Text systemType = new Text(assetTypeDAO.getNameFromID(system.getAssetTypeID()));
            // UI String constants
            String LINEAR_RUL = "Linear RUL: ";
            Text linearLabel = new Text(LINEAR_RUL);
            Text linearRUL = new Text(String.valueOf(new DecimalFormat("#.##").format(AssessmentController.getLatestEstimate(system.getId()))));

            Timeline timeline =
                    new Timeline(new KeyFrame(Duration.millis(1000), e -> linearRUL.setText(String.valueOf(new DecimalFormat("#.##").format(AssessmentController.getLatestEstimate(system.getId()))))));

            timeline.setCycleCount(Animation.INDEFINITE); // loop forever
            timeline.play();



            systemName.setId("systemName");
            systemType.setId("systemType");
            linearLabel.setId("linearLabel");
            linearRUL.setId("linearRUL");

            systemName.setLayoutX(14.0);
            systemName.setLayoutY(28.0);
            systemType.setLayoutX(14.0);
            systemType.setLayoutY(60.0);
            linearLabel.setLayoutX(14.0);
            linearLabel.setLayoutY(121.0);
            linearRUL.setLayoutX(230.0);
            linearRUL.setLayoutY(120.0);

            pane.getChildren().add(systemName);
            pane.getChildren().add(systemType);
            pane.getChildren().add(linearLabel);
            pane.getChildren().add(linearRUL);

            boxes.add(pane);
        }

        systemsThumbPane.getChildren().addAll(boxes);

    }


    /**
     * Creates a table element to list all the assets.
     *
     * @author Jeff
     */
    public void generateList() {
        TableView table = new TableView();

        // When TableRow is clicked, send data to SystemInfo scene.
        table.setRowFactory(tv -> {
            TableRow<Asset> row = new TableRow<>();
            row.setOnMouseClicked(event -> uiUtilities.changeScene(event, row, "/SystemInfo", row.getItem()));
            return row;
        });

        String TYPE_COL = "Type";
        TableColumn<Asset, String>  systemTypeCol = new TableColumn(TYPE_COL);
        systemTypeCol.setCellValueFactory( cellData -> new SimpleStringProperty(
                assetTypeDAO.getNameFromID(cellData.getValue().getAssetTypeID())));


        String SERIAL_NO_COL = "Serial No.";
        TableColumn serialNoCol = new TableColumn(SERIAL_NO_COL);
        serialNoCol.setCellValueFactory(
                new PropertyValueFactory<Asset, String>("serialNo"));

        String MODEL_COL = "Model";
        TableColumn<Asset, String>  modelCol = new TableColumn(MODEL_COL);
        modelCol.setCellValueFactory( cellData -> new SimpleStringProperty(
                modelDAO.getModelNameFromModelID(modelDAO.getModelsByAssetTypeID(cellData.getValue().getAssetTypeID()).getModelID())));

        String RUL_COL = "RUL";
        TableColumn<Asset, Double> modelRULCol = new TableColumn<>(RUL_COL);
        modelRULCol.setCellValueFactory(cellData -> new SimpleDoubleProperty(
                Double.parseDouble(new DecimalFormat("#.##").format(AssessmentController.getLatestEstimate(cellData.getValue().getId())))).asObject());

        String LOCATION_COL = "Location";
        TableColumn locationCol = new TableColumn(LOCATION_COL);
        locationCol.setCellValueFactory(
                new PropertyValueFactory<Asset, String>("location"));

        String MANUFACTURER_COL = "Manufacturer";
        TableColumn manufacturerCol = new TableColumn(MANUFACTURER_COL);
        manufacturerCol.setCellValueFactory(
                new PropertyValueFactory<Asset, String>("manufacturer"));

        String CATEGORY_COL = "Category";
        TableColumn categoryCol = new TableColumn(CATEGORY_COL);
        categoryCol.setCellValueFactory(
                new PropertyValueFactory<Asset, String>("category"));

        String SITE_COL = "Site";
        TableColumn siteCol = new TableColumn(SITE_COL);
        siteCol.setCellValueFactory(
                new PropertyValueFactory<Asset, String>("site"));

        String DESCRIPTION_COL = "Description";
        TableColumn descriptionCol = new TableColumn(DESCRIPTION_COL);
        descriptionCol.setCellValueFactory(
                new PropertyValueFactory<Asset, String>("description"));

        table.setItems(systems);
        table.setId("listTable");
        table.getColumns().addAll(systemTypeCol, serialNoCol,modelCol, modelRULCol, locationCol,siteCol,categoryCol,manufacturerCol,descriptionCol);
        AnchorPane.setBottomAnchor(table, 0.0);
        AnchorPane.setTopAnchor(table, 5.0);
        AnchorPane.setRightAnchor(table, 0.0);
        AnchorPane.setLeftAnchor(table, 0.0);
        systemsListPane.getChildren().addAll(table);

    }
}
