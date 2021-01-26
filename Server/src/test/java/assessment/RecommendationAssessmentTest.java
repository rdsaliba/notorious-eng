package assessment;

import org.junit.Before;
import org.junit.Test;
import rul.assessment.RecommendationAssessment;

import static org.junit.Assert.assertEquals;

public class RecommendationAssessmentTest {
    private static final String ASSET_TYPE = "1";
    private RecommendationAssessment recommendationAssessment;

    @Before
    public void setup() {
        recommendationAssessment = new RecommendationAssessment();
    }

    @Test
    public void testOkRecommendation() {
        assertEquals("Ok",
                recommendationAssessment.getRecommendation(2.0, ASSET_TYPE));
    }

}
