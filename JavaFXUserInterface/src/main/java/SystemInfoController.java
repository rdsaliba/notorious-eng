import app.item.Asset;
import app.item.AssetAttribute;
import app.item.Measurement;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Duration;
import local.AssetDAOImpl;
import local.AssetTypeDAOImpl;
import local.AttributeDAOImpl;
import local.ModelDAOImpl;
import rul.assessment.AssessmentController;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class SystemInfoController implements Initializable {
    private static final String S1_COL = "s1";
    private static final String S2_COL = "s2";
    private static final String S3_COL = "s3";
    private static final String S4_COL = "s4";
    private static final String S5_COL = "s5";
    private static final String S6_COL = "s6";
    private static final String S7_COL = "s7";
    private static final String S8_COL = "s8";
    private static final String S9_COL = "s9";
    private static final String S10_COL = "s10";
    private static final String S11_COL = "s11";
    private static final String S12_COL = "s12";
    private static final String S13_COL = "s13";
    private static final String S14_COL = "s14";
    private static final String S15_COL = "s15";
    private static final String S16_COL = "s16";
    private static final String S17_COL = "s17";
    private static final String S18_COL = "s18";
    private static final String S19_COL = "s19";
    private static final String S20_COL = "s20";
    private static final String S21_COL = "s21";
    private static final String S22_COL = "s22";
    private static final String S23_COL = "s23";
    private static final String S24_COL = "s24";
    private static final String S25_COL = "s25";
    private static final String S26_COL = "s26";
    private final String CYCLE_COL = "Cycle";
    private final String OP1_COL = "OP1";
    private final String OP2_COL = "OP2";
    private final String OP3_COL = "OP3";

    @FXML
    private Button systemMenuBtn;
    @FXML
    private Button deleteBtn;
    @FXML
    private Text systemName;
    @FXML
    private Text systemNameOutput;
    @FXML
    private FlowPane sensorFlowPane;
    @FXML
    private Text systemTypeOutput;
    @FXML
    private Text serialNumberOutput;
    @FXML
    private Text manufacturerOutput;
    @FXML
    private Text locationOutput;
    @FXML
    private Text siteOutput;
    @FXML
    private Text modelOutput;
    @FXML
    private Text rulOutput;
    @FXML
    private Text categoryOutput;
    @FXML
    private Text descriptionOutput;
    @FXML
    private Tab rawDataTab;
    @FXML
    private AnchorPane rawDataListPane;

    private Asset system;
    private AssetDAOImpl assetDAOImpl;
    private AssetTypeDAOImpl assetTypeDAOImpl;
    private AttributeDAOImpl attributeDAOImpl;
    private ModelDAOImpl modelDAO;
    private UIUtilities uiUtilities;

    // UI String constants
    private final String CYCLE = "Cycle";
    private final String SENSOR_VALUES = "Sensor Values";
    private final String ALERT_TITLE = "Confirmation Dialog";
    private final String ALERT_HEADER = "Confirmation of system deletion";
    private final String ALERT_CONTENT = "Are you sure you want to delete this system?";

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
        assetDAOImpl = new AssetDAOImpl();
        assetTypeDAOImpl = new AssetTypeDAOImpl();
        modelDAO = new ModelDAOImpl();
        attributeDAOImpl = new AttributeDAOImpl();
        uiUtilities = new UIUtilities();
        attachEvents();
    }

    /**
     * initData receives the Engine data that was selected from System.FXML
     * Then, uses that data to populate the text fields in the scene.
     *
     * @param system
     *
     * @author Jeff
     */
    void initData(Asset system) {
        this.system = system;
        String systemTypeName = assetTypeDAOImpl.getNameFromID(system.getAssetTypeID());
        systemName.setText(systemTypeName + " - " + system.getSerialNo());
        systemNameOutput.setText(system.getName());
        systemTypeOutput.setText(systemTypeName);
        serialNumberOutput.setText(system.getSerialNo());
        manufacturerOutput.setText(system.getManufacturer());
        locationOutput.setText(system.getLocation());
        siteOutput.setText(system.getSite());
        modelOutput.setText(modelDAO.getModelNameFromModelID(modelDAO.getModelsByAssetTypeID(system.getAssetTypeID()).getModelID()));
        categoryOutput.setText(system.getCategory());

        rulOutput.setText(new DecimalFormat("#.##").format(AssessmentController.getLatestEstimate(system.getId())));

        Timeline timeline =  new Timeline(new KeyFrame(Duration.millis(1000), e -> {
            rulOutput.setText(String.valueOf(new DecimalFormat("#.##").format(AssessmentController.getLatestEstimate(system.getId()))));
        }));

        timeline.setCycleCount(Animation.INDEFINITE); // loop forever
        timeline.play();


        descriptionOutput.setText(system.getDescription());
        constructSensorPanes();
    }

    /**
     * Constructs the sensor panes to be able to display data in a nice format.
     *
     * @author Jeff
     */
    public void constructSensorPanes() {
        for (AssetAttribute sensor: system.getAssetInfo().getAssetAttributes()) {
            Pane pane = new Pane();
            pane.getStyleClass().add("sensorPane");
            final CategoryAxis xAxis = new CategoryAxis();
            final CategoryAxis yAxis = new CategoryAxis();
            xAxis.setLabel(CYCLE);
            xAxis.setAnimated(false);
            final LineChart<String, String> sensorChart =
                    new LineChart<>(xAxis, yAxis);
            sensorChart.setTitle(SENSOR_VALUES);
            XYChart.Series series = new XYChart.Series();
            sensorChart.setAnimated(false);

            Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1000), e -> {
                ArrayList<Measurement> measurements = attributeDAOImpl.getLastXMeasurementsByAssetIDAndAttributeID(Integer.toString(system.getId()), Integer.toString(sensor.getId()), 5);
                if(!sensorChart.getXAxis().isValueOnAxis(Integer.toString(measurements.get(0).getTime())))
                    series.getData().add(new XYChart.Data(Integer.toString(measurements.get(0).getTime()), Double.toString(measurements.get(0).getValue())));
                else if (series.getData().size() != measurements.size()) {
                    for(int i = measurements.size() - 1; i >= 0; i--) {
                        series.getData().add(new XYChart.Data(Integer.toString(measurements.get(i).getTime()), Double.toString(measurements.get(i).getValue())));
                    }
                }
                if (series.getData().size() > 5)
                    series.getData().remove(0);
            }));
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.play();

            sensorChart.getData().add(series);
            sensorChart.setPrefWidth(275.0);
            sensorChart.setPrefHeight(163.0);
            sensorChart.setLayoutX(12.0);
            sensorChart.setLayoutY(50.0);
            pane.getChildren().add(sensorChart);
            Text sensorName = new Text(sensor.getName());
            sensorName.setId("sensorType");
            sensorName.setLayoutX(35.0);
            sensorName.setLayoutY(30.0);
            pane.getChildren().add(sensorName);

            sensorFlowPane.getChildren().add(pane);
        }
    }

    /**
     * Attaches events to elements in the scene.
     *
     * @author Jeff
     */
    public void attachEvents() {
        systemMenuBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                uiUtilities.changeScene(mouseEvent, "/Systems");
            }
        });
        deleteBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                deleteDialog(mouseEvent);
            }
        });
        rawDataTab.setOnSelectionChanged(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                rawDataListPane.getChildren().clear();
                generateRawDataTable();
            }
        });
    }

    /**
     * Send the asset ID to the Database class in order for it to be deleted.
     *
     * @author Jeff
     */
    public void deleteAsset() {
        assetDAOImpl.deleteAssetByID(system.getId());
    }

    /**
     * Creates a dialog box that asks user if they want to delete an asset.
     *
     * @param mouseEvent
     */
    void deleteDialog(MouseEvent mouseEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(ALERT_TITLE);
        alert.setHeaderText(ALERT_HEADER);
        alert.setContentText(ALERT_CONTENT);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            deleteAsset();
            uiUtilities.changeScene(mouseEvent, "/Systems");
        }
    }

    public void generateRawDataTable() {
        TableView table = new TableView();

        // When TableRow is clicked, send data to SystemInfo scene.
        table.setRowFactory(tv -> {
            TableRow<Asset> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                uiUtilities.changeScene(event, row, "/SystemInfo", row.getItem());
            });
            return row;
        });

        TableColumn cycle = new TableColumn(CYCLE_COL);

        TableColumn op1 = new TableColumn(OP1_COL);
        TableColumn op2 = new TableColumn(OP2_COL);
        TableColumn op3 = new TableColumn(OP3_COL);

        TableColumn s1 = new TableColumn(S1_COL);
        TableColumn s2 = new TableColumn(S2_COL);
        TableColumn s3 = new TableColumn(S3_COL);
        TableColumn s4 = new TableColumn(S4_COL);
        TableColumn s5 = new TableColumn(S5_COL);
        TableColumn s6 = new TableColumn(S6_COL);
        TableColumn s7 = new TableColumn(S7_COL);
        TableColumn s8 = new TableColumn(S8_COL);
        TableColumn s9 = new TableColumn(S9_COL);
        TableColumn s10 = new TableColumn(S10_COL);
        TableColumn s11 = new TableColumn(S11_COL);
        TableColumn s12 = new TableColumn(S12_COL);
        TableColumn s13 = new TableColumn(S13_COL);
        TableColumn s14 = new TableColumn(S14_COL);
        TableColumn s15 = new TableColumn(S15_COL);
        TableColumn s16 = new TableColumn(S16_COL);
        TableColumn s17 = new TableColumn(S17_COL);
        TableColumn s18 = new TableColumn(S18_COL);
        TableColumn s19 = new TableColumn(S19_COL);
        TableColumn s20 = new TableColumn(S20_COL);
        TableColumn s21 = new TableColumn(S21_COL);
        TableColumn s22 = new TableColumn(S22_COL);
        TableColumn s23 = new TableColumn(S23_COL);
        TableColumn s24 = new TableColumn(S24_COL);
        TableColumn s25 = new TableColumn(S25_COL);
        TableColumn s26 = new TableColumn(S26_COL);


        table.setId("RawDataTable");
        table.getColumns().addAll(cycle, op1, op2, op3, s1,s2,s3,s4,s5,s6,s7,s8,s9,s10,
                s11, s12, s13, s14, s15, s16, s17, s18, s19, s20, s21, s22, s23, s24, s25, s26);
        AnchorPane.setBottomAnchor(table, 0.0);
        AnchorPane.setTopAnchor(table, 5.0);
        AnchorPane.setRightAnchor(table, 0.0);
        AnchorPane.setLeftAnchor(table, 0.0);
        rawDataListPane.getChildren().addAll(table);

    }
}
