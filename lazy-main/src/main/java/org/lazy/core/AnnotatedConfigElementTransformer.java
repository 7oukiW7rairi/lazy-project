package org.lazy.core;

import org.lazy.common.BaseComponentDefinition;
import org.lazy.common.ComponentDefinition;
import org.lazy.common.FactoryDefinition;
import org.lazy.common.Produces;

import javax.annotation.processing.ProcessingEnvironment;
import javax.inject.Named;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import java.util.*;
import java.util.stream.Collectors;

public class AnnotatedConfigElementTransformer extends AbstractAnnotatedElementTransformer<List<ComponentDefinition>> {

    public AnnotatedConfigElementTransformer(ProcessingEnvironment processingEnv, Set<? extends Element> namedElements) {
        super(processingEnv, namedElements);
    }

    @Override
    public List<ComponentDefinition> transform(Element configElement) {
        List<ComponentDefinition> definitionList = new ArrayList<>();
        String factoryComponentFullName = constructClassFullName(configElement);
        String factoryComponentQualifier = configElement.getAnnotation(Named.class) != null ? configElement.getAnnotation(Named.class).value() : null;
        configElement.getEnclosedElements().stream()
                .filter(enclosedElement -> enclosedElement.getKind() == ElementKind.METHOD && enclosedElement.getAnnotation(Produces.class) != null)
                .forEach(enclosedElement -> {
                    ExecutableType method = (ExecutableType) enclosedElement.asType();
                    //logger.info("add config component enclosedElement " + enclosedElement.getSimpleName().toString());
                    String componentFullName = method.getReturnType().toString();
                    BaseComponentDefinition definition = new BaseComponentDefinition(componentFullName);
                    definition.setComponentType(enclosedElement.getAnnotation(Produces.class).value());
                    definition.setComponentSuperTypes(Collections.singletonList(componentFullName));
                    definition.setAbstract(false);
                    definition.setQualifier(factoryComponentQualifier);
                    definition.setFactoryDefinition(new FactoryDefinition(
                            factoryComponentFullName,
                            enclosedElement.getSimpleName().toString(),
                            method.getParameterTypes().stream().map(TypeMirror::toString).collect(Collectors.toList())));
                    definition.addDependency(factoryComponentFullName);
                    definition.addDependencies(method.getParameterTypes().stream()
                            .map(typeMirror -> parameterDependencyName(enclosedElement.getSimpleName().toString(), typeMirror, configElement.getSimpleName().toString())).collect(Collectors.toList()));
                    definitionList.add(definition);
                });
        return definitionList;
    }
}
