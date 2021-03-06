package UnitTests.external;

import app.item.Asset;
import external.AssetDAOImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class AssetDAOTest {
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
        List<Asset> archivedAssetList = assetDAO.getArchivedAssetsFromAssetTypeID(1);
        assertEquals(100, archivedAssetList.size());
        assertEquals(97, liveAssetList.size());
    }

    @Test
    public void getAllLiveAssetsTest() {
        List<Asset> allLiveAssetList = assetDAO.getAllLiveAssets();
        assertEquals(704, allLiveAssetList.size());
    }
}