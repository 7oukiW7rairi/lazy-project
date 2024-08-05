package org.lazy.core;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

import org.lazy.common.CommonProperties;

public class DefaultApplicationContextTest {

    private ApplicationContext componentFactory;

    @BeforeMethod
    public void setUp() {
        componentFactory = new DefaultApplicationContext(CommonProperties.LAZY_APPLICATION_JSON.getName());
    }

    @Test
    public void testGetComponentFromConfig() {
        Object componentInstance = componentFactory.getComponent("org.lazy.core.ComponentFromConfig");

        assertNotNull(componentInstance);
        assertEquals(componentInstance.getClass().getCanonicalName(), "org.lazy.core.ComponentFromConfig");
    }

    @Test
    public void testGetComponentFromInterface() {
        Object componentInstance = componentFactory.getComponent("org.lazy.core.DummyInterface");

        assertNotNull(componentInstance);
        assertEquals(componentInstance.getClass().getCanonicalName(), "org.lazy.core.DummyComponent");
    }
}