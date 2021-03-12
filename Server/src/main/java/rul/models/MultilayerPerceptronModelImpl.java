/* A Feed Forward Neural Network, unlike LSTM which is Recurrent. Multilayer Perceptron (MLP)
 * is learnt using back propogation to classify instances. It's part of Weka's Classifiers and
 * not part of DL4J.
 *
 * @author Khaled
 * @last_edit 02/14/2021
 */

package rul.models;

import app.item.parameter.Parameter;
import weka.classifiers.Classifier;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Instances;

import java.util.Map;

public class MultilayerPerceptronModelImpl extends ModelStrategy {

    //Parameters
    private boolean showGUIParameter;
    private boolean autoBuildParameter;
    private int batchSizeParameter;
    private boolean decayParameter;
    private double learningRateParameter;
    private double momentumParameter;
    private boolean nominalToBinaryFilterParameter;
    private boolean normalizeAttributesParameter;
    private boolean normalizeNumericClassParameter;
    private boolean resetParameter;
    private boolean resumeParameter;
    private int trainingTimeParameter;
    private int validationSizeParameter;
    private int validationThresholdParameter;

    public MultilayerPerceptronModelImpl()
    {
        this.showGUIParameter = false;
        this.autoBuildParameter = true;
        this.batchSizeParameter = 100;
        this.decayParameter = false;
        this.learningRateParameter = 0.3;
        this.momentumParameter = 0.2;
        this.nominalToBinaryFilterParameter = true;
        this.normalizeAttributesParameter = true;
        this.normalizeNumericClassParameter = true;
        this.resetParameter = true;
        this.resumeParameter = false;
        this.trainingTimeParameter = 500;
        this.validationSizeParameter = 0;
        this.validationThresholdParameter = 20;
    }

    /**
     * This function takes the assets as the training dataset, and returns the trained
     * Multilayer Perceptron classifier.
     *
     * @author Khaled
     */
    @Override
    public Classifier trainModel(Instances dataToTrain) {
        Classifier mlp = new MultilayerPerceptron();
        dataToTrain.setClassIndex(dataToTrain.numAttributes() - 1);

        try {
            mlp.buildClassifier(dataToTrain);
        }

        catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return mlp;
    }

    @Override
    public Map<String, Parameter> getDefaultParameters()
    {
        return null;
    }


}
