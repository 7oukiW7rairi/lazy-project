package org.lazy.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

public class JpaTransaction implements Transaction {

    private final EntityTransaction entityTransaction;
    private final EntityManager entityManager;

    public JpaTransaction(EntityManager entityManager) {
        this.entityTransaction = entityManager.getTransaction();
        this.entityManager = entityManager;
    }

    @Override
    public void begin() {
        entityTransaction.begin();
    }

    @Override
    public void commit() {
        entityTransaction.commit();
    }

    @Override
    public void rollback() {
        entityTransaction.rollback();
    }

    @Override
    public boolean isActive() {
        return entityTransaction.isActive();
    }

    @Override
    public void clean() {
        entityManager.close();
    }
}
