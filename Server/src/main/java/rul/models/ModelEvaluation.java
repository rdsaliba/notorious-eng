/*
  This class can be used to create objects to evaluate models in 3 different ways:
   1) Separate Training & Testing instances (constructor 1), method evaluateTrainWithTest()
   2) Only training instances, split by percentage (constructor 2), method evaluateTrainSplitByPercent
   3) Cross validation (folding) with training instances (constructor 3), method evaluateTrainCrossValidation

   Methods can be called to get RMSE using any of the 3 above approaches or to get toString of
   a more detailed Evaluation Summary.
   The instances passed need to have the RUL values (addRULCol from preprocessor for training).

   Quickest models to evaluate: Linear Regression, Additive Regression

  @author Khaled
  @last_edit 02/16/2020
 */

package rul.models;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;

import java.text.DecimalFormat;
import java.util.Random;

public class ModelEvaluation {
    private Classifier model;
    private Instances trainData;
    private Instances testData;
    private double percent;
    private int fold;
    private Evaluation eval;
    private double RMSE;

    //Constructor #1: for evaluating a model with separate training and testing data.
    public ModelEvaluation(Classifier newModel, Instances newTrainData, Instances newTestData) {
        this.model = newModel;
        this.trainData = newTrainData;
        this.testData = newTestData;
    }

    /*Constructor #2: for evaluating a model with only train data by splitting up the training data
      according to the percent value (given percent = training, rest = testing). Make sure
      to pass a double or the last constructor might be invoked (ex: 80.0 and not 80).
    */
    public ModelEvaluation(Classifier newModel, Instances newTrainData, double newPercent) {
        this.model = newModel;
        this.trainData = newTrainData;
        this.percent = newPercent;
    }

    /*Constructor #3: used for the cross-validation/folding approach of evaluating model.
      Recommended value for int fold: 5 to 15 (higher fold = longer time to evaluate).
     */
    public ModelEvaluation(Classifier newModel, Instances newTrainData, int fold) {
        this.model = newModel;
        this.trainData = newTrainData;
        this.fold = fold;
    }

    /** This method can be used by creating an object using the first constructor. Needs classifier,
     * training instances and testing instances, returns the double RMSE after evaluating the model
     * trained using trained instances against the test instances.
     *
     @author Khaled
     **/
    public double evaluateTrainWithTest() throws Exception {
        //this.trainData.setClassIndex(this.trainData.numAttributes() - 1);
        this.testData.setClassIndex(this.testData.numAttributes() - 1);
        model.buildClassifier(this.trainData);       //TODO: delete this line if model is already trained.
        //"No input instance format defined" error if model passed via constructor is not trained

        Evaluation eval = new Evaluation(this.trainData);

        eval.evaluateModel(this.model, this.testData);    //assumes model has already been trained
        this.RMSE = eval.rootMeanSquaredError();
        this.eval = eval;

        //Trim RMSE double to 2 decimal places
        DecimalFormat df = new DecimalFormat("#.##");
        return Double.parseDouble(df.format(eval.rootMeanSquaredError()));
    }

    /**
     * This method can be used by creating an object using the second constructor. Needs classifier,
     * training instances and a double percent value (decimal form) to split up the training instances.
     * Returns the double RMSE after evaluating the model using percent value as training data
     * and the rest as testing.
     *
     * @author Khaled
     */
    public double evaluateTrainSplitByPercent() throws Exception {
        int trainSize = (int) Math.round(this.trainData.numInstances() * ((double) this.percent) / 100);
        int testSize = this.trainData.numInstances() - trainSize;
        Instances train = new Instances(this.trainData, 0, trainSize);
        Instances test = new Instances(this.trainData, trainSize, testSize);

        this.trainData.randomize(new Random(1));          //Randomizing data
        Classifier classifier = this.model;
        classifier.buildClassifier(train);                //Needs to be retrained with split data

        Evaluation eval = new Evaluation(train);
        eval.evaluateModel(classifier, test);
        this.RMSE = eval.rootMeanSquaredError();
        this.eval = eval;

        DecimalFormat df = new DecimalFormat("#.##");
        return Double.parseDouble(df.format(eval.rootMeanSquaredError()));
    }

    /**
     *  This method can be used by creating an object using the third constructor. Needs classifier,
     *  training instances and an int n value for fold to divide the training instances into n parts .
     *  Ex: if fold = 10, data will be divided into 10 parts, each of those 10 parts will be used for
     *  testing once and the result will be averaged out.
     * @author Khaled
     */
    public double evaluateTrainCrossValidation() throws Exception {
        Evaluation eval = new Evaluation(this.trainData);
        eval.crossValidateModel(this.model, this.trainData, this.fold, new Random(1));
        this.RMSE = eval.rootMeanSquaredError();
        this.eval = eval;

        DecimalFormat df = new DecimalFormat("#.##");
        return Double.parseDouble(df.format(eval.rootMeanSquaredError()));
    }

    /**
     * Whereas the previous methods return only the double RMSE value, this toString will
     * return a more detailed analysis of the evaluation. Including: Model name, Correlation
     * Coefficient, Total Number of Instances, etc.
     *
     * @author Khaled
     */
    @Override
    public String toString() {
        return "Model: " + model.getClass().getSimpleName() +
               "\nModel Summary: " + eval.toSummaryString();
    }

    /*
        Getters and Setters for fields
        @author Khaled
     */
    public Classifier getModel() {
        return model;
    }

    public void setModel(Classifier model) {
        this.model = model;
    }

    public Instances getTrainData() {
        return trainData;
    }

    public void setTrainData(Instances trainData) {
        this.trainData = trainData;
    }

    public Instances getTestData() {
        return testData;
    }

    public void setTestData(Instances testData) {
        this.testData = testData;
    }

    public double getPercent() {
        return percent;
    }

    public void setPercent(double percent) {
        this.percent = percent;
    }

    public int getFold() {
        return fold;
    }

    public void setFold(int fold) {
        this.fold = fold;
    }

    public double getRMSE() {
        return RMSE;
    }

}
