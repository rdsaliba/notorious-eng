package app.item.parameter;

public class IntParameter extends Parameter {
    private int intValue;


    public IntParameter(int paramID, String paramName, boolean isLive, boolean isDefault, int intValue) {
        super(paramID, paramName, isLive, isDefault);
        this.intValue = intValue;
    }

    public int getIntValue() {
        return intValue;
    }

    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }
}
