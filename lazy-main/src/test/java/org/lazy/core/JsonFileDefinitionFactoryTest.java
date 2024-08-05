package org.lazy.core;

import org.lazy.common.BaseComponentDefinition;
import org.lazy.common.CommonProperties;
import org.lazy.common.ComponentDefinition;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

public class JsonFileDefinitionFactoryTest {

    private ConfigurableDefinitionFactory definitionFactory;

    @BeforeClass
    public void setUp() {
        definitionFactory = new JsonFileDefinitionFactory(CommonProperties.LAZY_APPLICATION_JSON.getName());
    }

    @Test
    public void testGetDefinitionValid() {
        ComponentDefinition definition = definitionFactory.getDefinition(DummyInterface.class.getName());

        assertNotNull(definition);
        assertEquals(definition.getComponentClassName(), DummyComponent.class.getName());
    }

    @Test
    public void testGetDefinitionInvalid() {
        assertNull(definitionFactory.getDefinition("DummyTest"));
    }

    @Test
    public void testGenerateDefinitionValid() {
        String componentName  = "org.lazy.core.NonExistingComponent";
        ComponentDefinition definition = new BaseComponentDefinition(componentName);

        definitionFactory.registerDefinition(componentName, definition);

        ComponentDefinition expectedDefinition = definitionFactory.getDefinition(componentName);
        assertNotNull(expectedDefinition);
        assertEquals(definition.getComponentClassName(), expectedDefinition.getComponentClassName());
    }
}