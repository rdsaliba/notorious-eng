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
import app.item.EvaluateModel;
import app.item.EvaluationList;
import app.item.Model;
import external.AssetDAOImpl;
import external.AssetTypeDAOImpl;
import external.ModelDAOImpl;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import javafx.util.Duration;
import preprocessing.DataPrePreprocessorController;
import rul.assessment.AssessmentController;
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
    private Button generateAllModelsBtn;
    @FXML
    private Button modelSaveBtn;
    private ObservableList<Model> modelObservableList;
    private Model selectedModel;
    private UIUtilities uiUtilities;
    private AssetTypeList assetType;
    private AssetTypeList originalAssetType;
    private ArrayList<Asset> assetsList;
    private AssetTypeDAOImpl assetTypeDAO;
    private ModelDAOImpl modelDAO;
    private AssetDAOImpl assetDAO;
    public static ArrayList<EvaluateModel> evaluateModels;
    private ModelController modelController;
    private Timeline rmseTimeline;
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
        evaluateModels = new ArrayList<EvaluateModel>();
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
        updateRMSE();
    }

    /**
     * Attaches events to elements in the scene.
     *
     * @author Najim, Paul
     * Edit: added all the text proprety listeners and text formaters for all the fields
     */
    public void attachEvents() {
        try {
            modelObservableList = FXCollections.observableArrayList(modelDAO.getAllModels());
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Change scenes to Assets.fxml
        backBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, TextConstants.ASSET_TYPE_LIST_SCENE));
        //Attach ability to close program
        exitMenuBtn.setOnMouseClicked(mouseEvent -> Platform.exit());

        modelTab.setOnSelectionChanged(event -> attachEventsModelTab());
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
                generateAllModelsBtn.setOnMouseClicked(mouseEvent -> {
                    ArrayList<EvaluateModel> modelList = new ArrayList<EvaluateModel>();
                    for (Model model : modelObservableList){
                        String modelName = model.getModelName();
                        int assetTypeID = Integer.parseInt(assetType.getId());
                        int from = (int) trainSlider.getValue() + 1;
                        int to = (int) trainSlider.getValue() + 1 + (int) testSlider.getValue();
                        EvaluateModel evaluateModel = new EvaluateModel();
                        evaluateModel.setAssetTypeID(assetTypeID);
                        evaluateModel.setModelName(modelName);
                        evaluateModel.setFrom(from);
                        evaluateModel.setTo(to);
                        modelList.add(evaluateModel);
                        modelDAO.insertToEvaluate(modelName, assetTypeID, (int) trainSlider.getValue(), (int) testSlider.getValue());
                    }

                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        modelsThumbPane.getChildren().clear();
        generateThumbnails();
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
    public void generateThumbnails() {
        ObservableList<Pane> boxes = FXCollections.observableArrayList();

        for (Model model : modelObservableList) {

            Pane pane = new Pane();

            pane.getStyleClass().add("modelPane");
            Text modelNameText = new Text(model.getModelName());
            Text modelDescriptionText = new Text(model.getDescription());
            Text RMSEText = new Text(RMSE + ": ");

            pane.setOnMouseClicked(mouseEvent -> selectedModel = model);
            Button evaluateModelBtn = new Button();
            evaluateModelBtn.setText("Evaluate");
//            TODO: Generate an evaluation for a single model
//            evaluateModelBtn.setOnMouseClicked(mouseEvent -> modelDAO.updateModelForAssetType(model.getModelID(), assetType.getId()));

            modelNameText.setId("modelNameText");
            modelDescriptionText.setId("modelDescriptionText");
            RMSEText.setId("RMSEText");
            SimpleStringProperty s = model.getRMSE();
            RMSEText.textProperty().bind(s);

            evaluateModelBtn.setId("SelectBtn");

            modelNameText.setLayoutX(14.0);
            modelNameText.setLayoutY(28.0);
            modelDescriptionText.setLayoutX(14.0);
            modelDescriptionText.setLayoutY(60.0);
            RMSEText.setLayoutX(14.0);
            RMSEText.setLayoutY(100.0);
            evaluateModelBtn.setLayoutX(28.0);
            evaluateModelBtn.setLayoutY(100.0);

            pane.getChildren().add(modelNameText);
            pane.getChildren().add(modelDescriptionText);
            pane.getChildren().add(RMSEText);
            pane.getChildren().add(evaluateModelBtn);

            boxes.add(pane);
        }
        modelsThumbPane.getChildren().addAll(boxes);
    }

    /**
     * Saves the association between the selected model and the current asset type in the database.
     * This function also set the retrain tag to true to indicate that the model chosen for the asset
     * type needs to be retrain. Finally, it labels all live assets of that asset type as updated.
     *
     * @author Jeremie
     */
    public void saveSelectedModelAssociation() {
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
    public void updateAssetsForSelectedModelAssociation(String AssetTypeID) {
        assetsList = assetDAO.getLiveAssetsFromAssetTypeID(AssetTypeID);
        for (Asset asset : assetsList) {
            assetDAO.setAssetToBeUpdated(asset.getId());
        }
    }
    public void updateRMSE(){
        for (Model model : modelObservableList) {
            model.setRMSE(String.valueOf(TextConstants.RMSEValueFormat.format(modelDAO.getLatestRMSE(Integer.valueOf(model.getModelID()),Integer.valueOf(assetType.getId())))));
        }
        rmseTimeline =
                new Timeline(new KeyFrame(Duration.millis(3000), e ->
                {
                    for (Model model : modelObservableList) {
                        model.setRMSE(String.valueOf(TextConstants.RMSEValueFormat.format(modelDAO.getLatestRMSE(Integer.valueOf(model.getModelID()),Integer.valueOf(assetType.getId())))));
                    }
                }));

        rmseTimeline.setCycleCount(Animation.INDEFINITE); // loop forever
        rmseTimeline.play();
    }
}
