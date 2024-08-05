package org.lazy.core;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class CoreUtils {

    private static final Map<Class<?>, Object> DEFAULT_TYPE_VALUES = Map.of(boolean.class, false, byte.class, (byte) 0, short.class, (short) 0, int.class, 0, long.class, (long) 0);

    private CoreUtils() {
    }

    public static <T> T instantiateClass(Constructor<T> ctor, Object... args) throws IllegalAccessException, InvocationTargetException, InstantiationException {
            Class<?>[] parameterTypes = ctor.getParameterTypes();
            Object[] argsWithDefaultValues = new Object[args.length];
            for (int i = 0 ; i < args.length; i++) {
                if (args[i] == null) {
                    Class<?> parameterType = parameterTypes[i];
                    argsWithDefaultValues[i] = parameterType.isPrimitive() ? DEFAULT_TYPE_VALUES.get(parameterType) : null;
                }
                else {
                    argsWithDefaultValues[i] = args[i];
                }
            }
            return ctor.newInstance(argsWithDefaultValues);
    }


    public static Properties loadProperties(String propertiesFileName) {
        Properties configuration = new Properties();
        try (BufferedInputStream inputStream = new BufferedInputStream(Files.newInputStream(Paths.get(propertiesFileName)))) {
            configuration.load(inputStream);
        } catch (IOException e) {
            throw new CoreException(e.getMessage());
        }
        return configuration;
    }

    public static Properties loadProperties(InputStream inputStream) {
        Properties properties = new Properties();
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new CoreException(e.getMessage());
        }
        return properties;
    }

}
