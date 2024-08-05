package org.lazy.web;

import java.util.*;

public class SerializerFactory {

    private static final Set<Serializer> SERIALIZERS = initializeSerializers();

    private static Set<Serializer> initializeSerializers() {
        Set<Serializer> serializers = new HashSet<>();
        serializers.add(new JsonSerializer());
        serializers.add(new XmlSerializer());
        ServiceLoader.load(Serializer.class).forEach(serializers::add);
        return serializers;
    }

    private static SerializerFactory INSTANCE;

    private SerializerFactory() {
    }

    public static SerializerFactory getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SerializerFactory();
        }
        return INSTANCE;
    }

    public Serializer getSerializer(MediaType mediaType) throws WebException {
        return SERIALIZERS.stream()
                .filter(serializer -> serializer.canSerialize(mediaType))
                .findFirst().orElseThrow(() -> new WebException("No Serializer found for media type " + mediaType.getType()));
    }

    public Serializer getSerializer(String mediaType) throws WebException {
        return getSerializer(MediaType.of(mediaType));
    }
}
