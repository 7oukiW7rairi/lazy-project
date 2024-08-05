package org.lazy.web;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RequestBodyReaderFactory {

    private static final RequestBodyReader<?> DEFAULT_READER = new DefaultRequestBodyReader();
    private static final List<RequestBodyReader> BODY_READERS = ServiceLoader.load(RequestBodyReader.class).stream()
            .map(ServiceLoader.Provider::get)
            .collect(Collectors.toList());

    public static RequestBodyReader getReader(Type bodyType) {
        if (bodyType instanceof Class<?>) {
            return BODY_READERS.stream().filter(reader -> reader.isReadable((Class<?>) bodyType)).findFirst().orElse(DEFAULT_READER);
        } else if(bodyType instanceof ParameterizedType) {
            Stream<Type> typeStream = Stream.of(((ParameterizedType) bodyType).getActualTypeArguments());
            return BODY_READERS.stream()
                    .filter(responseBodyWriter -> typeStream.anyMatch(type -> responseBodyWriter.isReadable((Class<?>) type)))
                    .findFirst().orElse(DEFAULT_READER);
        }
        return DEFAULT_READER;
    }
}
