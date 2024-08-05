package org.lazy.web;

public enum WebProperties {
    APP_CONTEXT_NAME("LAZY_WEB_CONTEXT"),
    BASE_PACKAGE("web.base_package");

    private String name;


    WebProperties(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
