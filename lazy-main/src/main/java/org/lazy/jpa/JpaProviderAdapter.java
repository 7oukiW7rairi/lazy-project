package org.lazy.jpa;

import javax.persistence.spi.PersistenceProvider;
import java.util.*;

public interface JpaProviderAdapter {

    PersistenceProvider getPersistenceProvider();

    AbstractPersistenceUnitInfo getPersistenceUnitInfo();

    Set<String> getJpaProviderPropertiesName();
}
