/*
  The RecommendationAssessment class is responsible to handle any assessment requests coming from the assessment Controller

  @author Paul Micu
  @last_edit 11/08/2020
 */
package rul.assessment;


import local.AssetTypeDAOImpl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

import static utilities.Constants.*;

public class RecommendationAssessment {

    private final AssetTypeDAOImpl assetTypeDAO;

    public RecommendationAssessment() {
        assetTypeDAO = new AssetTypeDAOImpl();
    }

    public String getRecommendation(Double rulEstimation, String assetTypeID) {
        HashMap<String, Double> thresholds = getAssetTypeThresholds(assetTypeID);
        return calculateThresholds(rulEstimation, thresholds);
    }

    private HashMap<String, Double> getAssetTypeThresholds(String assetTypeID) {
        return assetTypeDAO.getAssetTypeThresholds(assetTypeID);
    }

    private String calculateThresholds(Double rulEstimation, HashMap<String, Double> thresholds) {
        AtomicReference<String> recommendation = new AtomicReference<>(OK_THRESHOLD);
        String[] thresholdValues = {ADVISORY_THRESHOLD, CAUTION_THRESHOLD, WARNING_THRESHOLD, FAILED_THRESHOLD};

        Arrays.stream(thresholdValues).forEach(t -> {
            if (thresholds.containsKey(t) && rulEstimation != null && rulEstimation <= thresholds.get(t))
                recommendation.set(t);
        });
        return recommendation.get();

    }
}
