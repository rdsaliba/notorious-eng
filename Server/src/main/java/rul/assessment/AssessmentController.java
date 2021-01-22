/*
  The assessment controller is responsible to handle any assessment requests

  @author Paul Micu
  @version 1.0
  @last_edit 11/08/2020
 */
package rul.assessment;

import local.RulDAOImpl;
import weka.classifiers.Classifier;
import weka.core.Instances;

public class AssessmentController {
    private final HealthAssessment healthAssessment;
    private final RecommendationAssessment recommendationAssessment;

    public AssessmentController() {
        healthAssessment = new HealthAssessment();
        recommendationAssessment = new RecommendationAssessment();
    }

    public static double getLatestEstimate(int assetID) {
        RulDAOImpl rulDAO = new RulDAOImpl();
        return rulDAO.getLatestRUL(assetID);
    }

    public double estimateRUL(Instances testData, Classifier classifier) throws Exception {
        return healthAssessment.predictRUL(testData, classifier);
    }

    // RecommendationAssessment
    public String getRecommendation() {
        return null;
    }


}
