package UnitTests.assessment;

import local.AssetTypeDAOImpl;
import org.junit.Before;
import org.junit.Test;
import rul.assessment.RecommendationAssessment;

import static org.junit.Assert.assertEquals;

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
        assertEquals("Ok",
                recommendationAssessment.getRecommendation( assetTypeDAO.getAssetTypeBoundaries(ASSET_TYPE).get("Ok"), ASSET_TYPE));
    }

}
