package Controllers;

import Utilities.SystemTypeList;
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
import javafx.scene.text.Text;
import local.AssetDAOImpl;
import preprocessing.DataPrePreprocessorController;
import rul.models.*;
import weka.classifiers.Classifier;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.functions.Dl4jMlpClassifier;
import weka.classifiers.functions.LinearRegression;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.dl4j.layers.LSTM;

import java.io.Console;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class SystemTypeInfoController implements Initializable {
    private final String SYSTEM_TYPE_LIST = "/SystemTypeList";

    @FXML
    private Button systemMenuBtn;
    @FXML
    private Button systemTypeMenuBtn;
    @FXML
    private Button infoSaveBtn;
    @FXML
    private Button infoDeleteBtn;
    @FXML
    private TextField systemTypeName;
    @FXML
    private TextArea systemTypeDesc;
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
    private ImageView systemTypeImageView;
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
    private SystemTypeList assetType;
    private SystemTypeList originalAssetType;
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
     * initData receives the System Type data that was selected from Utilities.SystemTypeList.FXML
     * Then, uses that data to populate the text fields in the scene.
     *
     * @param assetType represents the asset type we want to get info on
     * @author Najim, Paul
     */
    public void initData(SystemTypeList assetType) throws Exception {
        this.assetType = assetType;
        this.originalAssetType = new SystemTypeList(assetType);
        systemTypeName.setText(assetType.getAssetType().getName());
        systemTypeDesc.setText(assetType.getAssetType().getDescription());

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

        //        ---------------------------- F10 --------------------------
        try {
            trainDataset  = DataPrePreprocessorController.getInstance().addRULCol(createInstancesFromAssets(assetDAO.getAssetsFromAssetTypeID(1)));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        trainSlider.setMax(trainDataset.size());
        trainValue.setText(Integer.toString(trainDataset.size()));
        testSlider.setMax(trainDataset.size());
        testValue.setText(Integer.toString(trainDataset.size() ));

        trainSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                trainValue.setText(Integer.toString((int)trainSlider.getValue()));
                testValue.setText(Integer.toString(trainDataset.size() - (int)trainSlider.getValue()));
                testSlider.setMax(trainDataset.size() - (int)trainSlider.getValue());
                trainSize = (int)trainSlider.getValue();
            }
        });
        testSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                testValue.setText(Integer.toString((int)testSlider.getValue()));
                trainValue.setText(Integer.toString(trainDataset.size() - (int)testSlider.getValue()));
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

        //        ---------------------------- F10 --------------------------

        // Change scenes to Systems.fxml
        systemMenuBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, "/Systems"));
        //Attach link to systemTypeMenuBtn to go to Utilities.SystemTypeList.fxml
        systemTypeMenuBtn.setOnMouseClicked(mouseEvent -> uiUtilities.changeScene(mouseEvent, SYSTEM_TYPE_LIST));
        infoDeleteBtn.setOnMouseClicked(this::deleteDialog);

        infoSaveBtn.setDisable(true);
        infoSaveBtn.setOnMouseClicked(mouseEvent -> {
            assetTypeDAO.updateAssetType(assetType.toAssetType());
            uiUtilities.changeScene(mouseEvent, SYSTEM_TYPE_LIST);
        });

        systemTypeName.textProperty().addListener((obs, oldText, newText) -> {
            if (handleTextChange(newText, originalAssetType.getName()))
                assetType.getAssetType().setName(newText);
        });
        systemTypeDesc.textProperty().addListener((obs, oldText, newText) -> {
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
     * Changes the Image depending on the System Type.
     *
     * @author Najim
     */
    public void setImage(String typeName) {
        if (typeName.contains("Engine")) {
            systemTypeImageView.setImage(new Image("imgs/system_type_engine.png"));
        } else {
            systemTypeImageView.setImage(new Image("imgs/unknown_system_type.png"));
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
        String ALERT_TITLE = "Confirmation Dialog";
        alert.setTitle(ALERT_TITLE);
        String ALERT_HEADER = "Confirmation of system type deletion";
        alert.setHeaderText(ALERT_HEADER);
        String ALERT_CONTENT = "Are you sure you want to delete this system type? \n " +
                "this will delete all the assets of this type";
        alert.setContentText(ALERT_CONTENT);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            deleteAssetType();
            uiUtilities.changeScene(mouseEvent, SYSTEM_TYPE_LIST);
        }
    }

    private void evaluateModels(MouseEvent mouseEvent)  {
        try {
            ArrayList<String> models = modelDAO.getListOfModels();
            trainDataset.setClassIndex(trainDataset.numAttributes() - 1);
            Instances trainSet = populateDataset(trainSize);
            Instances testSet = populateDataset(testSize);
            LinearRegression lr = new LinearRegression();
            double ratio = (trainSize/trainDataset.size())*100;
            int id=0;
//            Classifier model = null;
            Evaluation ev = new Evaluation(trainSet);
            LSTMModelImpl impl = new LSTMModelImpl();
            double rmse = 0.0;
            for(int i=0;i<models.size();i++){
            switch (models.get(i)) {
                case "Linear":
                    Classifier model =  new LinearRegression();
                    id=1;
                    model.buildClassifier(trainSet);
                    ev.evaluateModel(model,testSet);
                    rmse = ev.rootMeanSquaredError();
                    modelDAO.updateRMSE(rmse, id);
                    trainValue.setText(Double.toString(rmse));
                case "LSTM":
                    ModelStrategy lstmm = new LSTMModelImpl();
                    Classifier lstm = lstmm.trainModel(trainSet);
                    id=2;
                    ev.evaluateModel(lstm,testSet);
                    rmse = ev.rootMeanSquaredError();
                    modelDAO.updateRMSE(rmse, id);
                    trainValue.setText(Double.toString(rmse));
//                case "RandomForest":                            //To be entered in DB: RandomForest
//                    model =  new RandomForestModelImpl();
//                case "RandomCommittee":                    //To be entered in DB: RandomCommittee
//                    return new RandomCommitteeModelImpl();
                default:
                    model = null;
            }
            }
            }
//        }
        catch (Exception e){trainValue.setText(e.getMessage());}
    }

    private Instances populateDataset(int size){
        Instances set = trainDataset;
        for(int i=size;i<=trainDataset.size()-1;i++){
            set.delete(i);
        }
        for(int j=0;j<=size;j++){
            trainDataset.delete(j);
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
