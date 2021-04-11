/*
  This Controller is responsible for handling the assets view.
  It generates the thumbnails and the list of assets. It also
  handles the sorting of assets.

  @author Jeff, Paul, Najim
  @last_edit 02/7/2020
 */
package controllers;

import app.ModelController;
import app.item.Asset;
import app.item.AssetType;
import app.item.Item;
import external.AssetDAOImpl;
import external.AssetTypeDAOImpl;
import external.ModelDAOImpl;
import javafx.animation.*;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rul.assessment.AssessmentController;
import utilities.TextConstants;
import utilities.ThresholdEnum;
import utilities.UIUtilities;

import java.net.URL;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AssetsController extends Controller implements Initializable {

    private static final String SORT_DEFAULT = "Order";
    private static final String SORT_RUL_ASC = "Ascending RUL";
    private static final String SORT_RUL_DESC = "Descending RUL";
    private static final String SORT_CRITICAL_ASC = "Less Critical";
    private static final String SORT_CRITICAL_DESC = "Most Critical";
    private static final String RECOMMENDATION = "Recommendation";
    private static final String TYPE_COL = "Type";
    private static final String SERIAL_NO_COL = "Serial No.";
    private static final String MODEL_COL = "Model";
    private static final String RUL_COL = "RUL";
    private static final String LOCATION_COL = "Location";
    private static final String MANUFACTURER_COL = "Manufacturer";
    private static final String CATEGORY_COL = "Category";
    private static final String SITE_COL = "Site";
    private static final String DESCRIPTION_COL = "Description";
    private final AssetTypeDAOImpl assetTypeDAO;
    private final ModelDAOImpl modelDAO;
    private final AssetDAOImpl assetDAOImpl;
    private final TableView<Asset> table;
    private final HashMap<String, Boolean> assetTypeFilterCondition;
    private final HashMap<String, Boolean> thresholdFilterCondition;
    Logger logger = LoggerFactory.getLogger(AssetsController.class);
    @FXML
    private Button addAssetBtn;
    @FXML
    private FlowPane assetsFlowPane;
    @FXML
    private AnchorPane assetsListPane;
    @FXML
    private Tab thumbnailTab;
    @FXML
    private Tab listTab;
    @FXML
    private ChoiceBox<String> sortAsset;
    @FXML
    private Button filterAsset;
    @FXML
    private GridPane navList;
    @FXML
    private TextField search;
    @FXML
    private TabPane assetsTabPane;
    private String searchMatch = "No Search"; // search states to help display the right asset list: No Search / Match / No Match
    @FXML
    private AnchorPane root;

    private UIUtilities uiUtilities;
    private ObservableList<Asset> assets;
    private ObservableList<Asset> searchedAssets;
    private PauseTransition delaySearch;
    private HashMap<Integer, Image> imagesList;

    public AssetsController() {
        assetTypeDAO = new AssetTypeDAOImpl();
        assetDAOImpl = new AssetDAOImpl();
        modelDAO = new ModelDAOImpl();
        table = new TableView<>();
        assetTypeFilterCondition = new HashMap<>();
        thresholdFilterCondition = new HashMap<>();
        imagesList = new HashMap<>();
        //adding the default image
        imagesList.put(0, new Image("file:JavaFXUserInterface/src/main/resources/imgs/default.png"));
        try {
            assets = FXCollections.observableArrayList(ModelController.getInstance().getAllLiveAssets());
        } catch (Exception e) {
            logger.error("Exception: ", e);
        }
    }

    /**
     * Initialize runs before the scene is displayed.
     * It initializes elements and data in the scene.
     *
     * @param url            url to be used
     * @param resourceBundle resourceBundle to be used
     * @author Jeff
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        uiUtilities = new UIUtilities();
        root.setOpacity(0);
        uiUtilities.fadeInTransition(root);
        attachEvents();
        updateRULs();
        generateThumbnails();
    }

    /**
     * Updates the RUL values of all systems.
     *
     * @author Jeff
     */
    public void updateRULs() {
        for (Asset asset : assets) {
            asset.setRul(String.valueOf(TextConstants.RULValueFormat.format(AssessmentController.getLatestEstimate(asset.getId()))));
        }
        Timeline rulTimeline =
                new Timeline(new KeyFrame(Duration.millis(3000), e ->
                {
                    for (Asset asset : assets) {
                        asset.setRul(String.valueOf(TextConstants.RULValueFormat.format(AssessmentController.getLatestEstimate(asset.getId()))));
                    }
                    table.refresh();
                }));

        rulTimeline.setCycleCount(Animation.INDEFINITE); // loop forever
        rulTimeline.play();
        addTimeline(rulTimeline);
    }

    /**
     * Adds mouse events to all the buttons
     *
     * @author Jeff
     */
    public void attachEvents() {
        // As the window expands or shrinks, asset panes will adjust to the window size accordingly
        assetsFlowPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            double assetFlowWidth = (double) newVal - 54;
            int nbOfPanes = (int) (assetFlowWidth / 247);
            assetFlowWidth = (assetFlowWidth % 247);
            assetFlowWidth = assetFlowWidth / (nbOfPanes - 1);
            assetsFlowPane.setHgap(assetFlowWidth);
        });

        thumbnailTab.setOnSelectionChanged(event -> {
            if (thumbnailTab.isSelected()) {
                assetsFlowPane.getChildren().clear();
                generateThumbnails();
            }
        });

        listTab.setOnSelectionChanged(event -> {
            if (listTab.isSelected()) {
                assetsListPane.getChildren().clear();
                generateList();
            }
        });

        //Attach link to addAssetButton to go to AddAsset.fxml
        addAssetBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(TextConstants.ADD_ASSETS_SCENE, addAssetBtn.getScene()));

        sortingSetUp();
        filterSetUp();
        searchAssets();
    }

    /**
     * setup the transitions and the content for the filter options
     *
     * @author Paul
     */
    private void filterSetUp() {
        TranslateTransition openNav = new TranslateTransition(new Duration(350), navList);
        TranslateTransition closeNav = new TranslateTransition(new Duration(350), navList);
        FadeTransition fadeTransition = new FadeTransition(new Duration(350), navList);
        navList.setVisible(false);
        filterAsset.setOnAction((ActionEvent evt) -> {
            navList.setVisible(true);
            if (navList.getTranslateX() != 0) {
                fadeTransition.setToValue(1.0);
                fadeTransition.setFromValue(0.0);
                fadeTransition.play();
                openNav.setToX(0);
                openNav.play();
            } else {
                fadeTransition.setToValue(0.0);
                fadeTransition.setFromValue(1.0);
                fadeTransition.play();
                closeNav.setToX((navList.getWidth()));
                closeNav.play();
            }
        });

        int i = 1;
        CheckBox option;
        Label assetTypeTitle = new Label("Asset Type");
        navList.add(assetTypeTitle, 0, 0);
        for (AssetType assetType : assetTypeDAO.getAssetTypeList()) {
            assetTypeFilterCondition.put(assetType.getName(), false);
            option = new CheckBox(assetType.getName());
            option.selectedProperty().addListener((observable, oldValue, newValue) -> {
                assetTypeFilterCondition.replace(assetType.getName(), newValue);
                generateContent();
            });
            navList.add(option, 0, i);
            i++;
        }
        Label thresholdTitle = new Label("Threshold");
        navList.add(thresholdTitle, 1, 0);
        for (ThresholdEnum thresholdEnum : ThresholdEnum.values()) {
            thresholdFilterCondition.put(thresholdEnum.getValue(), false);
            option = new CheckBox(thresholdEnum.getValue());
            option.selectedProperty().addListener((observable, oldValue, newValue) -> {
                thresholdFilterCondition.replace(thresholdEnum.getValue(), newValue);
                generateContent();
            });
            navList.add(option, 1, ThresholdEnum.valueOf(thresholdEnum.name()).ordinal() + 1);
            i++;
        }

        navList.setHgap(20);
        navList.setVgap(10);
    }

    /**
     * this method generates 2 lists of predicates based on the filter checkbox selections
     * it creates the predicate for the asset type filtering as an OR
     * it creates the predicate for the threshold filtering as an OR
     * it then apply the asset type filtering AND threshold filtering on the input list
     *
     * @param assetsToDisplay the searched list or the full list
     * @return the filtered list
     * @author Paul
     */
    private ObservableList<Asset> applyFilters(ObservableList<Asset> assetsToDisplay) {
        List<Predicate<Asset>> assetTypePredicates = new ArrayList<>();
        List<Predicate<Asset>> thresholdPredicates = new ArrayList<>();

        if (!assetTypeFilterCondition.isEmpty())
            assetTypeFilterCondition.entrySet().stream()
                    .filter(Map.Entry::getValue)
                    .forEach(entry -> assetTypePredicates.add(s -> s.getAssetTypeName().equals(entry.getKey())));
        if (assetTypePredicates.isEmpty())
            assetTypePredicates.add(s -> !s.getAssetTypeName().isEmpty());

        if (!thresholdFilterCondition.isEmpty())
            thresholdFilterCondition.entrySet().stream()
                    .filter(Map.Entry::getValue)
                    .forEach(entry -> thresholdPredicates.add(s -> s.getRecommendation().equals(entry.getKey())));
        if (thresholdPredicates.isEmpty())
            thresholdPredicates.add(s -> !s.getRecommendation().isEmpty());

        if (assetsToDisplay != null)
            assetsToDisplay = assetsToDisplay.stream()
                    .filter((assetTypePredicates.stream().reduce(x -> false, Predicate::or)).and(thresholdPredicates.stream().reduce(x -> false, Predicate::or)))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));

        return assetsToDisplay;
    }

    /**
     * regenerate the thumbnail or list view as selected
     *
     * @author Paul
     */
    private void generateContent() {
        if (assetsTabPane.getSelectionModel().getSelectedItem().getId().equals("thumbnailTab")) {
            assetsFlowPane.getChildren().clear();
            generateThumbnails();
        } else if (assetsTabPane.getSelectionModel().getSelectedItem().getId().equals("listTab")) {
            assetsListPane.getChildren().clear();
            generateList();
        }
    }

    private void sortingSetUp() {
        //Adding items to the choiceBox (drop down list)
        sortAsset.getItems().add(SORT_DEFAULT);
        sortAsset.getItems().add(SORT_RUL_ASC);
        sortAsset.getItems().add(SORT_RUL_DESC);
        sortAsset.getItems().add(SORT_CRITICAL_ASC);
        sortAsset.getItems().add(SORT_CRITICAL_DESC);

        //Default Value
        sortAsset.setValue(SORT_DEFAULT);
        //Listener on the sort ChoiceBox. Depending on the sort selected, all systems panes are cleared and generated again
        //with the appropriate sort applied.
        sortAsset.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                switch (newValue) {
                    case SORT_DEFAULT:
                        FXCollections.sort(assets, Comparator.comparingInt(Item::getId));
                        if (!search.getText().trim().isEmpty() && !searchedAssets.isEmpty()) {
                            FXCollections.sort(searchedAssets, Comparator.comparingInt(Item::getId));
                        }
                        break;
                    case SORT_RUL_ASC:
                        FXCollections.sort(assets, Comparator.comparing(asset -> Double.valueOf(asset.getRul().getValue())));
                        if (!search.getText().trim().isEmpty() && !searchedAssets.isEmpty()) {
                            FXCollections.sort(searchedAssets, Comparator.comparing(asset -> Double.valueOf(asset.getRul().getValue())));
                        }
                        break;
                    case SORT_RUL_DESC:
                        FXCollections.sort(assets, (asset, t1) -> Double.valueOf(t1.getRul().getValue()).compareTo(Double.valueOf(asset.getRul().getValue())));
                        if (!search.getText().trim().isEmpty() && !searchedAssets.isEmpty()) {
                            FXCollections.sort(searchedAssets, (asset, t1) -> Double.valueOf(t1.getRul().getValue()).compareTo(Double.valueOf(asset.getRul().getValue())));
                        }
                        break;
                    case SORT_CRITICAL_ASC:
                        FXCollections.sort(assets, Comparator.comparingInt(Asset::mapCriticality));
                        if (!search.getText().trim().isEmpty() && !searchedAssets.isEmpty()) {
                            FXCollections.sort(searchedAssets, Comparator.comparingInt(Asset::mapCriticality));
                        }
                        break;
                    case SORT_CRITICAL_DESC:
                        FXCollections.sort(assets, (asset, t1) -> Integer.compare(t1.mapCriticality(), asset.mapCriticality()));
                        if (!search.getText().trim().isEmpty() && !searchedAssets.isEmpty()) {
                            FXCollections.sort(searchedAssets, (asset, t1) -> Integer.compare(t1.mapCriticality(), asset.mapCriticality()));
                        }
                        break;
                    default:
                        break;
                }
                assetsFlowPane.getChildren().clear();
                generateThumbnails();
            }
        });
    }

    /**
     * Live search for assets in the assets list based on the input from the search bar.
     * When a search has a match, all systems panes are cleared and generated again with the appropriate assets.
     * When a search has no match, all system panes are cleared effectively showing no assets.
     *
     * @author Najim
     */
    private void searchAssets() {
        searchedAssets = FXCollections.observableArrayList();
        delaySearch = new PauseTransition(Duration.seconds(0.25));
        search.textProperty().addListener((observable, oldValue, newValue) -> {
            delaySearch.setOnFinished(event -> {
                searchedAssets.clear();
                if (!newValue.equals("")) {
                    //For each asset, we determine if the value from the search is found in the asset's member variables.
                    //Member variables: Rul, name, description, serial number, manufacturer, category, site, location, recommendation and asset type name
                    assets.stream().filter(o -> StringUtils.containsIgnoreCase(o.getRul().get(), newValue) || StringUtils.containsIgnoreCase(o.getName(), newValue)
                            || StringUtils.containsIgnoreCase(o.getDescription(), newValue) || StringUtils.containsIgnoreCase(o.getSerialNo(), newValue)
                            || StringUtils.containsIgnoreCase(o.getManufacturer(), newValue) || StringUtils.containsIgnoreCase(o.getCategory(), newValue)
                            || StringUtils.containsIgnoreCase(o.getSite(), newValue) || StringUtils.containsIgnoreCase(o.getLocation(), newValue)
                            || StringUtils.containsIgnoreCase(o.getRecommendation(), newValue) || StringUtils.containsIgnoreCase(assetTypeDAO.getNameFromID(o.getAssetTypeID()), newValue)).forEach(
                            searchedAssets::add //Assets matched with search are added to the searchAssets list
                    );
                    if (searchedAssets.isEmpty()) {
                        searchMatch = "No Match";
                    } else {
                        searchMatch = "Match";
                    }
                } else {
                    searchMatch = "No Search";
                }
                generateContent();
            });
            delaySearch.playFromStart();
        });
    }

    /**
     * Determines which list of assets to be displayed between the searchAssets list
     * and the original assets list.
     *
     * @return assetsToDisplay
     * @author Najim
     */
    public ObservableList<Asset> setAssetListToDisplay() {
        ObservableList<Asset> assetsToDisplay;
        if (!searchedAssets.isEmpty()) {
            assetsToDisplay = FXCollections.observableArrayList(searchedAssets);
        } else {
            if (searchMatch.equals("No Match")) {
                assetsFlowPane.getChildren().clear();
                assetsToDisplay = null;
            } else {
                assetsToDisplay = FXCollections.observableArrayList(assets);
            }
        }
        assetsToDisplay = applyFilters(assetsToDisplay);
        return assetsToDisplay;
    }

    /**
     * Creates elements that are in the scene so the data can be displayed.
     *
     * @author Jeff
     */
    public void generateThumbnails() {
        ObservableList<Pane> boxes = FXCollections.observableArrayList();
        ObservableList<Asset> assetsDisplayed = setAssetListToDisplay();
        if (assetsDisplayed != null && !assetsDisplayed.isEmpty()) {
            for (Asset asset : assetsDisplayed) {

                Pane pane = new Pane();
                pane.setCache(true);
                pane.setOnMouseClicked(event -> uiUtilities.changeScene("/AssetInfo", asset, pane.getScene()));
                pane.getStyleClass().add("thumbnailPane");

                Pane imagePlaceholder = new Pane();
                imagePlaceholder.getStyleClass().add("imagePlaceholder");

                BorderPane borderPane = new BorderPane();
                borderPane.getStyleClass().add("borderPane");

                setImage(asset, borderPane);

                HBox rulPane = new HBox();
                HBox statusPane = new HBox();

                Text assetName = new Text(asset.getSerialNo());
                Text assetType = new Text(assetTypeDAO.getNameFromID(asset.getAssetTypeID()));

                Text rulLabel = new Text("RUL");
                Text recommendationLabel = new Text("Status");
                Text recommendation = new Text(asset.getRecommendation());

                Text rulValue = new Text();
                SimpleStringProperty s = asset.getRul();
                rulValue.textProperty().bind(s);

                assetName.getStyleClass().add("thumbnailHeader");
                assetType.getStyleClass().add("thumbnailSubHeader");
                rulLabel.getStyleClass().add("valueLabel");
                rulValue.getStyleClass().add("valueText");
                recommendationLabel.getStyleClass().add("valueLabel");
                recommendation.getStyleClass().add("valueText");
                rulPane.getStyleClass().addAll("valuePane", "none");
                statusPane.getStyleClass().add("valuePane");
                statusPane.getStyleClass().add(asset.getRecommendation().toLowerCase());

                assetName.setLayoutX(15.0);
                assetName.setLayoutY(35.0);
                assetType.setLayoutX(15.0);
                assetType.setLayoutY(63.0);
                borderPane.setLayoutX(15.0);
                borderPane.setLayoutY(80.0);
                rulLabel.setLayoutX(52.0);
                rulLabel.setLayoutY(239.0);
                rulPane.setLayoutX(15.0);
                rulPane.setLayoutY(243.0);
                rulValue.setLayoutY(21.0);
                recommendationLabel.setLayoutX(164.0);
                recommendationLabel.setLayoutY(238.0);
                statusPane.setLayoutX(133.0);
                statusPane.setLayoutY(243.0);
                recommendation.setLayoutY(21.0);

                pane.getChildren().add(assetName);
                pane.getChildren().add(assetType);
                pane.getChildren().add(rulLabel);
                rulPane.getChildren().add(rulValue);
                pane.getChildren().add(rulPane);
                pane.getChildren().add(recommendationLabel);
                statusPane.getChildren().add(recommendation);
                pane.getChildren().add(statusPane);
                pane.getChildren().add(borderPane);

                boxes.add(pane);
            }
            assetsFlowPane.getChildren().addAll(boxes);
        } else {
            Text noResult = new Text("No results found");
            noResult.getStyleClass().add("noResult");

            HBox noResultPane = new HBox();
            noResultPane.getStyleClass().add("noResultPane");
            noResultPane.setAlignment(Pos.CENTER);
            noResultPane.getChildren().add(noResult);

            assetsFlowPane.getChildren().add(noResultPane);
        }
    }

    private void setImage(Asset asset, BorderPane borderPane) {
        if (imagesList.get(asset.getImageId()) == null)
            imagesList.put(asset.getImageId(), assetDAOImpl.findImageById(asset.getImageId()));

        ImageView imageView = new ImageView(imagesList.get(asset.getImageId()));
        imageView.setFitWidth(133);
        imageView.setFitHeight(133);
        imageView.setCache(true);
        borderPane.setCenter(imageView);
    }

    /**
     * Creates a table element to list all the assets.
     *
     * @author Jeff
     */
    public void generateList() {
        ObservableList<Asset> assetsDisplayed = setAssetListToDisplay();

        // When TableRow is clicked, send data to AssetInfo scene.
        table.setRowFactory(tv -> {
            TableRow<Asset> row = new TableRow<>();
            row.setOnMouseClicked(event -> uiUtilities.changeScene(row, "/AssetInfo", row.getItem(), row.getScene()));
            return row;
        });

        TableColumn<Asset, String> assetTypeCol = new TableColumn<>(TYPE_COL);
        assetTypeCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getAssetTypeName()));

        TableColumn<Asset, String> serialNoCol = new TableColumn<>(SERIAL_NO_COL);
        serialNoCol.setCellValueFactory(
                new PropertyValueFactory<>("serialNo"));

        TableColumn<Asset, String> modelCol = new TableColumn<>(MODEL_COL);
        modelCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                modelDAO.getModelNameAssociatedWithAssetType(cellData.getValue().getAssetTypeID())));

        TableColumn<Asset, Number> modelRULCol = new TableColumn<>(RUL_COL);
        modelRULCol.setCellValueFactory(cellData -> new SimpleDoubleProperty(
                Double.parseDouble(TextConstants.ThresholdValueFormat.format(Double.parseDouble(cellData.getValue().getRul().getValue())))));

        TableColumn<Asset, String> recommendationCol = new TableColumn<>(RECOMMENDATION);
        recommendationCol.setCellValueFactory(
                new PropertyValueFactory<>("recommendation"));
        recommendationCol.setCellFactory(column -> new TableCell<>() {
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    getStyleClass().addAll(item.toLowerCase(), "cellStatus");
                }
            }
        });

        TableColumn<Asset, String> locationCol = new TableColumn<>(LOCATION_COL);
        locationCol.setCellValueFactory(
                new PropertyValueFactory<>("location"));

        TableColumn<Asset, String> manufacturerCol = new TableColumn<>(MANUFACTURER_COL);
        manufacturerCol.setCellValueFactory(
                new PropertyValueFactory<>("manufacturer"));

        TableColumn<Asset, String> categoryCol = new TableColumn<>(CATEGORY_COL);
        categoryCol.setCellValueFactory(
                new PropertyValueFactory<>("category"));

        TableColumn<Asset, String> siteCol = new TableColumn<>(SITE_COL);
        siteCol.setCellValueFactory(
                new PropertyValueFactory<>("site"));

        TableColumn<Asset, String> descriptionCol = new TableColumn<>(DESCRIPTION_COL);
        descriptionCol.setCellValueFactory(
                new PropertyValueFactory<>("description"));

        table.setPlaceholder(new Label("No results found"));
        table.setItems(assetsDisplayed);
        table.setId("listTable");
        table.getColumns().addAll(assetTypeCol, serialNoCol, modelCol, modelRULCol, recommendationCol, locationCol, siteCol, categoryCol, manufacturerCol, descriptionCol);
        AnchorPane.setBottomAnchor(table, 0.0);
        AnchorPane.setTopAnchor(table, 5.0);
        AnchorPane.setRightAnchor(table, 0.0);
        AnchorPane.setLeftAnchor(table, 0.0);
        assetsListPane.getChildren().addAll(table);
    }
}
