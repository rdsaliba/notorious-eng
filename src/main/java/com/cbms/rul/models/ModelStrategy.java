/**
 * Interface for the model generation, Part of the strategy design pattern
 * the trainModel will take an Instances object containing the reduced dataset to train
 * the return object will be a Classifier, either a linear regression or LSTM(for future releases)
 *
 * @author Paul Micu
 * @version 1.0
 * @last_edit 11/01/2020
 */
package com.cbms.rul.models;

import weka.classifiers.Classifier;
import weka.core.Instances;

public interface ModelStrategy {

    Classifier trainModel(Instances reducedData) throws Exception;
}
