package org.lazy.plugin;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.lazy.common.BaseComponentDefinition;
import org.lazy.common.CommonProperties;
import org.lazy.common.ComponentDefinition;
import org.lazy.common.ComponentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ApplicationFilesGenerator {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationFilesGenerator.class);

    private static ApplicationFilesGenerator INSTANCE;

    private final ObjectMapper objectMapper;
    private final Set<BaseComponentDefinition> componentDefinitions = new HashSet<>();

    private ApplicationFilesGenerator() {
        this.objectMapper = new ObjectMapper();
    }

    public static ApplicationFilesGenerator getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ApplicationFilesGenerator();
        }
        return INSTANCE;
    }

    public void addComponentDefinitions(InputStream inputStream) {
        try {
            componentDefinitions
                    .addAll(objectMapper.readValue(inputStream, new TypeReference<Set<BaseComponentDefinition>>() {
                    }));
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String generateLazyApplicationJson() {
        Map<String, BaseComponentDefinition> definitionMap = new HashMap<>();
        for (ComponentDefinition definition : componentDefinitions) {
            definition.getDependencies()
                    .forEach(type -> {
                        String[] dependencyNameParts = type.split("@");
                        if (dependencyNameParts.length > 2) {
                            throw new IllegalArgumentException("Malformed component dependency name " + type);
                        }
                        Predicate<ComponentDefinition> predicate = componentDefinition -> componentDefinition
                                .getComponentClassName().equals(type) ||
                                componentDefinition.getComponentSuperTypes().contains(type);
                        if (dependencyNameParts.length == 2) {
                            predicate = componentDefinition -> componentDefinition.getComponentSuperTypes()
                                    .contains(dependencyNameParts[0]) &&
                                    dependencyNameParts[1].equals(componentDefinition.getQualifier());
                        }
                        // TODO if there's multiple definition for a dependency, check if they have
                        // different qualifier and use it to create the mapping
                        List<BaseComponentDefinition> definitionsFound = componentDefinitions.stream().filter(predicate)
                                .collect(Collectors.toList());
                        if (definitionsFound.isEmpty()) {
                            logger.warn("No Component definitions found for " + type);
                        } else if (definitionsFound.size() == 1) {
                            definitionMap.putIfAbsent(type, definitionsFound.get(0));
                        } else {
                            definitionsFound.forEach(component -> definitionMap.put(
                                    component.getQualifier() != null ? type + "@" + component.getQualifier() : type,
                                    component));
                        }
                        /*
                         * .findFirst().ifPresentOrElse(componentDefinition ->
                         * definitionMap.putIfAbsent(type, componentDefinition),
                         * () -> logger.info("No Component definitions found for " + type));
                         */
                    });
        }
        try {
            return objectMapper.writeValueAsString(definitionMap);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    public String generateLazyApplicationProperties() throws IOException {
        Properties properties = new Properties();
        properties.put(CommonProperties.CONTROLLERS.getName(), getPropertyValueFromList(ComponentType.CONTROLLER));
        properties.put(CommonProperties.ENTITIES.getName(), getPropertyValueFromList(ComponentType.ENTITY));
        StringWriter writer = new StringWriter();
        properties.store(new PrintWriter(writer), "");
        return writer.getBuffer().toString();
    }

    public String getApplicationMainClass() {
        return componentDefinitions.stream()
                .filter(definition -> definition.getComponentType() == ComponentType.APPLICATION)
                .map(BaseComponentDefinition::getComponentClassName)
                .findFirst().orElse("");
    }

    private String getPropertyValueFromList(ComponentType componentType) {
        return componentDefinitions.stream()
                .filter(definition -> definition.getComponentType() == componentType)
                .map(BaseComponentDefinition::getComponentClassName).collect(Collectors.joining(","));
    }
}
