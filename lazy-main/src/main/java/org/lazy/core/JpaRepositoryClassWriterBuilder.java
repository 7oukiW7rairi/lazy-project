package org.lazy.core;

import org.lazy.jpa.JpaRepository;
import org.lazy.jpa.Query;
import org.objectweb.asm.ClassWriter;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import java.util.*;
import java.util.stream.Collectors;

public class JpaRepositoryClassWriterBuilder {

    private final ProcessingEnvironment processingEnv;
    private String className;
    private Element element;

    public JpaRepositoryClassWriterBuilder(String className, Element element, ProcessingEnvironment processingEnv) {
        this.className = className;
        this.element = element;
        this.processingEnv = processingEnv;
    }


    public ClassWriter build() {
        List<String> classArguments = processingEnv.getTypeUtils().directSupertypes(element.asType()).stream()
                .filter(typeMirror -> typeMirror.toString().startsWith(JpaRepository.class.getName()))
                .flatMap(typeMirror -> ((DeclaredType) typeMirror).getTypeArguments().stream())
                .map(TypeMirror::toString)
                .collect(Collectors.toList());
        List<JpaRepositoryMethod> jpaRepositoryMethods = element.getEnclosedElements().stream()
                .filter(enclosedElement -> enclosedElement.getKind() == ElementKind.METHOD && enclosedElement.getAnnotation(Query.class) != null)
                .map(enclosedElement -> {
                    ExecutableType method = (ExecutableType) enclosedElement.asType();
                    return new JpaRepositoryMethod(enclosedElement.getSimpleName().toString(),
                            method.getReturnType().toString(),
                            enclosedElement.getAnnotation(Query.class).value(),
                            method.getParameterTypes().stream().map(TypeMirror::toString).collect(Collectors.toList()));
                }).collect(Collectors.toList());
        return new JpaRepositoryClassWriter(className, classArguments, jpaRepositoryMethods).getClassWriter();
    }

}
