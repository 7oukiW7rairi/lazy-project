package org.lazy.jpa;

public class JpaException extends RuntimeException {

    public JpaException(RuntimeException e) {
        super(e);
    }
}
