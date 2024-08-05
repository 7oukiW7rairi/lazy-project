package org.lazy.core;


import org.lazy.common.ComponentDefinition;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class BaseComponentAssembler {

    private static final ComponentAssembler DEFAULT_COMPONENT_ASSEMBLER = new DefaultComponentAssembler();

    private final List<ComponentAssembler> componentAssemblers = ServiceLoader.load(ComponentAssembler.class).stream()
            .map(ServiceLoader.Provider::get)
            .collect(Collectors.toList());
    private Map<String, Object> singletonComponents;

    public BaseComponentAssembler(Map<String, Object> singletonComponents) {
        this.singletonComponents = singletonComponents;
    }
    // TODO this should be named postComponentConfig
    public Object assembleComponent(ComponentDefinition definition, Map<String, Object> dependencies) {
        Object component;
        try {
            component  = getComponentAssembler(definition).assembleComponent(definition, dependencies);
            if (definition.getInitMethodName() != null) {
                Method postInitMethod = component.getClass().getDeclaredMethod(definition.getInitMethodName());
                postInitMethod.invoke(component);
            }
            if (definition.isSingleton()) {
                singletonComponents.put(definition.getComponentClassName(), component);
            }
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
        throw new CoreException(e.getMessage());
        }
        return component;
    }

    private ComponentAssembler getComponentAssembler(ComponentDefinition componentDefinition) {
        return componentAssemblers.stream()
                .filter(componentAssembler -> componentAssembler.canAssemble(componentDefinition))
                .findFirst()
                .orElse(DEFAULT_COMPONENT_ASSEMBLER);
    }
}
