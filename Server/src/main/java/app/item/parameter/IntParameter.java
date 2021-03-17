package app.item.parameter;

public class IntParameter extends Parameter {
    private int intValue;


    public IntParameter(String paramName, int intValue) {
        super(paramName);
        this.intValue = intValue;
    }

    public int getIntValue() {
        return intValue;
    }

    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }
}
