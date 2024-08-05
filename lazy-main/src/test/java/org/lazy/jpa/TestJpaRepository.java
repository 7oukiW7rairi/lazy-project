package org.lazy.jpa;

import org.lazy.web.DummyTestClass;

import javax.persistence.EntityManager;

public class TestJpaRepository extends AbstractJpaRepository<DummyTestClass, Integer> {
    public TestJpaRepository(EntityManager entityManager) {
        super(entityManager);
    }
}
