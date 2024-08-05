package org.lazy.app;

import org.lazy.app.ServletComponentAssembler;
import org.lazy.core.ComponentWithoutDependency;
import org.lazy.core.CoreException;
import org.lazy.web.DummyServletTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.*;

import static org.testng.Assert.*;

public class ServletComponentAssemblerTest {

    private ServletComponentAssembler servletComponentAssembler;

    @BeforeMethod
    public void setUp() {
        servletComponentAssembler = new ServletComponentAssembler();
    }

    @Test
    public void testAssembleComponentThrowException() {
        assertThrows(CoreException.class,
                () -> servletComponentAssembler.assembleComponent(new DummyServletTest(), Map.of("nonExistingField", new ComponentWithoutDependency())));
    }
}