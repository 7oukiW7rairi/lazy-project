package org.lazy.web;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ExceptionHandlerFactoryTest {

    private static final String EXCEPTION_MESSAGE = "Dummy Exception message";
    private static final String ERROR_MESSAGE = "Dummy Error message";

    @Mock
    private Throwable throwableMock;

    @BeforeClass
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DataProvider
    public Object[][] validExceptionHandlerProvider() {
        return new Object[][]{
                {UnsupportedOperationException.class, EXCEPTION_MESSAGE, 500},
                {Exception.class, EXCEPTION_MESSAGE, 500},
                {OutOfMemoryError.class, ERROR_MESSAGE, 503},
                {Error.class, ERROR_MESSAGE, 503}};
    }

    @Test(dataProvider = "validExceptionHandlerProvider")
    public void testGetExceptionHandlerValid(Class<?> exceptionClass, String expectedMessage, int expectedStatus) {

        Mockito.when(throwableMock.getMessage()).thenReturn(expectedMessage);

        Response response = ExceptionHandlerFactory.getInstance().getExceptionHandler(exceptionClass).handleException(throwableMock);
        ApplicationError error = (ApplicationError) response.getBody();
        Assert.assertEquals(error.getMessage(), expectedMessage);
        Assert.assertEquals(error.getStatus(), expectedStatus);
    }

    @Test()
    public void testGetExceptionHandlerInvalid() {
        Assert.assertThrows(UnsupportedOperationException.class,
                () -> ExceptionHandlerFactory.getInstance().getExceptionHandler(Throwable.class).handleException(throwableMock));
    }
}