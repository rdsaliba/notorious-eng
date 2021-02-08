/*
  This Controller is responsible for handling the information view
  of an asset. It constructs attribute panes, generates the raw data table
  and handles the deletion of an asset.

  @author Jeff, Paul, Roy
  @last_edit 02/7/2020
 */
import app.item.Asset;
import app.item.AssetAttribute;
import app.item.Measurement;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.util.Callback;
import javafx.util.Duration;
import local.AssetDAOImpl;
import local.AssetTypeDAOImpl;
import local.AttributeDAOImpl;
import local.ModelDAOImpl;
import rul.assessment.AssessmentController;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.ResourceBundle;

public class AssetInfoController implements Initializable {

    @FXML
    private Button assetMenuBtn;
    @FXML
    private Button assetTypeMenuBtn;
    @FXML
    private Button deleteBtn;
    @FXML
    private Text assetName;
    @FXML
    private Text assetNameOutput;
    @FXML
    private FlowPane attributeFlowPane;
    @FXML
    private Text assetTypeOutput;
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
    private Text recommendationOutput;
    @FXML
    private Text categoryOutput;
    @FXML
    private Text descriptionOutput;
    @FXML
    private Tab rawDataTab;
    @FXML
    private AnchorPane rawDataListPane;

    private Asset asset;
    private AssetDAOImpl assetDAOImpl;
    private AssetTypeDAOImpl assetTypeDAOImpl;
    private AttributeDAOImpl attributeDAOImpl;
    private ModelDAOImpl modelDAO;
    private UIUtilities uiUtilities;

    private final String CYCLE = "Cycle";
    private final String Attribute_VALUES = "Attribute Values";
    private final String ALERT_HEADER = "Confirmation of asset deletion";
    private final String ALERT_CONTENT = "Are you sure you want to delete this asset?";

    /**
     * Initialize runs before the scene is displayed.
     * It initializes elements and data in the scene.
     *
     * @param url            url to be used
     * @param resourceBundle url to be used
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
     * initData receives the Engine data that was selected from Asset.FXML
     * Then, uses that data to populate the text fields in the scene.
     *
     * @param asset is an asset object that will get initialized
     * @author Jeff
     */
    void initData(Asset asset) {
        this.asset = asset;
        String assetTypeName = assetTypeDAOImpl.getNameFromID(asset.getAssetTypeID());
        assetName.setText(assetTypeName + " - " + asset.getSerialNo());
        assetNameOutput.setText(asset.getName());
        assetTypeOutput.setText(assetTypeName);
        serialNumberOutput.setText(asset.getSerialNo());
        manufacturerOutput.setText(asset.getManufacturer());
        locationOutput.setText(asset.getLocation());
        siteOutput.setText(asset.getSite());
        modelOutput.setText(modelDAO.getModelNameFromModelID(modelDAO.getModelsByAssetTypeID(asset.getAssetTypeID()).getModelID()));
        categoryOutput.setText(asset.getCategory());

        rulOutput.setText(new DecimalFormat("#.##").format(AssessmentController.getLatestEstimate(asset.getId())));
        recommendationOutput.setText(asset.getRecommendation());

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1000), e -> rulOutput.setText(String.valueOf(new DecimalFormat("#.##").format(AssessmentController.getLatestEstimate(asset.getId()))))));

        timeline.setCycleCount(Animation.INDEFINITE); // loop forever
        timeline.play();


        descriptionOutput.setText(asset.getDescription());
        constructAttributePanes();
    }

    /**
     * Constructs the attribute panes to be able to display data in a nice format.
     *
     * @author Jeff
     */
    public void constructAttributePanes() {
        for (AssetAttribute attribute : asset.getAssetInfo().getAssetAttributes()) {
            Pane pane = new Pane();
            pane.getStyleClass().add("attributePane");
            final CategoryAxis xAxis = new CategoryAxis();
            final CategoryAxis yAxis = new CategoryAxis();

            xAxis.setLabel(CYCLE);
            xAxis.setAnimated(false);
            final LineChart<String, String> attributeChart =
                    new LineChart<>(xAxis, yAxis);
            attributeChart.setTitle(Attribute_VALUES);
            XYChart.Series<String, String> series = new XYChart.Series<>();
            attributeChart.setAnimated(false);

            Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1000), e -> {
                ArrayList<Measurement> measurements = attributeDAOImpl.getLastXMeasurementsByAssetIDAndAttributeID(Integer.toString(asset.getId()), Integer.toString(attribute.getId()), 5);
                if (!attributeChart.getXAxis().isValueOnAxis(Integer.toString(measurements.get(0).getTime())))
                    series.getData().add(new XYChart.Data<>(Integer.toString(measurements.get(0).getTime()), Double.toString(measurements.get(0).getValue())));
                else if (series.getData().size() != measurements.size()) {
                    for (int i = measurements.size() - 1; i >= 0; i--) {
                        series.getData().add(new XYChart.Data<>(Integer.toString(measurements.get(i).getTime()), Double.toString(measurements.get(i).getValue())));
                    }
                }
                if (series.getData().size() > 5)
                    series.getData().remove(0);
            }));
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.play();

            attributeChart.getData().add(series);
            attributeChart.setPrefWidth(275.0);
            attributeChart.setPrefHeight(163.0);
            attributeChart.setLayoutX(12.0);
            attributeChart.setLayoutY(50.0);
            pane.getChildren().add(attributeChart);
            Text attributeName = new Text(attribute.getName());
            attributeName.setId("attributeType");
            attributeName.setLayoutX(35.0);
            attributeName.setLayoutY(30.0);
            pane.getChildren().add(attributeName);

            attributeFlowPane.getChildren().add(pane);
        }
    }

    /**
     * Attaches events to elements in the scene.
     *
     * @author Jeff
     */
    public void attachEvents() {
        assetMenuBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, "/Assets"));
        //Attach link to assetTypeMenuBtn to go to AssetTypeList.fxml
        assetTypeMenuBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, "/AssetTypeList"));
        deleteBtn.setOnMouseClicked(this::deleteDialog);

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
        assetDAOImpl.deleteAssetByID(asset.getId());
    }

    /**
     * Creates a dialog box that asks user if they want to delete an asset.
     *
     * @param mouseEvent is an event trigger for this delete dialog
     */
    void deleteDialog(MouseEvent mouseEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(TextConstants.ALERT_TITLE);
        alert.setHeaderText(ALERT_HEADER);
        alert.setContentText(ALERT_CONTENT);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            deleteAsset();
            uiUtilities.changeScene(mouseEvent, "/Assets");
        }
    }

    public void generateRawDataTable() {
        TableView<ObservableList<String>> table = new TableView();
        table.getItems().clear();
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(3000), e -> {
            ObservableList<AssetAttribute> attributes = FXCollections.observableArrayList(assetDAOImpl.createAssetInfo(asset.getId()).getAssetAttributes());

            int columnIndex = 1;
            TableColumn[] tableColumns = new TableColumn[attributes.size() + 1];

            ArrayList<ArrayList<Measurement>> data = new ArrayList();

            tableColumns[0] = new TableColumn(CYCLE);
            data.add(attributes.get(0).getMeasurements());
            setCellValue(0, tableColumns[0]);

            for (AssetAttribute attribute : attributes) {
                data.add(attribute.getMeasurements());
                tableColumns[columnIndex] = new TableColumn(attribute.getName());
                setCellValue(columnIndex, tableColumns[columnIndex]);
                columnIndex++;
            }
            table.getColumns().addAll(tableColumns);

            ObservableList<ObservableList<String>> dataPerColumn = FXCollections.observableArrayList();

            int outcounter = 0;
            for (ArrayList<Measurement> dataList : data) {
                int counter = 0;
                for (Measurement measurement : dataList) {
                    if (outcounter < dataList.size()) {
                        ObservableList<String> list = FXCollections.observableArrayList();
                        dataPerColumn.add(list);
                        dataPerColumn.get(outcounter).add(String.valueOf(measurement.getTime()));
                        outcounter++;
                    } else {
                        dataPerColumn.get(counter).add(String.valueOf(measurement.getValue()));
                        counter++;
                    }
                }
            }
            Collections.reverse(dataPerColumn);
            table.setItems(dataPerColumn);

        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        table.setId("RawDataTable");
        AnchorPane.setBottomAnchor(table, 0.0);
        AnchorPane.setTopAnchor(table, 5.0);
        AnchorPane.setRightAnchor(table, 0.0);
        AnchorPane.setLeftAnchor(table, 0.0);
        rawDataListPane.getChildren().addAll(table);
    }

    public void setCellValue(int index, TableColumn tableColumn) {
        tableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                return new SimpleStringProperty(param.getValue().get(index).toString());
            }
        });
    }
}
