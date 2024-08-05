package org.lazy.core;

public interface ComponentFactory {

    Object getComponent(String componentName) throws CoreException;

    boolean containsComponent(String componentName);
}
