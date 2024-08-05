package org.lazy.common;

public class SetterDefinition {

    private String setterName;
    private String parameterType;

    public SetterDefinition() {
    }

    public SetterDefinition(String setterName, String parameterType) {
        this.setterName = setterName;
        this.parameterType = parameterType;
    }

    public String getSetterName() {
        return setterName;
    }

    public String getParameterType() {
        return parameterType;
    }
}
