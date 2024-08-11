package org.lazy.app;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;

import org.lazy.web.BaseHttpServlet;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.testng.annotations.Test;

public class ControllerClassAdapterTest {


    @Test
    void testControllerClassAdapter() throws IOException, NoSuchFieldException, SecurityException {
        String testClassPath = "TestController.class";
        ClassReader classReader = new ClassReader(getClass().getResourceAsStream(testClassPath));
        ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        ControllerClassAdapter controllerClassAdapter = new ControllerClassAdapter(classWriter);
        classReader.accept(controllerClassAdapter, ClassReader.EXPAND_FRAMES);
        
        Class<?> controllerClass = TestUtils.loadClass(classWriter.toByteArray(), TestController.class.getName());
        assertEquals(controllerClass.getSuperclass().getName(), BaseHttpServlet.class.getName());
        assertTrue(controllerClass.getDeclaredField("testEntityRepository").isAnnotationPresent(Inject.class));
        assertTrue(controllerClass.getDeclaredField("testEntityRepository").isAnnotationPresent(Named.class));
    }
}
