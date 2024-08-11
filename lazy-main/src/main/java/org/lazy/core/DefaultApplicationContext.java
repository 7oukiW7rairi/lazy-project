package org.lazy.core;

import org.lazy.common.ComponentDefinition;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DefaultApplicationContext implements ApplicationContext {

    private static final ComponentAssembler<ComponentDefinition> DEFAULT_COMPONENT_ASSEMBLER = new DefaultComponentAssembler();
    private static final List<ComponentAssembler> ASSEMBLERS = ServiceLoader.load(ComponentAssembler.class).stream()
            .map(ServiceLoader.Provider::get)
            .collect(Collectors.toList());

    protected final Map<String, Object> singletonComponents = new ConcurrentHashMap<>();
    protected final ConfigurableDefinitionFactory definitionFactory;

    public DefaultApplicationContext(String definitionFilePath) {
        this.definitionFactory = new JsonFileDefinitionFactory(definitionFilePath);
    }

    @Override
    public synchronized Object getComponent(String componentName) throws CoreException {
        if (singletonComponents.containsKey(componentName)) {
            return singletonComponents.get(componentName);
        }
        Map<String, Object> initialisedComponents = new HashMap<>();
        Stack<String> stack = new Stack<>();
        stack.push(componentName);
        while (!stack.isEmpty()) {
            String currentComponentName = stack.pop();
            if (singletonComponents.containsKey(currentComponentName)) {
                initialisedComponents.put(currentComponentName, singletonComponents.get(currentComponentName));
            } else {
                // TODO add definition map so we don't repeat getting a definition twice
                ComponentDefinition currentDefinition = getDefinition(currentComponentName);
                if (initialisedComponents.containsKey(currentDefinition.getComponentClassName())) {
                    throw new CoreException("Circular dependencies for Component " + currentDefinition.getComponentClassName());
                }
                if (!currentDefinition.getDependencies().isEmpty()) {
                    if (currentDefinition.getDependencies().stream().allMatch(initialisedComponents::containsKey)) {
                        initialisedComponents.put(currentComponentName, initializeComponent(
                                currentComponentName,
                                currentDefinition,
                                initialisedComponents.entrySet().stream()
                                        .filter(entry -> currentDefinition.getDependencies().contains(entry.getKey()))
                                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                        ));
                    } else {
                        stack.push(currentComponentName);
                        for (String componentDependency : currentDefinition.getDependencies()) {
                            stack.push(componentDependency);
                        }
                    }
                } else {
                    initialisedComponents.put(currentComponentName, initializeComponent(currentComponentName, currentDefinition, Collections.emptyMap()));
                }
            }
        }
        return initialisedComponents.get(componentName);
    }

    @Override
    public boolean containsComponent(String componentName) {
        return singletonComponents.containsKey(componentName) || definitionFactory.containsDefinition(componentName);
    }

    private ComponentDefinition getDefinition(String componentName) {
        return Optional.ofNullable(getEnvironment().getActiveProfile())
                .map(profileName -> definitionFactory.getDefinition(componentName + "@" + profileName))
                .or(() -> Optional.ofNullable(definitionFactory.getDefinition(componentName)))
                .orElseThrow(() -> new CoreException("No Component definition found for " + componentName));
    }

    private Object initializeComponent(String componentName, ComponentDefinition definition, Map<String, Object> dependencies) {
        Object component;
        try {
            component  = getComponentAssembler(definition).assembleComponent(definition, dependencies);
            if (definition.getInitMethodName() != null) {
                Method postInitMethod = component.getClass().getDeclaredMethod(definition.getInitMethodName());
                postInitMethod.invoke(component);
            }
            if (definition.isSingleton()) {
                singletonComponents.put(componentName, component);
            }
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            throw new CoreException(e.getMessage());
        }
        return component;
    }

    private ComponentAssembler getComponentAssembler(ComponentDefinition componentDefinition) {
        return ASSEMBLERS.stream()
                .filter(componentAssembler -> componentAssembler.canAssemble(componentDefinition))
                .findFirst()
                .orElse(DEFAULT_COMPONENT_ASSEMBLER);
    }

    @Override
    public Environment getEnvironment() {
        return (Environment) singletonComponents.get(Environment.class.getName());
    }

    @Override
    public void destroyContext() {
        singletonComponents.clear();
    }
}
