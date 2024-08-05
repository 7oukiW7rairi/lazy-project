package org.lazy.app;

import org.lazy.core.ComponentAssembler;
import org.lazy.web.BaseHttpServlet;
import org.lazy.core.CoreException;

import java.lang.reflect.Field;
import java.util.*;

public class ServletComponentAssembler implements ComponentAssembler<BaseHttpServlet> {


    @Override
    public Object assembleComponent(BaseHttpServlet servlet, Map<String, Object> dependencies) throws CoreException {
        for (String fieldName : dependencies.keySet()) {
            Object fieldComponent = dependencies.get(fieldName);
            if (fieldComponent != null) {
                try {
                    Field field = servlet.getClass().getDeclaredField(fieldName);
                    field.setAccessible(true);
                    field.set(servlet, fieldComponent);
                } catch (IllegalAccessException | NoSuchFieldException e) {
                    throw new CoreException(e.getMessage());
                }
            }
        }
        return servlet;
    }

    @Override
    public boolean canAssemble(BaseHttpServlet componentBase) {
        return true;
    }
}
