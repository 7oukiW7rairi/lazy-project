package org.lazy.web;

import jakarta.servlet.http.HttpServletResponse;
import java.util.*;

public class ExceptionHandlerFactory {

    private static final Map<Class<?>, ExceptionHandler<?>> EXCEPTION_MAPPER = initializeExceptionHandlers();
    private static ExceptionHandlerFactory INSTANCE;

    private ExceptionHandlerFactory() {
    }

    public static ExceptionHandlerFactory getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ExceptionHandlerFactory();
        }
        return INSTANCE;
    }

    public ExceptionHandler getExceptionHandler(Class<?> expClass) throws UnsupportedOperationException {
        if (EXCEPTION_MAPPER.containsKey(expClass)) {
            return EXCEPTION_MAPPER.get(expClass);
        }
        return EXCEPTION_MAPPER.entrySet().stream()
                .filter(exceptionHandlerEntry -> exceptionHandlerEntry.getKey().isAssignableFrom(expClass))
                .map(Map.Entry::getValue)
                .findFirst().orElseThrow(() -> new UnsupportedOperationException("Unsupported Operation"));
    }

    // TODO add more exception handler for more specific exception
    private static Map<Class<?>, ExceptionHandler<?>> initializeExceptionHandlers() {
        Map<Class<?>, ExceptionHandler<?>> exceptionHandlerMap =  new HashMap<>();
        exceptionHandlerMap.put(Error.class,
                exp -> Response.body(new ApplicationError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, exp.getMessage())).build());
        exceptionHandlerMap.put(Exception.class,
                exp -> Response.body(new ApplicationError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exp.getMessage())).build());
        ServiceLoader.load(ExceptionHandler.class).forEach(exceptionHandler -> exceptionHandlerMap.put(exceptionHandler.getType(), exceptionHandler));
        return exceptionHandlerMap;
    }
}
