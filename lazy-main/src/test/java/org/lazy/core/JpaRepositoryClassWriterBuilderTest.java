package org.lazy.core;

import org.lazy.jpa.DummyEntityTest;
import org.lazy.jpa.JpaRepository;
import org.lazy.jpa.Query;
import org.mockito.Mockito;
import org.objectweb.asm.ClassWriter;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import static org.testng.Assert.*;

public class JpaRepositoryClassWriterBuilderTest {

    private JpaRepositoryClassWriterBuilder jpaRepositoryClassWriterBuilder;
    private Element element;
    private ProcessingEnvironment processingEnvironment;

    @BeforeMethod
    public void setUp() {
    }

    @Test
    public void testBuild() throws IOException {
        Query queryMock = Mockito.mock(Query.class);
        Mockito.when(queryMock.value()).thenReturn("select t from Test t");

        TypeMirror parameterMock = Mockito.mock(TypeMirror.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(parameterMock.toString()).thenReturn(Long.class.getName());

        ExecutableType methodMock = Mockito.mock(ExecutableType.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(methodMock.getReturnType().toString()).thenReturn("java.util.List<org.lazy.core.DummyComponent>");
        Mockito.when((List<TypeMirror>) methodMock.getParameterTypes()).thenReturn(Collections.singletonList(parameterMock));

        PackageElement enclosingElement = Mockito.mock(PackageElement.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(enclosingElement.getKind()).thenReturn(ElementKind.PACKAGE);
        Mockito.when(enclosingElement.getQualifiedName().toString()).thenReturn("org.lazy.jpa");

        Element enclosedElement = Mockito.mock(Element.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(enclosedElement.getKind()).thenReturn(ElementKind.METHOD);
        Mockito.when(enclosedElement.getAnnotation(Mockito.any())).thenReturn(queryMock);
        Mockito.when(enclosedElement.asType()).thenReturn(methodMock);
        Mockito.when(enclosedElement.getSimpleName().toString()).thenReturn("getComponentFromConfig");

        Element repositoryElementMock = Mockito.mock(Element.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(repositoryElementMock.getSimpleName().toString()).thenReturn("JpaRepositoryTest");
        Mockito.when(repositoryElementMock.getEnclosingElement()).thenReturn(enclosingElement);
        Mockito.when(repositoryElementMock.getAnnotation(Mockito.any())).thenReturn(null);

        TypeMirror parameter1 = Mockito.mock(TypeMirror.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(parameter1.toString()).thenReturn(Integer.class.getName());
        TypeMirror parameter2 = Mockito.mock(TypeMirror.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(parameter2.toString()).thenReturn(DummyEntityTest.class.getName());

        DeclaredType componentTypeMock = Mockito.mock(DeclaredType.class);
        Mockito.when(componentTypeMock.toString()).thenReturn(JpaRepository.class.getName());
        Mockito.when((List<TypeMirror>) componentTypeMock.getTypeArguments()).thenReturn(Arrays.asList(parameter1, parameter2));

        Types typeUtilsMock = Mockito.mock(Types.class);
        Mockito.when((List<DeclaredType>) typeUtilsMock.directSupertypes(repositoryElementMock.asType())).thenReturn(Collections.singletonList(componentTypeMock));

        processingEnvironment = Mockito.mock(ProcessingEnvironment.class);
        Mockito.when(processingEnvironment.getTypeUtils()).thenReturn(typeUtilsMock);


        Mockito.when((List<Element>) repositoryElementMock.getEnclosedElements()).thenReturn(Collections.singletonList(enclosedElement));

        jpaRepositoryClassWriterBuilder = new JpaRepositoryClassWriterBuilder("org.lazy.jpa.JpaRepositoryTest", repositoryElementMock, processingEnvironment);
        ClassWriter classWriter = jpaRepositoryClassWriterBuilder.build();

        Class<?> jpaRepositoryClass = loadClass(classWriter.toByteArray(), "org.lazy.jpa.JpaRepositoryTestImpl");
        assertNotNull(jpaRepositoryClass);
        assertEquals(jpaRepositoryClass.getSuperclass().getSimpleName(),"AbstractJpaRepository" );
        assertEquals(Arrays.stream(jpaRepositoryClass.getInterfaces()).map(Class::getSimpleName).collect(Collectors.toList()), Collections.singletonList("JpaRepositoryTest" ));
        assertEquals(Arrays.stream(jpaRepositoryClass.getDeclaredMethods()).map(Method::getName).collect(Collectors.toList()), Collections.singletonList("getComponentFromConfig"));
    }

    private Class<?> loadClass(byte[] byteArray, String className) {
        return new ClassLoader () {
            public Class<?> findClass(String name) {
                return defineClass(name,byteArray,0,byteArray.length);
            }

        }.findClass(className);
    }


}