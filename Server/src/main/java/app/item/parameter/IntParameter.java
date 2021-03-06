package app.item.parameter;

public class IntParameter extends parameter {
    private int intValue;


    public IntParameter(String paramName, int intValue) {
        super(paramName);
        this.intValue = intValue;
    }

    public IntParameter(int paramID, String paramName, int intValue) {
        super(paramID, paramName);
        this.intValue = intValue;
    }

    public int getIntValue() {
        return intValue;
    }

    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }
}
