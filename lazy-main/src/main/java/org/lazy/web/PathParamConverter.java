package org.lazy.web;

import java.util.*;

public class PathParamConverter {

    private static PathParamConverter INSTANCE;
    private static final Map<Class<?>, Converter<?, String>> CONVERTERS = initializeConverters();

    private PathParamConverter() {
    }

    public static PathParamConverter getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PathParamConverter();
        }
        return INSTANCE;
    }

    // TODO throw custom exception for conversion fail
    public Object convertPathParam(String requestString, Class<?> type) {
        return CONVERTERS.getOrDefault(type, s -> s).convert(requestString);
    }

    private static Map<Class<?>, Converter<?, String>> initializeConverters() {
        Map<Class<?>, Converter<?, String>> converterMap = new HashMap<>();
        converterMap.put(Boolean.class, s -> s == null ? null : Boolean.parseBoolean(s));
        converterMap.put(Byte.class, s -> s == null ? null : Byte.parseByte(s));
        converterMap.put(Short.class, s -> s == null ? null : Short.parseShort(s));
        converterMap.put(Integer.class, s -> s == null ? null : Integer.parseInt(s));
        converterMap.put(Long.class, s -> s == null ? null : Long.parseLong(s));
        converterMap.put(Float.class, s -> s == null ? null : Float.parseFloat(s));
        converterMap.put(Double.class, s -> s == null ? null : Double.parseDouble(s));
        ServiceLoader.load(Converter.class).forEach(converter -> converterMap.put(converter.getType(), converter));
        return converterMap;
    }
}
