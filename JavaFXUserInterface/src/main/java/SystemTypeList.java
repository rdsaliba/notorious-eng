import app.item.AssetType;
import app.item.AssetTypeParameter;

import java.util.ArrayList;

public class SystemTypeList {
    private AssetType assetType;
    private int liveAssets, archivedAssets;
    private String valueOk, valueCaution, valueAdvisory, valueWarning, valueFailed;

    public SystemTypeList(String name, int liveAssets) {
        this.assetType = new AssetType(name);
        this.liveAssets = liveAssets;
    }

    public SystemTypeList(SystemTypeList copy) {
        this.assetType = new AssetType(copy.getAssetType());
        this.liveAssets = copy.getLiveAssets();
        this.archivedAssets = copy.getArchivedAssets();
        this.valueOk = copy.getValueOk();
        this.valueCaution = copy.getValueCaution();
        this.valueAdvisory = copy.getValueAdvisory();
        this.valueWarning = copy.getValueWarning();
        this.valueFailed = copy.getValueFailed();
    }

    public SystemTypeList(AssetType assetType, int liveAssets, int archivedAssets, String valueOk, String valueCaution, String valueAdvisory, String valueWarning, String valueFailed) {
        this.assetType = assetType;
        this.liveAssets = liveAssets;
        this.archivedAssets = archivedAssets;
        this.valueOk = valueOk;
        this.valueCaution = valueCaution;
        this.valueAdvisory = valueAdvisory;
        this.valueWarning = valueWarning;
        this.valueFailed = valueFailed;
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

    public void setLiveAssets(int liveAssets) {
        this.liveAssets = liveAssets;
    }

    public int getArchivedAssets() {
        return archivedAssets;
    }

    public void setArchivedAssets(int archivedAssets) {
        this.archivedAssets = archivedAssets;
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
    public String getDescription() {
        return assetType.getDescription();
    }

    public void setName(String name) {
        this.assetType.setName(name);
    }

    public AssetType toAssetType(){
        AssetType newAssetType = new AssetType();
        ArrayList<AssetTypeParameter> newAssetTypeParameter = new ArrayList<>();
        newAssetType.setId(assetType.getId());
        newAssetType.setName(assetType.getName());
        newAssetType.setDescription(assetType.getDescription());

        if (this.valueOk.isEmpty())
            newAssetTypeParameter.add( new AssetTypeParameter(TextConstants.OK_THRESHOLD,null));
        else if (!this.valueOk.equals("-"))
            newAssetTypeParameter.add( new AssetTypeParameter(TextConstants.OK_THRESHOLD,Double.parseDouble(this.valueOk)));

        if (this.valueAdvisory.isEmpty())
            newAssetTypeParameter.add( new AssetTypeParameter(TextConstants.ADVISORY_THRESHOLD,null));
        else if (!this.valueAdvisory.equals("-"))
            newAssetTypeParameter.add( new AssetTypeParameter(TextConstants.ADVISORY_THRESHOLD,Double.parseDouble(this.valueAdvisory)));

        if (this.valueCaution.isEmpty())
            newAssetTypeParameter.add( new AssetTypeParameter(TextConstants.CAUTION_THRESHOLD,null));
        else if (!this.valueCaution.equals("-"))
            newAssetTypeParameter.add( new AssetTypeParameter(TextConstants.CAUTION_THRESHOLD,Double.parseDouble(this.valueCaution)));

        if (this.valueWarning.isEmpty())
            newAssetTypeParameter.add( new AssetTypeParameter(TextConstants.WARNING_THRESHOLD,null));
        else if (!this.valueWarning.equals("-"))
            newAssetTypeParameter.add( new AssetTypeParameter(TextConstants.WARNING_THRESHOLD,Double.parseDouble(this.valueWarning)));

        if (this.valueFailed.isEmpty())
            newAssetTypeParameter.add( new AssetTypeParameter(TextConstants.FAILED_THRESHOLD,null));
        else if (!this.valueFailed.equals("-"))
            newAssetTypeParameter.add( new AssetTypeParameter(TextConstants.FAILED_THRESHOLD,Double.parseDouble(this.valueFailed)));

        newAssetType.setThresholdList(newAssetTypeParameter);
        return newAssetType;
    }
}
