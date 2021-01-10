package com.cbms.source.local;

import com.cbms.app.item.AssetType;

import java.util.ArrayList;

public interface AssetTypeDAO {
    void insertAssetType(AssetType assetType);
    ArrayList<String> getAssetTypeList();
}
