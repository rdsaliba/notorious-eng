/*
  This Controller is the first thing to run as the program is started
  It will setup the database and train the models

  @author Paul Micu
  @version 1.0
  @last_edit 11/01/2020
 */
package com.cbms.app;

import com.cbms.app.item.Asset;
import com.cbms.preprocessing.DataPrePreprocessorController;
import com.cbms.rul.assessment.AssessmentController;
import com.cbms.rul.models.LinearRegressionModelImpl;
import com.cbms.rul.models.ModelsController;
import com.cbms.source.local.Database;
import weka.classifiers.Classifier;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class ModelController {
    private static ModelController instance = null;
    private ArrayList<Integer> trainingSets;
    private final Map<String, Instances> instancesSets;
    private final Map<String, Instances> reducedInstancesSets;
    private final Map<String, Classifier> classifierSets;
    private final DataPrePreprocessorController dataPrePreprocessorController;
    private final ModelsController modelsController;
    private final AssessmentController assessmentController;
    private final Database db;

    private ModelController() throws Exception {
        db = new Database();
        trainingSets = db.getTrainAssetTypes();
        instancesSets = new TreeMap<>();
        reducedInstancesSets = new TreeMap<>();
        classifierSets = new TreeMap<>();
        modelsController = new ModelsController(new LinearRegressionModelImpl());
        dataPrePreprocessorController = DataPrePreprocessorController.getInstance();
        assessmentController = new AssessmentController();
    }

    public static ModelController getInstance() throws Exception {
        if (instance == null)
            instance = new ModelController();
        return instance;
    }

    /**
     * This is the first thing that gets run when opening the application. it will initialize
     * the instances sets based on the asset types and train the models as Classifiers.
     *
     * @author Paul
     * */
    public void initializer() throws Exception {
            trainingSets = db.getTrainAssetTypes();

        // get instances from db
        for (Integer assetTypeID : trainingSets) {
            instancesSets.put(db.getAssetTypeNameFromID(assetTypeID), db.createInstancesFromAssetTypeID(assetTypeID, 1));
            System.out.println("Created Instances for " + assetTypeID);
        }

        // get trained classifier
        for (Map.Entry<String, Instances> instances : instancesSets.entrySet()) {
            Instances minimallyReducedData = dataPrePreprocessorController.minimallyReduceData(instances.getValue());
            reducedInstancesSets.put(instances.getKey(), minimallyReducedData);
            classifierSets.put(instances.getKey(), modelsController.trainModel(minimallyReducedData));
            System.out.println("Created classifier for " + instances.getKey());
        }

    }

    /**
     * This function is to stop the connection to the database
     *
     * @author Najim
     */
    public void stopDatabase() {
        db.stop();
    }

    /**
     * Calling this method will return an arraylist of assets that have been estimated using the linear regression
     *
     * @author Paul Micu
     */
    public ArrayList<Asset> estimate() throws Exception {
    // TODO: 2020-12-05 This estimate will have to eventually be changed to accept user input as the asset type instead of hard coded.
        ArrayList<Asset> assets = db.getAssetsFromAssetTypeID(1, 0);
        assets = estimateRUL(assets, "Engine");

        return assets;
    }

    /**
     * Given an arraylist of testing assets and the classifier's name, this will return the same arraylist
     *
     * @author Paul Micu
     */
    public ArrayList<Asset> estimateRUL(ArrayList<Asset> assets, String classifierID) throws Exception {
        double estimate;

        for (Asset asset : assets) {
            Instances toTest = db.createInstanceFromAssetID(asset.getId());
            toTest = dataPrePreprocessorController.removeAttributes(reducedInstancesSets.get(classifierID),toTest);
            toTest = dataPrePreprocessorController.addRULCol(toTest);
            estimate = assessmentController.estimateRUL(toTest, classifierSets.get(classifierID));
            asset.getAssetInfo().addRULMeasurement(estimate);
            db.addRULEstimate(asset.getId(), estimate);
        }

        return assets;

    }
}
