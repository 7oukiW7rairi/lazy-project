package org.lazy.web;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class BaseHttpServletTest {

    private static final String XML_CONTENT_TYPE = "application/xml";
    private static final String JSON_CONTENT_TYPE = "application/json";

    private DummyServletTest dummyServletTest;

    @Mock
    private HttpServletRequest requestMock;

    @Mock
    private HttpServletResponse responseMock;

    @BeforeClass
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        dummyServletTest = Mockito.spy(new DummyServletTest());
        Mockito.doReturn(Mockito.mock(ServletContext.class)).when(dummyServletTest).getServletContext();
        dummyServletTest.init();
    }

    @DataProvider
    public Object[][] httpGetProvider() {
        return new Object[][]{
                {"/web/dummy-xml-object.xml", "/1", XML_CONTENT_TYPE},
                {"/web/dummy-json-objects.json", "", JSON_CONTENT_TYPE}};
    }

    @Test(dataProvider = "httpGetProvider")
    public void testHttpGetMethod(String filePath, String pathInfo, String contentType) throws IOException, ServletException {

        ResponseOutputStream outputStream = new ResponseOutputStream();
        prepareTestMocks(pathInfo, HttpMethod.GET, contentType, outputStream);

        dummyServletTest.service(requestMock, responseMock);

        Assert.assertEquals(new String(outputStream.toByteArray()), new String(getClass().getResourceAsStream(filePath).readAllBytes()));

    }

    @Test
    public void testHttpPostMethod() throws IOException, ServletException {

        ResponseOutputStream outputStream = new ResponseOutputStream();
        prepareTestMocks("", HttpMethod.POST, JSON_CONTENT_TYPE, outputStream);
        Mockito.when(requestMock.getInputStream()).thenReturn(new RequestInputStream(getClass().getResourceAsStream("/web/dummy-json-object.json").readAllBytes()));

        dummyServletTest.service(requestMock, responseMock);

        Assert.assertEquals(new String(outputStream.toByteArray()), "Dummy object with id 1 saved successfully");
    }

    @Test
    public void testHttpPutMethod() throws IOException, ServletException {

        ResponseOutputStream outputStream = new ResponseOutputStream();
        prepareTestMocks("", HttpMethod.PUT, XML_CONTENT_TYPE, outputStream);
        Mockito.when(requestMock.getParameter("id")).thenReturn("1");
        Mockito.when(requestMock.getParameter("dateTime")).thenReturn("2023-12-02T17:49:37.332Z");
        Mockito.when(requestMock.getInputStream()).thenReturn(new RequestInputStream(getClass().getResourceAsStream("/web/dummy-xml-object.xml").readAllBytes()));

        dummyServletTest.service(requestMock, responseMock);

        Assert.assertEquals(new String(outputStream.toByteArray()), new String(getClass().getResourceAsStream("/web/dummy-json-object.json").readAllBytes()));
    }

    @Test
    public void testHttpDeleteMethod() throws IOException, ServletException {

        ResponseOutputStream outputStream = new ResponseOutputStream();
        prepareTestMocks("/delete/78", HttpMethod.DELETE, JSON_CONTENT_TYPE, outputStream);

        dummyServletTest.service(requestMock, responseMock);

        Assert.assertEquals(new String(outputStream.toByteArray()), "Dummy object with id 78 deleted successfully");
    }

    private void prepareTestMocks(String pathInfo, HttpMethod method, String contentType, ResponseOutputStream outputStream) throws IOException {
        Mockito.when(requestMock.getPathInfo()).thenReturn(pathInfo);
        Mockito.when(requestMock.getMethod()).thenReturn(method.getMethod());
        Mockito.when(requestMock.getContentType()).thenReturn(contentType);
        Mockito.when(responseMock.getOutputStream()).thenReturn(outputStream);
    }
}