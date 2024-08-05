package org.lazy.common;

import java.util.stream.Stream;

public enum ComponentType {

    SINGLETON("javax.inject.Singleton"),
    PROTOTYPE("org.lazy.common.Prototype"),
    CONFIGURATION("org.lazy.common.Configuration"),
    REPOSITORY("org.lazy.jpa.Repository"),
    CONTROLLER("org.lazy.web.annotation.Controller"),
    APPLICATION("org.lazy.web.integration.LazyApplication"),
    ENTITY("javax.persistence.Entity");

    private String annotationClassName;

    ComponentType(String annotationClassName) {
        this.annotationClassName = annotationClassName;
    }

    public String getAnnotationClassName() {
        return annotationClassName;
    }

    public static ComponentType of(String annotationClassName) {
        return Stream.of(values())
                .filter(componentScope -> componentScope.getAnnotationClassName().equals(annotationClassName))
                .findFirst().orElse(SINGLETON);
    }
}
