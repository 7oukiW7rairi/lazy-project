package org.lazy.app;

public interface LocalWebServer {

    void start() throws WebServerException;

    void stop() throws WebServerException;

    boolean isRunning();


    public enum Type {
        JETTY
    }

}
