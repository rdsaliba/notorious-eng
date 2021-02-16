package UnitTests;

import app.item.AssetType;
import external.AssetTypeDAOImpl;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class AssetTypeDAOImplTest {

    @Test
    public void testRemovingFunctionality(){
        String testName = "Testing type";
        AssetTypeDAOImpl assetTypeDAO = new AssetTypeDAOImpl();
        AssetType assetType = new AssetType();
        assetType.setName(testName);
        assetType.setThresholdList(new ArrayList<>());

        // adding the new asset type
        String testID = String.valueOf(assetTypeDAO.insertAssetType(assetType));

        // first assert
        Assert.assertEquals("Checking a new asset type was added", testName, assetTypeDAO.getNameFromID(testID) );

        // deleting the newly inserted asset type
        assetTypeDAO.deleteAssetTypeByID(testID);

        // second assert
        Assert.assertFalse(assetTypeDAO.getAssetTypeList().stream()
                .filter(s -> s.getId().equals(testID))
                .findAny()
                .isPresent());

    }
}
