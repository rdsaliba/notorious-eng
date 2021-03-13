package app.item.parameter;

public class Parameter {
    private Integer paramID;
    private String paramName;
    private boolean isLive;
    private boolean isDefault;

    public Parameter(int paramID, String paramName, boolean isLive, boolean isDefault) {
        this.paramID = paramID;
        this.paramName = paramName;
        this.isLive = isLive;
        this.isDefault = isDefault;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String name) {
        paramName = name;
    }

    public int getParamID() {
        return paramID;
    }

    public void setParamID(Integer paramID) {
        this.paramID = paramID;
    }

    public boolean isLive() {
        return isLive;
    }

    public void setLive(boolean live) {
        isLive = live;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }
}
