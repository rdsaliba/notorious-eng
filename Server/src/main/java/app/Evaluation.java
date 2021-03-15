package app;

import app.item.TrainedModel;
import local.ModelDAOImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rul.models.ModelEvaluation;
import rul.models.ModelStrategy;
import rul.models.ModelsController;
import weka.classifiers.Classifier;
import weka.core.Instances;

public class Evaluation implements Runnable {
    static Logger logger = LoggerFactory.getLogger(Evaluation.class);
    private final ModelDAOImpl modelDAOImpl;
    private TrainedModel model;
    private Instances trainDataset;
    private Instances testDataset;

    public Evaluation() {
        modelDAOImpl = new ModelDAOImpl();
    }

    public Evaluation(TrainedModel model, Instances trainDataset, Instances testDataset) {
        modelDAOImpl = new ModelDAOImpl();
        this.model = model;
        this.trainDataset = trainDataset;
        this.testDataset = testDataset;
    }

    public TrainedModel getModel() {
        return model;
    }

    @Override
    public void run() {
        try {
            evaluateModel();
        } catch (Exception exception) {
            logger.error("Exception in running the evaluation of Models", exception);
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

        if (evaluateStrategy != null) {
            ModelsController modelsController = new ModelsController(evaluateStrategy);
            model.setModelClassifier(modelsController.trainModel(trainDataset));
            calculateEvaluation(model.getModelClassifier(), trainDataset, testDataset, model.getModelID(), model.getAssetTypeID());
        }
    }

    /**
     * this function is used to calculate the rmse and store the new value in the database
     *
     * @param model       is the classifier to train
     * @param train       are the instances used to train the model
     * @param test        are the instances used to test the model
     * @param modelId     is the ID of the model
     * @param assetTypeId is the ID of the asset type on which the evaluation is performed
     * @author talal
     */
    public void calculateEvaluation(Classifier model, Instances train, Instances test, int modelId, int assetTypeId) throws Exception {
        ModelEvaluation modelEvaluation = new ModelEvaluation(model, train, test);
        double rmse = modelEvaluation.evaluateTrainWithTest();
        modelDAOImpl.updateEvaluationRMSE(rmse, modelId, assetTypeId);
    }
}
