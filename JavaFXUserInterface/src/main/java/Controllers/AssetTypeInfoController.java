/*
  This Controller is responsible for handling the information view
  of an asset type. It handles the editing of asset types
  and saving them to the database.

  @author Jeff, Paul, Roy, Najim
  @last_edit 02/7/2020
 */
package Controllers;

import Utilities.AssetTypeList;
import Utilities.TextConstants;
import Utilities.UIUtilities;
import app.ModelController;
import app.item.Asset;
import external.AssetTypeDAOImpl;
import external.ModelDAOImpl;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import local.AssetDAOImpl;
import preprocessing.DataPrePreprocessorController;
import rul.models.*;
import weka.classifiers.Classifier;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SMOreg;
import weka.classifiers.meta.AdditiveRegression;
import weka.classifiers.meta.RandomCommittee;
import weka.classifiers.meta.RandomSubSpace;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class AssetTypeInfoController implements Initializable {
    private static final String ALERT_HEADER = "Confirmation of asset type deletion";
    private static final String ALERT_CONTENT = "Are you sure you want to delete this asset type? \n " +
            "this will delete all the assets of this type";

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
    private Slider trainSlider;
    @FXML
    private Slider testSlider;
    @FXML
    private Label trainValue;
    @FXML
    private Label testValue;
    @FXML
    private Button modelEvaluateBtn;
    @FXML
    Tab modelTab;

    private UIUtilities uiUtilities;
    private AssetTypeList assetType;
    private AssetTypeList originalAssetType;
    private AssetTypeDAOImpl assetTypeDAO;
    private ModelDAOImpl modelDAO;
    private AssetDAOImpl assetDAO;
    private ModelController modelController;
    private Instances trainDataset;
    private DataPrePreprocessorController prePreprocessorController;
    private int trainSize = 0;
    private int testSize = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        uiUtilities = new UIUtilities();
        assetTypeDAO = new AssetTypeDAOImpl();
        modelDAO = new ModelDAOImpl();
        assetDAO = new AssetDAOImpl();
        prePreprocessorController = new DataPrePreprocessorController();

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
    public void initData(AssetTypeList assetType) throws Exception {
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
    public void attachEvents() throws Exception {

        modelTab.setOnSelectionChanged(event -> modelsButtonPressed());

        // Change scenes to Systems.fxml
        assetMenuBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, TextConstants.ASSETS_SCENE));
        //Attach link to systemTypeMenuBtn to go to Utilities.SystemTypeList.fxml
        assetTypeMenuBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, TextConstants.ASSET_TYPE_LIST_SCENE));
        infoDeleteBtn.setOnMouseClicked(this::deleteDialog);

        infoSaveBtn.setDisable(true);
        infoSaveBtn.setOnMouseClicked(mouseEvent -> {
            assetTypeDAO.updateAssetType(assetType.toAssetType());
            uiUtilities.changeScene(mouseEvent, TextConstants.ASSET_TYPE_LIST_SCENE);
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


    private void modelsButtonPressed(){
        if(modelTab.getId().equals("modelTab")){
            try {
                trainDataset  = DataPrePreprocessorController.getInstance().addRULCol(createInstancesFromAssets(assetDAO.getAssetsFromAssetTypeID(Integer.parseInt(assetType.getId()))));
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            trainSlider.setMax(trainDataset.size());
            trainValue.setText(String.valueOf(trainSlider.getValue()));
            testSlider.setMax(trainDataset.size());
            testValue.setText(String.valueOf(testSlider.getValue()));

            trainSlider.valueProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                    trainValue.setText(Integer.toString((int)trainSlider.getValue()));
                    testSlider.setMax(trainDataset.size() - (int)trainSlider.getValue());
                    trainSize = (int)trainSlider.getValue();
                }
            });
            testSlider.valueProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                    testValue.setText(Integer.toString((int)testSlider.getValue()));
                    trainSlider.setMax(trainDataset.size() - (int)testSlider.getValue());
                    testSize = (int)testSlider.getValue();
                }
            });

            try{
                modelEvaluateBtn.setOnMouseClicked(mouseEvent -> {
                    try {
                        evaluateModels(mouseEvent);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                });
            }
            catch (Exception e){}
        }
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
     * Creates a dialog box that asks user if they want to delete an assetType.
     *
     * @param mouseEvent is an event trigger for this delete dialog
     * @author Paul
     */
    private void deleteDialog(MouseEvent mouseEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(TextConstants.ALERT_TITLE_DIALOG);
        alert.setHeaderText(ALERT_HEADER);
        alert.setContentText(ALERT_CONTENT);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            deleteAssetType();
            uiUtilities.changeScene(mouseEvent, TextConstants.ASSET_TYPE_LIST_SCENE);
        }
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
            Instances trainSet = populateDataset(trainSize);
            Instances testSet = populateDataset(testSize);

            for(int i=0;i<models.size();i++){
                switch (models.get(i)) {
                    case "Linear":
                        LinearRegressionModelImpl linearRegressionModelImpl = new LinearRegressionModelImpl();
                        Classifier model =  linearRegressionModelImpl.trainModel(trainSet);
                        calculateEvaluation(model,trainSet,testSet,1);

                    case "LSTM":
                        ModelStrategy lstmm = new LSTMModelImpl();
                        Classifier lstm = lstmm.trainModel(trainSet);
                        calculateEvaluation(lstm,trainSet,testSet,2);

    //                    -------------------------------------------------------------------------------------------
    //                    --------------- this part is commented until the rest of the models are added -------------
    //                    -------------------------------------------------------------------------------------------
    //
    //                case "RandomForest":
    //                    Classifier forestModel =  new RandomForest();
    //                    calculateEvaluation(forestModel,trainSet,testSet,3);
    //
    //                case "RandomCommittee":
    //                    Classifier randomCommittee = new RandomCommittee();
    //                    calculateEvaluation(randomCommittee,trainSet,testSet, 4);
    //
    //                case "RandomSubSpace":
    //                    Classifier randomSubSpace = new RandomSubSpace();
    //                    calculateEvaluation(randomSubSpace,trainSet,testSet, 5);
    //
    //                case "AdditiveRegression":
    //                    Classifier additiveRegression = new AdditiveRegression();
    //                    calculateEvaluation(additiveRegression,trainSet,testSet, 6);
    //
    //                case "SMOReg":
    //                    Classifier smOreg = new SMOreg();
    //                    calculateEvaluation(smOreg,trainSet,testSet, 7);
    //
    //                case "MultilayerPerceptron":
    //                    Classifier multilayerPerceptron = new MultilayerPerceptron();
    //                    calculateEvaluation(multilayerPerceptron,trainSet,testSet, 8);

                    default:
                        model = null;
                }
                }
            }
//        }
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
     * Creates and Instances object based on the passed size
     *
     * @param size of the Instances object to be created.
     * @author Talal
     */
    private Instances populateDataset(int size){
        Instances set = trainDataset;
        if(set.size()==size) return set;
        for(int i=size+1;i<trainDataset.size();i++){
            trainDataset.delete(i);
        }
        return set;
    }

    public Instances createInstancesFromAssets(List<Asset> assets) {
        ArrayList<Attribute> attributesVector;
        Instances data;
        double[] values;
        ArrayList<String> attributeNames = assetDAO.getAttributesNameFromAssetID(assets.get(0).getId());
        String assetTypeName = assetDAO.getAssetTypeNameFromID(assets.get(0).getAssetTypeID());

        // 1. set up attributes
        attributesVector = new ArrayList<>();
        // - numeric
        attributesVector.add(new Attribute("Asset_id"));
        attributesVector.add(new Attribute("Time_Cycle"));
        for (String attributeName : attributeNames) {
            attributesVector.add(new Attribute(attributeName));
        }
        // 2. create Instances object
        data = new Instances(assetTypeName, attributesVector, 0);

        for (Asset asset : assets) {
            for (int timeCycle = 1; timeCycle <= asset.getAssetInfo().getAssetAttributes().get(1).getMeasurements().size(); timeCycle++) {
                values = new double[data.numAttributes()];
                values[0] = asset.getId();
                values[1] = timeCycle;
                for (int i = 0; i < asset.getAssetInfo().getAssetAttributes().size(); i++) {
                    values[i + 2] = asset.getAssetInfo().getAssetAttributes().get(i).getMeasurements(timeCycle);
                }
                data.add(new DenseInstance(1.0, values));       //changed from Instance to DenseInstance
            }
        }
        return data;
    }

    /**
     * Send the asset ID to the Database class in order for it to be deleted.
     *
     * @author Paul
     */
    private void deleteAssetType() {
        assetTypeDAO.deleteAssetTypeByID(assetType.getId());
    }
}
