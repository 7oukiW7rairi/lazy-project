package org.lazy.app;

public interface WebServerInitializer {

    ConfigurableLocalWebServer initializWebServer(int serverPort, String profile) throws WebServerException;

}
