/*
  Interface for the assetDAO object

  @author Paul Micu
  @version 1.0
  @last_edit 12/27/2020
 */
package external;

import app.item.Asset;
import app.item.AssetInfo;
import javafx.scene.image.Image;

import java.io.FileInputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface AssetDAO {

    void deleteAssetByID(int assetID);

    void insertAsset(Asset asset);

    void updateAsset(Asset asset);

    AssetInfo createAssetInfo(int assetID);

    List<Asset> getLiveAssetsFromAssetTypeID(int assetTypeID);

    Asset createFullAssetFromQueryResult(ResultSet assetsQuery) throws SQLException;

    List<Asset> getArchivedAssetsFromAssetTypeID(int assetTypeID);

    void setAssetToBeUpdated(int assetID);

    void setAssetToBeArchived(int assetID);

    void deleteAssetMeasurementsAfterTimeCycle(int assetID, int time);
    
    PreparedStatement storeImage(FileInputStream fileInputStream, String name);

    Image findImageById(int imageId);

    int findImageIdByName(String name);
}
