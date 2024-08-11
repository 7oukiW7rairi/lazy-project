package org.lazy.plugin;

import org.lazy.common.BaseComponentDefinition;
import org.lazy.common.CommonProperties;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.util.*;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class ApplicationFilesGeneratorTest {

    private ApplicationFilesGenerator applicationFilesGenerator;

    @BeforeTest
    public void setUp() {
        applicationFilesGenerator = ApplicationFilesGenerator.getInstance();
        applicationFilesGenerator.addComponentDefinitions(
                getClass().getClassLoader().getResourceAsStream(CommonProperties.COMPONENT_DEFINITIONS.getName()));
    }

    @Test
    public void testGenerateComponentDefinitionFile() throws URISyntaxException, IOException {

        Map<String, BaseComponentDefinition> definitionMap = convertJsonToMap(
                applicationFilesGenerator.generateLazyApplicationJson());

        assertNotNull(definitionMap);
        assertEquals(definitionMap.size(), 5);
        BaseComponentDefinition definition = definitionMap.get("org.lazy.core.ComponentFromConfig");
        assertNotNull(definition);
        
    }

    @Test
    void testGenerateLazyApplicationProperties() throws IOException {
        Properties properties = parsePropertiesString(applicationFilesGenerator.generateLazyApplicationProperties());

        assertEquals(properties.getProperty(CommonProperties.CONTROLLERS.getName()), "org.lazy.app.Controller");
        assertEquals(properties.getProperty(CommonProperties.ENTITIES.getName()), "org.lazy.app.Entity");
    }

    @Test
    void testGetApplicationMainClass() {
        assertEquals(applicationFilesGenerator.getApplicationMainClass(), "org.lazy.app.MainApplication");
    }

    private Map<String, BaseComponentDefinition> convertJsonToMap(String json) throws IOException {
        return new ObjectMapper().readValue(json.getBytes(), new TypeReference<Map<String, BaseComponentDefinition>>() {
        });
    }

    public Properties parsePropertiesString(String s) throws IOException {
        final Properties properties = new Properties();
        properties.load(new StringReader(s));
        return properties;
    }
}