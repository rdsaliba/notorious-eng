/*
  The Asset Type List class keeps track of asset types and their threshold values.
  @author
  @last_edit 02/7/2020
 */
package utilities;

import app.item.AssetType;
import app.item.AssetTypeParameter;

import java.util.ArrayList;

public class AssetTypeList {
    private AssetType assetType;
    private final int liveAssets;
    private final int archivedAssets;
    private String valueOk;
    private final int countOk;
    private String valueCaution;
    private final int countCaution;
    private String valueAdvisory;
    private final int countAdvisory;
    private String valueWarning;
    private final int countWarning;
    private String valueFailed;
    private final int countFailed;

    public AssetTypeList(AssetType assetType, int liveAssets, int archivedAssets, String valueOk, int countOk, String valueCaution, int countCaution, String valueAdvisory, int countAdvisory, String valueWarning, int countWarning, String valueFailed, int countFailed) {
        this.assetType = assetType;
        this.liveAssets = liveAssets;
        this.archivedAssets = archivedAssets;
        this.valueOk = valueOk;
        this.countOk = countOk;
        this.valueCaution = valueCaution;
        this.countCaution = countCaution;
        this.valueAdvisory = valueAdvisory;
        this.countAdvisory = countAdvisory;
        this.valueWarning = valueWarning;
        this.countWarning = countWarning;
        this.valueFailed = valueFailed;
        this.countFailed = countFailed;
    }

    public AssetTypeList(AssetTypeList copy) {
        this.assetType = new AssetType(copy.getAssetType());
        this.liveAssets = copy.getLiveAssets();
        this.archivedAssets = copy.getArchivedAssets();
        this.valueOk = copy.getValueOk();
        this.valueCaution = copy.getValueCaution();
        this.valueAdvisory = copy.getValueAdvisory();
        this.valueWarning = copy.getValueWarning();
        this.valueFailed = copy.getValueFailed();
        this.countOk = copy.getCountOk();
        this.countCaution = copy.getCountCaution();
        this.countAdvisory = copy.getCountAdvisory();
        this.countWarning = copy.getCountWarning();
        this.countFailed = copy.getCountFailed();
    }

    public AssetType getAssetType() {
        return assetType;
    }

    public void setAssetType(AssetType assetType) {
        this.assetType = assetType;
    }

    public int getLiveAssets() {
        return liveAssets;
    }

    public int getArchivedAssets() {
        return archivedAssets;
    }

    public String getValueOk() {
        return valueOk;
    }

    public void setValueOk(String valueOk) {
        this.valueOk = valueOk;
    }

    public String getValueCaution() {
        return valueCaution;
    }

    public void setValueCaution(String valueCaution) {
        this.valueCaution = valueCaution;
    }

    public String getValueAdvisory() {
        return valueAdvisory;
    }

    public void setValueAdvisory(String valueAdvisory) {
        this.valueAdvisory = valueAdvisory;
    }

    public String getValueWarning() {
        return valueWarning;
    }

    public void setValueWarning(String valueWarning) {
        this.valueWarning = valueWarning;
    }

    public String getValueFailed() {
        return valueFailed;
    }

    public void setValueFailed(String valueFailed) {
        this.valueFailed = valueFailed;
    }

    public String getId() {
        return assetType.getId();
    }

    public String getName() {
        return assetType.getName();
    }

    public void setName(String name) {
        this.assetType.setName(name);
    }

    public String getDescription() {
        return assetType.getDescription();
    }

    public int getCountOk() {
        return countOk;
    }


    public int getCountCaution() {
        return countCaution;
    }


    public int getCountAdvisory() {
        return countAdvisory;
    }


    public int getCountWarning() {
        return countWarning;
    }


    public int getCountFailed() {
        return countFailed;
    }


    /**
     * This methode generates an AssetType object given the values of the current object
     *
     * @return AssetType Object
     * @author Paul
     */
    public AssetType toAssetType() {
        AssetType newAssetType = new AssetType();
        ArrayList<AssetTypeParameter> newAssetTypeParameter = new ArrayList<>();
        newAssetType.setId(assetType.getId());
        newAssetType.setName(assetType.getName());
        newAssetType.setDescription(assetType.getDescription());

        if (this.valueOk.isEmpty())
            newAssetTypeParameter.add(new AssetTypeParameter(TextConstants.OK_THRESHOLD, null));
        else
            newAssetTypeParameter.add(new AssetTypeParameter(TextConstants.OK_THRESHOLD, Double.parseDouble(this.valueOk)));

        if (this.valueAdvisory.isEmpty())
            newAssetTypeParameter.add(new AssetTypeParameter(TextConstants.ADVISORY_THRESHOLD, null));
        else
            newAssetTypeParameter.add(new AssetTypeParameter(TextConstants.ADVISORY_THRESHOLD, Double.parseDouble(this.valueAdvisory)));

        if (this.valueCaution.isEmpty())
            newAssetTypeParameter.add(new AssetTypeParameter(TextConstants.CAUTION_THRESHOLD, null));
        else
            newAssetTypeParameter.add(new AssetTypeParameter(TextConstants.CAUTION_THRESHOLD, Double.parseDouble(this.valueCaution)));

        if (this.valueWarning.isEmpty())
            newAssetTypeParameter.add(new AssetTypeParameter(TextConstants.WARNING_THRESHOLD, null));
        else
            newAssetTypeParameter.add(new AssetTypeParameter(TextConstants.WARNING_THRESHOLD, Double.parseDouble(this.valueWarning)));

        if (this.valueFailed.isEmpty())
            newAssetTypeParameter.add(new AssetTypeParameter(TextConstants.FAILED_THRESHOLD, null));
        else
            newAssetTypeParameter.add(new AssetTypeParameter(TextConstants.FAILED_THRESHOLD, Double.parseDouble(this.valueFailed)));

        newAssetType.setThresholdList(newAssetTypeParameter);
        return newAssetType;
    }
}
