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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rul.models.ModelStrategy;
import utilities.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class AssetTypeInfoController implements Initializable {
    private static final String RMSE = "RMSE";
    static Logger logger = LoggerFactory.getLogger(AssetTypeInfoController.class);
    private final Text[] errorMessages = new Text[7];
    private final boolean[] validInput = new boolean[7];
    private final ModelPanes modelPanes = new ModelPanes();
    boolean validForm = true;
    int trainSize = 0;
    int testSize = 0;
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
    private TextArea assetTypeDesc;
    @FXML
    private TextField thresholdOK;
    @FXML
    private TextField thresholdAdvisory;
    @FXML
    private TextField thresholdCaution;
    @FXML
    private TextField thresholdWarning;
    @FXML
    private TextField thresholdFailed;
    @FXML
    private AnchorPane inputError;
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
    private ArrayList<Button> evaluateButtons;
    @FXML
    private Label associatedModelLabel;
    private ObservableList<Model> modelObservableList;
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
        this.assetType = assetType;
        this.originalAssetType = new AssetTypeList(assetType);
        assetTypeName.setText(assetType.getAssetType().getName());
        assetTypeDesc.setText(assetType.getAssetType().getDescription());
        associatedModelLabel.setText(modelDAO.getModelNameAssociatedWithAssetType(assetType.getId()));

        // Initializing Data for the threshold text fields
        ObservableList<TextField> thresholdTextFieldList = FXCollections.observableArrayList();
        thresholdTextFieldList.addAll(thresholdOK, thresholdAdvisory, thresholdCaution, thresholdWarning, thresholdFailed);
        try {
            if (assetType.getValueOk() != null && !assetType.getValueOk().equalsIgnoreCase("null"))
                thresholdOK.setText(TextConstants.ThresholdValueFormat.format(Double.parseDouble(assetType.getValueOk())));
            if (assetType.getValueAdvisory() != null && !assetType.getValueAdvisory().equalsIgnoreCase("null"))
                thresholdAdvisory.setText(TextConstants.ThresholdValueFormat.format(Double.parseDouble(assetType.getValueAdvisory())));
            if (assetType.getValueCaution() != null && !assetType.getValueCaution().equalsIgnoreCase("null"))
                thresholdCaution.setText(TextConstants.ThresholdValueFormat.format(Double.parseDouble(assetType.getValueCaution())));
            if (assetType.getValueWarning() != null && !assetType.getValueWarning().equalsIgnoreCase("null"))
                thresholdWarning.setText(TextConstants.ThresholdValueFormat.format(Double.parseDouble(assetType.getValueWarning())));
            if (assetType.getValueFailed() != null && !assetType.getValueFailed().equalsIgnoreCase("null"))
                thresholdFailed.setText(TextConstants.ThresholdValueFormat.format(Double.parseDouble(assetType.getValueFailed())));
        } catch (NumberFormatException e) {
            logger.error("NumberFormatException error inside initData");
            logger.error("Exception initData(): ", e);
        }
        for (TextField thresholdTextField : thresholdTextFieldList) {
            thresholdTextField.setPromptText("No current value for " + thresholdTextField.getId() + ". Please enter a value.");
        }

        updateRMSE();
    }

    /**
     * Attaches events to elements in the scene and specifically on the information tab
     *
     * @author Najim, Paul
     * Edit: added all the text proprety listeners and text formaters for all the fields
     */
    public void attachEvents() {
        // Change scenes to Assets.fxml
        backBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(TextConstants.ASSET_TYPE_LIST_SCENE, backBtn.getScene()));

        infoDeleteBtn.setOnMouseClicked(mouseEvent -> CustomDialog.DeleteAssetTypeConfirmationDialogShowAndWait(assetType.getId(), infoSaveBtn.getScene()));

        infoSaveBtn.setDisable(true);
        infoSaveBtn.setOnMouseClicked(mouseEvent -> {
            if (formInputValidation()) {
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
        assetTypeDesc.textProperty().addListener((obs, oldText, newText) -> {
            if (handleTextChange(newText, originalAssetType.getDescription()))
                assetType.getAssetType().setDescription(newText);
        });

        thresholdOK.textProperty().addListener((obs, oldText, newText) -> {
            if (handleTextChange(newText, originalAssetType.getValueOk()))
                assetType.setValueOk(newText);
        });
        thresholdOK.setTextFormatter(new TextFormatter<>(c -> UIUtilities.checkFormat(TextConstants.ThresholdValueFormat, c)));

        thresholdAdvisory.textProperty().addListener((obs, oldText, newText) -> {
            if (handleTextChange(newText, originalAssetType.getValueAdvisory()))
                assetType.setValueAdvisory(newText);
        });
        thresholdAdvisory.setTextFormatter(new TextFormatter<>(c -> UIUtilities.checkFormat(TextConstants.ThresholdValueFormat, c)));

        thresholdCaution.textProperty().addListener((obs, oldText, newText) -> {
            if (handleTextChange(newText, originalAssetType.getValueCaution()))
                assetType.setValueCaution(newText);
        });
        thresholdCaution.setTextFormatter(new TextFormatter<>(c -> UIUtilities.checkFormat(TextConstants.ThresholdValueFormat, c)));

        thresholdWarning.textProperty().addListener((obs, oldText, newText) -> {
            if (handleTextChange(newText, originalAssetType.getValueWarning()))
                assetType.setValueWarning(newText);
        });
        thresholdWarning.setTextFormatter(new TextFormatter<>(c -> UIUtilities.checkFormat(TextConstants.ThresholdValueFormat, c)));

        thresholdFailed.textProperty().addListener((obs, oldText, newText) -> {
            if (handleTextChange(newText, originalAssetType.getValueFailed()))
                assetType.setValueFailed(newText);
        });
        thresholdFailed.setTextFormatter(new TextFormatter<>(c -> UIUtilities.checkFormat(TextConstants.ThresholdValueFormat, c)));
    }

    /**
     * This initializes all the data and UI components for the model Tab
     *
     * @author Talal, Jeremie
     */
    private void initializeModelTab() {
        modelSaveBtn.setDisable(true);
        evaluateAllModelsBtn.setDisable(true);
        evaluateButtons = new ArrayList<>();
        evaluateButtons.add(evaluateAllModelsBtn);

        setTestAndTrainSliders();

        try {
            modelObservableList = FXCollections.observableArrayList(modelDAO.getAllModelsForEvaluation(Integer.parseInt(assetType.getId())));
        } catch (Exception e) {
            logger.error("Exception in getting all the models list", e);
        }
        modelsThumbPane.getChildren().clear();
        generateThumbnails();
        attachEventsModelTab();
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
            trainValue.setText(Integer.toString((int) trainSlider.getValue()));
            testSlider.setMax(nbOfAssets - trainSlider.getValue());
            trainSize = (int) trainSlider.getValue();
        });
        testSlider.valueProperty().addListener((observableValue, number, t1) -> {
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
            saveSelectedModelAssociation();
            uiUtilities.changeScene(TextConstants.ASSET_TYPE_LIST_SCENE, modelSaveBtn.getScene());
        });

        try {
            evaluateAllModelsBtn.setOnMouseClicked(mouseEvent -> {
                for (Model model : modelObservableList) {
                    saveModelToEvaluate(model);
                }
            });
        } catch (Exception e) {
            logger.error("Exception for evaluateAllModelsBtn.setOnMouseClicked(), e");
        }

        trainSlider.setOnMouseClicked(mouseEvent -> enableEvaluation(evaluateButtons));
        testSlider.setOnMouseClicked(mouseEvent -> enableEvaluation(evaluateButtons));
    }

    /**
     * This attaches the training and testing assets from the sliders to the model strategy
     * to be evaluated and saves that whole object (classifier) in the database. It updates
     * the serialized object for evaluation only.
     *
     * @param model is the model to be evaluated
     * @author Talal, Jeremie
     */
    public void saveModelToEvaluate(Model model) {
        int assetTypeID = Integer.parseInt(assetType.getId());
        int trainAssets = (int) trainSlider.getValue() + 1;
        int testAssets = (int) trainSlider.getValue() + 1 + (int) testSlider.getValue();
        ModelStrategy modelStrategy = modelDAO.getModelStrategy(model.getModelID(), assetTypeID);
        if (!Objects.isNull(modelStrategy)) {
            modelStrategy.setTrainAssets(trainAssets);
            modelStrategy.setTestAssets(testAssets);
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
     * Displays an error for a field when the validation criteria are not respected.
     *
     * @author Najim
     */
    public boolean formInputValidation() {
        String assetTypeNameValue = assetTypeName.getText();
        String assetTypeDescValue = assetTypeDesc.getText();
        double horizontalPosition = 0;

        assetTypeNameValidation(assetTypeNameValue, horizontalPosition);

        if (assetTypeDescValue.length() > 300) {
            validForm = false;
            validInput[1] = false;
            UIUtilities.createInputError(inputError, errorMessages, assetTypeDesc, TextConstants.MAX_300_CHARACTERS_ERROR, 85.0, horizontalPosition, 1);
        } else {
            validInput[1] = true;
            UIUtilities.removeInputError(inputError, errorMessages, validInput, assetTypeDesc, 1);
        }

        if (UIUtilities.compareThresholds(thresholdAdvisory, thresholdCaution)) {
            validInput[3] = true;
            validInput[4] = true;
            UIUtilities.removeInputError(inputError, errorMessages, validInput, thresholdAdvisory, 3);
            UIUtilities.removeInputError(inputError, errorMessages, validInput, thresholdCaution, 4);
        } else {
            validForm = false;
            validInput[3] = false;
            validInput[4] = false;
            UIUtilities.createInputError(inputError, errorMessages, thresholdAdvisory, TextConstants.ADVISORY_CAUTION, 178.0, horizontalPosition, 3);
            UIUtilities.createInputError(inputError, errorMessages, thresholdCaution, "", 0, 0, 4);
        }

        if (UIUtilities.compareThresholds(thresholdAdvisory, thresholdWarning)) {
            validInput[3] = true;
            validInput[5] = true;
            UIUtilities.removeInputError(inputError, errorMessages, validInput, thresholdAdvisory, 3);
            UIUtilities.removeInputError(inputError, errorMessages, validInput, thresholdWarning, 5);
        } else {
            validForm = false;
            validInput[3] = false;
            validInput[5] = false;
            UIUtilities.createInputError(inputError, errorMessages, thresholdAdvisory, TextConstants.ADVISORY_WARNING, 178.0, 0, 3);
            UIUtilities.createInputError(inputError, errorMessages, thresholdWarning, "", 0, 0, 5);
        }

        if (UIUtilities.compareThresholds(thresholdCaution, thresholdWarning)) {
            validInput[4] = true;
            validInput[5] = true;
            UIUtilities.removeInputError(inputError, errorMessages, validInput, thresholdCaution, 4);
            UIUtilities.removeInputError(inputError, errorMessages, validInput, thresholdWarning, 5);
        } else {
            validForm = false;
            validInput[4] = false;
            validInput[5] = false;
            UIUtilities.createInputError(inputError, errorMessages, thresholdCaution, TextConstants.CAUTION_WARNING, 218.0, horizontalPosition, 4);
            UIUtilities.createInputError(inputError, errorMessages, thresholdWarning, "", 0, 0, 5);
        }

        if (UIUtilities.compareThresholds(thresholdAdvisory, thresholdFailed)) {
            validInput[3] = true;
            validInput[6] = true;
            UIUtilities.removeInputError(inputError, errorMessages, validInput, thresholdAdvisory, 3);
            UIUtilities.removeInputError(inputError, errorMessages, validInput, thresholdFailed, 6);
        } else {
            validForm = false;
            validInput[3] = false;
            validInput[6] = false;
            UIUtilities.createInputError(inputError, errorMessages, thresholdAdvisory, TextConstants.ADVISORY_FAILED, 178.0, horizontalPosition, 3);
            UIUtilities.createInputError(inputError, errorMessages, thresholdFailed, "", 0, 0, 6);
        }
        if (UIUtilities.compareThresholds(thresholdCaution, thresholdFailed)) {
            validInput[4] = true;
            validInput[6] = true;
            UIUtilities.removeInputError(inputError, errorMessages, validInput, thresholdCaution, 4);
            UIUtilities.removeInputError(inputError, errorMessages, validInput, thresholdFailed, 6);
        } else {
            validForm = false;
            validInput[4] = false;
            validInput[6] = false;
            UIUtilities.createInputError(inputError, errorMessages, thresholdCaution, TextConstants.CAUTION_FAILED, 218.0, horizontalPosition, 4);
            UIUtilities.createInputError(inputError, errorMessages, thresholdFailed, "", 0, 0, 6);
        }
        if (UIUtilities.compareThresholds(thresholdWarning, thresholdFailed)) {
            validInput[5] = true;
            validInput[6] = true;
            UIUtilities.removeInputError(inputError, errorMessages, validInput, thresholdWarning, 5);
            UIUtilities.removeInputError(inputError, errorMessages, validInput, thresholdFailed, 6);
        } else {
            validForm = false;
            validInput[5] = false;
            validInput[6] = false;
            UIUtilities.createInputError(inputError, errorMessages, thresholdWarning, TextConstants.WARNING_FAILED, 258.0, horizontalPosition, 5);
            UIUtilities.createInputError(inputError, errorMessages, thresholdFailed, "", 0, 0, 6);
        }
        return validForm;
    }

    private void assetTypeNameValidation(String assetTypeNameValue, double horizontalPosition) {
        validForm = true;
        if (assetTypeNameValue.trim().isEmpty()) {
            validForm = false;
            validInput[0] = false;
            UIUtilities.createInputError(inputError, errorMessages, assetTypeName, TextConstants.EMPTY_FIELD_ERROR, 28.0, horizontalPosition, 0);
        } else if (assetTypeNameValue.length() > 50) {
            validForm = false;
            validInput[0] = false;
            UIUtilities.createInputError(inputError, errorMessages, assetTypeName, TextConstants.MAX_50_CHARACTERS_ERROR, 28.0, horizontalPosition, 0);
        } else {
            validInput[0] = true;
            UIUtilities.removeInputError(inputError, errorMessages, validInput, assetTypeName, 0);
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

        for (Model model : modelObservableList) {
            // Creating a Thumbnail element
            Pane modelPane = new Pane();
            modelPane.getStyleClass().add("modelPane");
            modelPane.setOnMouseClicked(mouseEvent -> {
                modelPanes.handleModelSelection(model, modelPane);
                modelSaveBtn.setDisable(false);
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
            rmseLabel.setLayoutY(239.0);
            rmsePane.setLayoutX(15.0);
            rmsePane.setLayoutY(243.0);
            rmseValue.setLayoutX(30.0);
            rmseValue.setLayoutY(265.0);
            evaluateModelBtn.setLayoutX(133.0);
            evaluateModelBtn.setLayoutY(243.0);

            modelPane.getChildren().add(modelNameLabel);
            modelPane.getChildren().add(modelDescriptionText);
            modelPane.getChildren().add(rmseLabel);
            modelPane.getChildren().add(rmsePane);
            modelPane.getChildren().add(rmseValue);
            modelPane.getChildren().add(evaluateModelBtn);

            modelPaneObservableList.add(modelPane);
        }
        modelPanes.setModelThumbnailsContainerPane(modelPaneObservableList, modelsThumbPane);
        modelPanes.highlightAssociatedModel(modelPaneObservableList, modelDAO.getModelIDAssociatedWithAssetType(assetType.getId()));
    }

    /**
     * Saves the association between the selected model and the current asset type in the database.
     * This function also set the retrain tag to true to indicate that the model chosen for the asset
     * type needs to be retrain. Finally, it labels all live assets of that asset type as updated.
     *
     * @author Jeremie
     */
    private void saveSelectedModelAssociation() {
        modelDAO.updateModelAssociatedWithAssetType(modelPanes.getSelectedModel().getModelID(), assetType.getId());
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
        modelObservableList = FXCollections.observableArrayList(modelDAO.getAllModelsForEvaluation(Integer.parseInt(assetType.getId())));
        for (Model model : modelObservableList) {
            model.setRMSE(String.valueOf(TextConstants.RMSEValueFormat.format(modelDAO.getLatestRMSE(model.getModelID(), Integer.parseInt(assetType.getId())))));
        }
        Timeline rmseTimeline = new Timeline(new KeyFrame(Duration.millis(3000), e ->
        {
            for (Model model : modelObservableList) {
                model.setRMSE(String.valueOf(TextConstants.RMSEValueFormat.format(modelDAO.getLatestRMSE(model.getModelID(), Integer.parseInt(assetType.getId())))));
            }
        }));

        rmseTimeline.setCycleCount(Animation.INDEFINITE); // loop forever
        rmseTimeline.play();
    }
}
