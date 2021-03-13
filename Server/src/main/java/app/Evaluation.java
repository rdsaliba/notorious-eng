package app;

import app.item.Asset;
import app.item.EvaluateModel;
import app.item.TrainedModel;
import local.AssetDAO;
import local.AssetDAOImpl;
import local.ModelDAOImpl;
import preprocessing.DataPrePreprocessorController;
import rul.models.*;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;

public class Evaluation implements Runnable {
    private AssetDAOImpl assetDaoImpl;
    private ModelController modelController;
    private ModelDAOImpl modelDAOImpl;
    private TrainedModel model;
    private Instances trainDataset;
    private Instances testDataset;
    public Evaluation(){
        assetDaoImpl = new AssetDAOImpl();
        modelController = ModelController.getInstance();
        modelDAOImpl = new ModelDAOImpl();
    }
    public Evaluation(TrainedModel model, Instances trainDataset, Instances testDataset){
        assetDaoImpl = new AssetDAOImpl();
        modelController = ModelController.getInstance();
        modelDAOImpl = new ModelDAOImpl();
        this.model = model;
        this.trainDataset = trainDataset;
        this.testDataset = testDataset;
    }


    public TrainedModel getModel(){return model;}
    @Override
    public void run() {
        try {
            evaluateModel();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }
    /**
     * Evaluates all models of a specific Asset Type
     *
     * @author Talal
     */
    private void evaluateModel() throws Exception {
        trainDataset.setClassIndex(trainDataset.numAttributes() - 1);
        testDataset.setClassIndex(testDataset.numAttributes() - 1);
        ModelStrategy evaluateStrategy = model.getModelStrategy();
                ;

        if (evaluateStrategy != null) {
            ModelsController modelsController = new ModelsController(evaluateStrategy);
            model.setModelClassifier(modelsController.trainModel(trainDataset));
            calculateEvaluation(model.getModelClassifier(),trainDataset,testDataset,model.getModelID(), model.getAssetTypeID());
        }
    }

    public void calculateEvaluation(Classifier model, Instances train, Instances test, int modelId, int assetTypeId) throws Exception {


        ModelEvaluation modelEvaluation = new ModelEvaluation(model, train, test);
        double rmse = modelEvaluation.evaluateTrainWithTest();
        modelDAOImpl.updateRMSE(rmse, modelId, assetTypeId);
    }
}
