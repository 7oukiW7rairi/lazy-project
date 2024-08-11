package org.lazy.core;

import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.processing.ProcessingEnvironment;
import javax.inject.Named;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractAnnotatedElementTransformer<T> implements AnnotatedComponentTransformer<Element, T> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractAnnotatedElementTransformer.class);

    protected ProcessingEnvironment processingEnv;
    private Set<? extends Element> namedElements;

    public AbstractAnnotatedElementTransformer(ProcessingEnvironment processingEnv, Set<? extends Element> namedElements) {
        this.processingEnv = processingEnv;
        this.namedElements = namedElements;
    }

    protected String parameterDependencyName(String methodName, TypeMirror typeMirror, String componentName) {
        String namedAnnotation = namedElements.stream()
                .filter(element -> element.getKind() == ElementKind.PARAMETER)
                .filter(element ->  element.asType().toString().equals(typeMirror.toString())/*  &&
                                    element.getEnclosingElement().getSimpleName().equals(methodName) &&
                                    element.getEnclosingElement().getEnclosingElement().getSimpleName().equals(componentName) */)
                .map(element -> element.getAnnotation(Named.class).value())
                .findFirst().orElse(null);
        return namedAnnotation != null ?
                    typeMirror.toString() + "@" +  namedAnnotation : typeMirror.toString();
    }

    protected String constructClassFullName(Element element) {
        String fullName = "";
        Element enclosingElement = element.getEnclosingElement();
        if (enclosingElement.getKind() == ElementKind.PACKAGE) {
            fullName = ((PackageElement) enclosingElement).getQualifiedName().toString() + ".";
        }
        return fullName + element.getSimpleName();
    }
}
