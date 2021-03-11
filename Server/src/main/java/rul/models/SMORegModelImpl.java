/* SMOReg model strategy implementation. Note that this model requires significant time
 * to train or evaluate.
 *
 * @author Khaled
 * @last_edit 02/14/2021
 */

package rul.models;

import weka.classifiers.Classifier;
import weka.classifiers.functions.SMOreg;
import weka.core.Instances;

public class SMORegModelImpl extends ModelStrategy {

    private int batchSizeParameter;
    private double cComplexityParameter;

    public SMORegModelImpl()
    {
        batchSizeParameter = 100;
        cComplexityParameter = 1.0;
    }
    /**
     * This function takes the assets as the training dataset, and returns the trained
     * SMOReg classifier.
     *
     * @author Khaled
     */
    @Override
    public Classifier trainModel(Instances dataToTrain) {
        SMOreg smOreg = new SMOreg();
        dataToTrain.setClassIndex(dataToTrain.numAttributes() - 1);

        try {
            smOreg.buildClassifier(dataToTrain);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return smOreg;
    }
}