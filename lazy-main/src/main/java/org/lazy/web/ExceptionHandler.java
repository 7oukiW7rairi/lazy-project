package org.lazy.web;

import java.lang.reflect.ParameterizedType;
import java.util.stream.Stream;

public interface ExceptionHandler<E extends Throwable> {

    Response handleException(E exception);

    @SuppressWarnings("unchecked")
    default Class<E> getType() {
        return (Class<E>) Stream.of(this.getClass().getGenericInterfaces())
                .filter(ParameterizedType.class::isInstance)
                .map(ParameterizedType.class::cast)
                .filter(a -> ExceptionHandler.class.equals(a.getRawType()))
                .map(type -> type.getActualTypeArguments()[0])
                .findFirst().orElse(Object.class);
    }
}
