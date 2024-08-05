package org.lazy.web;

import java.io.IOException;
import java.io.InputStream;

public class DefaultRequestBodyReader implements RequestBodyReader<Object> {
    @Override
    public Object read(Class<?> type, Serializer serializer, InputStream inputStream) throws IOException {
        return serializer.deserialize(inputStream, type);
    }

    @Override
    public boolean isReadable(Class<?> type) {
        return true;
    }
}
