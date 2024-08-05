package org.lazy.app;

import org.lazy.app.LocalWebServer.Type;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import static org.testng.Assert.*;

public class LazyWebLauncherTest {

    @BeforeMethod
    public void setUp() {
        //LazyWebLauncher.lunchWebapp(8080, "test");
    }

    @AfterMethod
    public void shutDown() {
        LazyWebLauncher.shutDownWebApp();

    }

    @Test
    public void testLunchWebapp() throws IOException, URISyntaxException, InterruptedException {

        LazyWebLauncher.lunchWebapp(Type.JETTY, 8080, "test");
        HttpResponse<String> response = sendRequest();
    
        assertEquals(response.body(), "Successfull");
    }

    private  HttpResponse<String> sendRequest() throws URISyntaxException, IOException, InterruptedException {
            HttpRequest request = HttpRequest.newBuilder().uri(new URI("http://localhost:8080/dummy-test")).GET().build();
            return HttpClient.newBuilder()
                    .build()
                    .send(request, HttpResponse.BodyHandlers.ofString());
    }
}