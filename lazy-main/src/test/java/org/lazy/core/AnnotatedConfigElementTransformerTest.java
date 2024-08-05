package org.lazy.core;

import org.lazy.common.ComponentDefinition;
import org.lazy.common.ComponentType;
import org.lazy.common.Produces;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.annotation.processing.ProcessingEnvironment;
import javax.inject.Named;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;

import java.util.*;

public class AnnotatedConfigElementTransformerTest {

    private static final String COMPONENT_NAME = "Test.Config.ComponentFromConfig";

    private AnnotatedConfigElementTransformer configElementTransformer;

    @BeforeMethod
    public void setUp() {
        ProcessingEnvironment processingEnvironment = Mockito.mock(ProcessingEnvironment.class);
        configElementTransformer = new AnnotatedConfigElementTransformer(processingEnvironment);
    }

    @Test
    public void testTransformWithConfigComponent() {

        Produces producesMock = Mockito.mock(Produces.class);
        Mockito.when(producesMock.value()).thenReturn(ComponentType.PROTOTYPE);
        Named namedMock = Mockito.mock(Named.class);
        Mockito.when(namedMock.value()).thenReturn("dummyName");

        TypeMirror parameterMock = Mockito.mock(TypeMirror.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(parameterMock.toString()).thenReturn("Test.Config.DependencyForConfigComponent");
        Mockito.when(parameterMock.getAnnotation(Mockito.any())).thenReturn(namedMock);

        ExecutableType methodMock = Mockito.mock(ExecutableType.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(methodMock.getReturnType().toString()).thenReturn(COMPONENT_NAME);
        Mockito.when((List<TypeMirror>) methodMock.getParameterTypes()).thenReturn(Collections.singletonList(parameterMock));

        PackageElement enclosingElement = Mockito.mock(PackageElement.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(enclosingElement.getKind()).thenReturn(ElementKind.PACKAGE);
        Mockito.when(enclosingElement.getQualifiedName().toString()).thenReturn("Test.Config");

        Element enclosedElement = Mockito.mock(Element.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(enclosedElement.getKind()).thenReturn(ElementKind.METHOD);
        Mockito.when(enclosedElement.getAnnotation(Mockito.any())).thenReturn(producesMock);
        Mockito.when(enclosedElement.asType()).thenReturn(methodMock);
        Mockito.when(enclosedElement.getSimpleName().toString()).thenReturn("getComponentFromConfig");

        Element configElementMock = Mockito.mock(Element.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(configElementMock.getSimpleName().toString()).thenReturn("DummyConfigComponent");
        Mockito.when(configElementMock.getEnclosingElement()).thenReturn(enclosingElement);
        Mockito.when(configElementMock.getAnnotation(Mockito.any())).thenReturn(null);
        Mockito.when((List<Element>) configElementMock.getEnclosedElements()).thenReturn(Collections.singletonList(enclosedElement));

        List<ComponentDefinition> definitions = configElementTransformer.transform(configElementMock);

        Assert.assertEquals(definitions.size(), 1);
        Assert.assertEquals(definitions.get(0).getComponentClassName(), COMPONENT_NAME);
        Assert.assertEquals(definitions.get(0).getComponentType(), ComponentType.PROTOTYPE);
        Assert.assertEquals(definitions.get(0).getComponentSuperTypes(), Collections.singletonList(COMPONENT_NAME));
        Assert.assertFalse(definitions.get(0).isAbstract());
        Assert.assertEquals(definitions.get(0).getDependencies(), Arrays.asList("Test.Config.DummyConfigComponent", "Test.Config.DependencyForConfigComponent@dummyName"));
        Assert.assertNotNull(definitions.get(0).getFactoryDefinition());
        Assert.assertEquals(definitions.get(0).getFactoryDefinition().getFactoryComponent(), "Test.Config.DummyConfigComponent");
        Assert.assertEquals(definitions.get(0).getFactoryDefinition().getFactoryMethod(), "getComponentFromConfig");
        Assert.assertEquals(definitions.get(0).getFactoryDefinition().getParameterTypes(), Collections.singletonList("Test.Config.DependencyForConfigComponent"));
    }
}