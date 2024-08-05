package org.lazy.web;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

public class DefaultRequestBodyReaderTest {


    private RequestBodyReader<Object> requestBodyReader =  new DefaultRequestBodyReader();

    @DataProvider
    public Object[][] requestBodyReaderProvider() {
        return new Object[][]{
                {new XmlSerializer(), "/web/dummy-xml-object.xml"},
                {new JsonSerializer(), "/web/dummy-json-object.json"}};
    }

    @Test(dataProvider = "requestBodyReaderProvider")
    void testRequestBodyReader(Serializer serializer, String filePath) throws IOException {
        DummyTestClass dummyTestClass = (DummyTestClass) requestBodyReader.read(DummyTestClass.class, serializer, getClass().getResourceAsStream(filePath));

        assertEquals(1, dummyTestClass.getId());
        assertFalse(dummyTestClass.isValid());
        assertEquals("dummy", dummyTestClass.getName());
        assertEquals(Date.from(Instant.parse("2023-12-02T17:49:37.332Z")), dummyTestClass.getDateTime());
    }

    @Test
    void isReadable() {
    }

}