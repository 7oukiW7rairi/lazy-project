package org.lazy.jpa;

import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.*;

@Singleton
public abstract class AbstractJpaRepository<T extends Serializable, I> implements JpaRepository<T, I> {

    private Class<T> persistentClass;

    @PersistenceContext
    protected EntityManager entityManager;

    public AbstractJpaRepository() {
        this(null);
    }

    public AbstractJpaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
        //noinspection unchecked
        this.persistentClass = (Class<T>) ((ParameterizedType) this.getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public T findOne(final I id) {
        return entityManager.find(persistentClass, id);
    }

    public T getOne(final I id) { return entityManager.getReference(persistentClass, id);}

    @SuppressWarnings("unchecked")
    public List<T> findAll() {
        return entityManager.createQuery("from " + persistentClass.getName()).getResultList();
    }

    public T create(final T entity) {
        entityManager.persist(entity);
        return entity;
    }


    public T update(final T entity) {
        return entityManager.merge(entity);
    }

    public void delete(final T entity) {
        entityManager.remove(entity);
    }

    public void deleteById(final I entityId) {
        final T entity = findOne(entityId);
        delete(entity);
    }

    public Long count() {
        return (Long) entityManager.createQuery("select count(*) from " + persistentClass.getName()).getSingleResult();
    }

    public boolean exist(T entity) {
        return entityManager.contains(entity);
    }

    public void beginTransaction() {
        entityManager.getTransaction().begin();
    }

    public void commit() {
        entityManager.getTransaction().commit();
    }

    public void rollBack() {
        entityManager.getTransaction().rollback();
    }

}
