/*
  Interface for the assetDAO object

  @author Paul Micu
  @last_edit 02/7/2020
 */
package local;

import app.item.Asset;
import app.item.AssetInfo;
import app.item.TrainedModel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public interface AssetDAO {
    ArrayList<Asset> getAssetsToUpdate();

    ArrayList<Asset> getAssetsFromAssetTypeID(int assetTypeID);

    ArrayList<String> getAttributesNameFromAssetID(int assetID);

    String getAssetTypeNameFromID(String assetTypeID);

    ArrayList<Asset> getAllLiveAssets();

    ArrayList<Asset> getAllLiveAssetsDes();

    void addRULEstimation(Double estimation, Asset asset, TrainedModel model);

    void resetAssetUpdate(int assetID);

    void setAssetUpdate(int assetID);

    Asset createAssetFromQueryResult(ResultSet assetsQuery) throws SQLException;

    Asset createFullAssetFromQueryResult(ResultSet assetsQuery) throws SQLException;

    AssetInfo createAssetInfo(int assetID);

    void updateRecommendation(int assetID, String recommendation);
}
