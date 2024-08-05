package org.lazy.app;

import static org.lazy.app.ThreadUtils.startDaemonThread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LazyWebServer {

    private static final Logger logger = LoggerFactory.getLogger(LazyWebServer.class);

    private static ConfigurableLocalWebServer server;

    // Synchronization objects
    private static final Object syncStart = new Object();
    private static final Object syncStop = new Object();

    // Exception that happened at start
    private static Exception startException;
    // Exception that happened at stop
    private static Exception stopException;

    private LazyWebServer() {
    }

    static void start(ConfigurableLocalWebServer localWebServer) throws Exception {

        startException = null;
        server = localWebServer;
        Runnable startServer = () -> {
            logger.info("Start server");
            try {
                server.start();
            } catch (Exception ex) {
                startException = ex;
                synchronized (syncStart) {
                    syncStart.notifyAll();
                }
            }
        };

        synchronized (syncStart) {
            startDaemonThread(startServer, "start-server");
            // to prevent the console from exiting after main method finish
            syncStart.wait(Long.MAX_VALUE);
        }

        if (startException != null) {
            logger.error("failed to start", startException);
            throw new WebServerException("Error while starting Server", startException);
        }

        boolean running = (server != null && server.isRunning());
        logger.debug("server running: {}", running);
    }

    static void stop(boolean callSystemExit) throws Exception {

        Runnable stopServer = () -> {
            try {
                server.stop();
            } catch (Exception ex) {
                stopException = ex;
            }
        };

        if (server == null) {
            logger.info("No server to stop");
        } else {
            stopException = null;
            synchronized (syncStop) {
                startDaemonThread(stopServer, "stop-server");
                syncStop.wait(Long.MAX_VALUE);
            }
            if (stopException != null) {
                throw new WebServerException("Error while stopping Server", stopException);
            }
        }

        if (callSystemExit) {
            boolean running = server != null && server.isRunning();
            int exitCode = running ? 1 : 0;
            logger.info("exit({})", exitCode);
            System.exit(exitCode);
        }

    }


}
