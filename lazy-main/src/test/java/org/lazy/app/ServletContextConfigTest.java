package org.lazy.app;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.EventListener;
import java.util.List;
import java.util.stream.Collectors;

import org.lazy.core.ConfigurableApplicationContext;
import org.testng.annotations.Test;

public class ServletContextConfigTest {

    @Test
    void testConverttoServletPathFormat() {

    }

    @Test
    void testGetApplicationContext() {
        ConfigurableApplicationContext applicationContext = ServletContextConfig.getInstance().getApplicationContext("test");

        assertNotNull(applicationContext);
        assertNotNull(applicationContext.getEnvironment());
        assertNotNull(applicationContext.getComponent("org.lazy.core.ConfigComponent"));
    }

    @Test
    void testGetControllers() {
        List<Class<?>> controllers = ServletContextConfig.getInstance().getControllers();
        assertEquals(controllers.size(), 1);
        Class<?> controllClass = controllers.get(0);
        assertEquals(controllClass.getName(), "org.lazy.app.DummyController");
        assertEquals(controllClass.getSuperclass().getName(), "org.lazy.web.BaseHttpServlet");
    }

    @Test
    void testGetWebFilters() {
        assertEquals(ServletContextConfig.getInstance().getWebFilters(), Collections.emptyList());
    }

    @Test
    void testGetWebListeners() {
        List<EventListener> webListeners = ServletContextConfig.getInstance().getWebListeners();

        assertEquals(webListeners.size(), 2);
        assertEquals(webListeners.stream().map(EventListener::getClass).collect(Collectors.toList()),
                     Arrays.asList(AppRequestListener.class, ApplicationContextListener.class));
    }
}
