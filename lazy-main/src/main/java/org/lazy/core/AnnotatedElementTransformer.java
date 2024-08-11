package org.lazy.core;

import org.lazy.app.LazyApplication;
import org.lazy.common.BaseComponentDefinition;
import org.lazy.common.ComponentDefinition;
import org.lazy.common.ComponentProxy;
import org.lazy.common.ComponentType;
import org.lazy.common.Configuration;
import org.lazy.common.ConstructorDefinition;
import org.lazy.common.Prototype;
import org.lazy.common.SetterDefinition;
import org.lazy.jpa.Repository;
import org.lazy.web.annotation.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.processing.ProcessingEnvironment;
import javax.inject.Inject;
import javax.inject.Named;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import javax.persistence.Entity;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;
import static javax.lang.model.element.Modifier.ABSTRACT;
import static org.lazy.common.StreamUtils.rethrowBiFunction;

public class AnnotatedElementTransformer extends AbstractAnnotatedElementTransformer<ComponentDefinition> {

    private static final Logger logger = LoggerFactory.getLogger(AnnotatedElementTransformer.class);

    public AnnotatedElementTransformer(ProcessingEnvironment processingEnv, Set<? extends Element> namedElements) {
        super(processingEnv, namedElements);
    }

    @Override
    public ComponentDefinition transform(Element component) {
        BaseComponentDefinition definition = new BaseComponentDefinition(constructClassFullName(component));
        definition.setComponentType(getComponentType(component));
        definition.setComponentProxy(component.getAnnotation(Transactional.class) != null ? ComponentProxy.TRANSACTIONAL : ComponentProxy.NONE);
        definition.setQualifier(component.getAnnotation(Named.class) != null ? component.getAnnotation(Named.class).value() : null);

        definition.setComponentSuperTypes(processingEnv.getTypeUtils().directSupertypes(component.asType()).stream()
                .map(typeMirror -> constructClassFullName(((DeclaredType) typeMirror).asElement()))
                .filter(not("java.lang.Object"::equals)).collect(Collectors.toList()));
        definition.setAbstract(component.getModifiers().stream().anyMatch(modifier -> modifier == ABSTRACT));
        Element constructor = getInjectConstructor(component.getEnclosedElements().stream().filter(enclosedElement -> enclosedElement.getKind() == ElementKind.CONSTRUCTOR).collect(Collectors.toList()));
        if (constructor != null) {
            List<TypeMirror> constructorParamTypes = new ArrayList<>(((ExecutableType) constructor.asType()).getParameterTypes());
            definition.setConstructor(new ConstructorDefinition(
                    constructorParamTypes.stream().map(TypeMirror::toString).collect(Collectors.toList()),
                    Collections.emptyList()));
            
            definition.addDependencies(constructorParamTypes.stream()
                    .map(typeMirror -> parameterDependencyName(constructor.getSimpleName().toString(), typeMirror, component.getSimpleName().toString())).collect(Collectors.toList()));
        } else {
            //throw new RuntimeException("No class constructor with @Inject annotation");
        }
        List<ExecutableType> setters = component.getEnclosedElements().stream()
                .filter(enclosedElement -> enclosedElement.getKind() == ElementKind.METHOD)
                .filter(this::isSetter)
                .map(element -> ((ExecutableType) element.asType()))
                .collect(Collectors.toList());
        definition.setSetters(setters.stream()
                .map(method -> new SetterDefinition(method.toString(),
                        method.getParameterTypes().get(0).toString())).collect(Collectors.toList()));
        for (ExecutableType setter : setters) {
            definition.addDependency(parameterDependencyName(setter.toString(), setter.getParameterTypes().get(0), component.getSimpleName().toString()));
        }
        /* definition.addDependencies(setters.stream()
                .map(method -> method.getParameterTypes().get(0))
                .map(this::getDependencyNameFromParameter)
                .collect(Collectors.toList())); */
        if (definition.getComponentProxy() == ComponentProxy.TRANSACTIONAL) {
            definition.addDependency("org.lazy.jpa.LocalTransactionManager");
        }
        return definition;
    }

    private ComponentType getComponentType(Element element) {
        ComponentType componentType;
        if (element.getAnnotation(Prototype.class) != null) {
            componentType = ComponentType.PROTOTYPE;
        } else if (element.getAnnotation(Configuration.class) != null) {
            componentType = ComponentType.CONFIGURATION;
        } else if (element.getAnnotation(Controller.class) != null) {
            componentType = ComponentType.CONTROLLER;
        } else if (element.getAnnotation(Repository.class) != null) {
            componentType = ComponentType.REPOSITORY;
        } else if (element.getAnnotation(LazyApplication.class) != null) {
            componentType = ComponentType.APPLICATION;
        } else if (element.getAnnotation(Entity.class) != null) {
            componentType = ComponentType.ENTITY;
        } else {
            componentType = ComponentType.SINGLETON;
        }
        return componentType;
    }

    private boolean isSetter(Element method) {
        return method.getAnnotation(Inject.class) != null && ((ExecutableType) method.asType()).getParameterTypes().size() == 1
                && method.getSimpleName().toString().startsWith("set");
    }

    private Element getInjectConstructor(List<? extends Element> enclosedElements) {
        return enclosedElements.size() == 1 ? enclosedElements.get(0) :
                enclosedElements.stream()
                        .filter(element ->  element.getAnnotation(Inject.class) != null)
                        .findFirst().orElse(null);
    }
}
