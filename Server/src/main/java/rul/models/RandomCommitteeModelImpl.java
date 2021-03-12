/* Third strategy design pattern and implementation of Model Strategy.
 * This class is to be used for the model RandomCommittee
 *
 * @author Khaled
 * @last_edit 02/14/2021
 */

package rul.models;

import app.item.parameter.Parameter;
import weka.classifiers.Classifier;
import weka.classifiers.meta.RandomCommittee;
import weka.core.Instances;

import java.util.Map;

public class RandomCommitteeModelImpl extends ModelStrategy {

    private int batchSizePara;
    private int numExecutionSlotsParamter;
    private int numIterationsParameter;

    public RandomCommitteeModelImpl()
    {
        this.batchSizePara = 100;
        this.numExecutionSlotsParamter = 1;
        this.numIterationsParameter = 10;
    }

    /**
     * This function takes the assets as the training dataset, and returns the trained
     * Random Committee classifier.
     *
     * @author Khaled
     */

    @Override
    public Classifier trainModel(Instances dataToTrain) {
        Classifier randomCommittee = new RandomCommittee();
        dataToTrain.setClassIndex(dataToTrain.numAttributes() - 1);

        try {
            randomCommittee.buildClassifier(dataToTrain);
        }

        catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return randomCommittee;
    }

    @Override
    public Map<String, Parameter> getDefaultParameters()
    {
        return null;
    }

}