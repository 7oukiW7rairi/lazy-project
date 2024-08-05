package org.lazy.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.BindException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class LazyWebLauncher {

    private static final Logger logger = LoggerFactory.getLogger(LazyWebServer.class);

    private LazyWebLauncher() {
    }

    public static void lunchWebapp(LocalWebServer.Type serverType, int serverPort, String profile) {
        logger.info("Lunch web app on server " + serverType + " using port " + serverPort + " with profile " + profile);
        boolean successfull = false;
        try {
            ConfigurableLocalWebServer  webServer = LocalWebServerFactory.getInstance().getLocalWebServer(serverType, serverPort, profile);  
            LazyWebServer.start(webServer);
            successfull = sendCheckRequest(serverPort);
        } catch (BindException e) {
            logger.error("Lunch web app fail to bind to localhost address " + e.getMessage());
            try {
                LazyWebServer.stop(false);
            } catch (Exception t) {
                logger.error("Stop web app server fail " + t.getMessage());
                successfull = false;
            }
        } catch (Exception t) {
            logger.error("Lunch web app fail " + t.getMessage());
            successfull = false;
        }
        if (!successfull) {
            Runtime.getRuntime().exit(1);
        }
    }

    public static boolean shutDownWebApp() {
        try {
            LazyWebServer.stop(true);
        } catch (Exception t) {
            return false;
        }
        return true;
    }

    private static boolean sendCheckRequest(int serverPort) throws IOException, InterruptedException, URISyntaxException {
        HttpRequest request = HttpRequest.newBuilder().uri(new URI("http://localhost:" + serverPort)).GET().build();
            return HttpClient.newBuilder()
                    .build()
                    .send(request, HttpResponse.BodyHandlers.ofString()).statusCode() == 200;
    }

}
