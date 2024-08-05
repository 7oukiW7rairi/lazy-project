package org.lazy.web.annotation;

import org.lazy.web.HttpMethod;
import org.lazy.web.MediaType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface PathMapping {
    String path() default "";

    HttpMethod method() default HttpMethod.GET;

    MediaType[] produces() default {MediaType.APPLICATION_JSON};

    MediaType[] consumes() default {MediaType.APPLICATION_JSON};
}
