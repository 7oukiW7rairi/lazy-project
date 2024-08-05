package org.lazy.app;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

public class JettyServerInitializerTest {


    @Test
    void testInitializWebServer() {
        ConfigurableLocalWebServer configurableLocalWebServer = new JettyServerInitializer().initializWebServer(8080, "test");

        assertNotNull(configurableLocalWebServer);
        configurableLocalWebServer.start();
        assertTrue(configurableLocalWebServer.isRunning());
        configurableLocalWebServer.stop();
        assertFalse(configurableLocalWebServer.isRunning());
    }
}
