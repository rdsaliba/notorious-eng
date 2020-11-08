/**
 * This class can be extended to the system need.
 * Will include all sensors information, data source origin and whatever is relevant to the asset
 *
 * @author      Roy Saliba
 * @version     1.0
 * @last_edit   11/07/2020
 */
package com.cbms.app.item;

import java.util.ArrayList;

public class AssetInfo {
    ArrayList<AssetAttribute> assetAttributes;

    public AssetInfo(){
        assetAttributes = new ArrayList<>();
    }

    public void addAttribute(AssetAttribute newAtt){
        assetAttributes.add(newAtt);
    }

    public ArrayList<AssetAttribute> getAssetAttributes() {
        return assetAttributes;
    }

    public int getLastRecorderTimeCycle(){
        return assetAttributes.get(0).getMeasurements().size();
    }
}