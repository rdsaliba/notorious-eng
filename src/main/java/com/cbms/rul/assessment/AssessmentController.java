/*
  The assessment controller is responsible to handle any assessment requests

  @author Paul Micu
 * @version 1.0
 * @last_edit 11/08/2020
 */
package com.cbms.rul.assessment;

import weka.classifiers.Classifier;
import weka.core.Instances;

public class AssessmentController {
    private HealthAssessment healthAssessment;

    public AssessmentController() {
        healthAssessment = new HealthAssessment();
    }

    public double estimateRUL(Instances testData, Classifier classifier) throws Exception {
        return healthAssessment.predictRUL(testData, classifier);
    }


}
