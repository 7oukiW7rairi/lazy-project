package org.lazy.jpa.integration;

import com.google.auto.service.AutoService;
import org.lazy.common.ComponentProxy;
import org.lazy.core.ComponentAssembler;
import org.lazy.core.CoreException;
import org.lazy.jpa.EntityManagerContextHolder;
import org.lazy.common.ComponentDefinition;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.lang.reflect.Proxy;
import java.util.*;

@AutoService(ComponentAssembler.class)
public class EntityManagerComponentAssembler implements ComponentAssembler<ComponentDefinition> {

    @Override
    public Object assembleComponent(ComponentDefinition definition, Map<String, Object> dependencies) throws CoreException {
        EntityManagerFactory entityManagerFactory = (EntityManagerFactory) dependencies.get(definition.getFactoryDefinition().getFactoryComponent());
        return Proxy.newProxyInstance(
                EntityManager.class.getClassLoader(),
                new Class[]{EntityManager.class},
                (proxy, method, args) -> {
                    EntityManager entityManager = EntityManagerContextHolder.getEntityManager();
                    if (entityManager == null) {
                        entityManager = entityManagerFactory.createEntityManager();
                        EntityManagerContextHolder.setEntityManager(entityManager);

                    }
                    return method.invoke(entityManager, args);
                });
    }

    @Override
    public boolean canAssemble(ComponentDefinition componentDefinition) {
        return ComponentProxy.ENTITY_MANAGER == componentDefinition.getComponentProxy();
    }
}
