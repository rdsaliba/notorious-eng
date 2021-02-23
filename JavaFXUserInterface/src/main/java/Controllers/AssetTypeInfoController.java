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
import app.item.Model;
import app.item.Asset;
import external.AssetTypeDAOImpl;
import external.ModelDAOImpl;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import external.ModelDAOImpl;
import javafx.scene.input.MouseEvent;
import local.AssetDAOImpl;
import app.ModelController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import local.AssetDAOImpl;
import preprocessing.DataPrePreprocessorController;
import weka.core.Instances;
import preprocessing.DataPrePreprocessorController;
import rul.models.*;
import weka.classifiers.Classifier;
import weka.core.Instances;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class AssetTypeInfoController implements Initializable {
    private static final String RMSE = "RMSE";
    private static final String ALERT_HEADER = "Confirmation of asset type deletion";
    private static final String ALERT_CONTENT = "Are you sure you want to delete this asset type? \n " +
            "this will delete all the assets of this type";
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
    private Button selectBtn;
    @FXML
    private Button modelSaveBtn;
    private ObservableList<Model> models;
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
    private int trainSize = 0;
    private int testSize = 0;
    private Text[] errorMessages = new Text[7];
    private boolean[] validInput = new boolean[7];

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
                        evaluateModels(mouseEvent);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                });
            } catch (Exception e) {
            }
        }
        try {
            models = FXCollections.observableArrayList(modelDAO.getAllModels());
        } catch (Exception e) {
            e.printStackTrace();
        }
        generateThumbnails();
    }

    /**
     * Evaluates all models of a specific Asset Type
     *
     * @param mouseEvent is an event trigger to evlauate models
     * @author Talal
     */
    private void evaluateModels(MouseEvent mouseEvent)  {
        try {
            ArrayList<String> models = modelDAO.getListOfModels();
            trainDataset.setClassIndex(trainDataset.numAttributes() - 1);
            testDataset.setClassIndex(testDataset.numAttributes() - 1);


            for(int i=0;i<models.size();i++){
                switch (models.get(i)) {
                    case "Linear":
                        LinearRegressionModelImpl linearRegressionModelImpl = new LinearRegressionModelImpl();
                        Classifier model =  linearRegressionModelImpl.trainModel(trainDataset);
                        calculateEvaluation(model,trainDataset,testDataset,1);

                    case "LSTM":
                        ModelStrategy lstmm = new LSTMModelImpl();
                        Classifier lstm = lstmm.trainModel(trainDataset);
                        calculateEvaluation(lstm,trainDataset,testDataset,2);

                    case "RandomForest":
                        RandomForestModelImpl randomForestModel = new RandomForestModelImpl();
                        Classifier forestModel = randomForestModel.trainModel(trainDataset);
                        calculateEvaluation(forestModel,trainDataset,testDataset,3);

                    case "RandomCommittee":
                        RandomCommitteeModelImpl randomCommitteeModel = new RandomCommitteeModelImpl();
                        Classifier randomCommittee = randomCommitteeModel.trainModel(trainDataset);
                        calculateEvaluation(randomCommittee,trainDataset,testDataset, 4);

                    case "RandomSubSpace":
                        RandomSubSpaceModelImpl randomSubSpaceModel = new RandomSubSpaceModelImpl();
                        Classifier randomSubSpace = randomSubSpaceModel.trainModel(trainDataset);
                        calculateEvaluation(randomSubSpace,trainDataset,testDataset, 5);

                    case "AdditiveRegression":
                        AdditiveRegressionModelImpl additiveRegressionModel = new AdditiveRegressionModelImpl();
                        Classifier additiveRegression = additiveRegressionModel.trainModel(trainDataset);
                        calculateEvaluation(additiveRegression,trainDataset,testDataset, 6);

                    case "SMOReg":
                        SMORegModelImpl smoRegModel = new SMORegModelImpl();
                        Classifier smOreg = smoRegModel.trainModel(trainDataset);
                        calculateEvaluation(smOreg,trainDataset,testDataset, 7);

                    case "MultilayerPerceptron":
                        MultilayerPerceptronModelImpl multilayerPerceptronModel = new MultilayerPerceptronModelImpl();
                        Classifier multilayerPerceptron = multilayerPerceptronModel.trainModel(trainDataset);
                        calculateEvaluation(multilayerPerceptron,trainDataset,testDataset, 8);

                    default:
                        model = null;
                }
            }
        }
        catch (Exception e){trainValue.setText(e.getMessage());}
    }

    /**
     * Calculates the rmse for a model and and the value to be stored in the databse
     *
     * @param model to be evaluated,
     * @param train training dataset,
     * @param test testing dataset,
     * @param modelId model id in the database
     * @author Talal
     */
    public void calculateEvaluation(Classifier model, Instances train, Instances test, int modelId) throws Exception {
        ModelEvaluation modelEvaluation = new ModelEvaluation(model, train, test);
        double rmse =modelEvaluation.evaluateTrainWithTest();
        modelDAO.updateRMSE(rmse, modelId , Integer.parseInt(assetType.getId()));

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

    public void generateThumbnails() {
        ObservableList<Pane> boxes = FXCollections.observableArrayList();

        for (Model model : models) {

            Pane pane = new Pane();

            pane.getStyleClass().add("modelPane");
            Text modelNameText = new Text(model.getModelName());
            Text modelDescriptionText = new Text(model.getDescription());
            Text RMSEText = new Text(RMSE + ": ");

            modelNameText.setId("modelNameText");
            modelDescriptionText.setId("modelDescriptionText");
            RMSEText.setId("RMSEText");


            modelNameText.setLayoutX(14.0);
            modelNameText.setLayoutY(28.0);
            modelDescriptionText.setLayoutX(14.0);
            modelDescriptionText.setLayoutY(60.0);
            RMSEText.setLayoutX(14.0);
            RMSEText.setLayoutY(100.0);

            pane.getChildren().add(modelNameText);
            pane.getChildren().add(modelDescriptionText);
            pane.getChildren().add(RMSEText);

            boxes.add(pane);
        }
    }

    public void selectModel() {
//        selectBtn.setOnMouseClicked(mouseEvent -> modelDAO.updateModelForAssetType(modelID, assetType.getId());
    }

    public void saveSelection() {
//        modelSaveBtn.setOnMouseClicked(mouseEvent -> modelDAOImpl.);
    }
}
