/**
 * This Controller is the first thing to run as the programm is started
 * It will setup the database and train the models
 *
 * @author      Paul Micu
 * @version     1.0
 * @last_edit   11/01/2020
 */
package com.cbms.app;

import com.cbms.rul.assessment.HealthAssesement;
import com.cbms.rul.models.LinearRegressionModelImpl;
import com.cbms.rul.models.ModelsController;
import com.cbms.preprocessing.DataPrePreprossesorController;
import com.cbms.source.local.Database;
import com.cbms.source.local.LocalDataSource;
import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

//    TODO 1 - query the db and get all of the datasets that have a a train tag and query the model table to get all the models we want to train get a map dataset,model we want to train
//    TODO 2- get all the data for each of the datasets and convert them to ARFF, list of arff files with models to use on them
//    TODO 3- call preprossesorController and reduce data for all the datasets return as Instances objectP
//    TODO 4- give the reduced datasets to the model class selected and return the model for each dataset
//        models for each dataset (4)
//        return an model object

public class ModelController {
    private ArrayList<Integer> trainingSets;
    private Map<String,Instances> instancesSets;
    private Map<String,Instances> reducedInstancesSets;
    private Map<String,Classifier> classifierSets ;
    private static ModelController instance = null;
    private DataPrePreprossesorController dataPrePreprossesorController;
    private ModelsController modelsController;
    private HealthAssesement healthAssesement;
    private Database db;

    private ModelController() throws Exception {
        db = new Database();
        trainingSets = db.getTrainDatasets();
        instancesSets = new TreeMap<>();
        reducedInstancesSets = new TreeMap<>();
        classifierSets = new TreeMap<>();
        modelsController = new ModelsController(new LinearRegressionModelImpl());
        dataPrePreprossesorController = DataPrePreprossesorController.getInstance();
        healthAssesement = new HealthAssesement();
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
        for (Integer setID: trainingSets) {
            instancesSets.put(db.getDatasetNameFromID(setID),db.createInstances(setID));
            System.out.println("Created Instances for "+setID);
        }

        // get trained classifier
        for (Map.Entry<String,Instances> instances : instancesSets.entrySet()){
            Instances minimallyReducedData = dataPrePreprossesorController.minimallyReduceData(instances.getValue());
            reducedInstancesSets.put(instances.getKey(),minimallyReducedData);
            classifierSets.put(instances.getKey() , modelsController.trainModel(minimallyReducedData));
            System.out.println("Created classifier for "+instances.getKey());
        }

    }

    public void evaluate() throws Exception {
        Instances toTest = db.createInstanceFromAssetID(155);
        toTest = dataPrePreprossesorController.addRULCol(toTest);
        //dataPrePreprossesorController.removeAttributes(toTest, reducedInstancesSets.get("FD001"));

        System.out.println(healthAssesement.predictRUL(toTest,classifierSets.get("FD001")));
    }
    /*public Classifier generateModels() throws Exception {
        // todo get a list of instances that need training
        Instances originalData = LocalDataSource.loadTrainingData("Dataset/Converted/train_FD001_withRUL.arff");
        DataPrePreprossesorController dataPrePreprossesorController = DataPrePreprossesorController.getInstance();
        ModelsController modelsController = new ModelsController(new LinearRegressionModelImpl());

        Instances reducedData = dataPrePreprossesorController.reduceData(originalData);  // reduce data
        Instances testData = LocalDataSource.loadTrainingData("Dataset/Converted/engine2.arff");

        //To only remove data that 100% don't hold any valuable information use minimallyReducedData
        Instances minimallyReducedData = dataPrePreprossesorController.minimallyReduceData(originalData);

        Classifier trained = modelsController.trainModel(minimallyReducedData); // train model and return Classifier
        HealthAssesement healthAssesement = new HealthAssesement();
        Remove remove= dataPrePreprossesorController.getRemovedIndexList();
        testData = Filter.useFilter(testData, remove);
        double  results =  healthAssesement.predictRUL(testData,trained);

        System.out.println(results);

        System.out.println("trained");
        return trained;
    }*/
}
