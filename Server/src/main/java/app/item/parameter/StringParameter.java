package app.item.parameter;

public class StringParameter extends parameter {
    private String stringValue;


    public StringParameter(String paramName, String stringValue) {
        super(paramName);
        this.stringValue = stringValue;
    }

    public StringParameter(int paramID, String paramName, String stringValue) {
        super(paramID, paramName);
        this.stringValue = stringValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }
}
