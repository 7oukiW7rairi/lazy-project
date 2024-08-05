package org.lazy.core;

import org.lazy.common.ComponentDefinition;

import java.util.*;

public interface AutoConfig {

    void configure(ConfigurableApplicationContext context);

    boolean shouldConfigure(Set<String> propertiesName);

    List<ComponentDefinition> getComponentDefinitions();
}
