/*
  This Controller is the first thing to run as the program is started
  It will setup the database and train the models

  @author Paul Micu
  @version 2.0
  @last_edit 12/27/2020
 */
package com.cbms.app;

import com.cbms.app.item.Asset;
import com.cbms.preprocessing.DataPrePreprocessorController;
import com.cbms.rul.assessment.AssessmentController;
import com.cbms.rul.models.LinearRegressionModelImpl;
import com.cbms.rul.models.ModelStrategy;
import com.cbms.rul.models.ModelsController;
import com.cbms.source.local.AssetDAOImpl;
import com.cbms.source.local.ModelDAOImpl;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ModelController {
    private static ModelController instance = null;
    private final AssetDAOImpl assetDaoImpl;
    private final ModelDAOImpl modelDAOImpl;
    private ArrayList<Asset> liveAssets;

    private ModelController() {
        assetDaoImpl = new AssetDAOImpl();
        modelDAOImpl = new ModelDAOImpl();
        liveAssets = assetDaoImpl.getAllLiveAssets();
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
    }

    /**
     *  This function will return an arraylist of all live assets
     *
     * @author Paul
     */
    public ArrayList<Asset> getAllLiveAssets() {
        if (checkAssets())
            liveAssets = assetDaoImpl.getAllLiveAssets();
        return liveAssets;
    }

    /**
     *  this function checks all Assets for updated status
     *  and if the asset needs to be updated, it will recalculate the RUL using the corresponding model
     *
     * @author Paul
     */
    private boolean checkAssets() {
        //check for assets that need a new calculation
        ArrayList<Asset> assetsToUpdate = assetDaoImpl.getAssetsToUpdate();

        for (Asset asset : assetsToUpdate) {
            TrainedModel trainedModel = modelDAOImpl.getModelsByAssetTypeID(asset.getAssetTypeID());
            Double estimation = estimateRUL(asset, trainedModel.getModelClassifier());
            assetDaoImpl.addRULEstimation(estimation, asset, trainedModel);
        }

        assetDaoImpl.closeConnection();
        modelDAOImpl.closeConnection();

        return !assetsToUpdate.isEmpty();
    }

    /**
     *  This function checks all models for a retrain tag
     *  the retrain tag is only actif if new archived assets are added
     *  if it needs retraining it will retrain using the corresponding
     *  asset and model info
     *
     * @author Paul
     */
    public void checkModels() {

        // check for models that need retraining
        ArrayList<TrainedModel> trainedModelsToRetrain = modelDAOImpl.getModelsToTrain();

        // retrain models if necessary
        if (!trainedModelsToRetrain.isEmpty()) {
            for (TrainedModel trainedModel : trainedModelsToRetrain) {
                try {
                    trainModel(trainedModel);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            modelDAOImpl.setModelsToTrain(trainedModelsToRetrain);
        }

        assetDaoImpl.closeConnection();
        modelDAOImpl.closeConnection();
    }

    /**
     *  Given a trained model this function will retrain it with the current data and settings of the model
     *
     * @author Paul
     */
    private void trainModel(TrainedModel trainedModel) throws SQLException {
        Instances trainingSet = createInstancesFromAssets(assetDaoImpl.getAssetsFromAssetTypeID(trainedModel.getAssetTypeID()));
        Instances reducedData = DataPrePreprocessorController.getInstance().minimallyReduceData(trainingSet);
        ModelStrategy modelStrategy = getModelStrategy(trainedModel);
        if (modelStrategy != null) {
            ModelsController modelsController = new ModelsController(modelStrategy);
            trainedModel.setModelClassifier(modelsController.trainModel(reducedData));
        }
    }

    /**
     *  This function simply returns the Model Strategy object that is referenced
     *  in the trained model object
     *
     * @author Paul
     */
    private ModelStrategy getModelStrategy(TrainedModel trainedModel) {
        String stratName = modelDAOImpl.getModelNameFromModelID(trainedModel.getModelID());
        switch (stratName) {
            case "Linear":
                return new LinearRegressionModelImpl();
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
        Double estimate = -10000000.0;
        Instances toTest = createInstancesFromAssets(new ArrayList<Asset>(List.of(asset)));
        //toTest = DataPrePreprocessorController.getInstance().removeAttributes(reducedInstancesSets.get(classifierID),toTest);
        try {
            DataPrePreprocessorController dppc = DataPrePreprocessorController.getInstance();
            toTest = dppc.addRULCol(toTest);
            estimate = assessmentController.estimateRUL(toTest, classifier);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return estimate;
        }
    }

    /**
     *  given a list of assets this function will return the corresponding
     *  WEKA instances object
     *
     * @author Paul
     */
    public Instances createInstancesFromAssets(ArrayList<Asset> assets) {
        FastVector attributesVector;
        Instances data;
        double[] values;
        ArrayList<String> attributeNames = assetDaoImpl.getAttributesNameFromAssetID(assets.get(0).getId());
        String assetTypeName = assetDaoImpl.getAssetTypeNameFromID(assets.get(0).getAssetTypeID());

        // 1. set up attributes
        attributesVector = new FastVector();
        // - numeric
        attributesVector.addElement(new Attribute("Asset_id"));
        attributesVector.addElement(new Attribute("Time_Cycle"));
        for (String attributeName : attributeNames) {
            attributesVector.addElement(new Attribute(attributeName));
        }
        // 2. create Instances object
        data = new Instances(assetTypeName, attributesVector, 0);

        for (Asset asset : assets) {
            for (int timeCycle = 1; timeCycle <= asset.getAssetInfo().getLastRecorderTimeCycle(); timeCycle++) {
                values = new double[data.numAttributes()];
                values[0] = asset.getId();
                values[1] = timeCycle;
                for (int i = 0; i < asset.getAssetInfo().getAssetAttributes().size(); i++) {
                    values[i + 2] = asset.getAssetInfo().getAssetAttributes().get(i).getMeasurements(timeCycle);
                }
                data.add(new Instance(1.0, values));
            }
        }
        return data;
    }

}
