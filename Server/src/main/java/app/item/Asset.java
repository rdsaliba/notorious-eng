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
    private final SimpleStringProperty serialNo;
    private final SimpleStringProperty name;
    private final SimpleStringProperty assetTypeID;
    private final SimpleStringProperty location;
    private final SimpleStringProperty description;
    private final SimpleStringProperty recommendation;
    private final SimpleStringProperty manufacturer;
    private final SimpleStringProperty category;
    private final SimpleStringProperty site;
    private final SimpleStringProperty assetTypeName;
    private AssetInfo assetInfo;

    public Asset() {
        rul = new SimpleStringProperty();
        serialNo = new SimpleStringProperty();
        name = new SimpleStringProperty();
        assetTypeID = new SimpleStringProperty();
        location = new SimpleStringProperty();
        description = new SimpleStringProperty();
        recommendation = new SimpleStringProperty();
        manufacturer = new SimpleStringProperty();
        category = new SimpleStringProperty();
        site = new SimpleStringProperty();
        assetTypeName = new SimpleStringProperty();
    }

    public SimpleStringProperty rulProperty() {
        return rul;
    }

    public SimpleStringProperty serialNoProperty() {
        return serialNo;
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public SimpleStringProperty assetTypeIDProperty() {
        return assetTypeID;
    }

    public SimpleStringProperty locationProperty() {
        return location;
    }

    public SimpleStringProperty descriptionProperty() {
        return description;
    }

    public SimpleStringProperty recommendationProperty() {
        return recommendation;
    }

    public SimpleStringProperty manufacturerProperty() {
        return manufacturer;
    }

    public SimpleStringProperty categoryProperty() {
        return category;
    }

    public SimpleStringProperty siteProperty() {
        return site;
    }

    public SimpleStringProperty assetTypeNameProperty() {
        return assetTypeName;
    }

    public String getSerialNo() {
        return serialNo.getValue();
    }

    public void setSerialNo(String serialNo) {
        this.serialNo.setValue(serialNo);
    }

    public String getName() {
        return name.getValue();
    }

    public void setName(String name) {
        this.name.setValue(name);
    }

    public String getAssetTypeID() {
        return assetTypeID.getValue();
    }

    public void setAssetTypeID(String assetTypeID) {
        this.assetTypeID.setValue(assetTypeID);
    }

    public String getLocation() {
        return location.getValue();
    }

    public void setLocation(String location) {
        this.location.setValue(location);
    }

    public String getDescription() {
        return description.getValue();
    }

    public void setDescription(String description) {
        this.description.setValue(description);
    }

    public AssetInfo getAssetInfo() {
        return assetInfo;
    }

    public void setAssetInfo(AssetInfo assetInfo) {
        this.assetInfo = assetInfo;
    }

    public String getRecommendation() {
        return recommendation.getValue();
    }

    public void setRecommendation(String recommendation) {
        this.recommendation.setValue(recommendation);
    }

    public String getManufacturer() {
        return manufacturer.getValue();
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer.setValue(manufacturer);
    }

    public String getCategory() {
        return category.getValue();
    }

    public void setCategory(String category) {
        this.category.setValue(category);
    }

    public String getSite() {
        return site.getValue();
    }

    public void setSite(String site) {
        this.site.setValue(site);
    }

    public SimpleStringProperty getRul() {
        return rul;
    }

    public void setRul(String rul) {
        this.rul.setValue(rul);
    }

    public String getAssetTypeName() {
        return assetTypeName.getValue();
    }

    public void setAssetTypeName(String assetTypeName) {
        this.assetTypeName.setValue(assetTypeName);
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