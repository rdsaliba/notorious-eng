package rul.assessment;


import local.AssetTypeDAOImpl;
import java.util.HashMap;

public class RecommendationAssessment {

    private AssetTypeDAOImpl assetTypeDAO;

    public RecommendationAssessment() {
        assetTypeDAO = new AssetTypeDAOImpl();
    }

    public String getRecommendation(Double rulEstimation, String assetTypeID) {
        HashMap<String, Double> boundaries = getAssetTypeBoundaries(assetTypeID);
        return calculateBoundaries(rulEstimation, boundaries);
    }

    private HashMap getAssetTypeBoundaries(String assetTypeID) {
        return assetTypeDAO.getAssetTypeBoundaries(assetTypeID);
    }

    private String calculateBoundaries(Double rulEstimation, HashMap<String, Double> boundaries) {
        if (rulEstimation >= boundaries.get("Ok"))
            return "Ok";
        else if (rulEstimation >= boundaries.get("Advisory"))
            return "Advisory";
        else if (rulEstimation >= boundaries.get("Caution"))
            return "Caution";
        else if (rulEstimation >= boundaries.get("Warning"))
            return "Warning";
        else
            return "Failed";
    }
}
