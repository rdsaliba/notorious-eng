package UnitTests.rul.models;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import rul.models.AdditiveRegressionModelImpl;
import rul.models.ModelEvaluation;
import rul.models.ModelStrategy;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.meta.AdditiveRegression;
import weka.core.Instances;

import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

import static org.junit.Assert.assertEquals;

public class ModelEvaluationTest {
    private ModelEvaluation modelEvaluationFold;
    private ModelEvaluation modelEvaluationSplit;
    private ModelEvaluation modelEvaluationWithTest;

    @Before
    public void setUp() throws IOException {
        FileReader trainFile = new FileReader("src/test/resources/FD01_Train_RUL.arff");
        Instances  trainData = new Instances(trainFile);

        FileReader testFile = new FileReader("src/test/resources/FD01_Test_RUL.arff");
        Instances  testData = new Instances(testFile);

        ModelStrategy model = new AdditiveRegressionModelImpl();

        modelEvaluationFold = new ModelEvaluation(model.trainModel(trainData), trainData, 10);
        modelEvaluationSplit = new ModelEvaluation(model.trainModel(trainData), trainData, 80.0);
        modelEvaluationWithTest = new ModelEvaluation(model.trainModel(trainData), trainData, testData);
    }

    @After
    public void tearDown() {
        modelEvaluationFold = null;
        modelEvaluationSplit = null;
        modelEvaluationWithTest = null;
    }

    @Test
    public void evaluateTrainWithTest() throws Exception {
        FileReader trainFile = new FileReader("src/test/resources/FD01_Train_RUL.arff");
        Instances  trainData = new Instances(trainFile);
        trainData.setClassIndex(trainData.numAttributes() - 1);

        FileReader testFile = new FileReader("src/test/resources/FD01_Test_RUL.arff");
        Instances  testData = new Instances(testFile);
        testData.setClassIndex(testData.numAttributes() - 1);

        Classifier model = new AdditiveRegression();
        model.buildClassifier(trainData);

        Evaluation eval = new Evaluation(trainData);

        eval.evaluateModel(model, testData);
        assertEquals("Should Return double RMSE value within 0.01", eval.rootMeanSquaredError(),
                     modelEvaluationWithTest.evaluateTrainWithTest(), 0.01);
    }

    @Test
    public void evaluateTrainSplitByPercent() throws Exception {
        FileReader trainFile = new FileReader("src/test/resources/FD01_Train_RUL.arff");
        Instances  trainData = new Instances(trainFile);
        trainData.setClassIndex(trainData.numAttributes() - 1);

        int trainSize = (int) Math.round(trainData.numInstances() * 80.0 / 100);
        int testSize = trainData.numInstances() - trainSize;
        Instances train = new Instances(trainData, 0, trainSize);
        Instances test = new Instances(trainData, trainSize, testSize);

        trainData.randomize(new Random(1));
        Classifier classifier = new AdditiveRegression();
        classifier.buildClassifier(train);

        Evaluation eval = new Evaluation(train);
        eval.evaluateModel(classifier, test);

        assertEquals("Should Return double RMSE value within 0.01", eval.rootMeanSquaredError(),
                     modelEvaluationSplit.evaluateTrainSplitByPercent(), 0.01);
    }

    @Test
    public void evaluateTrainCrossValidation() throws Exception {
        FileReader trainFile = new FileReader("src/test/resources/FD01_Train_RUL.arff");
        Instances  trainData = new Instances(trainFile);
        trainData.setClassIndex(trainData.numAttributes() - 1);

        Classifier classifier = new AdditiveRegression();
        classifier.buildClassifier(trainData);

        Evaluation eval = new Evaluation(trainData);
        eval.crossValidateModel(classifier, trainData, 10, new Random(1));

        assertEquals("Should Return double RMSE value within 0.01", eval.rootMeanSquaredError(),
                     modelEvaluationFold.evaluateTrainCrossValidation(), 0.01);
    }
}
