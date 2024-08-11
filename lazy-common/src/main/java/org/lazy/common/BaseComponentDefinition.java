package org.lazy.common;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.*;

public class BaseComponentDefinition implements ComponentDefinition {

    private String componentClassName;
    private List<String> componentSuperTypes = new ArrayList<>();
    private ComponentType componentType;
    private boolean isAbstract;
    private List<String> dependencies = new ArrayList<>();
    private ConstructorDefinition constructor;
    private List<SetterDefinition> setters = new ArrayList<>();
    private String initMethodName;
    private String destroyMethodName;
    private FactoryDefinition factoryDefinition;
    private ComponentProxy componentProxy;
    private String qualifier;


    public BaseComponentDefinition() {
        this(null);
    }

    public BaseComponentDefinition(String componentClassName) {
        this.componentClassName = componentClassName;
    }

    public void setComponentClassName(String componentClassName) {
        this.componentClassName = componentClassName;
    }

    @Override
    public String getComponentClassName() {
        return this.componentClassName;
    }

    public void setComponentSuperTypes(List<String> componentSuperTypes) {
        this.componentSuperTypes = componentSuperTypes;
    }

    @Override
    public List<String> getComponentSuperTypes() {
        return this.componentSuperTypes;
    }

    public void setComponentType(ComponentType componentType) {
        this.componentType = componentType;
    }

    @Override
    public ComponentType getComponentType() {
        return this.componentType;
    }

    @JsonIgnore
    @Override
    public boolean isSingleton() {
        return ComponentType.SINGLETON == this.componentType;
    }

    @JsonIgnore
    @Override
    public boolean isPrototype() {
        return ComponentType.PROTOTYPE == this.componentType;
    }

    public void setAbstract(boolean isAbstract) {
        this.isAbstract = isAbstract;
    }

    @JsonIgnore
    @Override
    public boolean isAbstract() {
        return this.isAbstract;
    }

    public void setDependencies( List<String> dependencies) {
        this.dependencies = dependencies;
    }

    public void addDependency(String dependency) {
        this.dependencies.add(dependency);
    }

    public void addDependencies(List<String> dependencies) {
        this.dependencies.addAll(dependencies);
    }

    public List<String> getDependencies() {
        return this.dependencies;
    }

    @Override
    public ConstructorDefinition getConstructor() {
        return this.constructor;
    }

    @Override
    public List<SetterDefinition> getSetters() {
        return setters;
    }

    public void setConstructor(ConstructorDefinition constructor) {
        this.constructor = constructor;
    }

    public void setSetters(List<SetterDefinition> setters) {
        this.setters = setters;
    }

    public void setInitMethodName(String initMethodName) {
        this.initMethodName = initMethodName;
    }

    @Override
    public String getInitMethodName() {
        return this.initMethodName;
    }

    public void setDestroyMethodName(String destroyMethodName) {
        this.destroyMethodName = destroyMethodName;
    }

    @Override
    public String getDestroyMethodName() {
        return this.destroyMethodName;
    }

    @Override
    public FactoryDefinition getFactoryDefinition() {
        return factoryDefinition;
    }

    public void setFactoryDefinition(FactoryDefinition factoryDefinition) {
        this.factoryDefinition = factoryDefinition;
    }

    @Override
    public ComponentProxy getComponentProxy() {
        return componentProxy;
    }

    public void setComponentProxy(ComponentProxy componentProxy) {
        this.componentProxy = componentProxy;
    }

    @Override
    public String getQualifier() {
        return qualifier;
    }

    public void setQualifier(String qualifier) {
        this.qualifier = qualifier;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((componentClassName == null) ? 0 : componentClassName.hashCode());
        result = prime * result + ((qualifier == null) ? 0 : qualifier.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BaseComponentDefinition other = (BaseComponentDefinition) obj;
        if (componentClassName == null) {
            if (other.componentClassName != null)
                return false;
        } else if (!componentClassName.equals(other.componentClassName))
            return false;
        if (qualifier == null) {
            if (other.qualifier != null)
                return false;
        } else if (!qualifier.equals(other.qualifier))
            return false;
        return true;
    }

    
}
