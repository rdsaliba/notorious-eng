package app.item.parameter;

public class parameter {
    private Integer paramID;
    private String paramName;

    public parameter(String paramName) {
        this.paramName = paramName;
        this.paramID = null;
    }

    public parameter(int paramID, String paramName) {
        this.paramID = paramID;
        this.paramName = paramName;
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

    public void setParamID(int paramID) {
        this.paramID = paramID;
    }
}
