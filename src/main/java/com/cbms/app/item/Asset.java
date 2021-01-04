/*
  Model for the assets of the predictive system.

  @author Roy Saliba
  @version 1.0
  @last_edit 11/07/2020
 */
package com.cbms.app.item;

public class Asset extends Item {

    private String serialNo;
    private String assetTypeID;
    private String assetTypeName;
    private String location;
    private String description;
    private String recommendation;
    private AssetInfo assetInfo;

    public Asset() {

    }


    public Asset(String serialNo, String assetTypeID, String assetTypeName, String location, String description, String recommendation) {
        this.assetTypeID = assetTypeID;
        this.assetTypeName = assetTypeName;
        this.serialNo = serialNo;
        this.location = location;
        this.description = description;
        this.recommendation = recommendation;
    }

    public Asset(String serialNo, String assetTypeID, String assetTypeName, String location, String description, String recommendation, AssetInfo assetInfo) {
        this.assetTypeID = assetTypeID;
        this.assetTypeName = assetTypeName;
        this.serialNo = serialNo;
        this.location = location;
        this.description = description;
        this.assetInfo = assetInfo;
        this.recommendation = recommendation;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getAssetTypeID() {
        return assetTypeID;
    }

    public void setAssetTypeID(String assetTypeID) {
        this.assetTypeID = assetTypeID;
    }

    public String getAssetTypeName() { return assetTypeName; }

    public void setAssetTypeName(String assetTypeName) { this.assetTypeName = assetTypeName; }

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

    public String getRecommendation() { return recommendation; }

    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }

    @Override
    public String toString() {
        return "Asset{" +
                "serialNo='" + serialNo + '\'' +
                ", assetTypeID='" + assetTypeID + '\'' +
                ", location='" + location + '\'' +
                ", description='" + description + '\'' +
                ", recommendation='" + recommendation + '\'' +
                ", assetInfo=" + assetInfo +
                '}';
    }
}