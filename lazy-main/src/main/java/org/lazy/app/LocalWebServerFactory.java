package org.lazy.app;

public class LocalWebServerFactory {

    private static LocalWebServerFactory INSTANCE;

    private LocalWebServerFactory() {

    }

    public static LocalWebServerFactory getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LocalWebServerFactory();
        }
        return INSTANCE;
    }

    public ConfigurableLocalWebServer getLocalWebServer(LocalWebServer.Type serverType, int serverPort, String profile) {
        if (serverType == LocalWebServer.Type.JETTY) {
            return new JettyServerInitializer().initializWebServer(serverPort, profile);
        } else {
            throw new UnsupportedOperationException("Server is not supported");
        }
    }

}
