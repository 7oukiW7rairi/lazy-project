package org.lazy.common;

import java.util.ArrayList;
import java.util.List;

public class ConstructorDefinition {

    private List<String> parameterTypes = new ArrayList<>();
    private List<Object> parameterValues = new ArrayList<>();

    public ConstructorDefinition() {
    }

    public ConstructorDefinition(List<String> parameterTypes, List<Object> parameterValues) {
        this.parameterTypes = parameterTypes;
        this.parameterValues = parameterValues;
    }

    public List<String> getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(List<String> parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public List<Object> getParameterValues() {
        return parameterValues;
    }

    public void setParameterValues(List<Object> parameterValues) {
        this.parameterValues = parameterValues;
    }

    public void addIndexedParameterValue(int index, Object parameterValue) {
        this.parameterValues.add(index, parameterValue);
    }
}




