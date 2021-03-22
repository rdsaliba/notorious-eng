/* SMOReg model strategy implementation. Note that this model requires significant time
 * to train or evaluate.
 *
 * @author Khaled
 * @last_edit 03/12/2021
 */

package rul.models;

import app.item.parameter.FloatParameter;
import app.item.parameter.IntParameter;
import app.item.parameter.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.classifiers.Classifier;
import weka.classifiers.functions.SMOreg;
import weka.core.Instances;

import java.util.HashMap;
import java.util.Map;

public class SMORegModelImpl extends ModelStrategy {

    //Default Parameters
    private static final float C_COMPLEXITY_PARAM_DEFAULT = 1.0F;
    private static final int BATCH_SIZE_PARAM_DEFAULT = 100;

    private final FloatParameter cComplexityPara;
    private final IntParameter batchSizePara;

    private SMOreg smOreg;

    public SMORegModelImpl() {
        cComplexityPara = new FloatParameter("C Complexity", C_COMPLEXITY_PARAM_DEFAULT);
        batchSizePara = new IntParameter("Batch Size", BATCH_SIZE_PARAM_DEFAULT);

        addParameter(cComplexityPara);
        addParameter(batchSizePara);
    }

    static Logger logger = LoggerFactory.getLogger(SMORegModelImpl.class);

    /**
     * This function takes the assets as the training dataset, and returns the trained
     * SMOReg classifier.
     *
     * @author Khaled
     */
    @Override
    public Classifier trainModel(Instances dataToTrain) {
        smOreg = new SMOreg();
        dataToTrain.setClassIndex(dataToTrain.numAttributes() - 1);

        smOreg.setC(((FloatParameter) getParameters().get(cComplexityPara.getParamName())).getFloatValue());
        smOreg.setBatchSize(String.valueOf(((IntParameter) getParameters().get(batchSizePara.getParamName())).getIntValue()));

        try {
            smOreg.buildClassifier(dataToTrain);
        } catch (Exception e) {
            logger.error("Exception: ", e);
        }

        setClassifier(smOreg);
        return smOreg;
    }

    @Override
    public Map<String, Parameter> getDefaultParameters() {
        FloatParameter cComplexityParaDefault = new FloatParameter("C Complexity", C_COMPLEXITY_PARAM_DEFAULT);
        IntParameter batchSizeParaDefault = new IntParameter("Batch Size", BATCH_SIZE_PARAM_DEFAULT);

        Map<String, Parameter> parameters = new HashMap<>();
        parameters.put(cComplexityParaDefault.getParamName(), cComplexityParaDefault);
        parameters.put(batchSizeParaDefault.getParamName(), batchSizeParaDefault);

        return parameters;
    }

    public SMOreg getSmOregObject() {
        return this.smOreg;
    }

}