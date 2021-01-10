/*
  Interface for the assetDAO object

  @author Paul Micu
  @version 1.0
  @last_edit 12/27/2020
 */
package com.cbms.source.local;

import com.cbms.app.TrainedModel;
import com.cbms.app.item.Asset;
import com.cbms.app.item.AssetType;

import java.util.ArrayList;

public interface AssetDAO {
    ArrayList<Asset> getAssetsToUpdate();

    void deleteAssetByID(int assetID);

    ArrayList<Asset> getAssetsFromAssetTypeID(int assetTypeID);

    ArrayList<String> getAttributesNameFromAssetID(int assetID);

    String getAssetTypeNameFromID(String assetTypeID);

    ArrayList<Asset> getAllLiveAssets();

    void addRULEstimation(Double estimation, Asset asset, TrainedModel model);
}
