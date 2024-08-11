package org.lazy.app;

public class TestUtils {

    private TestUtils() {

    }

    public static Class<?> loadClass(byte[] byteArray, String className) {
        return new ClassLoader () {
            public Class<?> findClass(String name) {
                return defineClass(name,byteArray,0,byteArray.length);
            }

        }.findClass(className);
    }

}
