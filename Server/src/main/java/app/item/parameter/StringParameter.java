package app.item.parameter;

public class StringParameter extends Parameter {
    private String stringValue;

    public StringParameter(int paramID, String paramName, boolean isLive, boolean isDefault, String stringValue) {
        super(paramID, paramName, isLive, isDefault);
        this.stringValue = stringValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }
}
