package org.lazy.plugin;

public enum ArchiveType {

    JAR("jar"),
    WAR("war");

    ArchiveType(String type) {
        this.type = type;
    }

    private String type;

    public String getType() {
        return type;
    }

    
}
