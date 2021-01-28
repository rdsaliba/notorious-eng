package app.item;

public class AssetTypeParameter {
    private String name;
    private Double value;

    public AssetTypeParameter() {

    }

    public AssetTypeParameter(String name, Double value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Double getValue() {
        return value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(Double value) {
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
