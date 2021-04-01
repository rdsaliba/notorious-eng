/*
  This Controller is responsible for handling the information view
  of an asset type. It handles the editing of asset types
  and saving them to the database.

  @author Jeff, Paul, Roy, Najim
  @last_edit 02/7/2020
 */
package controllers;

import app.item.Asset;
import app.item.Model;
import app.item.TrainedModel;
import app.item.parameter.*;
import external.AssetDAOImpl;
import external.AssetTypeDAOImpl;
import external.ModelDAOImpl;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rul.models.ModelStrategy;
import utilities.*;

import java.net.URL;
import java.util.*;

import static utilities.TextConstants.*;

public class AssetTypeInfoController extends Controller implements Initializable {
    private static final String RMSE = "RMSE";
    static Logger logger = LoggerFactory.getLogger(AssetTypeInfoController.class);
    private final ModelPanes modelPanes = new ModelPanes();
    int trainSize = 0;
    int testSize = 0;
    @FXML
    private Text title;
    @FXML
    private Tab modelTab;
    @FXML
    private FlowPane modelsThumbPane;
    @FXML
    private Button infoSaveBtn;
    @FXML
    private Button infoDeleteBtn;
    @FXML
    private Button backBtn;
    @FXML
    private TextField assetTypeName;
    @FXML
    private TextArea assetTypeDescription;
    @FXML
    private TextField thresholdAdvisory;
    @FXML
    private TextField thresholdCaution;
    @FXML
    private TextField thresholdWarning;
    @FXML
    private TextField thresholdFailed;
    @FXML
    private AnchorPane assetTypeInformationAnchorPane;
    @FXML
    private Slider trainSlider;
    @FXML
    private Slider testSlider;
    @FXML
    private Label trainValue;
    @FXML
    private Label testValue;
    @FXML
    private Button evaluateAllModelsBtn;
    @FXML
    private Button modelSaveBtn;
    @FXML
    private Button resetBtn;
    @FXML
    private Button modelDefaultBtn;
    @FXML
    private ArrayList<Button> evaluateButtons;
    @FXML
    private Label associatedModelLabel;
    @FXML
    private VBox modelParameters;

    private ObservableList<TrainedModel> modelObservableList;
    private int associatedModelID;

    private UIUtilities uiUtilities;
    private AssetTypeList assetType;
    private AssetTypeList originalAssetType;
    private List<Asset> assetsList;
    private AssetTypeDAOImpl assetTypeDAO;
    private ModelDAOImpl modelDAO;
    private AssetDAOImpl assetDAO;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        uiUtilities = new UIUtilities();
        assetTypeDAO = new AssetTypeDAOImpl();
        modelDAO = new ModelDAOImpl();
        assetDAO = new AssetDAOImpl();
        assetsList = new ArrayList<>();
        try {
            attachEvents();
        } catch (Exception exception) {
            logger.error("Exception in initialize(): ", exception);
        }
    }

    /**
     * initData receives the Asset Type data that was selected from Utilities.AssetTypeList.FXML
     * Then, uses that data to populate the text fields in the scene.
     *
     * @param assetType represents the asset type we want to get info on
     * @author Najim, Paul
     */
    public void initData(AssetTypeList assetType) {
        title.setText("Edit " + assetType.getName());
        this.assetType = assetType;
        this.originalAssetType = new AssetTypeList(assetType);
        assetTypeName.setText(assetType.getAssetType().getName());
        assetTypeDescription.setText(assetType.getAssetType().getDescription());
        associatedModelLabel.setText(modelDAO.getModelNameAssociatedWithAssetType(assetType.getId()));

        // Initializing Data for the threshold text fields
        ObservableList<TextField> thresholdTextFieldList = FXCollections.observableArrayList();
        thresholdTextFieldList.addAll(thresholdAdvisory, thresholdCaution, thresholdWarning, thresholdFailed);
        try {
            if (assetType.getValueAdvisory() != null && !assetType.getValueAdvisory().equalsIgnoreCase("null"))
                thresholdAdvisory.setText(ThresholdValueFormat.format(Double.parseDouble(assetType.getValueAdvisory())));
            if (assetType.getValueCaution() != null && !assetType.getValueCaution().equalsIgnoreCase("null"))
                thresholdCaution.setText(ThresholdValueFormat.format(Double.parseDouble(assetType.getValueCaution())));
            if (assetType.getValueWarning() != null && !assetType.getValueWarning().equalsIgnoreCase("null"))
                thresholdWarning.setText(ThresholdValueFormat.format(Double.parseDouble(assetType.getValueWarning())));
            if (assetType.getValueFailed() != null && !assetType.getValueFailed().equalsIgnoreCase("null"))
                thresholdFailed.setText(ThresholdValueFormat.format(Double.parseDouble(assetType.getValueFailed())));
        } catch (NumberFormatException e) {
            logger.error("NumberFormatException error inside initData");
            logger.error("Exception initData(): ", e);
        }
        for (TextField thresholdTextField : thresholdTextFieldList) {
            thresholdTextField.setPromptText("No current value for " + thresholdTextField.getId() + ". Please enter a value.");
        }
    }

    /**
     * Attaches events to elements in the scene and specifically on the information tab
     *
     * @author Najim, Paul
     * Edit: added all the text proprety listeners and text formaters for all the fields
     */
    public void attachEvents() {
        // Change scenes to Assets.fxml
        backBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(ASSET_TYPE_LIST_SCENE, backBtn.getScene()));

        infoDeleteBtn.setOnMouseClicked(mouseEvent -> CustomDialog.DeleteAssetTypeConfirmationDialogShowAndWait(assetType.getId(), infoSaveBtn.getScene()));

        infoSaveBtn.setDisable(true);
        infoSaveBtn.setOnMouseClicked(mouseEvent -> {
            if (FormInputValidation.assetTypeFormInputValidation(assetTypeInformationAnchorPane, assetTypeName, assetTypeDescription, thresholdAdvisory, thresholdCaution, thresholdWarning, thresholdFailed)) {
                assetTypeDAO.updateAssetType(assetType.toAssetType());
                uiUtilities.changeScene(TextConstants.ASSET_TYPE_LIST_SCENE, backBtn.getScene());
                backBtn.fire();
            }
        });

        attachAssetTypeTextFieldsEvents();
        modelTab.setOnSelectionChanged(event -> initializeModelTab());
    }

    /**
     * This attaches an event listener for every asset type information input field. It will handle
     * the text or value change on all those fields.
     *
     * @author Paul
     */
    private void attachAssetTypeTextFieldsEvents() {
        assetTypeName.textProperty().addListener((obs, oldText, newText) -> {
            if (handleTextChange(newText, originalAssetType.getName()))
                assetType.getAssetType().setName(newText);
        });
        assetTypeDescription.textProperty().addListener((obs, oldText, newText) -> {
            if (handleTextChange(newText, originalAssetType.getDescription()))
                assetType.getAssetType().setDescription(newText);
        });

        thresholdAdvisory.textProperty().addListener((obs, oldText, newText) -> {
            if (handleTextChange(newText, originalAssetType.getValueAdvisory()))
                assetType.setValueAdvisory(newText);
        });
        thresholdAdvisory.setTextFormatter(new TextFormatter<>(c -> FormInputValidation.checkFormat(ThresholdValueFormat, c)));

        thresholdCaution.textProperty().addListener((obs, oldText, newText) -> {
            if (handleTextChange(newText, originalAssetType.getValueCaution()))
                assetType.setValueCaution(newText);
        });
        thresholdCaution.setTextFormatter(new TextFormatter<>(c -> FormInputValidation.checkFormat(ThresholdValueFormat, c)));

        thresholdWarning.textProperty().addListener((obs, oldText, newText) -> {
            if (handleTextChange(newText, originalAssetType.getValueWarning()))
                assetType.setValueWarning(newText);
        });
        thresholdWarning.setTextFormatter(new TextFormatter<>(c -> FormInputValidation.checkFormat(ThresholdValueFormat, c)));

        thresholdFailed.textProperty().addListener((obs, oldText, newText) -> {
            if (handleTextChange(newText, originalAssetType.getValueFailed()))
                assetType.setValueFailed(newText);
        });
        thresholdFailed.setTextFormatter(new TextFormatter<>(c -> FormInputValidation.checkFormat(ThresholdValueFormat, c)));
    }

    /**
     * This initializes all the data and UI components for the model Tab
     *
     * @author Talal, Jeremie
     */
    private void initializeModelTab() {
        evaluateAllModelsBtn.setDisable(true);
        evaluateButtons = new ArrayList<>();
        evaluateButtons.add(evaluateAllModelsBtn);

        setTestAndTrainSliders();

        try {
            modelObservableList = FXCollections.observableArrayList(modelDAO.getModelsByAssetTypeID(assetType.getId(), Constants.STATUS_EVALUATION));
            associatedModelID = modelDAO.getModelIDAssociatedWithAssetType(assetType.getId());
        } catch (Exception e) {
            logger.error("Exception in getting all the models list", e);
        }
        modelsThumbPane.getChildren().clear();
        generateThumbnails();
        attachEventsModelTab();

        updateRMSE();
    }

    /**
     * This sets configures the test and train sliders to hold values based on the specific asset
     * type. It also adds an event listener to keep track of the current selection (input) of one
     * of the sliders to adjust the maximum value of the other so the number of assets used in total
     * is always the correct one.
     *
     * @author Talal
     */
    private void setTestAndTrainSliders() {
        List<Asset> assets = assetDAO.getArchivedAssetsFromAssetTypeID(Integer.parseInt(assetType.getId()));
        int nbOfAssets = assets.size();
        trainSlider.setMax(nbOfAssets);
        trainValue.setText(String.valueOf(trainSlider.getValue()));
        testSlider.setMax(nbOfAssets);
        testValue.setText(String.valueOf(testSlider.getValue()));

        trainSlider.valueProperty().addListener((observableValue, number, t1) -> {
            trainSlider.setValue(t1.intValue());
            trainValue.setText(Integer.toString((int) trainSlider.getValue()));
            testSlider.setMax(nbOfAssets - trainSlider.getValue());
            trainSize = (int) trainSlider.getValue();
        });
        testSlider.valueProperty().addListener((observableValue, number, t1) -> {
            testSlider.setValue(t1.intValue());
            testValue.setText(Integer.toString((int) testSlider.getValue()));
            trainSlider.setMax(nbOfAssets - testSlider.getValue());
            testSize = (int) testSlider.getValue();
        });
    }

    /**
     * This attaches all the events to the UI components for the model tab
     *
     * @author Talal, Jeremie
     */
    private void attachEventsModelTab() {
        modelSaveBtn.setOnMouseClicked(mouseEvent -> {
            modelObservableList.stream()
                    .filter(trainedModel -> trainedModel.getModelID() == modelPanes.getSelectedModel().getModelID())
                    .findFirst().ifPresent(selected -> {
                selected.getModelStrategy().setParameters(selected.getModelStrategy().getParameters());
                modelDAO.updateModelStrategy(selected.getModelStrategy(), selected.getModelID(), selected.getAssetTypeID());
            });
            saveSelectedModelAssociation();
            uiUtilities.changeScene(ASSET_TYPE_LIST_SCENE, modelSaveBtn.getScene());
        });
        resetBtn.setOnMouseClicked(mouseEvent -> modelObservableList.stream()
                .filter(trainedModel -> trainedModel.getModelID() == modelPanes.getSelectedModel().getModelID())
                .findFirst().ifPresent(selected -> {
                    selected.setModelStrategy((modelDAO.getModelStrategy(selected.getModelID(), selected.getAssetTypeID())));
                    generateParameters(selected, false);
                }));
        modelDefaultBtn.setOnMouseClicked(mouseEvent -> modelObservableList.stream()
                .filter(trainedModel -> trainedModel.getModelID() == modelPanes.getSelectedModel().getModelID())
                .findFirst()
                .ifPresent(selected -> generateParameters(selected, true)));

        try {
            evaluateAllModelsBtn.setOnMouseClicked(mouseEvent -> {
                for (TrainedModel model : modelObservableList) {
                    saveModelToEvaluate(model);
                }
            });
        } catch (Exception e) {
            logger.error("Exception for evaluateAllModelsBtn.setOnMouseClicked(), e");
        }

        trainSlider.setOnMouseClicked(mouseEvent -> enableEvaluation(evaluateButtons));
        testSlider.setOnMouseClicked(mouseEvent -> enableEvaluation(evaluateButtons));

        modelsThumbPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            double modelFlowWidth = (double) newVal - 22;
            int nbOfPanes = (int) (modelFlowWidth / 247);
            modelFlowWidth = (modelFlowWidth % 247);
            modelFlowWidth = modelFlowWidth / (nbOfPanes - 1);
            modelsThumbPane.setHgap(modelFlowWidth);
        });

    }

    /**
     * This attaches the training and testing assets from the sliders to the model strategy
     * to be evaluated and saves that whole object (classifier) in the database. It updates
     * the serialized object for evaluation only.
     *
     * @param model is the model to be evaluated
     * @author Talal, Jeremie
     */
    public void saveModelToEvaluate(TrainedModel model) {
        int assetTypeID = Integer.parseInt(assetType.getId());
        int trainAssets = (int) trainSlider.getValue() + 1;
        int testAssets = (int) trainSlider.getValue() + 1 + (int) testSlider.getValue();
        ModelStrategy modelStrategy = model.getModelStrategy();
        if (!Objects.isNull(modelStrategy)) {
            modelStrategy.setTrainAssets(trainAssets);
            modelStrategy.setTestAssets(testAssets);
            modelStrategy.setParameters(model.getModelStrategy().getParameters());
            modelDAO.updateModelStrategy(modelStrategy, model.getModelID(), assetTypeID);
        } else {
            CustomDialog.nullModelAlertDialogShowAndWait();
        }
    }

    /**
     * Enables or disables evaluate model buttons depending on the train slider and test slider
     * values. It enables the buttons when both slider values are higher than 0.
     *
     * @param enableBtnList The list of model evaluation buttons, including the evaluation for all models
     *                      and the individual evaluation model buttons.
     * @author Jeremie
     */
    public void enableEvaluation(List<Button> enableBtnList) {
        for (Button enableButton : enableBtnList) {
            if (trainSize > 0 && testSize > 0) {
                enableButton.setDisable(false);
            } else if (trainSize == 0 || testSize == 0) {
                enableButton.setDisable(true);
            }
        }
    }

    /**
     * Handle the text change of the user fields to turn on or off the save functionality
     *
     * @author Paul
     */
    private boolean handleTextChange(String newText, String field) {
        if (field == null) {
            if (!newText.isEmpty()) {
                infoSaveBtn.setDisable(false);
                return true;
            } else {
                infoSaveBtn.setDisable(true);
                return false;
            }
        } else if ((field).equals(originalAssetType.getName()) || field.equals(originalAssetType.getDescription())) {
            if (!newText.isEmpty() && !newText.equals(field)) {
                infoSaveBtn.setDisable(false);
                return true;
            } else {
                infoSaveBtn.setDisable(true);
                return false;
            }
        } else if (!newText.isEmpty() && Double.parseDouble(newText) == Double.parseDouble(field)) {
            infoSaveBtn.setDisable(true);
            return false;
        } else {
            infoSaveBtn.setDisable(false);
            return true;
        }
    }

    /**
     * Generates the thumbnails for each model that exist in the database. This function will display
     * on each thumbnail: the name of the model, its description, a button to generate the evaluation
     * of the model for that asset type as well as the the RMSE value generated from the evaluation.
     *
     * @author Jeremie
     */
    public void generateThumbnails() {
        ObservableList<Pane> modelPaneObservableList = FXCollections.observableArrayList();
        for (TrainedModel model : modelObservableList) {
            // Creating a Thumbnail element
            Pane modelPane = new Pane();
            modelPane.getStyleClass().add("modelPane");
            modelPane.setOnMouseClicked(mouseEvent -> {
                modelPanes.handleModelSelection(model, modelPane);
                generateParameters(model, false);
            });

            // Generating items to display for the Thumbnail
            Text modelNameLabel = new Text(model.getModelName());
            Text modelDescriptionText = new Text(model.getDescription());
            Text rmseLabel = new Text(RMSE);
            Text rmseValue = new Text();

            HBox rmsePane = new HBox();
            rmsePane.getStyleClass().add("rmsePane");
            rmsePane.setAlignment(Pos.CENTER);

            Button evaluateModelBtn = new Button();
            evaluateModelBtn.setText("Evaluate");
            evaluateModelBtn.setDisable(true);
            evaluateButtons.add(evaluateModelBtn);
            evaluateModelBtn.setOnMouseClicked(mouseEvent -> saveModelToEvaluate(model));

            //Setting IDs for the elements
            modelNameLabel.getStyleClass().add("modelName");
            modelDescriptionText.setId("modelDescriptionText");
            rmseLabel.getStyleClass().add("rmseLabel");
            rmseValue.getStyleClass().add("rmseValue");
            SimpleStringProperty s = model.getRMSE();
            rmseValue.textProperty().bind(s);
            evaluateModelBtn.getStyleClass().add("selectBtn");

            //Setting the Layout of the elements
            modelNameLabel.setLayoutX(15.0);
            modelNameLabel.setLayoutY(35.0);
            modelDescriptionText.setLayoutX(15.0);
            modelDescriptionText.setLayoutY(80.0);
            rmseLabel.setLayoutX(47.0);
            rmseLabel.setLayoutY(151.0);
            rmsePane.setLayoutX(15.0);
            rmsePane.setLayoutY(155.0);
            rmseValue.setLayoutX(30.0);
            rmseValue.setLayoutY(177.0);
            evaluateModelBtn.setLayoutX(133.0);
            evaluateModelBtn.setLayoutY(155.0);

            modelPane.getChildren().add(modelNameLabel);
            modelPane.getChildren().add(modelDescriptionText);
            modelPane.getChildren().add(rmseLabel);
            modelPane.getChildren().add(rmsePane);
            modelPane.getChildren().add(rmseValue);
            modelPane.getChildren().add(evaluateModelBtn);

            modelPaneObservableList.add(modelPane);
        }
        modelPanes.setModelThumbnailsContainerPane(modelPaneObservableList, modelsThumbPane);
        modelPanes.highlightAssociatedModel(modelPaneObservableList, associatedModelID);
        modelPanes.setSelectedModel(modelObservableList.stream().filter(model -> model.getModelID() == associatedModelID).findFirst().orElse(null));
        generateParameters(modelObservableList.stream().filter(model -> model.getModelID() == associatedModelID).findFirst().orElse(null), false);
    }

    /**
     * @param model         the model currently selected
     * @param defaultParams true if we want the default parameters, false otherwise
     * @author Jeff, Paul
     */
    public void generateParameters(TrainedModel model, boolean defaultParams) {
        modelParameters.getChildren().clear();
        Map<String, Parameter> params = (defaultParams) ? model.getModelStrategy().getDefaultParameters() : model.getModelStrategy().getParameters();
        model.getModelStrategy().setParameters(params);
        Iterator<String> iterator = params.keySet().iterator();
        double layoutX = 50.0;
        double layoutY = 20.0;
        double tfLayoutX = 300.0;

        while (iterator.hasNext()) {
            String paramName = iterator.next();
            Parameter parameter = params.get(paramName);

            //make the pane
            Pane pane = new Pane();
            pane.getStyleClass().add("paramPane");
            pane.setLayoutX(layoutX);
            pane.setLayoutY(layoutY);

            // Make the label itself
            Label paramNameLabel = new Label();
            paramNameLabel.getStyleClass().add("paramLabel");
            paramNameLabel.setText(paramName);
            pane.getChildren().add(paramNameLabel);

            // Depending on the parameter type, generate corresponding input field
            if (parameter instanceof BoolParameter) {
                CheckBox checkBox = new CheckBox();
                checkBox.setSelected(((BoolParameter) parameter).getBoolValue());
                checkBox.setLayoutX(tfLayoutX);
                checkBox.setLayoutY(0.0);
                checkBox.selectedProperty().addListener((ov, old_val, new_val) -> ((BoolParameter) params.get(paramName)).setBoolValue(new_val));
                pane.getChildren().addAll(checkBox);
            } else if (parameter instanceof IntParameter) {
                TextField tf = new TextField();
                tf.setText(String.valueOf(((IntParameter) parameter).getIntValue()));
                tf.getStyleClass().add("paramTextField");
                tf.setLayoutX(tfLayoutX);
                tf.setLayoutY(0.0);
                tf.setTextFormatter(new TextFormatter<>(c -> FormInputValidation.checkFormat(intRegex, c)));
                tf.textProperty().addListener((ov, old_val, new_val) -> ((IntParameter) params.get(paramName)).setIntValue(Integer.parseInt(new_val)));
                pane.getChildren().add(tf);
            } else if (parameter instanceof ListParameter) {
                ChoiceBox<String> listBox = new ChoiceBox<>();
                listBox.setItems(FXCollections.observableArrayList(((ListParameter) parameter).getListValues()));
                listBox.setValue(((ListParameter) parameter).getSelectedValue());
                listBox.getStyleClass().add("paramTextField");
                listBox.setLayoutX(tfLayoutX);
                listBox.setLayoutY(0.0);
                listBox.getSelectionModel().selectedItemProperty().addListener((ov, old_val, new_val) -> ((ListParameter) params.get(paramName)).setSelectedValue(new_val));
                pane.getChildren().add(listBox);
            } else if (parameter instanceof FloatParameter) {
                TextField tf = new TextField();
                tf.setText(String.valueOf(((FloatParameter) parameter).getFloatValue()));
                tf.getStyleClass().add("paramTextField");
                tf.setLayoutX(tfLayoutX);
                tf.setLayoutY(0.0);
                tf.setTextFormatter(new TextFormatter<>(c -> FormInputValidation.checkFormat(floatRegex, c)));
                tf.textProperty().addListener((ov, old_val, new_val) -> ((FloatParameter) params.get(paramName)).setFloatValue(Float.parseFloat(new_val)));
                pane.getChildren().add(tf);
            }
            modelParameters.getChildren().add(pane);
            layoutY += 40.0;
        }
    }

    /**
     * Saves the association between the selected model and the current asset type in the database.
     * This function also set the retrain tag to true to indicate that the model chosen for the asset
     * type needs to be retrain. Finally, it labels all live assets of that asset type as updated.
     *
     * @author Jeremie
     */
    private void saveSelectedModelAssociation() {
        modelDAO.updateModelAssociatedWithAssetType(modelPanes.getSelectedModel(), assetType.getId());
        modelDAO.setModelToTrain(assetType.getId());
        updateAssetsForSelectedModelAssociation(Integer.parseInt(assetType.getId()));
    }

    /**
     * This function updates all live assets for the current asset type by setting their status as
     * updated. This means that their RUl needs to be re-evaluated.
     *
     * @param assetTypeID Is the Asset type ID of the current AssetType
     * @author Jeremie
     */
    private void updateAssetsForSelectedModelAssociation(int assetTypeID) {
        assetsList = assetDAO.getLiveAssetsFromAssetTypeID(assetTypeID);
        for (Asset asset : assetsList) {
            assetDAO.setAssetToBeUpdated(asset.getId());
        }
    }

    /**
     * This function continuously updates the RMSE values for the different models that are used for evaluation
     *
     * @author Talal
     */
    public void updateRMSE() {
        for (Model model : modelObservableList) {
            model.setRMSE(String.valueOf(RMSEValueFormat.format(modelDAO.getLatestRMSE(model.getModelID(), Integer.parseInt(assetType.getId())))));
        }
        Timeline rmseTimeline = new Timeline(new KeyFrame(Duration.millis(3000), e ->
        {
            for (Model model : modelObservableList) {
                model.setRMSE(String.valueOf(RMSEValueFormat.format(modelDAO.getLatestRMSE(model.getModelID(), Integer.parseInt(assetType.getId())))));
            }
        }));

        rmseTimeline.setCycleCount(Animation.INDEFINITE); // loop forever
        rmseTimeline.play();
        addTimeline(rmseTimeline);
    }
}