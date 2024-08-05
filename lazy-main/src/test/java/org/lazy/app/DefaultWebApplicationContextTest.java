package org.lazy.app;

import org.lazy.app.DefaultWebApplicationContext;
import org.lazy.common.CommonProperties;
import org.lazy.core.Environment;
import org.lazy.web.DummyServletTest;
import org.mockito.Mockito;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class DefaultWebApplicationContextTest {

    private DefaultWebApplicationContext applicationContext;

    @BeforeMethod
    public void setUp() {
        applicationContext = new DefaultWebApplicationContext(CommonProperties.LAZY_APPLICATION_JSON.getName());
    }

    @Test
    public void testGetWebComponent() {
        DummyServletTest dummyServletTest = new DummyServletTest();
        applicationContext.getWebComponent(dummyServletTest);

        assertNotNull(dummyServletTest.getComponentWithoutDependency());

    }

    @Test
    public void testGetComponent() {
        Environment environment = Mockito.mock(Environment.class);
        Mockito.when(environment.getActiveProfile()).thenReturn("dummyProfile");
        applicationContext.registerSingleton(Environment.class.getName(), environment);

        Object componentInstance = applicationContext.getComponent("org.lazy.core.ComponentFromConfigWithProfile");

        assertNotNull(componentInstance);
        assertEquals(componentInstance.getClass().getCanonicalName(), "org.lazy.core.ComponentFromConfig");
    }

    @Test
    public void testRegisterSingleton() {
    }
}