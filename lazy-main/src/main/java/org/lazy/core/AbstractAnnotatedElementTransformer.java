package org.lazy.core;

import javax.annotation.processing.ProcessingEnvironment;
import javax.inject.Named;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.type.TypeMirror;

public abstract class AbstractAnnotatedElementTransformer<T> implements AnnotatedComponentTransformer<Element, T> {

    protected ProcessingEnvironment processingEnv;

    public AbstractAnnotatedElementTransformer(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
    }

    protected String getDependencyNameFromParameter(TypeMirror parameterType) {
        return parameterType.getAnnotation(Named.class) != null ?
                parameterType.toString() + "@" +  parameterType.getAnnotation(Named.class).value() : parameterType.toString();
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
