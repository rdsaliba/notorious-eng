/*
  Interface for the assetDAO object

  @author Paul Micu
  @version 1.0
  @last_edit 12/27/2020
 */
package local;

import app.TrainedModel;
import app.item.Asset;

import java.util.ArrayList;

public interface AssetDAO {
    ArrayList<Asset> getAssetsToUpdate();

    //Asset getAssetToUpdate(int assetID);

    void deleteAssetByID(int assetID);

    ArrayList<Asset> getAssetsFromAssetTypeID(int assetTypeID);

    ArrayList<String> getAttributesNameFromAssetID(int assetID);

    String getAssetTypeNameFromID(String assetTypeID);

    ArrayList<Asset> getAllLiveAssets();

    void addRULEstimation(Double estimation, Asset asset, TrainedModel model);
}
