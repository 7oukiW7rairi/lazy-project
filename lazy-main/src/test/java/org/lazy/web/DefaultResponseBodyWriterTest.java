package org.lazy.web;

import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.*;

public class DefaultResponseBodyWriterTest {

    private ResponseBodyWriter<Object> responseBodyWriter = new DefaultResponseBodyWriter();

    @DataProvider
    public Object[][] responseBodyWriterProvider() {
        return new Object[][]{
                {new XmlSerializer(), "/web/dummy-xml-object.xml"},
                {new JsonSerializer(), "/web/dummy-json-object.json"}};
    }

    @Test(dataProvider = "responseBodyWriterProvider")
    public void testResponseBodyWriter(Serializer serializer, String filePath) throws IOException {
        DummyTestClass testClassMock = Mockito.mock(DummyTestClass.class);
        Mockito.when(testClassMock.getId()).thenReturn(1);
        Mockito.when(testClassMock.getName()).thenReturn("dummy");
        Mockito.when(testClassMock.isValid()).thenReturn(false);
        Mockito.when(testClassMock.getDateTime()).thenReturn(Date.from(Instant.parse("2023-12-02T17:49:37.332Z")));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        responseBodyWriter.write(testClassMock, serializer, outputStream);

        Assert.assertEquals(outputStream.toString(), new String(getClass().getResourceAsStream(filePath).readAllBytes()));
    }

    @Test
    public void testIsWritable() {
    }
}