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

import static local.DatabaseConnection.getConnection;
import static org.junit.Assert.assertEquals;

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
        assertEquals(26, attributesName.size());
    }

    @Test
    public void getAssetsFromAssetTypeID() {
        ArrayList<Asset> assets = assetDAO.getAssetsFromAssetTypeID(1);
        assertEquals(0, assets.size());
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
        String addRULQuery="INSERT INTO asset_model_calculation values( 1417 , 1 ,now(), 89.2)";
        try (PreparedStatement ps = getConnection().prepareStatement(addRULQuery)) {
            try (ResultSet rs = ps.executeQuery()) {
                assertEquals(1417,rs.getInt("asset_id"));
                }
            } catch (SQLException e) {
        }
    }

    @Test
    public void updateRecommendation() {
        String updateAsset="UPDATE asset set recommendation = \"Ok\" WHERE asset_id = 101";
        try (PreparedStatement ps = getConnection().prepareStatement(updateAsset)) {
            try (ResultSet rs = ps.executeQuery()) {
                assertEquals("Ok",rs.getString("recommendation"));
            }
        } catch (SQLException e) {
        }
    }

    @Test
    public void resetAssetUpdate() {
        String resetAsset = "UPDATE asset set updated = 0 where asset_id = 69";
        try (PreparedStatement ps = getConnection().prepareStatement(resetAsset)) {
            try (ResultSet rs = ps.executeQuery()) {
                assertEquals(0,rs.getInt("updated"));
            }
        } catch (SQLException e) {
        }
    }

    @Test
    public void setAssetUpdate() {
        String resetAsset = "UPDATE asset set updated = 1 where asset_id = 42";
        try (PreparedStatement ps = getConnection().prepareStatement(resetAsset)) {
            try (ResultSet rs = ps.executeQuery()) {
                assertEquals(1,rs.getInt("updated"));
            }
        } catch (SQLException e) {
        }
    }

    @Test
    public void createAssetFromQueryResult() {

    }

    @Test
    public void createAssetInfo() {

    }
}