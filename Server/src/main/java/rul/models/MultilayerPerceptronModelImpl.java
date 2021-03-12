/* A Feed Forward Neural Network, unlike LSTM which is Recurrent. Multilayer Perceptron (MLP)
 * is learnt using back propogation to classify instances. It's part of Weka's Classifiers and
 * not part of DL4J.
 *
 * @author Khaled
 * @last_edit 03/12/2021
 */

package rul.models;

import app.item.parameter.*;
import weka.classifiers.Classifier;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Instances;

import java.util.HashMap;
import java.util.Map;

public class MultilayerPerceptronModelImpl extends ModelStrategy {

    //Default Parameters:
    private final boolean SHOW_GUI_PARAM_DEFAULT = false;
    private final boolean AUTO_BUILD_PARAM_DEFAULT = true;
    private final boolean DECAY_PARAM_DEFAULT = false;
    private final boolean NOMINAL_TO_BINARY_FILTER_PARAM_DEFAULT = true;
    private final boolean NORMALIZE_ATTRIBUTE_PARAM_DEFAULT = true;
    private final boolean NORMALIZE_NUMERIC_CLASS_PARAM_DEFAULT = true;
    private final boolean RESET_PARAM_DEFAULT = true;
    private final boolean RESUME_PARAM_DEFAULT = false;

    private final float LEARNING_RATE_PARAM_DEFAULT = 0.3F;
    private final float MOMENTUM_PARAM_DEFAULT = 0.2F;

    private final int TRAINING_TIME_PARAM_DEFAULT = 500;
    private final int VALIDATION_SIZE_PARAM_DEFAULT = 0;
    private final int VALIDATION_THRESHOLD_PARAM_DEFAULT = 20;

    private final String BATCH_SIZE_PARAM_DEFAULT = "100";

    //Parameters
    private BoolParameter showGUIPara;
    private BoolParameter autoBuildPara;
    private BoolParameter decayPara;
    private BoolParameter nominalToBinaryFilterPara;
    private BoolParameter normalizeAttributesPara;
    private BoolParameter normalizeNumericClassPara;
    private BoolParameter resetPara;
    private BoolParameter resumePara;

    private FloatParameter learningRatePara;
    private FloatParameter momentumPara;

    private IntParameter trainingTimePara;
    private IntParameter validationSizePara;
    private IntParameter validationThresholdPara;

    private StringParameter batchSizePara;

    private MultilayerPerceptron multilayerPerceptron;

    public MultilayerPerceptronModelImpl()
    {
        showGUIPara = new BoolParameter("Show GUI", SHOW_GUI_PARAM_DEFAULT);
        autoBuildPara = new BoolParameter("Auto Build", AUTO_BUILD_PARAM_DEFAULT);
        decayPara = new BoolParameter("Decay", DECAY_PARAM_DEFAULT);
        nominalToBinaryFilterPara = new BoolParameter("Nominal to Binary Filter", NOMINAL_TO_BINARY_FILTER_PARAM_DEFAULT);
        normalizeAttributesPara = new BoolParameter("Normalize Attributes", NORMALIZE_ATTRIBUTE_PARAM_DEFAULT);
        normalizeNumericClassPara = new BoolParameter("Normalize Numeric Class", NORMALIZE_NUMERIC_CLASS_PARAM_DEFAULT);
        resetPara = new BoolParameter("Reset", RESET_PARAM_DEFAULT);
        resumePara = new BoolParameter("Resume", RESUME_PARAM_DEFAULT);

        learningRatePara = new FloatParameter("Learning Rate", LEARNING_RATE_PARAM_DEFAULT);
        momentumPara = new FloatParameter("Momentum", MOMENTUM_PARAM_DEFAULT);

        trainingTimePara = new IntParameter("Training Time", TRAINING_TIME_PARAM_DEFAULT);
        validationSizePara = new IntParameter("Validation Size", VALIDATION_SIZE_PARAM_DEFAULT);
        validationThresholdPara = new IntParameter("Validation Threshold", VALIDATION_THRESHOLD_PARAM_DEFAULT);

        batchSizePara = new StringParameter("Batch Size", BATCH_SIZE_PARAM_DEFAULT);

        addParameter(showGUIPara);
        addParameter(autoBuildPara);
        addParameter(decayPara);
        addParameter(nominalToBinaryFilterPara);
        addParameter(normalizeAttributesPara);
        addParameter(normalizeNumericClassPara);
        addParameter(resetPara);
        addParameter(resumePara);

        addParameter(learningRatePara);
        addParameter(momentumPara);

        addParameter(trainingTimePara);
        addParameter(validationSizePara);
        addParameter(validationThresholdPara);

        addParameter(batchSizePara);
    }

    /**
     * This function takes the assets as the training dataset, and returns the trained
     * Multilayer Perceptron classifier.
     *
     * @author Khaled
     */
    @Override
    public Classifier trainModel(Instances dataToTrain) {
        multilayerPerceptron = new MultilayerPerceptron();
        dataToTrain.setClassIndex(dataToTrain.numAttributes() - 1);

        multilayerPerceptron.setGUI(((BoolParameter) getParameters().get(showGUIPara.getParamName())).getBoolValue());
        multilayerPerceptron.setAutoBuild(((BoolParameter) getParameters().get(autoBuildPara.getParamName())).getBoolValue());
        multilayerPerceptron.setDecay(((BoolParameter) getParameters().get(decayPara.getParamName())).getBoolValue());
        multilayerPerceptron.setNominalToBinaryFilter(((BoolParameter) getParameters().get(nominalToBinaryFilterPara.getParamName())).getBoolValue());
        multilayerPerceptron.setNormalizeAttributes(((BoolParameter) getParameters().get(normalizeAttributesPara.getParamName())).getBoolValue());
        multilayerPerceptron.setNormalizeNumericClass(((BoolParameter) getParameters().get(normalizeNumericClassPara.getParamName())).getBoolValue());
        multilayerPerceptron.setReset(((BoolParameter) getParameters().get(resetPara.getParamName())).getBoolValue());
        multilayerPerceptron.setResume(((BoolParameter) getParameters().get(resumePara.getParamName())).getBoolValue());

        multilayerPerceptron.setLearningRate(((FloatParameter) getParameters().get(learningRatePara.getParamName())).getFloatValue());
        multilayerPerceptron.setMomentum(((FloatParameter) getParameters().get(momentumPara.getParamName())).getFloatValue());

        multilayerPerceptron.setTrainingTime(((IntParameter) getParameters().get(trainingTimePara.getParamName())).getIntValue());
        multilayerPerceptron.setValidationSetSize(((IntParameter) getParameters().get(validationSizePara.getParamName())).getIntValue());
        multilayerPerceptron.setValidationThreshold(((IntParameter) getParameters().get(validationThresholdPara.getParamName())).getIntValue());

        try {
            multilayerPerceptron.buildClassifier(dataToTrain);
        }

        catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        setClassifier(multilayerPerceptron);
        return multilayerPerceptron;
    }

    @Override
    public Map<String, Parameter> getDefaultParameters()
    {
        BoolParameter showGUIParaDefault = new BoolParameter("Show GUI", SHOW_GUI_PARAM_DEFAULT);
        BoolParameter autoBuildParaDefault = new BoolParameter("Auto Build", AUTO_BUILD_PARAM_DEFAULT);
        BoolParameter decayParaDefaut = new BoolParameter("Decay", DECAY_PARAM_DEFAULT);
        BoolParameter nominalToBinaryFilterParaDefault = new BoolParameter("Nominal to Binary Filter", NOMINAL_TO_BINARY_FILTER_PARAM_DEFAULT);
        BoolParameter normalizeAttributesParaDefault = new BoolParameter("Normalize Attributes", NORMALIZE_ATTRIBUTE_PARAM_DEFAULT);
        BoolParameter normalizeNumericClassParaDefault = new BoolParameter("Normalize Numeric Class", NORMALIZE_NUMERIC_CLASS_PARAM_DEFAULT);
        BoolParameter resetParaDefault = new BoolParameter("Reset", RESET_PARAM_DEFAULT);
        BoolParameter resumeParaDefault = new BoolParameter("Resume", RESUME_PARAM_DEFAULT);

        FloatParameter learningRateParaDefault = new FloatParameter("Learning Rate", LEARNING_RATE_PARAM_DEFAULT);
        FloatParameter momentumParaDefault = new FloatParameter("Momentum", MOMENTUM_PARAM_DEFAULT);

        IntParameter trainingTimeParaDefault = new IntParameter("Training Time", TRAINING_TIME_PARAM_DEFAULT);
        IntParameter validationSizeParaDefault = new IntParameter("Validation Size", VALIDATION_SIZE_PARAM_DEFAULT);
        IntParameter validationThresholdParaDefault = new IntParameter("Validation Threshold", VALIDATION_THRESHOLD_PARAM_DEFAULT);

        batchSizePara = new StringParameter("Batch Size", BATCH_SIZE_PARAM_DEFAULT);

        Map<String, Parameter> parameters = new HashMap<>();
        parameters.put(showGUIParaDefault.getParamName(), showGUIParaDefault);
        parameters.put(autoBuildParaDefault.getParamName(), autoBuildParaDefault);
        parameters.put(decayParaDefaut.getParamName(), decayParaDefaut);
        parameters.put(nominalToBinaryFilterParaDefault.getParamName(), nominalToBinaryFilterParaDefault);
        parameters.put(normalizeAttributesParaDefault.getParamName(), normalizeAttributesParaDefault);
        parameters.put(normalizeNumericClassParaDefault.getParamName(), normalizeNumericClassParaDefault);
        parameters.put(resetParaDefault.getParamName(), resetParaDefault);
        parameters.put(resumeParaDefault.getParamName(), resumeParaDefault);

        parameters.put(learningRateParaDefault.getParamName(), learningRateParaDefault);
        parameters.put(momentumParaDefault.getParamName(), momentumParaDefault);

        parameters.put(trainingTimeParaDefault.getParamName(), trainingTimeParaDefault);
        parameters.put(validationSizeParaDefault.getParamName(), validationSizeParaDefault);
        parameters.put(validationThresholdParaDefault.getParamName(), validationThresholdParaDefault);

        return parameters;
    }

    public MultilayerPerceptron getMultilayerPerceptronObject()
    {
        return this.multilayerPerceptron;
    }


}
