package org.lazy.jpa;

public interface Transaction {

    void begin();

    void commit();

    void rollback();

    boolean isActive();

    void clean();
}
