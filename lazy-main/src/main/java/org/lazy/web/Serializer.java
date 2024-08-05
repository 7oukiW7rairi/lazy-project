package org.lazy.web;

import java.io.IOException;
import java.io.InputStream;

/**
 * An interface to provide a custom serializer for each MediaType
 */
public interface Serializer {

    <T, E> T serialize(E object) throws IOException;

    <T> T deserialize(InputStream src, Class<T> objectType) throws IOException;

    boolean canSerialize(MediaType mediaType);

}
