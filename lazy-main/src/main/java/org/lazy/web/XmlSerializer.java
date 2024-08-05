package org.lazy.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.IOException;
import java.io.InputStream;

public class XmlSerializer implements Serializer {

    private final ObjectMapper xmlMapper;

    public XmlSerializer() {
        xmlMapper = new XmlMapper().enable(SerializationFeature.INDENT_OUTPUT);
    }


    @Override
    public String serialize(Object object) throws IOException {
        return xmlMapper.writeValueAsString(object);
    }

    @Override
    public <T> T deserialize(InputStream src, Class<T> objectType) throws IOException {
        return xmlMapper.readValue(src, objectType);
    }

    @Override
    public boolean canSerialize(MediaType mediaType) {
        return MediaType.APPLICATION_XML == mediaType;
    }
}
