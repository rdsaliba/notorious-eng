/*
  This Controller is responsible for handling the information view
  of an asset type. It handles the editing of asset types
  and saving them to the database.

  @author Jeff, Paul, Roy, Najim
  @last_edit 02/7/2020
 */
package controllers;

import utilities.AssetTypeList;
import utilities.CustomDialog;
import utilities.TextConstants;
import utilities.UIUtilities;
import app.ModelController;
import external.AssetTypeDAOImpl;
import external.ModelDAOImpl;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import local.AssetDAOImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import preprocessing.DataPrePreprocessorController;
import rul.models.*;
import weka.classifiers.Classifier;
import weka.core.Instances;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class AssetTypeInfoController implements Initializable {
    @FXML
    Tab modelTab;
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
    private Button modelEvaluateBtn;
    private UIUtilities uiUtilities;
    private AssetTypeList assetType;
    private AssetTypeList originalAssetType;
    private AssetTypeDAOImpl assetTypeDAO;
    private ModelDAOImpl modelDAO;
    private AssetDAOImpl assetDAO;
    private ModelController modelController;
    private Instances trainDataset;
    private Instances testDataset;
    private DataPrePreprocessorController prePreprocessorController;

    private final Text[] errorMessages = new Text[7];
    private final boolean[] validInput = new boolean[7];

    public int trainSize = 0;
    public int testSize = 0;

    static Logger logger = LoggerFactory.getLogger(AssetInfoController.class);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        uiUtilities = new UIUtilities();
        assetTypeDAO = new AssetTypeDAOImpl();
        modelDAO = new ModelDAOImpl();
        assetDAO = new AssetDAOImpl();
        modelController = ModelController.getInstance();
        prePreprocessorController = DataPrePreprocessorController.getInstance();
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
            logger.error("NumberFormatException error inside initData");
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

        modelTab.setOnSelectionChanged(event -> modelsButtonPressed());

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

    private void modelsButtonPressed() {

        if (modelTab.getId().equals("modelTab")) {
            int nbOfAssets = assetDAO.getAssetsFromAssetTypeID(Integer.parseInt(assetType.getId())).size();
            trainSlider.setMax(nbOfAssets);
            trainValue.setText(String.valueOf(trainSlider.getValue()));
            testSlider.setMax(nbOfAssets);
            testValue.setText(String.valueOf(testSlider.getValue()));

            trainSlider.valueProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                    trainValue.setText(Integer.toString((int) trainSlider.getValue()));
                    testSlider.setMax(nbOfAssets - (int) trainSlider.getValue());
                    trainSize = (int) trainSlider.getValue();
                }
            });
            testSlider.valueProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                    testValue.setText(Integer.toString((int) testSlider.getValue()));
                    trainSlider.setMax(nbOfAssets - (int) testSlider.getValue());
                    testSize = (int) testSlider.getValue();
                }
            });

            try {
                modelEvaluateBtn.setOnMouseClicked(mouseEvent -> {
                    try {
                        int from = (int) trainSlider.getValue() + 1;
                        int to = (int) trainSlider.getValue() + 1 + (int) testSlider.getValue();
                        trainDataset = DataPrePreprocessorController.getInstance().addRULCol(modelController.createInstancesFromAssets(assetDAO.getAssetsFromAssetTypeID(Integer.parseInt(assetType.getId())).subList(0, (int) trainSlider.getValue() - 1)));
                        testDataset = DataPrePreprocessorController.getInstance().addRULCol(modelController.createInstancesFromAssets(assetDAO.getAssetsFromAssetTypeID(Integer.parseInt(assetType.getId())).subList(from, to - 1)));
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                    try {
                        evaluateModels();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                });
            } catch (Exception e) {
                logger.error("There was an error pressing the button in modelsButtonPressed()");
            }
        }
    }

    /**
     * Evaluates all models of a specific Asset Type
     *
     * @author Talal
     */
    private void evaluateModels() {
        try {
            ArrayList<String> models = modelDAO.getListOfModels();
            trainDataset.setClassIndex(trainDataset.numAttributes() - 1);
            testDataset.setClassIndex(testDataset.numAttributes() - 1);


            for (String s : models) {
                switch (s) {
                    case "Linear":
                        LinearRegressionModelImpl linearRegressionModelImpl = new LinearRegressionModelImpl();
                        Classifier model = linearRegressionModelImpl.trainModel(trainDataset);
                        calculateEvaluation(model, trainDataset, testDataset, 1);
                        break;

                    case "LSTM":
                        ModelStrategy lstmm = new LSTMModelImpl();
                        Classifier lstm = lstmm.trainModel(trainDataset);
                        calculateEvaluation(lstm, trainDataset, testDataset, 2);
                        break;

                    case "RandomForest":
                        RandomForestModelImpl randomForestModel = new RandomForestModelImpl();
                        Classifier forestModel = randomForestModel.trainModel(trainDataset);
                        calculateEvaluation(forestModel, trainDataset, testDataset, 3);
                        break;

                    case "RandomCommittee":
                        RandomCommitteeModelImpl randomCommitteeModel = new RandomCommitteeModelImpl();
                        Classifier randomCommittee = randomCommitteeModel.trainModel(trainDataset);
                        calculateEvaluation(randomCommittee, trainDataset, testDataset, 4);
                        break;

                    case "RandomSubSpace":
                        RandomSubSpaceModelImpl randomSubSpaceModel = new RandomSubSpaceModelImpl();
                        Classifier randomSubSpace = randomSubSpaceModel.trainModel(trainDataset);
                        calculateEvaluation(randomSubSpace, trainDataset, testDataset, 5);
                        break;

                    case "AdditiveRegression":
                        AdditiveRegressionModelImpl additiveRegressionModel = new AdditiveRegressionModelImpl();
                        Classifier additiveRegression = additiveRegressionModel.trainModel(trainDataset);
                        calculateEvaluation(additiveRegression, trainDataset, testDataset, 6);
                        break;

                    case "SMOReg":
                        SMORegModelImpl smoRegModel = new SMORegModelImpl();
                        Classifier smOreg = smoRegModel.trainModel(trainDataset);
                        calculateEvaluation(smOreg, trainDataset, testDataset, 7);
                        break;

                    case "MultilayerPerceptron":
                        MultilayerPerceptronModelImpl multilayerPerceptronModel = new MultilayerPerceptronModelImpl();
                        Classifier multilayerPerceptron = multilayerPerceptronModel.trainModel(trainDataset);
                        calculateEvaluation(multilayerPerceptron, trainDataset, testDataset, 8);
                        break;

                    default:
                        break; // This behavior needs to be adjusted
                }
            }
        } catch (Exception e) {
            trainValue.setText(e.getMessage());
        }
    }

    /**
     * Calculates the rmse for a model and and the value to be stored in the databse
     *
     * @param model   to be evaluated,
     * @param train   training dataset,
     * @param test    testing dataset,
     * @param modelId model id in the database
     * @author Talal
     */
    public void calculateEvaluation(Classifier model, Instances train, Instances test, int modelId) throws Exception {
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
     * Send the asset ID to the Database class in order for it to be deleted.
     *
     * @author Paul
     */
    public void deleteAssetType() {
        assetTypeDAO.deleteAssetTypeByID(assetType.getId());
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
}
