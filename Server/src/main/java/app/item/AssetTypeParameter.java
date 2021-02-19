/*
  The asset type parameter class contains all the details of the asset type parameter (thresholds).
  This includes the name of the asset type parameter and its value.

  @author
  @last_edit 02/7/2020
 */
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
