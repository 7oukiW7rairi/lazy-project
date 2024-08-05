package org.lazy.core;

import org.lazy.common.ComponentDefinition;

public interface ComponentDefinitionFactory {

    ComponentDefinition getDefinition(String componentName) throws CoreException;

    boolean containsDefinition(String componentName) throws CoreException;
}
