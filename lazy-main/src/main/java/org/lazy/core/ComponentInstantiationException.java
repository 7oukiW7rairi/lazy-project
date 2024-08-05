package org.lazy.core;

public class ComponentInstantiationException extends Exception {

    public ComponentInstantiationException(String message) {
        super(message);
    }

    public ComponentInstantiationException(String message, Exception exception) {
        super(message, exception);
    }
}
