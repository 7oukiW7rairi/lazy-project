package org.lazy.jpa;

import java.io.Serializable;
import java.util.List;

public interface JpaRepository<T extends Serializable, I> {

    T findOne(final I id);

    T getOne(final I id);

    List<T> findAll();

    T create(final T entity);

    T update(final T entity);

    void delete(final T entity);

    void deleteById(final I entityId);

    Long count();

    //boolean exist(I id);

    boolean exist(T entity);
}
