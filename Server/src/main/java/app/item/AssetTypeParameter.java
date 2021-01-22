package app.item;

public class AssetTypeParameter {
    private String name;
    private double value;

    public AssetTypeParameter() {
    }

    public AssetTypeParameter(String name, double value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public double getValue() {
        return value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "AssetTypeParameter{" +
                "name='" + name + '\'' +
                ", value=" + value +
                '}';
    }
}
