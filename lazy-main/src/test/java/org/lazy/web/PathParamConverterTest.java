package org.lazy.web;

import org.testng.Assert;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class PathParamConverterTest {

    @Test
    public void testConvertPathParamValid() {
        PathParamConverter pathParamConverter = PathParamConverter.getInstance();
        Assert.assertTrue((Boolean) pathParamConverter.convertPathParam("true", Boolean.class));
        assertEquals(pathParamConverter.convertPathParam("-215", Integer.class), -215);
        assertEquals(pathParamConverter.convertPathParam("15.87", Double.class), 15.87);
        assertEquals(pathParamConverter.convertPathParam("dummy string", String.class), "dummy string");
        assertEquals(pathParamConverter.convertPathParam("54878478", Float.class), (float) 54878478.0);
        assertEquals(pathParamConverter.convertPathParam("439L", null), "439L");
    }

    @Test
    public void testConvertPathParamInvalid() {
        PathParamConverter pathParamConverter = PathParamConverter.getInstance();
        Assert.assertFalse((Boolean) pathParamConverter.convertPathParam("some bla bla", Boolean.class));
        Assert.assertThrows(NumberFormatException.class, () -> pathParamConverter.convertPathParam("not Integer", Integer.class));
        Assert.assertThrows(NumberFormatException.class, () -> pathParamConverter.convertPathParam("C", Double.class));
        Assert.assertThrows(NumberFormatException.class, () -> pathParamConverter.convertPathParam("Bla", Byte.class));
        Assert.assertNull(pathParamConverter.convertPathParam(null, String.class));
    }
}