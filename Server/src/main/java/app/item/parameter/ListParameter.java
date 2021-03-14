package app.item.parameter;

import java.util.ArrayList;
import java.util.List;

public class ListParameter extends Parameter {
    private List<String> listValues;
    private String selectedValue;

    public ListParameter(int paramID, String paramName, boolean isLive, boolean isDefault) {
        super(paramID, paramName, isLive, isDefault);
        this.listValues = new ArrayList<>();
        this.selectedValue = null;
    }

    public void addToList(String value){
        listValues.add(value);
    }

    public List<String> getListValues() {
        return listValues;
    }

    public void setListValues(List<String> listValues) {
        this.listValues = listValues;
    }

    public String getSelectedValue() {
        return selectedValue;
    }

    public void setSelectedValue(String selectedValue) {
        this.selectedValue = selectedValue;
    }
}
