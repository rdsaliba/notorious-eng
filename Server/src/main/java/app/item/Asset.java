/*
  Model for the assets of the predictive system.

  @author Roy Saliba
  @last_edit 02/7/2020
 */
package app.item;

import javafx.beans.property.SimpleStringProperty;

import static utilities.Constants.*;

public class Asset extends Item {

    private final SimpleStringProperty rul;
    private String serialNo;
    private String name;
    private String assetTypeID;
    private String location;
    private String description;
    private String recommendation;
    private String manufacturer;
    private String category;
    private String site;
    private String assetTypeName;
    private AssetInfo assetInfo;

    public Asset() {
        rul = new SimpleStringProperty();
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAssetTypeID() {
        return assetTypeID;
    }

    public void setAssetTypeID(String assetTypeID) {
        this.assetTypeID = assetTypeID;
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

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public SimpleStringProperty getRul() {
        return rul;
    }

    public void setRul(String rul) {
        this.rul.setValue(rul);
    }

    public String getAssetTypeName() {
        return assetTypeName;
    }

    public void setAssetTypeName(String assetTypeName) {
        this.assetTypeName = assetTypeName;
    }

    public int mapCriticality() {
        switch (this.getRecommendation()) {
            case OK_THRESHOLD:
                return 1;
            case ADVISORY_THRESHOLD:
                return 2;
            case CAUTION_THRESHOLD:
                return 3;
            case WARNING_THRESHOLD:
                return 4;
            case FAILED_THRESHOLD:
                return 5;

            default:
                return 0;
        }
    }

    @Override
    public String toString() {
        return "Asset{" +
                "serialNo='" + serialNo + '\'' +
                ", name='" + name + '\'' +
                ", assetTypeID='" + assetTypeID + '\'' +
                ", location='" + location + '\'' +
                ", description='" + description + '\'' +
                ", recommendation='" + recommendation + '\'' +
                ", assetInfo=" + assetInfo +
                ", manufacturer='" + manufacturer + '\'' +
                ", category='" + category + '\'' +
                ", site='" + site + '\'' +
                '}';
    }
}