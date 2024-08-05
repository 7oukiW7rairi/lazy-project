package org.lazy.jpa.integration;

 import org.lazy.jpa.AbstractPersistenceUnitInfo;
 import org.lazy.jpa.JpaProviderAdapter;
 import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;
import javax.sql.DataSource;
import java.util.*;
import java.util.stream.Collectors;

 import static org.lazy.core.ClassPathScanUtils.getClassesName;

public class LocalEntityManagerFactoryBuilder {

    private final PersistenceProvider persistenceProvider;
    private final AbstractPersistenceUnitInfo persistenceUnitInfo;
    private Set<String> providerPropertiesName;
    private Map<String, Object> jpaProperties = new HashMap<>();

    public LocalEntityManagerFactoryBuilder(JpaProviderAdapter jpaProviderAdapter) {
        this.persistenceProvider = jpaProviderAdapter.getPersistenceProvider();
        this.persistenceUnitInfo = jpaProviderAdapter.getPersistenceUnitInfo();
        this.providerPropertiesName = jpaProviderAdapter.getJpaProviderPropertiesName();
    }

    public LocalEntityManagerFactoryBuilder dataSource(DataSource dataSource) {
        persistenceUnitInfo.setDataSource(dataSource);
        return this;
    }

    public LocalEntityManagerFactoryBuilder packageToScan(String packageToScan) {
        return managedClassNames(getClassesName(packageToScan, null, null, "javax.persistence.Entity"));
    }

    public LocalEntityManagerFactoryBuilder managedClassNames(List<String> managedClassNames) {
        persistenceUnitInfo.setManagedClassNames(managedClassNames);
        return this;
    }

    public LocalEntityManagerFactoryBuilder jpaProperties(Map<String, Object> jpaProperties) {
        this.jpaProperties = jpaProperties;
        return this;
    }

    public EntityManagerFactory build() {
        if (persistenceUnitInfo.getNonJtaDataSource() == null) {
            throw new IllegalArgumentException("No Datasource provided");
        }
        if (persistenceUnitInfo.getManagedClassNames() == null) {
            managedClassNames(Collections.emptyList());
        }
        return persistenceProvider.createContainerEntityManagerFactory(persistenceUnitInfo, filterValidJpaProperties());
    }

    private Map<String, Object> filterValidJpaProperties() {
        return jpaProperties.entrySet().stream()
                .filter(entry -> providerPropertiesName.contains(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}
