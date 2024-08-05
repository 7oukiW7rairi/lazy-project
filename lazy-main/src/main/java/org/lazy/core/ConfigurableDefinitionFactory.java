package org.lazy.core;

import org.lazy.common.ComponentDefinition;

public interface ConfigurableDefinitionFactory extends ComponentDefinitionFactory {

    void registerDefinition(String componentName, ComponentDefinition definition) throws CoreException;
}
