package org.lazy.core;

import org.lazy.common.ComponentDefinition;
import org.lazy.common.ComponentProxy;
import org.lazy.common.SetterDefinition;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static org.lazy.common.StreamUtils.withoutCheckExceptions;

public class DefaultComponentAssembler implements ComponentAssembler<ComponentDefinition> {

    @Override
    public Object assembleComponent(ComponentDefinition definition, Map<String, Object> dependencies) throws CoreException {
        Object component;
        try {
            Class<?> clazz = Class.forName(definition.getComponentClassName());
            if (definition.getFactoryDefinition() != null) {
                Object factoryComponent = dependencies.get(definition.getFactoryDefinition().getFactoryComponent());
                Method factoryMethod = factoryComponent.getClass().getDeclaredMethod(
                        definition.getFactoryDefinition().getFactoryMethod(),
                        getParameterTypes(definition.getFactoryDefinition().getParameterTypes()));
                component = factoryMethod.invoke(factoryComponent,
                        definition.getFactoryDefinition().getParameterTypes().stream()
                                .map(param -> dependencies.get(getDependencyName(param, definition.getDependencies())))
                                .toArray(Object[]::new));
            } else {
                Class<?>[] types = getParameterTypes(definition.getConstructor().getParameterTypes());
                Constructor<?> constructor = clazz.getDeclaredConstructor(types);
                Object[] args = new Object[types.length];
                for (int i = 0; i < types.length; i++) {
                    try {
                        args[i] = definition.getConstructor().getParameterValues().get(i);
                    } catch (IndexOutOfBoundsException ex) {
                        args[i] = dependencies.get(getDependencyName(types[i].getName(), definition.getDependencies()));
                    }
                }
                component = CoreUtils.instantiateClass(constructor, args);
                for (SetterDefinition setter : definition.getSetters()) {
                    Class<?> parameterType = findClass(setter.getParameterType());
                    Method method = clazz.getMethod(setter.getSetterName(), parameterType);
                    Object setterComponent = dependencies.get(getDependencyName(parameterType.getName(), definition.getDependencies()));
                    if (setterComponent != null) {
                        method.invoke(component, setterComponent);
                    }
                }
            }
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | ClassNotFoundException | InstantiationException e) {
            throw new CoreException(e.getMessage());
        }
        return component;
    }

    @Override
    public boolean canAssemble(ComponentDefinition componentDefinition) {
        return ComponentProxy.NONE == componentDefinition.getComponentProxy();
    }

    private String getDependencyName(String name, List<String> dependenciesName) {
        return dependenciesName.contains(name) ? name : dependenciesName.stream()
                .filter(dependency -> dependency.split("@")[0].equals(name))
                .findFirst().orElseThrow(() -> new CoreException(name + " dependency not found"));
    }

    private Class<?>[] getParameterTypes(List<String> parameterTypes) {
        return parameterTypes.stream()
                .map(withoutCheckExceptions(this::findClass))
                .filter(Objects::nonNull).toArray(Class[]::new);
    }

    private Class<?> findClass(String type) throws ClassNotFoundException {
        try {
            return Class.forName(type);
        } catch (ClassNotFoundException e) {
            return ClassLoader.getPlatformClassLoader().loadClass(type);
        }
    }

}
