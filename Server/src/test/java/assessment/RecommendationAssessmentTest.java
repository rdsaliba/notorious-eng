package assessment;

import local.AssetTypeDAOImpl;
import org.junit.Before;
import org.junit.Test;
import rul.assessment.RecommendationAssessment;

import static org.junit.Assert.assertEquals;
import static utilities.Constants.*;

public class RecommendationAssessmentTest {
    private static final String ASSET_TYPE = "1";
    private RecommendationAssessment recommendationAssessment;
    private AssetTypeDAOImpl assetTypeDAO;

    @Before
    public void setup() {
        recommendationAssessment = new RecommendationAssessment();
        assetTypeDAO = new AssetTypeDAOImpl();
    }

    @Test
    public void testOkRecommendation() {
        assertEquals(OK_THRESHOLD,
                recommendationAssessment.getRecommendation(assetTypeDAO.getAssetTypeBoundaries(ASSET_TYPE).get(OK_THRESHOLD), ASSET_TYPE));
    }

    @Test
    public void testAdvisoryRecommendation() {
        assertEquals(ADVISORY_THRESHOLD,
                recommendationAssessment.getRecommendation(assetTypeDAO.getAssetTypeBoundaries(ASSET_TYPE).get(ADVISORY_THRESHOLD), ASSET_TYPE));
    }

    @Test
    public void testWarningRecommendation() {
        assertEquals(WARNING_THRESHOLD,
                recommendationAssessment.getRecommendation(assetTypeDAO.getAssetTypeBoundaries(ASSET_TYPE).get(WARNING_THRESHOLD), ASSET_TYPE));
    }

    @Test
    public void testFailedRecommendation() {
        assertEquals(FAILED_THRESHOLD,
                recommendationAssessment.getRecommendation(assetTypeDAO.getAssetTypeBoundaries(ASSET_TYPE).get(FAILED_THRESHOLD), ASSET_TYPE));
    }

    @Test
    public void testCautionRecommendation() {
        assertEquals(CAUTION_THRESHOLD,
                recommendationAssessment.getRecommendation(assetTypeDAO.getAssetTypeBoundaries(ASSET_TYPE).get(CAUTION_THRESHOLD), ASSET_TYPE));
    }

}
