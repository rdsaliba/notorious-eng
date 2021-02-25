/*
  This Controller is responsible for handling the information view
  of an asset type. It handles the editing of asset types
  and saving them to the database.

  @author Jeff, Paul, Roy, Najim
  @last_edit 02/7/2020
 */
package Controllers;

import Utilities.AssetTypeList;
import Utilities.CustomDialog;
import Utilities.TextConstants;
import Utilities.UIUtilities;
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
import preprocessing.DataPrePreprocessorController;
import rul.models.*;
import weka.classifiers.Classifier;
import weka.core.Instances;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class AssetTypeInfoController implements Initializable {
    private static final String RMSE = "RMSE";
    private final Text[] errorMessages = new Text[7];
    private final boolean[] validInput = new boolean[7];
    @FXML
    Tab modelTab;
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
    private ObservableList<Model> modelObservableList;
    private Model selectedModel;
    private int selectedModelIndex;
    private UIUtilities uiUtilities;
    private AssetTypeList assetType;
    private AssetTypeList originalAssetType;
    private ArrayList<Asset> assetsList;
    private AssetTypeDAOImpl assetTypeDAO;
    private ModelDAOImpl modelDAO;
    private AssetDAOImpl assetDAO;
    private ModelController modelController;
    private Instances trainDataset;
    private Instances testDataset;
    private int trainSize = 0;
    private int testSize = 0;

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
            exception.printStackTrace();
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
        try {
            thresholdOK.setText(TextConstants.ThresholdValueFormat.format(Double.parseDouble(assetType.getValueOk())));
            thresholdAdvisory.setText(TextConstants.ThresholdValueFormat.format(Double.parseDouble(assetType.getValueAdvisory())));
            thresholdCaution.setText(TextConstants.ThresholdValueFormat.format(Double.parseDouble(assetType.getValueCaution())));
            thresholdWarning.setText(TextConstants.ThresholdValueFormat.format(Double.parseDouble(assetType.getValueWarning())));
            thresholdFailed.setText(TextConstants.ThresholdValueFormat.format(Double.parseDouble(assetType.getValueFailed())));
        } catch (NumberFormatException e) {
            e.printStackTrace();
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

        modelSaveBtn.setDisable(true);
        modelSaveBtn.setOnMouseClicked(mouseEvent -> saveSelectedModelAssociation());

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
        if (modelTab.getId().equals("modelTab")) {
            int nbOfAssets = assetDAO.getAssetsFromAssetTypeID(Integer.parseInt(assetType.getId())).size();
            trainSlider.setMax(nbOfAssets);
            trainValue.setText(String.valueOf(trainSlider.getValue()));
            testSlider.setMax(nbOfAssets);
            testValue.setText(String.valueOf(testSlider.getValue()));

            trainSlider.valueProperty().addListener((observableValue, number, t1) -> {
                trainValue.setText(Integer.toString((int) trainSlider.getValue()));
                testSlider.setMax(nbOfAssets - (int) trainSlider.getValue());
                trainSize = (int) trainSlider.getValue();
            });
            testSlider.valueProperty().addListener((observableValue, number, t1) -> {
                testValue.setText(Integer.toString((int) testSlider.getValue()));
                trainSlider.setMax(nbOfAssets - (int) testSlider.getValue());
                testSize = (int) testSlider.getValue();
            });

            try {
                evaluateAllModelsBtn.setOnMouseClicked(mouseEvent -> evaluateAllModels());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            modelObservableList = FXCollections.observableArrayList(modelDAO.getAllModels());
        } catch (Exception e) {
            e.printStackTrace();
        }
        modelsThumbPane.getChildren().clear();
        generateThumbnails();
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
            exception.printStackTrace();
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
            exception.printStackTrace();
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
        if (trainSlider.getValue() == 0 || testSlider.getValue() == 0) {
            //TODO error message
            System.out.print("The number of train and test assets as to be higher than 0");
        } else {
            try {
                int from = trainSize + 1;
                int to = trainSize + 1 + testSize;
                trainDataset = DataPrePreprocessorController.getInstance().addRULCol(modelController.createInstancesFromAssets(assetDAO.getAssetsFromAssetTypeID(Integer.parseInt(assetType.getId())).subList(0, trainSize - 1)));
                testDataset = DataPrePreprocessorController.getInstance().addRULCol(modelController.createInstancesFromAssets(assetDAO.getAssetsFromAssetTypeID(Integer.parseInt(assetType.getId())).subList(from, to - 1)));
            } catch (Exception exception) {
                exception.printStackTrace();
            }
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

                    case "LSTM":
                        ModelStrategy lstmModel = new LSTMModelImpl();
                        Classifier lstmClf = lstmModel.trainModel(trainDataset);
                        calculateRMSEEvaluation(lstmClf, trainDataset, testDataset, 2);

                    case "RandomForest":
                        RandomForestModelImpl randomForestModel = new RandomForestModelImpl();
                        Classifier randomForestClf = randomForestModel.trainModel(trainDataset);
                        calculateRMSEEvaluation(randomForestClf, trainDataset, testDataset, 3);

                    case "RandomCommittee":
                        RandomCommitteeModelImpl randomCommitteeModel = new RandomCommitteeModelImpl();
                        Classifier randomCommitteeClf = randomCommitteeModel.trainModel(trainDataset);
                        calculateRMSEEvaluation(randomCommitteeClf, trainDataset, testDataset, 4);

                    case "RandomSubSpace":
                        RandomSubSpaceModelImpl randomSubSpaceModel = new RandomSubSpaceModelImpl();
                        Classifier randomSubSpaceClf = randomSubSpaceModel.trainModel(trainDataset);
                        calculateRMSEEvaluation(randomSubSpaceClf, trainDataset, testDataset, 5);

                    case "AdditiveRegression":
                        AdditiveRegressionModelImpl additiveRegressionModel = new AdditiveRegressionModelImpl();
                        Classifier additiveRegressionClf = additiveRegressionModel.trainModel(trainDataset);
                        calculateRMSEEvaluation(additiveRegressionClf, trainDataset, testDataset, 6);

                    case "SMOReg":
                        SMORegModelImpl smoRegModel = new SMORegModelImpl();
                        Classifier Clf = smoRegModel.trainModel(trainDataset);
                        calculateRMSEEvaluation(Clf, trainDataset, testDataset, 7);

                    case "MultilayerPerceptron":
                        MultilayerPerceptronModelImpl multilayerPerceptronModel = new MultilayerPerceptronModelImpl();
                        Classifier multilayerPerceptronClf = multilayerPerceptronModel.trainModel(trainDataset);
                        calculateRMSEEvaluation(multilayerPerceptronClf, trainDataset, testDataset, 8);

                    default:
                        break;
                }
            }
        } catch (Exception e) {
            trainValue.setText(e.getMessage());
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
        boolean validForm = true;

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

    /**
     * Generates the thumbnails for each model that exist in the database. This function will display
     * on each thumbnail: the name of the model, its description, a button to generate the evaluation
     * of the model for that asset type as well as the the RMSE value generated from the evaluation.
     *
     * @author Jeremie
     */
    private void generateThumbnails() {
        ObservableList<Pane> boxes = FXCollections.observableArrayList();

        for (Model model : modelObservableList) {
            // Creating a Thumbnail element
            Pane pane = new Pane();
            pane.getStyleClass().add("modelPane");
            pane.setOnMouseClicked(mouseEvent -> handleModelSelection(model, pane));

            // Generating items to display for the Thumbnail
            Text modelNameLabel = new Text(model.getModelName());
            Text modelDescriptionText = new Text(model.getDescription());
            Text RMSELabel = new Text(RMSE + ": ");
            Text RMSEValue = new Text();
            SimpleStringProperty observableRMSEValue = new SimpleStringProperty(modelDAO.getGetModelEvaluation(model.getModelID(), assetType.getId()));
            RMSEValue.textProperty().bind(observableRMSEValue);

            Button evaluateModelBtn = new Button();
            evaluateModelBtn.setText("Evaluate");
            evaluateModelBtn.setOnMouseClicked(mouseEvent -> evaluateSelectedModel(model));

            //Setting IDs for the elements
            modelNameLabel.setId("modelNameLabel");
            modelDescriptionText.setId("modelDescriptionText");
            RMSELabel.setId("RMSELabel");
            RMSEValue.setId("RMSEValue");
            evaluateModelBtn.setId("SelectBtn");

            //Setting the Layout of the elements
            modelNameLabel.setLayoutX(14.0);
            modelNameLabel.setLayoutY(28.0);
            modelDescriptionText.setLayoutX(14.0);
            modelDescriptionText.setLayoutY(60.0);
            RMSELabel.setLayoutX(14.0);
            RMSELabel.setLayoutY(100.0);
            RMSEValue.setLayoutX(50.0);
            RMSEValue.setLayoutY(100.0);
            evaluateModelBtn.setLayoutX(150.0);
            evaluateModelBtn.setLayoutY(75.0);

            pane.getChildren().add(modelNameLabel);
            pane.getChildren().add(modelDescriptionText);
            pane.getChildren().add(RMSELabel);
            pane.getChildren().add(RMSEValue);
            pane.getChildren().add(evaluateModelBtn);

            boxes.add(pane);
        }
        setModelThumbnailsPane(boxes);
    }

    private void setModelThumbnailsPane(ObservableList<Pane> boxes) {
        modelsThumbPane.setPrefWidth((300.0 + modelsThumbPane.getHgap()) * (boxes.size()) + (2 * modelsThumbPane.getHgap()));
        modelsThumbPane.getChildren().addAll(boxes);
        modelsThumbPane.setOnMouseClicked(mouseEvent -> handleModelSelectionChange(boxes));
    }

    private void handleModelSelectionChange(ObservableList<Pane> boxes) {
        for (int i = 0; i < boxes.size(); i++) {
            if ((i + 1) != selectedModelIndex) {
                boxes.get(i).setStyle("-fx-border-color: transparent");
            }
        }
    }

    private void handleModelSelection(Model model, Pane pane) {
        pane.setStyle("-fx-border-color: red");
        selectedModel = model;
        selectedModelIndex = Integer.parseInt(model.getModelID());
        modelSaveBtn.setDisable(false);
    }

    /**
     * Saves the association between the selected model and the current asset type in the database.
     * This function also set the retrain tag to true to indicate that the model chosen for the asset
     * type needs to be retrain. Finally, it labels all live assets of that asset type as updated.
     *
     * @author Jeremie
     */
    private void saveSelectedModelAssociation() {
        modelDAO.updateModelAssociatedWithAssetType(selectedModel.getModelID(), assetType.getId());
        modelDAO.setModelToTrain(assetType.getId());
        updateAssetsForSelectedModelAssociation(assetType.getId());
    }

    /**
     * This function updates all live assets for the current asset type by setting their status as
     * updated. This means that their RUl needs to be re-evaluated.
     *
     * @param AssetTypeID Is the Asset type ID of the current AssetType
     * @author Jeremie
     */
    private void updateAssetsForSelectedModelAssociation(String AssetTypeID) {
        assetsList = assetDAO.getLiveAssetsFromAssetTypeID(AssetTypeID);
        for (Asset asset : assetsList) {
            assetDAO.setAssetToBeUpdated(asset.getId());
        }
    }
}
