package org.lazy.web;

import java.io.IOException;
import java.io.OutputStream;

public class DefaultResponseBodyWriter implements ResponseBodyWriter<Object> {
    @Override
    public void write(Object body, Serializer serializer, OutputStream outputStream) throws IOException {
        String response;
        if (String.class.equals(body.getClass())) {
            response = (String) body;
        } else {
            response = serializer.serialize(body);
        }
        outputStream.write(response.getBytes());
    }

    @Override
    public boolean isWritable(Class<?> type) {
        return true;
    }
}
