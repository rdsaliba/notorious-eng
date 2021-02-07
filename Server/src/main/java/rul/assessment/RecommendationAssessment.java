package rul.assessment;


import local.AssetTypeDAOImpl;

import java.util.HashMap;

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
        if (rulEstimation >= thresholds.get("Ok"))
            return "Ok";
        else if (rulEstimation >= thresholds.get("Advisory"))
            return "Advisory";
        else if (rulEstimation >= thresholds.get("Caution"))
            return "Caution";
        else if (rulEstimation >= thresholds.get("Warning"))
            return "Warning";
        else
            return "Failed";
    }
}
