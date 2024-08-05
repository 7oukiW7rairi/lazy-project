package org.lazy.core;

import org.lazy.common.ComponentDefinition;
import org.mockito.Mockito;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.annotation.processing.ProcessingEnvironment;
import javax.inject.Named;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.*;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;

public class AnnotatedElementTransformerTest {

    private AnnotatedElementTransformer elementTransformer;
    private ProcessingEnvironment processingEnvironment;

    @BeforeMethod
    public void setUp() {
    }

    @Test
    public void testTransform() {

        Element elementMock = Mockito.mock(Element.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(elementMock.asType()).thenReturn(Mockito.mock(TypeMirror.class));

        PackageElement enclosingElement = Mockito.mock(PackageElement.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(enclosingElement.getKind()).thenReturn(ElementKind.PACKAGE);
        Mockito.when(enclosingElement.getQualifiedName().toString()).thenReturn("Test.Component");

        Element interfaceMock = Mockito.mock(Element.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(interfaceMock.getSimpleName().toString()).thenReturn("DummyInterfaceComponent");
        Mockito.when(interfaceMock.getEnclosingElement()).thenReturn(enclosingElement);

        DeclaredType componentTypeMock = Mockito.mock(DeclaredType.class);
        Mockito.when(componentTypeMock.asElement()).thenReturn(interfaceMock);

        Types typeUtilsMock = Mockito.mock(Types.class);
        Mockito.when((List<DeclaredType>) typeUtilsMock.directSupertypes(elementMock.asType())).thenReturn(Collections.singletonList(componentTypeMock));

        processingEnvironment = Mockito.mock(ProcessingEnvironment.class);
        Mockito.when(processingEnvironment.getTypeUtils()).thenReturn(typeUtilsMock);

        Element constructorElement = Mockito.mock(Element.class, Mockito.RETURNS_DEEP_STUBS);
        ExecutableType constructor = getExecutable("Test.Component.DependencyForComponent", "<init>");
        Mockito.when(constructorElement.getKind()).thenReturn(ElementKind.CONSTRUCTOR);
        Mockito.when(constructorElement.asType()).thenReturn(constructor);

        Element setterElement = Mockito.mock(Element.class, Mockito.RETURNS_DEEP_STUBS);
        ExecutableType setter = getExecutable("Test.Component.DummyDependency", "setDependencyForComponent");
        Mockito.when(setterElement.getKind()).thenReturn(ElementKind.METHOD);
        Mockito.when(setterElement.asType()).thenReturn(setter);
        Mockito.when(setterElement.getSimpleName().toString()).thenReturn("setDependencyForComponent");

        Mockito.when(elementMock.getSimpleName().toString()).thenReturn("DummyComponent");
        Mockito.when(elementMock.getEnclosingElement()).thenReturn(enclosingElement);
        Mockito.when(elementMock.getAnnotation(Mockito.any())).thenReturn(Mockito.mock(Named.class));
        Mockito.when((List<Element>) elementMock.getEnclosedElements())
                .thenReturn(Arrays.asList(constructorElement, setterElement, Mockito.mock(Element.class, Mockito.RETURNS_DEEP_STUBS)));

        elementTransformer = new AnnotatedElementTransformer(processingEnvironment);
        ComponentDefinition definition = elementTransformer.transform(elementMock);

        assertEquals(definition.getComponentClassName(), "Test.Component.DummyComponent");
        //assertEquals(definition.getScope(), ComponentScope.PROTOTYPE);
        assertEquals(definition.getComponentSuperTypes(), Collections.singletonList("Test.Component.DummyInterfaceComponent"));
        assertFalse(definition.isAbstract());
        assertEquals(definition.getDependencies(), Arrays.asList(
                "Test.Component.DependencyForComponent",
                "Test.Component.DummyDependency",
                "org.lazy.jpa.TransactionManager"));

        assertNotNull(definition.getConstructor());
        assertEquals(definition.getConstructor().getParameterTypes(), Collections.singletonList("Test.Component.DependencyForComponent"));

        assertEquals(definition.getSetters().size(), 1);
        assertEquals(definition.getSetters().get(0).getSetterName(), "setDependencyForComponent");
        assertEquals(definition.getSetters().get(0).getParameterType(), "Test.Component.DummyDependency");
    }

    private ExecutableType getExecutable(String parameterName, String executableName) {
        TypeMirror parameterMock = Mockito.mock(TypeMirror.class);
        Mockito.when(parameterMock.toString()).thenReturn(parameterName);

        ExecutableType executableType = Mockito.mock(ExecutableType.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(executableType.getAnnotation(Mockito.any())).thenReturn(null);
        Mockito.when(executableType.toString()).thenReturn(executableName);
        Mockito.when((List<TypeMirror>) executableType.getParameterTypes()).thenReturn(Collections.singletonList(parameterMock));
        return executableType;
    }
}