/**
 * This Controller is the first thing to run as the programm is started
 * It will setup the database and train the models
 *
 * @author Paul Micu
 * @version 1.0
 * @last_edit 11/01/2020
 */
package com.cbms.app;

import com.cbms.app.item.Asset;
import com.cbms.preprocessing.DataPrePreprossesorController;
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
    private Map<String, Instances> instancesSets;
    private Map<String, Instances> reducedInstancesSets;
    private Map<String, Classifier> classifierSets;
    private DataPrePreprossesorController dataPrePreprossesorController;
    private ModelsController modelsController;
    private AssessmentController assessmentController;
    private Database db;

    private ModelController() throws Exception {
        db = new Database();
        trainingSets = db.getTrainDatasets();
        instancesSets = new TreeMap<>();
        reducedInstancesSets = new TreeMap<>();
        classifierSets = new TreeMap<>();
        modelsController = new ModelsController(new LinearRegressionModelImpl());
        dataPrePreprossesorController = DataPrePreprossesorController.getInstance();
        assessmentController = new AssessmentController();
    }

    public static ModelController getInstance() throws Exception {
        if (instance == null)
            instance = new ModelController();
        return instance;
    }

    /**
     * Work in progress
     *
     * @author Paul
     * */
    public void initializer() throws Exception {
        // to get all of the sets
        // trainingSets = db.getTrainDatasets();
        trainingSets = new ArrayList<>();
        trainingSets.add(1);


        // get instances from db
        for (Integer setID : trainingSets) {
            instancesSets.put(db.getDatasetNameFromID(setID), db.createInstances(setID));
            System.out.println("Created Instances for " + setID);
        }

        // get trained classifier
        for (Map.Entry<String, Instances> instances : instancesSets.entrySet()) {
            Instances minimallyReducedData = dataPrePreprossesorController.minimallyReduceData(instances.getValue());
            reducedInstancesSets.put(instances.getKey(), minimallyReducedData);
            Instances notReduced = dataPrePreprossesorController.addRULCol(instances.getValue());
            classifierSets.put(instances.getKey(), modelsController.trainModel(notReduced));
            System.out.println("Created classifier for " + instances.getKey());
        }

    }

    public void estimate() throws Exception {

        ArrayList<Asset> assets = db.getAssetsFromDatasetID(2);
        assets = estimateRUL(assets, "FD001");

        for (Asset a : assets) {
            System.out.println(a.toString());
        }
    }

    public ArrayList<Asset> estimateRUL(ArrayList<Asset> assets, String classifierID) throws Exception {
        double estimate = -0.0;

        for (Asset asset : assets) {
            Instances toTest = db.createInstanceFromAssetID(asset.getId());
            toTest = dataPrePreprossesorController.addRULCol(toTest);

            //dataPrePreprossesorController.removeAttributes(toTest, reducedInstancesSets.get("FD001"));
            estimate = assessmentController.estimateRUL(toTest, classifierSets.get(classifierID));
            asset.getAssetInfo().addRULMeasurement(estimate);
            db.addRULEstimate(asset.getId(), estimate);
        }

        return assets;

    }
}
