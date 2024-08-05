package org.lazy.core;

import org.lazy.common.ComponentDefinition;

public interface ConfigurableApplicationContext extends ApplicationContext {

    void registerComponent(String componentName, ComponentDefinition definition);

    void registerSingleton(String singletonName, Object singletonInstance);
}
