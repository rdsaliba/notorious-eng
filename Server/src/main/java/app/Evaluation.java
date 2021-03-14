package app;

import app.item.TrainedModel;
import local.AssetDAOImpl;
import local.ModelDAOImpl;
import rul.models.*;
import weka.classifiers.Classifier;
import weka.core.Instances;

public class Evaluation implements Runnable {
    private ModelDAOImpl modelDAOImpl;
    private TrainedModel model;
    private Instances trainDataset;
    private Instances testDataset;
    public Evaluation(){
        modelDAOImpl = new ModelDAOImpl();
    }
    public Evaluation(TrainedModel model, Instances trainDataset, Instances testDataset){
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

    /**
     * this function is used to calculate the rmse and store the new value in the databse
     * @param model
     * @param train
     * @param test
     * @param modelId
     * @param assetTypeId
     * @author talal
     */
    public void calculateEvaluation(Classifier model, Instances train, Instances test, int modelId, int assetTypeId) throws Exception {
        ModelEvaluation modelEvaluation = new ModelEvaluation(model, train, test);
        double rmse = modelEvaluation.evaluateTrainWithTest();
        modelDAOImpl.updateRMSE(rmse, modelId, assetTypeId);
    }
}
