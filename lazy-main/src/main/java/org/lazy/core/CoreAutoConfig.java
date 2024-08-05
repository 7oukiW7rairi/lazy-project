package org.lazy.core;

import com.google.auto.service.AutoService;
import org.lazy.common.BaseComponentDefinition;
import org.lazy.common.ComponentDefinition;
import org.lazy.common.ComponentType;
import org.lazy.common.ConstructorDefinition;

import java.util.*;

@AutoService(AutoConfig.class)
public class CoreAutoConfig implements AutoConfig {

    @Override
    public void configure(ConfigurableApplicationContext componentFactory) {

    }

    @Override
    public boolean shouldConfigure(Set<String> propertiesName) {
        return false;
    }

    @Override
    public List<ComponentDefinition> getComponentDefinitions() {
        return Collections.singletonList(environmentDefinition());
    }

    private ComponentDefinition environmentDefinition() {
        BaseComponentDefinition definition = new BaseComponentDefinition(DefaultEnvironment.class.getName());
        definition.setComponentType(ComponentType.SINGLETON);
        definition.setComponentSuperTypes(Collections.singletonList(Environment.class.getName()));
        definition.setConstructor(new ConstructorDefinition(Collections.singletonList(String.class.getName()), Collections.emptyList()));
        definition.addDependency(String.class.getName());
        return definition;
    }
}
