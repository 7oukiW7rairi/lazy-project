package org.lazy.web;

import org.lazy.web.annotation.QueryParam;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import static org.testng.Assert.assertEquals;

public class PathParamHandlerFactoryTest {

    private PathParamHandlerFactory pathParamHandlerFactory;

    @Mock
    private HttpServletRequest requestMock;

    @Mock
    private QueryParam paramAnnotationMock;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Parameter parameterMock;

    @Mock
    private Method handler;

    @BeforeClass
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        pathParamHandlerFactory = PathParamHandlerFactory.getInstance();
    }

    // TODO Fix and implement other test to cover all cases
    @Test(enabled = false)
    public void testHandlerRequestParamValue() throws IOException {
        String param = "integerParam";
        Integer paramValue = 6765;
        Mockito.when(paramAnnotationMock.value()).thenReturn(param);

        Mockito.when(parameterMock.getAnnotation(QueryParam.class)).thenReturn(paramAnnotationMock);
        Mockito.when(parameterMock.isAnnotationPresent(QueryParam.class)).thenReturn(true);
        //Mockito.when(parameterMock.getType()).thenReturn(Integer.class);
        Mockito.when(requestMock.getParameter(param)).thenReturn("" + paramValue);

        Integer expectedParamValue = (Integer) pathParamHandlerFactory.getPathParamHandler(parameterMock).handlePathParam(parameterMock, requestMock, handler);

        assertEquals(expectedParamValue, paramValue);
    }
}