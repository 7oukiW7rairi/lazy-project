package org.lazy.core;

import org.lazy.common.ComponentDefinition;
import org.lazy.common.ComponentType;
import org.lazy.common.ConstructorDefinition;
import org.lazy.common.FactoryDefinition;
import org.lazy.common.SetterDefinition;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.*;

import static org.testng.Assert.*;

public class DefaultComponentAssemblerTest {
    
    private ComponentAssembler componentAssembler;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    ComponentDefinition definition;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        componentAssembler = new DefaultComponentAssembler();
    }

    @Test
    public void testAssembleComponentWithConstructorValid() {

        ConstructorDefinition constructorDefinition = Mockito.mock(ConstructorDefinition.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(constructorDefinition.getParameterTypes())
                .thenReturn(Arrays.asList("org.lazy.core.ComponentWithoutDependency", "org.lazy.core.DummyInterface"));

        Mockito.when(definition.getComponentClassName()).thenReturn("org.lazy.core.ComponentWithDependencies");
        Mockito.when(definition.getComponentType()).thenReturn(ComponentType.SINGLETON);
        Mockito.when(definition.isAbstract()).thenReturn(false);
        Mockito.when(definition.getDependencies())
                .thenReturn(Arrays.asList("org.lazy.core.ComponentWithoutDependency", "org.lazy.core.DummyInterface"));
        Mockito.when(definition.getFactoryDefinition()).thenReturn(null);
        Mockito.when(definition.getConstructor()).thenReturn(constructorDefinition);
        Mockito.when(definition.getSetters()).thenReturn(Collections.emptyList());

        Object componentInstance = componentAssembler.assembleComponent(definition,
                Map.of("org.lazy.core.ComponentWithoutDependency", new ComponentWithoutDependency(),
                        "org.lazy.core.DummyInterface@DummyComponent", new DummyComponent()));

        assertNotNull(componentInstance);
        assertEquals(componentInstance.getClass().getCanonicalName(), "org.lazy.core.ComponentWithDependencies");
    }

    @Test
    public void testAssembleComponentFromFactoryValid() {

        FactoryDefinition factory = Mockito.mock(FactoryDefinition.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(factory.getFactoryComponent()).thenReturn("org.lazy.core.ConfigComponent");
        Mockito.when(factory.getFactoryMethod()).thenReturn("getComponentFromConfig");
        Mockito.when(factory.getParameterTypes()).thenReturn(Collections.emptyList());

        Mockito.when(definition.getComponentClassName()).thenReturn("org.lazy.core.ComponentFromConfig");
        Mockito.when(definition.getComponentType()).thenReturn(ComponentType.PROTOTYPE);
        Mockito.when(definition.isAbstract()).thenReturn(false);
        Mockito.when(definition.getDependencies()).thenReturn(Collections.singletonList("org.lazy.core.ConfigComponent"));
        Mockito.when(definition.getFactoryDefinition()).thenReturn(factory);

        Object componentInstance = componentAssembler.assembleComponent(definition,
                Collections.singletonMap("org.lazy.core.ConfigComponent", new ConfigComponent()));

        assertNotNull(componentInstance);
        assertEquals(componentInstance.getClass().getCanonicalName(), "org.lazy.core.ComponentFromConfig");
    }

    @Test
    public void testAssembleComponentWithSetterValid() {

        ConstructorDefinition constructorDefinition = Mockito.mock(ConstructorDefinition.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(constructorDefinition.getParameterTypes()).thenReturn(Collections.emptyList());

        SetterDefinition setterDefinition = Mockito.mock(SetterDefinition.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(setterDefinition.getSetterName()).thenReturn("setComponent");
        Mockito.when(setterDefinition.getParameterType()).thenReturn("org.lazy.core.ComponentFromConfig");

        Mockito.when(definition.getComponentClassName()).thenReturn("org.lazy.core.DummyComponent");
        Mockito.when(definition.getComponentType()).thenReturn(ComponentType.SINGLETON);
        Mockito.when(definition.isAbstract()).thenReturn(false);
        Mockito.when(definition.getDependencies()).thenReturn(Collections.singletonList("org.lazy.core.ComponentFromConfig"));
        Mockito.when(definition.getFactoryDefinition()).thenReturn(null);
        Mockito.when(definition.getConstructor()).thenReturn(constructorDefinition);
        Mockito.when(definition.getSetters()).thenReturn(Collections.singletonList(setterDefinition));

        Object componentInstance = componentAssembler.assembleComponent(definition,
                Collections.singletonMap("org.lazy.core.ComponentFromConfig", new ComponentFromConfig()));

        assertNotNull(componentInstance);
        assertEquals(componentInstance.getClass().getCanonicalName(), "org.lazy.core.DummyComponent");
    }

    @Test
    public void testAssembleComponentWithNonExistingSetter() {

        ConstructorDefinition constructorDefinition = Mockito.mock(ConstructorDefinition.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(constructorDefinition.getParameterTypes()).thenReturn(Collections.emptyList());

        SetterDefinition setterDefinition = Mockito.mock(SetterDefinition.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(setterDefinition.getSetterName()).thenReturn("notExistingSetter");
        Mockito.when(setterDefinition.getParameterType()).thenReturn("org.lazy.core.ComponentFromConfig");

        Mockito.when(definition.getComponentClassName()).thenReturn("org.lazy.core.DummyComponent");
        Mockito.when(definition.getComponentType()).thenReturn(ComponentType.SINGLETON);
        Mockito.when(definition.isAbstract()).thenReturn(false);
        Mockito.when(definition.getDependencies()).thenReturn(Collections.singletonList("org.lazy.core.ComponentFromConfig"));
        Mockito.when(definition.getFactoryDefinition()).thenReturn(null);
        Mockito.when(definition.getConstructor()).thenReturn(constructorDefinition);
        Mockito.when(definition.getSetters()).thenReturn(Collections.singletonList(setterDefinition));

        Assert.assertThrows("NoSuchMethodException", CoreException.class, () -> componentAssembler.assembleComponent(definition,
                Collections.singletonMap("org.lazy.core.ComponentFromConfig", new ComponentFromConfig())));
    }
}