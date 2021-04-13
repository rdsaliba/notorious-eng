package UnitTests.local;

import local.AssetTypeDAOImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class AssetTypeDAOImplTest {
    private AssetTypeDAOImpl assetTypeDAO;
    @Before
    public void setUp() {
        assetTypeDAO = new AssetTypeDAOImpl();
    }

    @After
    public void tearDown() {
        assetTypeDAO = null;
    }

    @Test
    public void getAssetTypeThresholds() {
        HashMap<String, Double> thresholds = assetTypeDAO.getAssetTypeThresholds("3");
        assertEquals(5,thresholds.size());
    }
}