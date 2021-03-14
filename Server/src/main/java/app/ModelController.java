/*
  This Controller is the first thing to run as the program is started
  It will setup the database and train the models

  @author Paul Micu
  @last_edit 12/27/2020
 */
package app;

import app.item.*;
import local.AssetDAOImpl;
import local.ModelDAOImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import preprocessing.DataPrePreprocessorController;
import rul.assessment.AssessmentController;
import rul.models.*;
import utilities.Constants;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

import java.util.*;

public class ModelController {
    static Logger logger = LoggerFactory.getLogger(ModelController.class);
    private static ModelController instance = null;
    private final AssetDAOImpl assetDaoImpl;
    private final ModelDAOImpl modelDAOImpl;

    public ModelController() {
        assetDaoImpl = new AssetDAOImpl();
        modelDAOImpl = new ModelDAOImpl();
    }

    public static ModelController getInstance() {
        if (instance == null)
            instance = new ModelController();
        return instance;
    }

    /**
     * This is the first thing that gets run when opening the application.
     * It will check and update any models that need to
     * it will check and recalculate the RUL for any asset that got new measurements
     *
     * @author Paul
     */
    public void initializer() {
        checkModels();
        checkAssets();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                logger.info("Initialize - checkAsset - start");
                checkAssets();
                logger.info("Initialize - checkAsset - end");
                logger.info("Initialize - checkModels - start");
                checkModels();
                logger.info("Initialize - checkModels - end");
            }
        }, 0, 2000);
    }


    /**
     * This function will return an list of all live assets
     *
     * @return a List of all live assets
     * @author Paul
     */
    public List<Asset> getAllLiveAssets() {
        return assetDaoImpl.getAllLiveAssets();
    }


    /**
     * this function checks all Assets for updated status
     * and if the asset needs to be updated, it will recalculate the RUL using the corresponding model
     *
     * @author Paul
     */
    public void checkAssets() {
        AssessmentController assessmentController = new AssessmentController();
        //check for assets that need a new calculation
        ArrayList<Asset> assetsToUpdate = assetDaoImpl.getAssetsToUpdate();
        ArrayList<TrainedModel> trainedModels = new ArrayList<>();
        TrainedModel trainedModel;
        Double estimation;
        for (Asset asset : assetsToUpdate) {
            trainedModel = getModelForAssetType(trainedModels, asset.getAssetTypeID());
            estimation = estimateRUL(asset, trainedModel.getModelClassifier());
            asset.setRecommendation(assessmentController.getRecommendation(estimation, asset.getAssetTypeID()));
            assetDaoImpl.updateRecommendation(asset.getId(), asset.getRecommendation());
            assetDaoImpl.addRULEstimation(estimation, asset, trainedModel);
        }
    }

    public TrainedModel getModelForAssetType(List<TrainedModel> trainedModels, String assetTypeID) {
        for (TrainedModel tm : trainedModels) {
            if (tm.getAssetTypeID() == Integer.parseInt(assetTypeID))
                return tm;
        }
        trainedModels.add(modelDAOImpl.getModelsByAssetTypeID(assetTypeID, Constants.STATUS_LIVE));
        return trainedModels.get(trainedModels.size() - 1);
    }

    /**
     * This function checks all models for a retrain tag
     * the retrain tag is only active if new archived assets are added
     * if it needs retraining it will retrain using the corresponding
     * asset and model info
     *
     * @author Paul
     */
    public void checkModels() {
        // check for models that need retraining
        ArrayList<TrainedModel> trainedModelsToRetrain = modelDAOImpl.getModelsToTrain();
        trainedModelsToRetrain
                .stream()
                .filter(tm -> tm.getStatusID() == Constants.STATUS_LIVE)
                .forEach(this::trainAndSave);
        trainedModelsToRetrain
                .stream()
                .filter(tm -> tm.getStatusID() == Constants.STATUS_EVALUATION)
                .findFirst().ifPresent(this::evaluate);
        trainedModelsToRetrain
                .stream()
                .filter(tm -> tm.getStatusID() == Constants.STATUS_EVALUATION)
                .findFirst().ifPresent(this::trainAndSave);
    }

    /**
     * evaluates a specific model using function 'modelToEvaluate
     * @param tm
     * @author talal
     */
    public void evaluate(TrainedModel tm){
        try{
            if(!Objects.isNull(tm.getModelStrategy()));
            modelToEvaluate(tm);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * train the specific model and save it in the db
     *
     * @author Paul
     */
    private void trainAndSave(TrainedModel tm) {
        try {
            trainModel(tm);
            modelDAOImpl.setModelToTrain(tm);

        } catch (Exception e) {
            logger.error("Exception in trainAndSave(): ", e);
        }
    }

    /**
     * Given a trained model this function will retrain it with the current data and settings of the model
     *
     * @author Paul
     */
    private void trainModel(TrainedModel trainedModel) throws Exception {
        Instances trainingSet = createInstancesFromAssets(assetDaoImpl.getAssetsFromAssetTypeID(trainedModel.getAssetTypeID()));
        Instances reducedData = DataPrePreprocessorController.getInstance().addRULCol(trainingSet);
        ModelStrategy modelStrategy = trainedModel.getModelStrategy();
        if (modelStrategy == null) {
            modelStrategy = getModelStrategy(trainedModel);
            trainedModel.setModelStrategy(modelStrategy);
        }
        ModelsController modelsController = new ModelsController(modelStrategy);
        trainedModel.setModelClassifier(modelsController.trainModel(reducedData));
    }

    /**
     * This function simply returns the Model Strategy object that is referenced
     * in the trained model object
     *
     * @author Paul
     */
    private ModelStrategy getModelStrategy(TrainedModel tm) {
        String stratName = modelDAOImpl.getModelNameFromModelID(String.valueOf(tm.getModelID()));
        switch (stratName) {
            case "Linear":                                  //1: Linear
                return new LinearRegressionModelImpl();
            case "LSTM":                                    //2: LSTM
                return new LSTMModelImpl();
            case "RandomForest":                            //3: RandomForest
                return new RandomForestModelImpl();
            case "RandomCommittee":                         //4: RandomCommittee
                return new RandomCommitteeModelImpl();
            case "RandomSubSpace":                          //5: RandomSubSpace
                return new RandomSubSpaceModelImpl();
            case "AdditiveRegression":                      //6: AdditiveRegression
                return new AdditiveRegressionModelImpl();
            case "SMOReg":                                  //7: SMOReg
                return new SMORegModelImpl();
            case "MultilayerPerceptron":                    //8: MultilayerPerceptron
                return new MultilayerPerceptronModelImpl();
            default:
                return null;
        }
    }


    /**
     * Given an asset and the classifier, this will return the double value of the estimation
     *
     * @author Paul Micu
     */
    public Double estimateRUL(Asset asset, Classifier classifier) {
        AssessmentController assessmentController = new AssessmentController();
        double estimate = -10000000.0;
        Instances toTest = createInstancesFromAssets(new ArrayList<>(Collections.singletonList(asset)));
        try {
            DataPrePreprocessorController dppc = DataPrePreprocessorController.getInstance();
            toTest = dppc.addRULCol(toTest);
            estimate = assessmentController.estimateRUL(toTest, classifier);
        } catch (Exception e) {
            logger.error("Exception: ", e);
        }
        return estimate;
    }

    /**
     * given a list of assets this function will return the corresponding
     * WEKA instances object
     *
     * @author Paul
     */
    public Instances createInstancesFromAssets(List<Asset> assets) {
        System.out.println("in create");
        ArrayList<Attribute> attributesVector;
        Instances data;
        double[] values;
        ArrayList<String> attributeNames = assetDaoImpl.getAttributesNameFromAssetID(assets.get(0).getId());
        String assetTypeName = assetDaoImpl.getAssetTypeNameFromID(assets.get(0).getAssetTypeID());

        attributesVector = new ArrayList<>();
        attributesVector.add(new Attribute("Asset_id"));
        attributesVector.add(new Attribute("Time_Cycle"));
        for (String attributeName : attributeNames) {
            attributesVector.add(new Attribute(attributeName));
        }
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
     * evaluates a model on a separate thread using the evaluation class
     * @param trainedModel
     * @author talal
     */
    public void modelToEvaluate(TrainedModel trainedModel) throws Exception {
        trainedModel.setAssetTypeID(1);
        int trainAssets =trainedModel.getModelStrategy().getTrainsAssets();
        int testAssets=trainedModel.getModelStrategy().getTestAssets();
        Instances trainDataset = DataPrePreprocessorController.getInstance().addRULCol(createInstancesFromAssets(assetDaoImpl.getAssetsFromAssetTypeID(trainedModel.getAssetTypeID()).subList(0, trainAssets-1)));
        Instances testDataset = DataPrePreprocessorController.getInstance().addRULCol(createInstancesFromAssets(assetDaoImpl.getAssetsFromAssetTypeID(trainedModel.getAssetTypeID()).subList(trainAssets, trainAssets+testAssets - 1)));
        Evaluation evaluation = new Evaluation(trainedModel,trainDataset,testDataset);
        Thread t = new Thread(evaluation);
        t.start();
    }
}

