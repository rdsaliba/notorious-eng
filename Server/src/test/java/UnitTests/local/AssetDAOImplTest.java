package UnitTests.local;

import app.item.Asset;
import local.AssetDAOImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class AssetDAOImplTest {
    private AssetDAOImpl assetDAO;

    @Before
    public void setUp() {
        assetDAO = new AssetDAOImpl();
    }

    @After
    public void tearDown() {
        assetDAO = null;
    }

    @Test
    public void getAssetsToUpdate() {
        ArrayList<Asset> assets = assetDAO.getAssetsToUpdate();
        assertEquals(0,assets.size());
    }

    @Test
    public void getAttributesNameFromAssetID() {
        List<String> attributesName = assetDAO.getAttributesNameFromAssetID(2);
        assertEquals(7462, attributesName.size());
    }

    @Test
    public void getAssetsFromAssetTypeID() {
        ArrayList<Asset> assets = assetDAO.getAssetsFromAssetTypeID(1);
        assertEquals(260, assets.size());
    }

    @Test
    public void getAssetTypeNameFromID() {
        String name = assetDAO.getAssetTypeNameFromID("1");
        assertEquals("Turbojet Engine",name);
    }

    @Test
    public void getAllLiveAssets() {
        ArrayList<Asset> assets = assetDAO.getAllLiveAssets();
        assertEquals(704,assets.size());
    }

    @Test
    public void addRULEstimation() {

    }

    @Test
    public void updateRecommendation() {
    }

    @Test
    public void resetAssetUpdate() {
    }

    @Test
    public void setAssetUpdate() {
    }

    @Test
    public void createAssetFromQueryResult() {
    }

    @Test
    public void createAssetInfo() {
    }
}