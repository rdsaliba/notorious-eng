/*
  Interface for the assetDAO object

  @author Paul Micu
  @version 1.0
  @last_edit 12/27/2020
 */
package external;

import app.item.Asset;
import app.item.AssetInfo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public interface AssetDAO {

    void deleteAssetByID(int assetID);

    ArrayList<Asset> getAllLiveAssets();

    void insertAsset(Asset asset);

    Asset createAssetFromQueryResult(ResultSet assetsQuery) throws SQLException;

    AssetInfo createAssetInfo(int assetID);

    ArrayList<Asset> getLiveAssetsFromAssetTypeID(String assetTypeID);

    Asset createFullAssetFromQueryResult(ResultSet assetsQuery) throws SQLException;

    ArrayList<Asset> getAssetsFromAssetTypeID(int assetTypeID);

    void setAssetToBeUpdated(int assetID);
}