/* Third strategy design pattern and implementation of Model Strategy.
 * This class is to be used for the model RandomCommittee
 *
 * @author Khaled
 * @last_edit 02/14/2021
 */

package rul.models;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.classifiers.Classifier;
import weka.classifiers.meta.RandomCommittee;
import weka.core.Instances;

public class RandomCommitteeModelImpl implements ModelStrategy {

    Logger logger = LoggerFactory.getLogger(RandomCommitteeModelImpl.class);

    /**
     * This function takes the assets as the training dataset, and returns the trained
     * Random Committee classifier.
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
            logger.error("Exception: ", e);
            return null;
        }

        return randomCommittee;
    }
}