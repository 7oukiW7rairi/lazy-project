package org.lazy.common;

public enum CommonProperties {

    COMPONENT_DEFINITIONS("component-definitions.json"),
    LAZY_APPLICATION_JSON("lazy-application.json"),
    LAZY_APPLICATION_PROPERTIES("lazy-application.properties"),
    CONTROLLERS("org.lazy.web.controllers"),
    ENTITIES("org.lazy.jpa.entities");

    private String name;

    CommonProperties(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
