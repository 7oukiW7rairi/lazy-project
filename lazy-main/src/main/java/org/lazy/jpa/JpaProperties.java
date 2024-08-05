package org.lazy.jpa;

public enum JpaProperties {

    PACKAGE_TO_SCAN("jpa.package_to_scan"),
    DATABSE_DRIVER_CLASS_NAME("jpa.database.driver_class_name"),
    DATABASE_URL("jpa.database.url"),
    DATABASE_USERNAME("jpa.database.username"),
    DATABASE_PASSWORD("jpa.database.password");


    private String name;

    JpaProperties(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
