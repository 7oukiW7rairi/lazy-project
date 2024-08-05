package org.lazy.jpa.integration;

import org.lazy.core.DefaultEnvironment;
import org.lazy.core.Environment;
import org.lazy.app.DefaultWebApplicationContext;
import org.lazy.common.CommonProperties;
import org.lazy.core.AutoConfig;
import org.lazy.core.ConfigurableApplicationContext;
import org.lazy.jpa.LocalTransactionManager;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.persistence.EntityManagerFactory;

import java.util.*;

import static org.testng.Assert.*;

public class JpaAutoconfigTest {

    private AutoConfig jpaConfig;

    @BeforeMethod
    public void setUp() {
        jpaConfig = new JpaAutoconfig();
    }

    @Test
    public void testConfigure() {
        ConfigurableApplicationContext context = new DefaultWebApplicationContext(CommonProperties.LAZY_APPLICATION_JSON.getName());
        context.registerSingleton(Environment.class.getName(), new DefaultEnvironment("test"));
        assertFalse(context.containsComponent(EntityManagerFactory.class.getName()));

        jpaConfig.configure(context);
        EntityManagerFactory entityManagerFactory = (EntityManagerFactory) context.getComponent(EntityManagerFactory.class.getName());
        assertNotNull(entityManagerFactory);
        assertTrue(entityManagerFactory.getProperties().containsKey("hibernate.ejb.persistenceUnitName")); // default jpa provider
        assertNotNull(context.getComponent(LocalTransactionManager.class.getName()));
    }

    @Test
    public void testShouldConfigure() {
        assertTrue(jpaConfig.shouldConfigure(Set.of("jpa.database.url", "non.existing.jpa.property")));
    }
}