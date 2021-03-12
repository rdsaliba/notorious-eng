/* Another regression model strategy implementation. This model is more flexible than Linear
 * Regression (which is a special case of Additive Regression).
 *
 * @author Khaled
 * @last_edit 03/11/2021
 */
package rul.models;

import app.item.parameter.*;
import weka.classifiers.Classifier;
import weka.classifiers.meta.AdditiveRegression;
import weka.core.Instances;

import java.util.HashMap;
import java.util.Map;

public class AdditiveRegressionModelImpl extends ModelStrategy
{
    //Default Parameters
    private final boolean MINIMIZE_ABSOLUTE_ERROR_PARAM_DEFAULT = false;
    private final boolean RESUME_PARAM_DEFAULT = false;
    private final float SHRINKAGE_PARAM_DEFAULT = 1.0F;
    private final int NUM_ITERATIONS_PARAM_DEFAULT = 10;
    private final String BATCH_SIZE_PARAM_DEFAULT = "100";


    private BoolParameter minimizeAbsoluteErrorPara;
    private BoolParameter resumePara;
    private FloatParameter shrinkagePara;
    private IntParameter numIterationsPara;
    private StringParameter batchSizePara;

    private AdditiveRegression additiveRegression;

    public AdditiveRegressionModelImpl()
    {
        batchSizePara = new StringParameter("Batch Size", BATCH_SIZE_PARAM_DEFAULT);
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
    public Classifier trainModel(Instances dataToTrain)
    {
        additiveRegression = new AdditiveRegression();
        dataToTrain.setClassIndex(dataToTrain.numAttributes() - 1);

        additiveRegression.setBatchSize(((StringParameter) getParameters().get(batchSizePara.getParamName())).getStringValue());
        additiveRegression.setMinimizeAbsoluteError(((BoolParameter) getParameters().get(minimizeAbsoluteErrorPara.getParamName())).getBoolValue());
        additiveRegression.setNumIterations(((IntParameter) getParameters().get(numIterationsPara.getParamName())).getIntValue());
        additiveRegression.setResume(((BoolParameter) getParameters().get(resumePara.getParamName())).getBoolValue());
        additiveRegression.setShrinkage(((FloatParameter) getParameters().get(shrinkagePara.getParamName())).getFloatValue());
        try
        {
            additiveRegression.buildClassifier(dataToTrain);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        setClassifier(additiveRegression);
        return additiveRegression;
    }

    @Override
    public Map<String, Parameter> getDefaultParameters()
    {
        StringParameter batchSizeParaDefault             = new StringParameter("Batch Size", BATCH_SIZE_PARAM_DEFAULT);
        BoolParameter   minimizeAbsoluteErrorParaDefault = new BoolParameter("Minimize Absolute Error", MINIMIZE_ABSOLUTE_ERROR_PARAM_DEFAULT);
        IntParameter    numIterationsParaDefault         = new IntParameter("Number of Iterations", NUM_ITERATIONS_PARAM_DEFAULT);
        BoolParameter   resumeParaDefault                = new BoolParameter("Resume", RESUME_PARAM_DEFAULT);
        FloatParameter  shrinkageParaDefault             = new FloatParameter("Shrinkage", SHRINKAGE_PARAM_DEFAULT);

        Map<String, Parameter> parameters = new HashMap<>();
        parameters.put(batchSizeParaDefault.getParamName(), batchSizeParaDefault);
        parameters.put(minimizeAbsoluteErrorParaDefault.getParamName(), minimizeAbsoluteErrorParaDefault);
        parameters.put(numIterationsParaDefault.getParamName(), numIterationsParaDefault);
        parameters.put(resumeParaDefault.getParamName(), resumeParaDefault);
        parameters.put(shrinkageParaDefault.getParamName(), shrinkageParaDefault);

        return parameters;
    }

    public AdditiveRegression getAdditiveRegressionObject()
    {
        return this.additiveRegression;
    }
}