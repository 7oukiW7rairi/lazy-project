package org.lazy.jpa;

import org.hibernate.jpa.HibernatePersistenceProvider;

import javax.sql.DataSource;
import java.util.*;

public class HibernatePersistenceUnitInfo extends AbstractPersistenceUnitInfo {

    @Override
    public void setPersistenceUnitName(String persistenceUnitName) {
        this.persistenceUnitName = persistenceUnitName;
    }

    @Override
    public void setManagedClassNames(List<String> managedClassNames) {
        this.managedClassNames = managedClassNames;
    }

    @Override
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    @Override
    public void setPersistenceProviderClassName(String persistenceProviderClassName) {
        this.persistenceProviderClassName = HibernatePersistenceProvider.class.getName();
    }
}
