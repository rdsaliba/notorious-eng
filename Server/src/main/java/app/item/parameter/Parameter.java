package app.item.parameter;

import java.io.Serializable;

public class Parameter implements Serializable {
    private String paramName;

    public Parameter(String paramName) {
        this.paramName = paramName;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String name) {
        paramName = name;
    }

}
