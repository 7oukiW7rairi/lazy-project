package org.lazy.jpa.integration;

import com.google.auto.service.AutoService;
import org.lazy.common.BaseComponentDefinition;
import org.lazy.common.CommonProperties;
import org.lazy.common.ComponentDefinition;
import org.lazy.common.ComponentType;
import org.lazy.common.ConstructorDefinition;
import org.lazy.core.ConfigurableApplicationContext;
import org.lazy.core.Environment;
import org.lazy.jpa.AbstractJpaRepository;
import org.lazy.jpa.HibernateJpaProviderAdapter;
import org.lazy.jpa.JpaLocalTransactionManager;
import org.lazy.jpa.JpaProperties;
import org.lazy.jpa.JpaProviderAdapter;
import org.lazy.jpa.JpaRepository;
import org.lazy.jpa.LocalTransactionManager;
import org.lazy.core.AutoConfig;
import org.lazy.common.FactoryDefinition;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import static org.lazy.core.CoreUtils.loadProperties;

import java.util.*;
import java.util.stream.Collectors;

@AutoService(AutoConfig.class)
public class JpaAutoconfig implements AutoConfig {

    private static final String ENTITY_MANAGER = EntityManager.class.getName();

    @Override
    public void configure(ConfigurableApplicationContext context) {
        if (!context.containsComponent(EntityManagerFactory.class.getName())) {
            Environment environment = context.getEnvironment();
            JpaProviderAdapter jpaProviderAdapter = findJpaProvider(environment);
            EntityManagerFactory entityManagerFactory = new LocalEntityManagerFactoryBuilder(jpaProviderAdapter)
                    .dataSource(dataSource(environment))
                    // TODO use lazy-application.properties entities and package_to_scan to filter
                    .managedClassNames(getEntities())
                    .jpaProperties(providerProperties(environment, jpaProviderAdapter.getJpaProviderPropertiesName()))
                    .build();
            context.registerSingleton(EntityManagerFactory.class.getName(), entityManagerFactory);
            context.registerSingleton(LocalTransactionManager.class.getName(), new JpaLocalTransactionManager(entityManagerFactory));
        }
    }

    private List<String> getEntities() {
         return Arrays.asList(loadProperties(getClass().getClassLoader().getResourceAsStream(CommonProperties.LAZY_APPLICATION_PROPERTIES.getName()))
                    .getProperty(CommonProperties.ENTITIES.getName()).split(","));
    }

    @Override
    public boolean shouldConfigure(Set<String> propertiesName) {
        return EnumSet.allOf(JpaProperties.class).stream()
                .map(JpaProperties::getName).anyMatch(propertiesName::contains);
    }

    @Override
    public List<ComponentDefinition> getComponentDefinitions() {
        return Arrays.asList(jpaEntityManager(), jpaRepository());
    }

    private ComponentDefinition jpaEntityManager() {
        BaseComponentDefinition definition = new BaseComponentDefinition(ENTITY_MANAGER);
        definition.setComponentType(ComponentType.PROTOTYPE);
        definition.setComponentSuperTypes(Collections.singletonList(ENTITY_MANAGER));
        definition.setFactoryDefinition(new FactoryDefinition(EntityManagerFactory.class.getName(),
                "createEntityManager", Collections.emptyList()));
        definition.addDependency(EntityManagerFactory.class.getName());
        return definition;
    }

    private ComponentDefinition jpaRepository() {
        BaseComponentDefinition definition = new BaseComponentDefinition(AbstractJpaRepository.class.getName());
        definition.setComponentType(ComponentType.SINGLETON);
        definition.setAbstract(true);
        definition.setComponentSuperTypes(Collections.singletonList(JpaRepository.class.getName()));
        definition.setConstructor(new ConstructorDefinition(Collections.singletonList(ENTITY_MANAGER), Collections.emptyList()));
        definition.addDependency(ENTITY_MANAGER);
        return definition;
    }

    private JpaProviderAdapter findJpaProvider(Environment environment) {
        // TODO add support for more jpa provider
        if (environment.getPropertyNames().stream().anyMatch(property -> "eclipselink".equals(property.toLowerCase()))) {
            throw new UnsupportedOperationException("Hibernate is the only supported Jpa provider");
        }
        return  new HibernateJpaProviderAdapter();
    }

    private DataSource dataSource(Environment environment) {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(environment.getProperty(JpaProperties.DATABSE_DRIVER_CLASS_NAME.getName()));
        dataSource.setUrl(environment.getProperty(JpaProperties.DATABASE_URL.getName()));
        dataSource.setUsername(environment.getProperty(JpaProperties.DATABASE_USERNAME.getName()));
        dataSource.setPassword(environment.getProperty(JpaProperties.DATABASE_PASSWORD.getName()));
        return dataSource;
    }

    private Map<String, Object> providerProperties(Environment environment, Set<String> providerProperties) {
        return providerProperties.stream()
                .filter(property -> Objects.nonNull(environment.getProperty(property)))
                .collect(Collectors.toMap(property -> property, environment::getProperty));
    }
}
