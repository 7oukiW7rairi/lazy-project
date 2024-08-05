package org.lazy.web;

import java.lang.reflect.ParameterizedType;
import java.util.stream.Stream;

public interface Converter<T,I> {

    T convert(I from);


    @SuppressWarnings("unchecked")
    default Class<T> getType() {
        return (Class<T>) Stream.of(this.getClass().getGenericInterfaces())
                .filter(ParameterizedType.class::isInstance)
                .map(ParameterizedType.class::cast)
                .filter(a -> Converter.class.equals(a.getRawType()))
                .map(type -> type.getActualTypeArguments()[0])
                .findFirst().orElse(Object.class);
    }
}
