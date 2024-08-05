package org.lazy.app;

import org.eclipse.jetty.server.Server;

public class JettyWebServer implements ConfigurableLocalWebServer {

    private Server server;
    public JettyWebServer(Server server) {
        this.server = server;
    }

    @Override
    public void start() throws WebServerException {
        try {
            server.start();
            server.join();
        } catch (Exception e) {
            throw new WebServerException(e.getMessage(), e);
        }
    }

    @Override
    public void stop() throws WebServerException {
        try {
            server.stop();
        } catch (Exception e) {
            throw new WebServerException(e.getMessage(), e);
        }
    }

    @Override
    public boolean isRunning() {
        return server.isRunning();
    }

}
