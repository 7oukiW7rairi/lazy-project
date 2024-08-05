package org.lazy.core;

public enum CoreProperties {

    PROFILE("lazy.profile");

    private String name;

    CoreProperties(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
