/*
  Interface for the model generation, Part of the strategy design pattern
  the trainModel will take an Instances object containing the reduced dataset to train
  the return object will be a Classifier, either a linear regression or LSTM(for future releases)

  @author Paul Micu
  @last_edit 11/01/2020
 */
package rul.models;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.classifiers.Classifier;
import weka.core.Instances;

public interface ModelStrategy {

    Logger logger = LoggerFactory.getLogger(ModelStrategy.class);


    Classifier trainModel(Instances reducedData);

}
