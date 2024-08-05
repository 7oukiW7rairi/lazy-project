package org.lazy.common;

import java.util.List;

public interface ComponentDefinition {


    String getComponentClassName();

    List<String> getComponentSuperTypes();

    ComponentType getComponentType();

    boolean isSingleton();

    boolean isPrototype();

    boolean isAbstract();

    List<String> getDependencies();

    ConstructorDefinition getConstructor();

    List<SetterDefinition> getSetters();

    String getInitMethodName();

    String getDestroyMethodName();

    FactoryDefinition getFactoryDefinition();

    ComponentProxy getComponentProxy();

    String getQualifier();
}
