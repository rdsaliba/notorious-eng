/* Third strategy design pattern and implementation of Model Strategy.
 * This class is to be used for the model RandomCommittee
 *
 * @author Khaled
 * @last_edit 03/12/2021
 */

package rul.models;

import app.item.parameter.IntParameter;
import app.item.parameter.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.classifiers.Classifier;
import weka.classifiers.meta.RandomCommittee;
import weka.core.Instances;

import java.util.HashMap;
import java.util.Map;

public class RandomCommitteeModelImpl extends ModelStrategy {
    private static final long serialVersionUID = 8114341751139749245L;

    //Default Parameters
    private static final int NUM_EXECUTION_SLOTS_PARAM_DEFAULT = 1;
    private static final int NUM_ITERATIONS_PARAM_DEFAULT = 10;
    private static final int BATCH_SIZE_PARAM_DEFAULT = 100;
    static Logger logger = LoggerFactory.getLogger(RandomCommitteeModelImpl.class);
    private final IntParameter numExecutionSlotsPara;
    private final IntParameter numIterationsPara;
    private final IntParameter batchSizePara;
    private RandomCommittee randomCommittee;

    public RandomCommitteeModelImpl() {
        numExecutionSlotsPara = new IntParameter("Number of Execution Slots", NUM_EXECUTION_SLOTS_PARAM_DEFAULT);
        numIterationsPara = new IntParameter("Number of Iterations", NUM_ITERATIONS_PARAM_DEFAULT);
        batchSizePara = new IntParameter("Batch Size", BATCH_SIZE_PARAM_DEFAULT);

        addParameter(numExecutionSlotsPara);
        addParameter(numIterationsPara);
        addParameter(batchSizePara);
    }

    /**
     * This function takes the assets as the training dataset, and returns the trained
     * Random Committee classifier.
     *
     * @author Khaled
     */

    @Override
    public Classifier trainModel(Instances dataToTrain) {
        randomCommittee = new RandomCommittee();
        dataToTrain.setClassIndex(dataToTrain.numAttributes() - 1);

        randomCommittee.setNumExecutionSlots(((IntParameter) getParameters().get(numExecutionSlotsPara.getParamName())).getIntValue());
        randomCommittee.setNumIterations(((IntParameter) getParameters().get(numIterationsPara.getParamName())).getIntValue());
        randomCommittee.setBatchSize(String.valueOf(((IntParameter) getParameters().get(batchSizePara.getParamName())).getIntValue()));

        try {
            randomCommittee.buildClassifier(dataToTrain);
        } catch (Exception e) {
            logger.error("Exception: ", e);
        }

        setClassifier(randomCommittee);
        return randomCommittee;
    }


    @Override
    public Map<String, Parameter> getDefaultParameters() {
        IntParameter numExecutionSlotsParaDefault = new IntParameter("Number of Execution Slots", NUM_EXECUTION_SLOTS_PARAM_DEFAULT);
        IntParameter numIterationsParaDefault = new IntParameter("Number of Iterations", NUM_ITERATIONS_PARAM_DEFAULT);
        IntParameter batchSizeParaDefault = new IntParameter("Batch Size", BATCH_SIZE_PARAM_DEFAULT);

        Map<String, Parameter> parameters = new HashMap<>();
        parameters.put(batchSizeParaDefault.getParamName(), batchSizeParaDefault);
        parameters.put(numExecutionSlotsParaDefault.getParamName(), numExecutionSlotsParaDefault);
        parameters.put(numIterationsParaDefault.getParamName(), numIterationsParaDefault);

        return parameters;
    }

    public RandomCommittee getRandomCommitteeObject() {
        return this.randomCommittee;
    }

}