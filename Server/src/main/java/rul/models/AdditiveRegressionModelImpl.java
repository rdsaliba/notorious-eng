/* Another regression model strategy implementation. This model is more flexible than Linear
 * Regression (which is a special case of Additive Regression).
 *
 * @author Khaled
 * @last_edit 03/11/2021
 */
package rul.models;

import app.item.parameter.BoolParameter;
import app.item.parameter.FloatParameter;
import app.item.parameter.IntParameter;
import app.item.parameter.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.classifiers.Classifier;
import weka.classifiers.meta.AdditiveRegression;
import weka.core.Instances;

import java.util.HashMap;
import java.util.Map;

public class AdditiveRegressionModelImpl extends ModelStrategy {
    private static final long serialVersionUID = -8209380806602425484L;

    //Default Parameters
    private static final boolean MINIMIZE_ABSOLUTE_ERROR_PARAM_DEFAULT = false;
    private static final boolean RESUME_PARAM_DEFAULT = false;
    private static final float SHRINKAGE_PARAM_DEFAULT = 1.0F;
    private static final int NUM_ITERATIONS_PARAM_DEFAULT = 10;
    private static final int BATCH_SIZE_PARAM_DEFAULT = 100;
    static Logger logger = LoggerFactory.getLogger(AdditiveRegressionModelImpl.class);
    private final BoolParameter minimizeAbsoluteErrorPara;
    private final BoolParameter resumePara;
    private final FloatParameter shrinkagePara;
    private final IntParameter numIterationsPara;
    private final IntParameter batchSizePara;
    private AdditiveRegression additiveRegression;

    public AdditiveRegressionModelImpl() {
        batchSizePara = new IntParameter("Batch Size", BATCH_SIZE_PARAM_DEFAULT);
        minimizeAbsoluteErrorPara = new BoolParameter("Minimize Absolute Error", MINIMIZE_ABSOLUTE_ERROR_PARAM_DEFAULT);
        numIterationsPara = new IntParameter("Number of Iterations", NUM_ITERATIONS_PARAM_DEFAULT);
        resumePara = new BoolParameter("Resume", RESUME_PARAM_DEFAULT);
        shrinkagePara = new FloatParameter("Shrinkage", SHRINKAGE_PARAM_DEFAULT);

        addParameter(batchSizePara);
        addParameter(minimizeAbsoluteErrorPara);
        addParameter(numIterationsPara);
        addParameter(resumePara);
        addParameter(shrinkagePara);
    }

    /**
     * This function takes the assets as the training dataset, and returns the trained
     * Additive Regression classifier.
     *
     * @author Khaled
     */
    @Override
    public Classifier trainModel(Instances dataToTrain) {
        additiveRegression = new AdditiveRegression();
        dataToTrain.setClassIndex(dataToTrain.numAttributes() - 1);

        additiveRegression.setBatchSize(String.valueOf(((IntParameter) getParameters().get(batchSizePara.getParamName())).getIntValue()));
        additiveRegression.setMinimizeAbsoluteError(((BoolParameter) getParameters().get(minimizeAbsoluteErrorPara.getParamName())).getBoolValue());
        additiveRegression.setNumIterations(((IntParameter) getParameters().get(numIterationsPara.getParamName())).getIntValue());
        additiveRegression.setResume(((BoolParameter) getParameters().get(resumePara.getParamName())).getBoolValue());
        additiveRegression.setShrinkage(((FloatParameter) getParameters().get(shrinkagePara.getParamName())).getFloatValue());
        try {
            additiveRegression.buildClassifier(dataToTrain);
        } catch (Exception e) {
            logger.error("Exception:", e);
        }

        setClassifier(additiveRegression);
        return additiveRegression;
    }

    @Override
    public Map<String, Parameter> getDefaultParameters() {
        IntParameter batchSizeParaDefault = new IntParameter("Batch Size", BATCH_SIZE_PARAM_DEFAULT);
        BoolParameter minimizeAbsoluteErrorParaDefault = new BoolParameter("Minimize Absolute Error", MINIMIZE_ABSOLUTE_ERROR_PARAM_DEFAULT);
        IntParameter numIterationsParaDefault = new IntParameter("Number of Iterations", NUM_ITERATIONS_PARAM_DEFAULT);
        BoolParameter resumeParaDefault = new BoolParameter("Resume", RESUME_PARAM_DEFAULT);
        FloatParameter shrinkageParaDefault = new FloatParameter("Shrinkage", SHRINKAGE_PARAM_DEFAULT);

        Map<String, Parameter> parameters = new HashMap<>();
        parameters.put(batchSizeParaDefault.getParamName(), batchSizeParaDefault);
        parameters.put(minimizeAbsoluteErrorParaDefault.getParamName(), minimizeAbsoluteErrorParaDefault);
        parameters.put(numIterationsParaDefault.getParamName(), numIterationsParaDefault);
        parameters.put(resumeParaDefault.getParamName(), resumeParaDefault);
        parameters.put(shrinkageParaDefault.getParamName(), shrinkageParaDefault);

        return parameters;
    }

    public AdditiveRegression getAdditiveRegressionObject() {
        return this.additiveRegression;
    }
}