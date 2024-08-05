package org.lazy.common;

import java.util.*;

public class FactoryDefinition {

    private String factoryComponent;
    private String factoryMethod;
    private List<String> parameterTypes;

    public FactoryDefinition() {
    }

    public FactoryDefinition(String factoryComponent, String factoryMethod, List<String> parameterTypes) {
        this.factoryComponent = factoryComponent;
        this.factoryMethod = factoryMethod;
        this.parameterTypes = parameterTypes;
    }

    public String getFactoryComponent() {
        return factoryComponent;
    }

    public void setFactoryComponent(String factoryComponent) {
        this.factoryComponent = factoryComponent;
    }

    public String getFactoryMethod() {
        return factoryMethod;
    }

    public void setFactoryMethod(String factoryMethod) {
        this.factoryMethod = factoryMethod;
    }

    public List<String> getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(List<String> parameterTypes) {
        this.parameterTypes = parameterTypes;
    }
}
