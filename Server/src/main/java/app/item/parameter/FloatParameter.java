package app.item.parameter;

public class FloatParameter extends Parameter {
    private float floatValue;

    public FloatParameter(String paramName, float floatValue) {
        super(paramName);
        this.floatValue = floatValue;
    }

    public float getFloatValue() {
        return floatValue;
    }

    public void setFloatValue(float floatValue) {
        this.floatValue = floatValue;
    }
}
