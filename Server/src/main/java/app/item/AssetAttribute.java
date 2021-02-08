/*
  The asset Attribute class contains all the details of the asset attribute (sensors, operational settings, etc).
  This includes the id of the asset, its name and all the measurements we have for it.

  @author Paul Micu, Jeremie Chouteau
  @version 2.0
  @last_edit 24/12/2020
 */
package app.item;

import java.util.ArrayList;
import java.util.List;

public class AssetAttribute {
    private final ArrayList<Measurement> measurements;
    private int id;
    private String name;

    public AssetAttribute() {
        measurements = new ArrayList<>();
    }

    public void addMeasurement(int time, double value) {
        Measurement mNew = new Measurement(time, value);
        measurements.add(mNew);
    }

    public List<Measurement> getMeasurements() {
        return measurements;
    }

    public Double getMeasurements(int time) {
        for (Measurement measurement : measurements) {
            if (measurement.getTime() == time) {
                return measurement.getValue();
            }
        }
        return null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "AssetAttribute{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", measurements=" + measurements.toString() +
                '}';
    }
}
