/*
  This Controller is responsible for handling the information view
  of an asset type. It handles the editing of asset types
  and saving them to the database.

  @author Jeff, Paul, Roy, Najim
  @last_edit 02/7/2020
 */
package controllers;

import app.ModelController;
import app.item.Asset;
import app.item.Model;
import external.AssetDAOImpl;
import external.AssetTypeDAOImpl;
import external.ModelDAOImpl;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import preprocessing.DataPrePreprocessorController;
import rul.models.*;
import utilities.*;
import weka.classifiers.Classifier;
import weka.core.Instances;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
    private Button assetMenuBtn;
    @FXML
    private Button assetTypeMenuBtn;
    @FXML
    private Button infoSaveBtn;
    @FXML
    private Button infoDeleteBtn;
    @FXML
    private Button backBtn;
    @FXML
    private Button exitMenuBtn;
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
    private ImageView assetTypeImageView;
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
    private int associatedModelID;
    private UIUtilities uiUtilities;
    private AssetTypeList assetType;
    private AssetTypeList originalAssetType;
    private List<Asset> assetsList;
    private AssetTypeDAOImpl assetTypeDAO;
    private ModelDAOImpl modelDAO;
    private AssetDAOImpl assetDAO;
    private ModelController modelController;
    private Instances trainDataset;
    private Instances testDataset;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        uiUtilities = new UIUtilities();
        assetTypeDAO = new AssetTypeDAOImpl();
        modelDAO = new ModelDAOImpl();
        assetDAO = new AssetDAOImpl();
        modelController = ModelController.getInstance();
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
        associatedModelID = modelDAO.getModelIDFromAssetTypeID(assetType.getId());
        associatedModelLabel.setText(modelDAO.getModelNameFromAssetTypeID(assetType.getId()));
        try {
            thresholdOK.setText(TextConstants.ThresholdValueFormat.format(Double.parseDouble(assetType.getValueOk())));
            thresholdAdvisory.setText(TextConstants.ThresholdValueFormat.format(Double.parseDouble(assetType.getValueAdvisory())));
            thresholdCaution.setText(TextConstants.ThresholdValueFormat.format(Double.parseDouble(assetType.getValueCaution())));
            thresholdWarning.setText(TextConstants.ThresholdValueFormat.format(Double.parseDouble(assetType.getValueWarning())));
            thresholdFailed.setText(TextConstants.ThresholdValueFormat.format(Double.parseDouble(assetType.getValueFailed())));
        } catch (NumberFormatException e) {
            logger.error("NumberFormatException error inside initData");
            logger.error("Exception initData(): ", e);
        }
    }

    /**
     * Attaches events to elements in the scene.
     *
     * @author Najim, Paul
     * Edit: added all the text proprety listeners and text formaters for all the fields
     */
    public void attachEvents() {
        // Change scenes to Assets.fxml
        backBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, TextConstants.ASSET_TYPE_LIST_SCENE));
        //Attach ability to close program
        exitMenuBtn.setOnMouseClicked(mouseEvent -> Platform.exit());

        modelTab.setOnSelectionChanged(event -> attachEventsModelTab());

        // Change scenes to Assets.fxml
        assetMenuBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, TextConstants.ASSETS_SCENE));
        //Attach link to assetTypeMenuBtn to go to Utilities.AssetTypeList.fxml
        assetTypeMenuBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, TextConstants.ASSET_TYPE_LIST_SCENE));
        infoDeleteBtn.setOnMouseClicked(mouseEvent -> CustomDialog.systemTypeInfoControllerDialog(mouseEvent, assetType.getId()));

        infoSaveBtn.setDisable(true);
        infoSaveBtn.setOnMouseClicked(mouseEvent -> {
            if (formInputValidation()) {
                assetTypeDAO.updateAssetType(assetType.toAssetType());
                uiUtilities.changeScene(mouseEvent, TextConstants.ASSET_TYPE_LIST_SCENE);
            }
        });

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

    private void attachEventsModelTab() {
        modelSaveBtn.setDisable(true);
        modelSaveBtn.setOnMouseClicked(mouseEvent -> {
            saveSelectedModelAssociation();
            uiUtilities.changeScene(mouseEvent, TextConstants.ASSET_TYPE_LIST_SCENE);
        });
        evaluateAllModelsBtn.setDisable(true);
        evaluateButtons = new ArrayList<>();
        evaluateButtons.add(evaluateAllModelsBtn);

        if (modelTab.getId().equals("modelTab")) {
            int nbOfAssets = assetDAO.getArchivedAssetsFromAssetTypeID(Integer.parseInt(assetType.getId())).size();
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
            try {
                evaluateAllModelsBtn.setOnMouseClicked(mouseEvent -> evaluateAllModels());
            } catch (Exception e) {
                logger.error("Exception for evaluateAllModelsBtn.setOnMouseClicked(), e");
            }
        }
        try {
            modelObservableList = FXCollections.observableArrayList(modelDAO.getAllModels());
        } catch (Exception e) {
            logger.error("Exception for modelObservableList, e");
        }
        modelsThumbPane.getChildren().clear();
        generateThumbnails();
        trainSlider.setOnMouseClicked(mouseEvent -> enableEvaluation(evaluateButtons));
        testSlider.setOnMouseClicked(mouseEvent -> enableEvaluation(evaluateButtons));
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
     * This function evaluates all models for the current asset type
     *
     * @author Talal, Jeremie
     */
    private void evaluateAllModels() {
        setTrainAndTestInstances();
        try {
            evaluateModelClassifiers(modelObservableList);
        } catch (Exception exception) {
            logger.error("Exception in evaluating all models ", exception);
        }
    }

    /**
     * This function evaluates a selected model for the current asset type
     *
     * @author Talal, Jeremie
     */
    private void evaluateSelectedModel(Model selectedModel) {
        setTrainAndTestInstances();
        ObservableList<Model> selectedModelToEvaluateList = FXCollections.observableArrayList();
        selectedModelToEvaluateList.add(selectedModel);
        try {
            evaluateModelClassifiers(selectedModelToEvaluateList);
        } catch (Exception exception) {
            logger.error("Exception in evaluating a model: ", exception);
        }
        selectedModelToEvaluateList.clear();
    }

    /**
     * This function set and create the Train and Test instances based on the values given on the
     * test and train sliders. Those values are being chosen by the user.
     *
     * @author Talal, Jeremie
     */
    private void setTrainAndTestInstances() {
        try {
            int from = trainSize + 1;
            int to = trainSize + 1 + testSize;
            trainDataset = DataPrePreprocessorController.getInstance().addRULCol(modelController.createInstancesFromAssets(assetDAO.getArchivedAssetsFromAssetTypeID(Integer.parseInt(assetType.getId())).subList(0, trainSize - 1)));
            testDataset = DataPrePreprocessorController.getInstance().addRULCol(modelController.createInstancesFromAssets(assetDAO.getArchivedAssetsFromAssetTypeID(Integer.parseInt(assetType.getId())).subList(from, to - 1)));
        } catch (Exception exception) {
            logger.error("Exception setting anf training test instances ", exception);
        }
    }

    /**
     * This function generates the classifier of all models and Evaluates all models of a specific Asset Type
     *
     * @author Talal
     */
    private void evaluateModelClassifiers(ObservableList<Model> modelListToEvaluate) {
        try {
            trainDataset.setClassIndex(trainDataset.numAttributes() - 1);
            testDataset.setClassIndex(testDataset.numAttributes() - 1);

            for (Model model : modelListToEvaluate) {
                switch (model.getModelName()) {
                    case "Linear":
                        LinearRegressionModelImpl linearRegressionModelImpl = new LinearRegressionModelImpl();
                        Classifier linearClf = linearRegressionModelImpl.trainModel(trainDataset);
                        calculateRMSEEvaluation(linearClf, trainDataset, testDataset, 1);
                        break;
                    case "LSTM":
                        ModelStrategy lstmModel = new LSTMModelImpl();
                        Classifier lstmClf = lstmModel.trainModel(trainDataset);
                        calculateRMSEEvaluation(lstmClf, trainDataset, testDataset, 2);
                        break;
                    case "RandomForest":
                        RandomForestModelImpl randomForestModel = new RandomForestModelImpl();
                        Classifier randomForestClf = randomForestModel.trainModel(trainDataset);
                        calculateRMSEEvaluation(randomForestClf, trainDataset, testDataset, 3);
                        break;
                    case "RandomCommittee":
                        RandomCommitteeModelImpl randomCommitteeModel = new RandomCommitteeModelImpl();
                        Classifier randomCommitteeClf = randomCommitteeModel.trainModel(trainDataset);
                        calculateRMSEEvaluation(randomCommitteeClf, trainDataset, testDataset, 4);
                        break;
                    case "RandomSubSpace":
                        RandomSubSpaceModelImpl randomSubSpaceModel = new RandomSubSpaceModelImpl();
                        Classifier randomSubSpaceClf = randomSubSpaceModel.trainModel(trainDataset);
                        calculateRMSEEvaluation(randomSubSpaceClf, trainDataset, testDataset, 5);
                        break;
                    case "AdditiveRegression":
                        AdditiveRegressionModelImpl additiveRegressionModel = new AdditiveRegressionModelImpl();
                        Classifier additiveRegressionClf = additiveRegressionModel.trainModel(trainDataset);
                        calculateRMSEEvaluation(additiveRegressionClf, trainDataset, testDataset, 6);
                        break;
                    case "SMOReg":
                        SMORegModelImpl smoRegModel = new SMORegModelImpl();
                        Classifier smoRegClf = smoRegModel.trainModel(trainDataset);
                        calculateRMSEEvaluation(smoRegClf, trainDataset, testDataset, 7);
                        break;
                    case "MultilayerPerceptron":
                        MultilayerPerceptronModelImpl multilayerPerceptronModel = new MultilayerPerceptronModelImpl();
                        Classifier multilayerPerceptronClf = multilayerPerceptronModel.trainModel(trainDataset);
                        calculateRMSEEvaluation(multilayerPerceptronClf, trainDataset, testDataset, 8);
                        break;
                    default:
                        break;
                }
            }
            updateThumbnails();
        } catch (Exception e) {
            trainValue.setText(e.getMessage());
            logger.error("Exception in evaluateModelClassifiers(): ", e);
        }
    }

    /**
     * Calculates the rmse for a model and and the value to be stored in the database
     *
     * @param model   to be evaluated,
     * @param train   training dataset,
     * @param test    testing dataset,
     * @param modelId model id in the database
     * @author Talal
     */
    public void calculateRMSEEvaluation(Classifier model, Instances train, Instances test, int modelId) throws Exception {
        ModelEvaluation modelEvaluation = new ModelEvaluation(model, train, test);
        double rmse = modelEvaluation.evaluateTrainWithTest();
        modelDAO.updateRMSE(rmse, modelId, Integer.parseInt(assetType.getId()));
    }

    /**
     * Handle the text change of the user fields to turn on or off the save functionality
     *
     * @author Paul
     */
    private boolean handleTextChange(String newText, String field) {
        if ((field).equals(originalAssetType.getName()) || field.equals(originalAssetType.getDescription())) {
            if (!newText.isEmpty() && !newText.equals(field)) {
                infoSaveBtn.setDisable(false);
                infoSaveBtn.getStyleClass().clear();
                infoSaveBtn.getStyleClass().add("infoSaveTrue");
                return true;
            } else {
                infoSaveBtn.setDisable(true);
                infoSaveBtn.getStyleClass().clear();
                infoSaveBtn.getStyleClass().add("infoSaveFalse");
                return false;
            }
        } else if (!newText.isEmpty() && !field.equals("-") && Double.parseDouble(newText) == Double.parseDouble(field)) {
            infoSaveBtn.setDisable(true);
            infoSaveBtn.getStyleClass().clear();
            infoSaveBtn.getStyleClass().add("infoSaveFalse");
            return false;
        } else {
            infoSaveBtn.setDisable(false);
            infoSaveBtn.getStyleClass().clear();
            infoSaveBtn.getStyleClass().add("infoSaveTrue");
            return true;
        }
    }

    /**
     * Changes the Image depending on the Asset Type.
     *
     * @author Najim
     */
    public void setImage(String typeName) {
        if (typeName.contains("Engine")) {
            assetTypeImageView.setImage(new Image("imgs/asset_type_engine.png"));
        } else {
            assetTypeImageView.setImage(new Image("imgs/unknown_asset_type.png"));
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
            Text rmseLabel = new Text(RMSE + ": ");
            Text rmseValue = new Text();
            String rmseValueObject = modelDAO.getGetModelEvaluation(model.getModelID(), assetType.getId());
            SimpleStringProperty observableRMSEValue = new SimpleStringProperty(rmseValueObject);
            rmseValue.textProperty().bind(observableRMSEValue);

            Button evaluateModelBtn = new Button();
            evaluateModelBtn.setText("Evaluate");
            evaluateModelBtn.setDisable(true);
            evaluateButtons.add(evaluateModelBtn);
            evaluateModelBtn.setOnMouseClicked(mouseEvent -> evaluateSelectedModel(model));

            //Setting IDs for the elements
            modelNameLabel.setId("modelNameLabel");
            modelDescriptionText.setId("modelDescriptionText");
            rmseLabel.setId("RMSELabel");
            rmseValue.setId("RMSEValue");
            evaluateModelBtn.setId("SelectBtn");

            //Setting the Layout of the elements
            modelNameLabel.setLayoutX(14.0);
            modelNameLabel.setLayoutY(28.0);
            modelDescriptionText.setLayoutX(14.0);
            modelDescriptionText.setLayoutY(60.0);
            rmseLabel.setLayoutX(14.0);
            rmseLabel.setLayoutY(100.0);
            rmseValue.setLayoutX(50.0);
            rmseValue.setLayoutY(100.0);
            evaluateModelBtn.setLayoutX(150.0);
            evaluateModelBtn.setLayoutY(75.0);

            modelPane.getChildren().add(modelNameLabel);
            modelPane.getChildren().add(modelDescriptionText);
            modelPane.getChildren().add(rmseLabel);
            modelPane.getChildren().add(rmseValue);
            modelPane.getChildren().add(evaluateModelBtn);

            modelPaneObservableList.add(modelPane);
        }
        modelPanes.setModelThumbnailsContainerPane(modelPaneObservableList, modelsThumbPane);
        modelPanes.highlightAssociatedModel(modelPaneObservableList, associatedModelID);
    }

    private void updateThumbnails() {
        modelsThumbPane.getChildren().removeAll();
        generateThumbnails();
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
}
