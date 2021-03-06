package app.item.parameter;

public class BoolParameter extends parameter {
    private boolean boolValue;


    public BoolParameter(String paramName, boolean boolValue) {
        super(paramName);
        this.boolValue = boolValue;
    }

    public BoolParameter(int paramID, String paramName, boolean boolValue) {
        super(paramID, paramName);
        this.boolValue = boolValue;
    }

    public boolean getBoolValue() {
        return boolValue;
    }

    public void setBoolValue(boolean boolValue) {
        this.boolValue = boolValue;
    }
}
