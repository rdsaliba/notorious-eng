/* Another regression model strategy implementation. This model is more flexible than Linear
 * Regression (which is a special case of Additive Regression).
 *
 * @author Khaled
 * @last_edit 02/14/2021
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

    private final String BATCH_SIZE_PARAM_DEFAULT = "100";
    private final boolean MINIMIZE_ABSOLUTE_ERROR_PARAM_DEFAULT = false;
    private final int NUM_ITERATIONS_PARAM_DEFAULT = 10;
    private final boolean RESUME_PARAM_DEFAULT = false;
    private final float SHRINKAGE_PARAM_DEFAULT = 1.0F;

    private StringParameter batchSizePara;
    private BoolParameter minimizeAbsoluteErrorPara;
    private IntParameter numIterationsPara;
    private BoolParameter resumePara;
    private FloatParameter shrinkagePara;

    private AdditiveRegression additiveRegression;

    public AdditiveRegressionModelImpl()
    {
        batchSizePara = new StringParameter("batchSize", BATCH_SIZE_PARAM_DEFAULT);
        minimizeAbsoluteErrorPara = new BoolParameter("minimizeAbsoluteError", MINIMIZE_ABSOLUTE_ERROR_PARAM_DEFAULT);
        numIterationsPara = new IntParameter("numIterations", NUM_ITERATIONS_PARAM_DEFAULT);
        resumePara = new BoolParameter("resume", RESUME_PARAM_DEFAULT);
        shrinkagePara = new FloatParameter("shrinkage", SHRINKAGE_PARAM_DEFAULT);


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
        StringParameter batchSizeParaDefault             = new StringParameter("batchSize", BATCH_SIZE_PARAM_DEFAULT);
        BoolParameter   minimizeAbsoluteErrorParaDefault = new BoolParameter("minimizeAbsoluteError", MINIMIZE_ABSOLUTE_ERROR_PARAM_DEFAULT);
        IntParameter    numIterationsParaDefault         = new IntParameter("numIterations", NUM_ITERATIONS_PARAM_DEFAULT);
        BoolParameter   resumeParaDefault                = new BoolParameter("resume", RESUME_PARAM_DEFAULT);
        FloatParameter  shrinkageParaDefault             = new FloatParameter("shrinkage", SHRINKAGE_PARAM_DEFAULT);

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