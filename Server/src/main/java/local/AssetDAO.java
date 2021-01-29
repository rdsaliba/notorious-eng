/*
  Interface for the assetDAO object

  @author Paul Micu
  @version 1.0
  @last_edit 12/27/2020
 */
package local;

import app.TrainedModel;
import app.item.Asset;
import app.item.AssetInfo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public interface AssetDAO {
    ArrayList<Asset> getAssetsToUpdate();

    void deleteAssetByID(int assetID);

    ArrayList<Asset> getAssetsFromAssetTypeID(int assetTypeID);

    ArrayList<String> getAttributesNameFromAssetID(int assetID);

    String getAssetTypeNameFromID(String assetTypeID);

    ArrayList<Asset> getAllLiveAssets();

    ArrayList<Asset> getAllLiveAssetsDes();

    void addRULEstimation(Double estimation, Asset asset, TrainedModel model);

    void insertAsset(Asset asset);

    void resetAssetUpdate(int assetID);

    void setAssetUpdate(int assetID);

    Asset createAssetFromQueryResult(ResultSet assetsQuery) throws SQLException;

    AssetInfo createAssetInfo(int assetID);

    void updateRecommendation(int assetID, String recommendation);
}
