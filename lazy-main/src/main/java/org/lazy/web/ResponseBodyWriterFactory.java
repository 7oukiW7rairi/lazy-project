package org.lazy.web;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Stream;

public class ResponseBodyWriterFactory {

    public static final ResponseBodyWriter<Object> DEFAULT_WRITER = new DefaultResponseBodyWriter();
    private static final Set<ResponseBodyWriter> RESPONSE_BODY_WRITERS = initializeResponseBodyWriters();
    private static ResponseBodyWriterFactory INSTANCE;

    private ResponseBodyWriterFactory() {
    }

    public static ResponseBodyWriterFactory getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ResponseBodyWriterFactory();
        }
        return INSTANCE;
    }

    private static Set<ResponseBodyWriter> initializeResponseBodyWriters() {
        Set<ResponseBodyWriter> responseBodyWriters = new HashSet<>();
        responseBodyWriters.add(DEFAULT_WRITER);
        ServiceLoader.load(ResponseBodyWriter.class).forEach(responseBodyWriters::add);
        return responseBodyWriters;
    }

    public ResponseBodyWriter getResponseBodyWriter(Type returnType) {
        if (returnType instanceof Class<?>) {
            return RESPONSE_BODY_WRITERS.stream().filter(writer -> writer.isWritable((Class<?>) returnType)).findFirst().orElse(DEFAULT_WRITER);
        } else if(returnType instanceof ParameterizedType) {
            Stream<Type> typeStream = Stream.of(((ParameterizedType) returnType).getActualTypeArguments());
            return RESPONSE_BODY_WRITERS.stream()
                    .filter(responseBodyWriter -> typeStream.anyMatch(type -> responseBodyWriter.isWritable((Class<?>) type)))
                    .findFirst().orElse(DEFAULT_WRITER);
        }
        return DEFAULT_WRITER;
    }
}
