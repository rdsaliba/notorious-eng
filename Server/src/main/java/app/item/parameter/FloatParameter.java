package app.item.parameter;

public class FloatParameter extends Parameter {
    private float floatValue;

    public FloatParameter(int paramID, String paramName, boolean isLive, boolean isDefault, float floatValue) {
        super(paramID, paramName, isLive, isDefault);
        this.floatValue = floatValue;
    }

    public float getFloatValue() {
        return floatValue;
    }

    public void setFloatValue(float floatValue) {
        this.floatValue = floatValue;
    }
}
