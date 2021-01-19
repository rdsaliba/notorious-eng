import app.item.Asset;
import app.item.AssetAttribute;
import app.item.Measurement;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
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

    private Asset system;
    private AssetDAOImpl assetDAOImpl;
    private AssetTypeDAOImpl assetTypeDAOImpl;
    private AttributeDAOImpl attributeDAOImpl;
    private ModelDAOImpl modelDAO;
    private UIUtilities uiUtilities;

    /**
     * Initialize runs before the scene is displayed.
     * It initializes elements and data in the scene.
     *
     * @param url url to be used
     * @param resourceBundle url to be used
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
     * @param system is an asset object that will get initialized
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

        Timeline timeline =  new Timeline(new KeyFrame(Duration.millis(1000), e -> rulOutput.setText(String.valueOf(new DecimalFormat("#.##").format(AssessmentController.getLatestEstimate(system.getId()))))));

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
            // UI String constants
            String CYCLE = "Cycle";
            xAxis.setLabel(CYCLE);
            xAxis.setAnimated(false);
            final LineChart<String, String> sensorChart =
                    new LineChart<>(xAxis, yAxis);
            String SENSOR_VALUES = "Sensor Values";
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
        systemMenuBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, "/Systems"));
        deleteBtn.setOnMouseClicked(this::deleteDialog);
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
     * @param mouseEvent is an event trigger for this delete dialog
     */
    void deleteDialog(MouseEvent mouseEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        String ALERT_TITLE = "Confirmation Dialog";
        alert.setTitle(ALERT_TITLE);
        String ALERT_HEADER = "Confirmation of system deletion";
        alert.setHeaderText(ALERT_HEADER);
        String ALERT_CONTENT = "Are you sure you want to delete this system?";
        alert.setContentText(ALERT_CONTENT);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            deleteAsset();
            uiUtilities.changeScene(mouseEvent, "/Systems");
        }
    }
}
