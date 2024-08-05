package org.lazy.jpa;

import org.lazy.jpa.integration.LocalEntityManagerFactoryBuilder;
import org.h2.jdbcx.JdbcDataSource;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.*;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertThrows;

public class LocalEntityManagerFactoryBuilderTest {

    private LocalEntityManagerFactoryBuilder localEntityManagerFactoryBuilder;
    private JpaProviderAdapter jpaProviderAdapter;

    @BeforeMethod
    public void setUp() {
    }

    @Test
    public void testDataSource() {
        jpaProviderAdapter = new HibernateJpaProviderAdapter();
        localEntityManagerFactoryBuilder = new LocalEntityManagerFactoryBuilder(jpaProviderAdapter)
                .dataSource(createTestDatasource());

        EntityManagerFactory entityManagerFactory = localEntityManagerFactoryBuilder.build();
        assertNotNull(entityManagerFactory);
        assertEquals(entityManagerFactory.getMetamodel().getEntities().size(), 0);
    }

    @Test
    public void testPackageToScan() {
        jpaProviderAdapter = new HibernateJpaProviderAdapter();
        localEntityManagerFactoryBuilder = new LocalEntityManagerFactoryBuilder(jpaProviderAdapter)
                .dataSource(createTestDatasource()).packageToScan("org.lazy.jpa");

        EntityManagerFactory entityManagerFactory = localEntityManagerFactoryBuilder.build();
        assertNotNull(entityManagerFactory);
        assertEquals(entityManagerFactory.getMetamodel().getEntities().size(), 1);
        assertNull(entityManagerFactory.getProperties().get("hibernate.hbm2ddl.auto"));
    }

    @Test
    public void testJpaProperties() {
        jpaProviderAdapter = new HibernateJpaProviderAdapter();
        localEntityManagerFactoryBuilder = new LocalEntityManagerFactoryBuilder(jpaProviderAdapter)
                .dataSource(createTestDatasource()).jpaProperties(Map.of("hibernate.hbm2ddl.auto", "update"));

        EntityManagerFactory entityManagerFactory = localEntityManagerFactoryBuilder.build();
        assertNotNull(entityManagerFactory);
        assertEquals(entityManagerFactory.getMetamodel().getEntities().size(), 0);
        assertEquals(entityManagerFactory.getProperties().get("hibernate.hbm2ddl.auto"), "update");
    }

    @Test
    public void testBuild() {
        jpaProviderAdapter = new HibernateJpaProviderAdapter();
        localEntityManagerFactoryBuilder = new LocalEntityManagerFactoryBuilder(jpaProviderAdapter);

        assertThrows("No Datasource provided", IllegalArgumentException.class, () -> localEntityManagerFactoryBuilder.build());
    }

    private DataSource createTestDatasource() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:default;MODE=LEGACY;DB_CLOSE_DELAY=-1;");
        dataSource.setUser("sa");
        dataSource.setPassword("sa");
        return dataSource;
    }
}