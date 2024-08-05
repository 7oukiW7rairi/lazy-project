package org.lazy.web;

import org.lazy.web.annotation.PathMapping;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HandlerMappingTest {

    @Mock
    HttpServletRequest requestMock;

    @Mock
    PathMapping pathMappingMock;

    @Mock
    Method methodMock;

    private HandlerMapping handlerMapping;

    @BeforeClass
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DataProvider
    public Object[][] getHandlerProvider() {
        return new Object[][]{
                {"GET", "/32", "/{id}"},
                {"PUT", "/user/14", "/user/{id}"},
                {"DELETE", "/dummy_name/delete", "/{name}/delete"},
                {"POST", "", ""}};
    }

    @Test(dataProvider = "getHandlerProvider")
    public void testGetHandlerValid(String method, String requestPath, String path) throws WebException {
        Mockito.when(pathMappingMock.method()).thenReturn(HttpMethod.of(method));
        Mockito.when(pathMappingMock.path()).thenReturn(path);

        Mockito.when(methodMock.getAnnotation(PathMapping.class)).thenReturn(pathMappingMock);
        Mockito.when(methodMock.isAnnotationPresent(PathMapping.class)).thenReturn(true);
        List<Method> regularMethods = createMethods();
        regularMethods.add(methodMock);

        Mockito.when(requestMock.getMethod()).thenReturn(method);
        Mockito.when(requestMock.getPathInfo()).thenReturn(requestPath);

        handlerMapping = new HandlerMapping(regularMethods.toArray(createMethods().toArray(new Method[0])));
        Method actualMethod = handlerMapping.getHandler(requestMock);

        Assert.assertEquals(actualMethod.getAnnotation(PathMapping.class).method(), HttpMethod.of(requestMock.getMethod()));
        Assert.assertEquals(actualMethod.getAnnotation(PathMapping.class).path(), path);
    }

    @DataProvider
    public Object[][] getInvalidHandlerProvider() {
        return new Object[][]{
                {"GET", "/{id}/title", "GET", "/32/dummyTitle"},
                {"PUT", "/user/{id}", "GET", "/user/56"},
                //{"POST", "/item/{title}", "POST", "/item/title/dummy"},
                {null, "/dummy_name/delete", "DELETE", "/{name}/delete"}};
    }

    @Test(dataProvider = "getInvalidHandlerProvider")
    public void testGetHandlerInvalid(String method, String path, String requestMethod, String requestPath) {

        Mockito.when(pathMappingMock.method()).thenReturn(HttpMethod.of(method));
        Mockito.when(pathMappingMock.path()).thenReturn(path);

        Mockito.when(methodMock.getAnnotation(PathMapping.class)).thenReturn(pathMappingMock);
        Mockito.when(methodMock.isAnnotationPresent(PathMapping.class)).thenReturn(method != null);

        Mockito.when(requestMock.getMethod()).thenReturn(requestMethod);
        Mockito.when(requestMock.getPathInfo()).thenReturn(requestPath);

        handlerMapping = new HandlerMapping(methodMock);

        Assert.assertThrows(WebException.class, () -> handlerMapping.getHandler(requestMock));
    }

    private List<Method> createMethods() {
        return Stream.of(HttpMethod.values()).map(httpMethod -> {
            PathMapping pathMappingMock = Mockito.mock(PathMapping.class, Mockito.RETURNS_DEFAULTS);
            Mockito.when(pathMappingMock.method()).thenReturn(httpMethod);
            Mockito.when(pathMappingMock.path()).thenReturn("");

            Method methodMock = Mockito.mock(Method.class);
            Mockito.when(methodMock.getAnnotation(PathMapping.class)).thenReturn(pathMappingMock);
            Mockito.when(methodMock.isAnnotationPresent(PathMapping.class)).thenReturn(true);
            return methodMock;
        }).collect(Collectors.toList());
    }
}