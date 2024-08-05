package org.lazy.jpa;


import javax.persistence.EntityManager;
import java.util.*;

public class EntityManagerContextHolder {

    private static final ThreadLocal<Stack<EntityManager>>  LOCAL_ENTITY_MANAGER = new ThreadLocal<>();

    public static EntityManager getEntityManager() {
        if (LOCAL_ENTITY_MANAGER.get() == null) {
            return null;
        }
        return LOCAL_ENTITY_MANAGER.get().peek();
    }

    public static void setEntityManager(EntityManager entityManager) {
        if (LOCAL_ENTITY_MANAGER.get() == null) {
            LOCAL_ENTITY_MANAGER.set(new Stack<>());
        }
        LOCAL_ENTITY_MANAGER.get().push(entityManager);
    }

    public static void clearEntityManager() {
        LOCAL_ENTITY_MANAGER.remove();
    }
}
