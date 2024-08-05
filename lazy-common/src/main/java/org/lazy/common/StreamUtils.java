package org.lazy.common;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class StreamUtils {

    private StreamUtils() {
    }

    public static <T, R, E extends Exception> Function<T, R> withoutCheckExceptions(FunctionWithException<T, R, E> function) {
        return arg -> {
            try {
                return function.apply(arg);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    public static <T> Stream<T> enumerationAsStream(Enumeration<T> e) {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(
                        new Iterator<
                                >() {
                            public T next() {
                                return e.nextElement();
                            }

                            public boolean hasNext() {
                                return e.hasMoreElements();
                            }

                            public void forEachRemaining(Consumer<? super T> action) {
                                while (e.hasMoreElements()) action.accept(e.nextElement());
                            }
                        },
                        Spliterator.ORDERED), false);
    }

    public static <T, K, V> Collector<T, Map<K, V>, Map<K, V>> toHashMapWithoutNullValues(final Function<? super T, K> keyMapper,
            final Function<T, V> valueMapper) {
        return Collector.of(
                HashMap::new,
                (kvMap, t) -> {
                    if (valueMapper.apply(t) != null) {
                        kvMap.put(keyMapper.apply(t), valueMapper.apply(t));
                    }
                },
                (kvMap, kvMap2) -> {
                    kvMap.putAll(kvMap2);
                    return kvMap;
                },
                Function.identity(),
                Collector.Characteristics.IDENTITY_FINISH);
    }

    /** .map(rethrowFunction(name -> Class.forName(name))) or .map(rethrowFunction(Class::forName)) */
    public static <T, U, R, E extends Throwable> BiFunction<T, U, R> rethrowBiFunction(BiFunctionWithExceptions<T, U, R, E> function) {
        return (t, u) -> {
            try { return function.apply(t, u); }
            catch (Throwable exception) {
                throw new RuntimeException(exception);
            }
        };
    }

    @FunctionalInterface
    public interface BiFunctionWithExceptions<T, U, R, E extends Throwable> {
        R apply(T t, U u) throws E;
    }
}
