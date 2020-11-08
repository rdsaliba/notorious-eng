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
