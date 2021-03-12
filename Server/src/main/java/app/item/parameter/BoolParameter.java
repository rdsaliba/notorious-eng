package app.item.parameter;

public class BoolParameter extends Parameter {
    private boolean boolValue;

    public BoolParameter(int paramID, String paramName, boolean isLive, boolean isDefault, boolean boolValue) {
        super(paramID, paramName, isLive, isDefault);
        this.boolValue = boolValue;
    }

    public boolean getBoolValue() {
        return boolValue;
    }

    public void setBoolValue(boolean boolValue) {
        this.boolValue = boolValue;
    }
}
