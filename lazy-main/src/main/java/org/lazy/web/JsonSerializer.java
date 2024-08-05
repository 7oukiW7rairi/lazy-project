package org.lazy.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.io.InputStream;

public class JsonSerializer implements Serializer {

    private final ObjectMapper objectMapper;

    public JsonSerializer() {
        this.objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    public String serialize(Object object) throws IOException {
        return objectMapper.writeValueAsString(object);
    }

    @Override
    public <T> T deserialize(InputStream src, Class<T> objectType) throws IOException {
        return objectMapper.readValue(src, objectType);
    }

    @Override
    public boolean canSerialize(MediaType mediaType) {
        return MediaType.APPLICATION_JSON == mediaType;
    }
}
