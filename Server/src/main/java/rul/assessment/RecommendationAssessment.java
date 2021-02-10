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
        HashMap<String, Double> boundaries = getAssetTypeBoundaries(assetTypeID);
        return calculateBoundaries(rulEstimation, boundaries);
    }

    private HashMap<String, Double> getAssetTypeBoundaries(String assetTypeID) {
        return assetTypeDAO.getAssetTypeBoundaries(assetTypeID);
    }

    private String calculateBoundaries(Double rulEstimation, HashMap<String, Double> boundaries) {
        AtomicReference<String> recommendation = new AtomicReference<>(OK_THRESHOLD);
        String[] thresholds = {ADVISORY_THRESHOLD, CAUTION_THRESHOLD, WARNING_THRESHOLD, FAILED_THRESHOLD};

        Arrays.stream(thresholds).forEach(t -> {
            if (boundaries.containsKey(t) && rulEstimation != null && rulEstimation <= boundaries.get(t))
                recommendation.set(t);
        });
        return recommendation.get();

    }
}
