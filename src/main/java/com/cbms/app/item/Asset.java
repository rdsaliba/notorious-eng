/**
 * Model for the assets of the predictive system.
 *
 * @author      Roy Saliba
 * @version     1.0
 * @last_edit   11/07/2020
 */
package com.cbms.app.item;

public class Asset extends Item {

    public String serialNo;
    public String assetType;
    public String location;
    public String description;
    public AssetInfo assetInfo;

    public Asset(){

    }


    public Asset(String serialNo, String assetType, String location, String description){
        this.assetType = assetType;
        this.serialNo = serialNo;
        this.location = location;
        this.description = description;

    }

    public Asset(String serialNo, String assetType, String location, String description, AssetInfo assetInfo){
        this.assetType = assetType;
        this.serialNo = serialNo;
        this.location = location;
        this.description = description;
        this.assetInfo = assetInfo;

    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AssetInfo getAssetInfo() {
        return assetInfo;
    }

    public void setAssetInfo(AssetInfo assetInfo) {
        this.assetInfo = assetInfo;
    }
}