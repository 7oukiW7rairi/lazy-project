package org.lazy.app;

import org.lazy.core.ComponentAssembler;
import org.lazy.web.BaseHttpServlet;
import org.lazy.common.ComponentDefinition;
import org.lazy.core.ConfigurableApplicationContext;
import org.lazy.core.DefaultApplicationContext;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

public class DefaultWebApplicationContext extends DefaultApplicationContext implements WebApplicationContext, ConfigurableApplicationContext {

    private final ComponentAssembler<BaseHttpServlet> servletComponentAssembler;

    public DefaultWebApplicationContext(String definitionFilePath) {
        super(definitionFilePath);
        this.servletComponentAssembler = new ServletComponentAssembler();
    }

    @Override
    public Object getWebComponent(BaseHttpServlet httpServlet) {
        Map<String, Object> dependencies = Arrays.stream(httpServlet.getClass().getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Inject.class))
                .map(field -> new AbstractMap.SimpleEntry<>(field.getName(), getComponent(field.getType().getName())))
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
        return servletComponentAssembler.assembleComponent(httpServlet, dependencies);
    }

    @Override
    public void registerComponent(String componentName, ComponentDefinition definition) {
        super.definitionFactory.registerDefinition(componentName, definition);
    }

    @Override
    public void registerSingleton(String singletonName, Object singletonInstance) {
        super.singletonComponents.putIfAbsent(singletonName, singletonInstance);
    }
}
