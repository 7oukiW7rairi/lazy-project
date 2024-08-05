package org.lazy.web;

import java.io.IOException;
import java.io.InputStream;

public interface RequestBodyReader<T> {

    T read(Class<?> type, Serializer serializer, InputStream reader) throws IOException;

    boolean isReadable(Class<?> type);
}
