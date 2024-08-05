package org.lazy.jpa;

import org.hibernate.jpa.HibernatePersistenceProvider;

import javax.persistence.spi.PersistenceProvider;
import java.util.*;
import java.util.stream.Collectors;

public class HibernateJpaProviderAdapter implements JpaProviderAdapter {


    public PersistenceProvider getPersistenceProvider() {
        return new HibernatePersistenceProvider();
    }

    public HibernatePersistenceUnitInfo getPersistenceUnitInfo() {
        return new HibernatePersistenceUnitInfo();
    }

    @Override
    public Set<String> getJpaProviderPropertiesName() {
        return EnumSet.allOf(HibernateProperties.class).stream()
                .map(HibernateProperties::getPropertyName).collect(Collectors.toSet());
    }


}
