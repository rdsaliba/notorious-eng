package app.item.parameter;

import java.util.ArrayList;

public class ListParameter extends Parameter {
    private ArrayList<String> listValues;
    private String selectedValue;

    public ListParameter(int paramID, String paramName, boolean isLive, boolean isDefault) {
        super(paramID, paramName, isLive, isDefault);
        this.listValues = new ArrayList<>();
        this.selectedValue = null;
    }

    public void addToList(String value){
        listValues.add(value);
    }

    public ArrayList<String> getListValues() {
        return listValues;
    }

    public void setListValues(ArrayList<String> listValues) {
        this.listValues = listValues;
    }

    public String getSelectedValue() {
        return selectedValue;
    }

    public void setSelectedValue(String selectedValue) {
        this.selectedValue = selectedValue;
    }
}
