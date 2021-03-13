/* Another regression model strategy implementation. This model is more flexible than Linear
 * Regression (which is a special case of Additive Regression).
 *
 * @author Khaled
 * @last_edit 02/14/2021
 */
package rul.models;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.classifiers.Classifier;
import weka.classifiers.meta.AdditiveRegression;
import weka.core.Instances;

public class AdditiveRegressionModelImpl extends ModelStrategy {

    static Logger logger = LoggerFactory.getLogger(AdditiveRegressionModelImpl.class);

    /**
     * This function takes the assets as the training dataset, and returns the trained
     * Additive Regression classifier.
     *
     * @author Khaled
     */
    @Override
    public Classifier trainModel(Instances dataToTrain) {
        Classifier additiveRegression = new AdditiveRegression();
        dataToTrain.setClassIndex(dataToTrain.numAttributes() - 1);

        try {
            additiveRegression.buildClassifier(dataToTrain);
        } catch (Exception e) {
            logger.error("Exception:", e);
        }

        return additiveRegression;
    }

}