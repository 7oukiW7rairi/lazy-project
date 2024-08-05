package org.lazy.core;

public interface AnnotatedComponentTransformer<T, I> {

    I transform(T annotatedComponent);
}
