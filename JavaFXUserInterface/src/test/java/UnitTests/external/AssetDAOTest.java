package UnitTests.external;

import app.item.Asset;
import external.AssetDAOImpl;
import external.DAO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class AssetDAOTest extends DAO {
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
    public void getAssetsFromAssetTypeIDTest() {
        List<Asset> liveAssetList = assetDAO.getLiveAssetsFromAssetTypeID(1);
        int archivedAssetList = assetDAO.getArchivedAssetsFromAssetTypeID(1);
        assertEquals(100, archivedAssetList);
        assertEquals(97, liveAssetList.size());
    }

    @Test
    public void deleteAssetMeasurementsAfterTimeCycleTest() {
        String insert_into_asset = "insert into attribute_measurements (asset_id,attribute_id,time,value) VALUES(1,1,193,0)";
        try (PreparedStatement ps = getConnection().prepareStatement(insert_into_asset)) {
            ps.executeQuery();
        } catch (SQLException e) {
        }
        assetDAO.deleteAssetMeasurementsAfterTimeCycle(1, 192);
        String select_from_asset = "select * from attribute_measurements  where asset_id=1 and time >192 order by time desc";
        try (PreparedStatement ps = getConnection().prepareStatement(select_from_asset)) {
            try (ResultSet rs = ps.executeQuery()) {
                assertNull(rs.getObject(0));
            }
        } catch (SQLException e) {
        }
    }

    @Test
    public void setAssetToBeArchivedTest() {
        assetDAO.setAssetToBeArchived(101);
        String select_from_asset = "select * from asset  where asset_id=101 ";
        String update_asset = "update asset set archived=0 where asset_id=101";
        try (PreparedStatement ps = getConnection().prepareStatement(select_from_asset)) {
            try (ResultSet rs = ps.executeQuery()) {
                assertEquals(1, rs.getInt("archived"));
            }
        } catch (SQLException e) {
        }
        try (PreparedStatement ps = getConnection().prepareStatement(update_asset)) {
            ps.executeQuery();
        } catch (SQLException e) {
        }

    }

    @Test
    public void setAssetToBeUpdatedTest() {
        assetDAO.setAssetToBeUpdated(101);
        String select_from_asset = "select * from asset  where asset_id=101 ";
        String update_asset = "update asset set updated=0 where asset_id=101";
        try (PreparedStatement ps = getConnection().prepareStatement(select_from_asset)) {
            try (ResultSet rs = ps.executeQuery()) {
                assertEquals(1, rs.getInt("updated"));
            }
        } catch (SQLException e) {
        }
        try (PreparedStatement ps = getConnection().prepareStatement(update_asset)) {
            ps.executeQuery();
        } catch (SQLException e) {
        }
    }

    @Test
    public void getArchivedAssetsFromAssetTypeIDTest() {
        assetDAO.getArchivedAssetsFromAssetTypeID(1);
        int numberAssets = assetDAO.getArchivedAssetsFromAssetTypeID(1);
        assertEquals(100, numberAssets);
    }

    @Test
    public void getLiveAssetsFromAssetTypeIDTest() {
        assetDAO.getLiveAssetsFromAssetTypeID(1);
        List<Asset> assets = assetDAO.getLiveAssetsFromAssetTypeID(1);
        assertEquals(97, assets.size());
        assertEquals(101, assets.get(0).getId());
        assertEquals("1", assets.get(0).getAssetTypeID());
    }
}