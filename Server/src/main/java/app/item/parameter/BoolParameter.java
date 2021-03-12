package app.item.parameter;

public class BoolParameter extends Parameter {
    private boolean boolValue;

    public BoolParameter(String paramName, boolean boolValue) {
        super(paramName);
        this.boolValue = boolValue;
    }

    public boolean getBoolValue() {
        return boolValue;
    }

    public void setBoolValue(boolean boolValue) {
        this.boolValue = boolValue;
    }
}
