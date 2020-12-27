package com.cbms.ui.controller;

import com.cbms.app.ModelController;
import com.cbms.app.item.Asset;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.ResourceBundle;

public class SystemsController implements Initializable {

    @FXML
    private Button systemMenuButton;
    @FXML
    private Button addSystemButton;
    @FXML
    private FlowPane systemsThumbPane;
    @FXML
    private AnchorPane systemsListPane;
    @FXML
    private Tab thumbnailTab;
    @FXML
    private Tab listTab;
    @FXML
    private ChoiceBox<String> sortSystem;
    @FXML
    private TabPane tabs;

    private final ObservableList<Pane> boxes = FXCollections.observableArrayList();
    private UIUtilities uiUtilities;
    private ObservableList<Asset> systems;

    public SystemsController() {

    }

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
        uiUtilities = new UIUtilities();

        try {
            systems = FXCollections.observableArrayList(ModelController.getInstance().estimate());
        } catch (Exception e) {
            e.printStackTrace();
        }

        linkButtons();
        generateThumbnails("Default");
    }

    /**
     * Adds mouse events to all the buttons
     *
     * @author Jeff
     */
    public void linkButtons() {

        tabs.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> observable, Tab oldTab, Tab newTab) {
                if (newTab == thumbnailTab) {
                    System.out.println("When Clicking on thumbnail tab, value of sort selected: " + sortSystem.getValue());
                    systemsThumbPane.getChildren().clear();
                    generateThumbnails(sortSystem.getValue());
                }
                if (newTab == listTab) {
                    System.out.println("When Clicking on list tab, value of sort selected: " + sortSystem.getValue());
                    systemsListPane.getChildren().clear();
                    generateList(sortSystem.getValue());
                }
            }
        });

//        thumbnailTab.setOnSelectionChanged(new EventHandler<Event>() {
//            @Override
//            public void handle(Event event) {
//                currentTabIsThumbnails = true;
//                System.out.println("When Clicking on thumbnail tab, value of sort selected: " + sortSystem.getValue());
//                systemsThumbPane.getChildren().clear();
//                generateThumbnails(sortSystem.getValue());
//            }
//        });
//
//        listTab.setOnSelectionChanged(new EventHandler<Event>() {
//            @Override
//            public void handle(Event event) {
//                currentTabIsThumbnails = false;
//                systemsListPane.getChildren().clear();
//                generateList(sortSystem.getValue());
//            }
//        });

        //Attach link to systemMenuButton to go to Systems.fxml
        systemMenuButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                uiUtilities.changeScene(mouseEvent, "/Systems");
            }
        });

        //Attach link to addSystemButton to go to AddSystem.fxml
        addSystemButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                uiUtilities.changeScene(mouseEvent, "/AddSystem");
            }
        });

        //Adding items to the choiceBox (drop down list)
        sortSystem.getItems().add("Default");
        sortSystem.getItems().add("Ascending RUL");
        sortSystem.getItems().add("Descending RUL");
        //Default Value
        sortSystem.setValue("Default");
        sortSystem.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                switch (newValue) {
                    case "Ascending RUL":
                        if (thumbnailTab.isSelected()) {
                            systemsThumbPane.getChildren().clear();
                            generateThumbnails("Ascending RUL");
                        } else {
                            systemsListPane.getChildren().clear();
                            generateList("Ascending RUL");
                        }
                        System.out.println("After generate ascending");
                        break;
                    case "Descending RUL":
                        if (thumbnailTab.isSelected()) {
                            systemsThumbPane.getChildren().clear();
                            generateThumbnails("Descending RUL");
                        } else {
                            systemsListPane.getChildren().clear();
                            generateList("Descending RUL");
                        }
                        System.out.println("After generate Descending");
                        break;
                    default:
                        if (thumbnailTab.isSelected()) {
                            systemsThumbPane.getChildren().clear();
                            generateThumbnails("Default");
                        } else {
                            systemsListPane.getChildren().clear();
                            generateList("Default");
                        }
                        System.out.println("After generate Default");
                        break;
                }
            }
        });
    }

    /**
     * Creates elements that are in the scene so the data can be displayed.
     *
     * @author Jeff
     */
    public void generateThumbnails(String sortSelected) {
        ObservableList<Asset> sortedSystems = sortSystems(sortSelected);
        for (Asset system : sortedSystems) {
            System.out.println("Asset ID: " + system.getSerialNo() + " // Asset RUL: " + system.getAssetInfo().getRULMeasurement());
        }
        for (Asset system : sortedSystems) {
            Pane pane = new Pane();

            //When clicked on a system, open SystemInfo.FXML for that system.
            pane.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    Stage primaryStage = (Stage) pane.getScene().getWindow();
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
            Text systemType = new Text(system.getAssetTypeID());
            Text linearLabel = new Text("Linear Regression RUL:");
            Text linearRUL = new Text(String.valueOf(new DecimalFormat("#.##").format(system.getAssetInfo().getRULMeasurement())));
            //Text lstmLabel = new Text("LSTM RUL:");
            //Text lstmRUL = new Text(String.valueOf(system.getLstmRUL()));

            systemName.setId("systemName");
            systemType.setId("systemType");
            linearLabel.setId("linearLabel");
            linearRUL.setId("linearRUL");
            //lstmLabel.setId(("lstmLabel"));
            //lstmRUL.setId("lstmRUL");

            systemName.setLayoutX(14.0);
            systemName.setLayoutY(28.0);
            systemType.setLayoutX(14.0);
            systemType.setLayoutY(60.0);
            linearLabel.setLayoutX(14.0);
            linearLabel.setLayoutY(121.0);
            linearRUL.setLayoutX(230.0);
            linearRUL.setLayoutY(120.0);
            //lstmLabel.setLayoutX(14.0);
            //lstmLabel.setLayoutY(190.0);
            //lstmRUL.setLayoutX(250.0);
            //lstmRUL.setLayoutY(190.0);

            pane.getChildren().add(systemName);
            pane.getChildren().add(systemType);
            pane.getChildren().add(linearLabel);
            pane.getChildren().add(linearRUL);
            //pane.getChildren().add(lstmLabel);
            //pane.getChildren().add(lstmRUL);

            boxes.add(pane);
        }

        systemsThumbPane.getChildren().addAll(boxes);
    }


    /**
     * Creates a table element to list all the assets.
     *
     * @author Jeff
     */
    public void generateList(String sortSelected) {
        ObservableList<Asset> sortedSystems = sortSystems(sortSelected);
        for (Asset system : sortedSystems) {
            System.out.println("Asset ID: " + system.getSerialNo() + " // Asset RUL: " + system.getAssetInfo().getRULMeasurement());
        }
        TableView table = new TableView();

        // When TableRow is clicked, send data to SystemInfo scene.
        table.setRowFactory(tv -> {
            TableRow<Asset> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                uiUtilities.changeScene(event, row, "/SystemInfo", row.getItem());
            });
            return row;
        });

        TableColumn systemTypeCol = new TableColumn("Type");
        systemTypeCol.setCellValueFactory(
                new PropertyValueFactory<Asset, String>("assetTypeID"));

        TableColumn serialNoCol = new TableColumn("Serial No.");
        serialNoCol.setCellValueFactory(
                new PropertyValueFactory<Asset, String>("serialNo"));

        TableColumn<Asset, Double> linearRULCol = new TableColumn<>("Linear RUL");
        linearRULCol.setCellValueFactory(cellData -> new SimpleDoubleProperty(
                Double.parseDouble(new DecimalFormat("#.##").format(cellData.getValue().getAssetInfo().getRULMeasurement()))).asObject());

        TableColumn locationCol = new TableColumn("Location");
        locationCol.setCellValueFactory(
                new PropertyValueFactory<Asset, String>("location"));

        table.setItems(sortedSystems);
        table.getColumns().addAll(systemTypeCol, serialNoCol, linearRULCol, locationCol);
        AnchorPane.setBottomAnchor(table, 0.0);
        AnchorPane.setTopAnchor(table, 5.0);
        AnchorPane.setRightAnchor(table, 0.0);
        AnchorPane.setLeftAnchor(table, 0.0);
        systemsListPane.getChildren().addAll(table);
    }

    /**
     * Returns a sorted ObservableList depending on
     * the type of sort selected.
     *
     * @author Najim
     */
    public ObservableList<Asset> sortSystems(String selectedSort) {
        ObservableList<Asset> sortedSystems = FXCollections.observableArrayList(systems);
        switch (selectedSort) {
            case "Ascending RUL":
                System.out.println(selectedSort + " Sort");
                sort(sortedSystems);
                break;
            case "Descending RUL":
                System.out.println(selectedSort + " Sort");
                sort(sortedSystems);
                Collections.reverse(sortedSystems);
                break;
            default:
                System.out.println("Default Sort");
                sortedSystems = FXCollections.observableArrayList(systems);
                break;
        }

        return sortedSystems;
    }

    public void sort(ObservableList<Asset> list) {
        quickSort(list, 0, list.size() - 1);
    }

    public void quickSort(ObservableList<Asset> list, int from, int to) {
        if (from < to) {
            int pivot = from;
            int left = from + 1;
            int right = to;
            double pivotValue = list.get(pivot).getAssetInfo().getRULMeasurement();
            while (left <= right) {
                // left <= to -> limit protection
                while (left <= to && pivotValue >= list.get(left).getAssetInfo().getRULMeasurement()) {
                    left++;
                }
                // right > from -> limit protection
                while (right > from && pivotValue < list.get(right).getAssetInfo().getRULMeasurement()) {
                    right--;
                }
                if (left < right) {
                    Collections.swap(list, left, right);
                }
            }
            Collections.swap(list, pivot, left - 1);
            quickSort(list, from, right - 1);
            quickSort(list, right + 1, to);
        }
    }
}
