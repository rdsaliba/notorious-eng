package app.item.parameter;

public class FloatParameter extends parameter {
    private float floatValue;


    public FloatParameter(String paramName, float floatValue) {
        super(paramName);
        this.floatValue = floatValue;
    }

    public FloatParameter(int paramID, String paramName, float floatValue) {
        super(paramID, paramName);
        this.floatValue = floatValue;
    }

    public float getFloatValue() {
        return floatValue;
    }

    public void setFloatValue(float floatValue) {
        this.floatValue = floatValue;
    }
}
