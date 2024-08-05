package org.lazy.web;

import java.io.IOException;
import java.io.OutputStream;

public interface ResponseBodyWriter<T extends Object> {

    void write(T body, Serializer serializer, OutputStream outputStream) throws IOException;

    boolean isWritable(Class<?> type);

}
