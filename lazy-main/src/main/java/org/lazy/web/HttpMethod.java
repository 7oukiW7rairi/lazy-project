package org.lazy.web;

import java.util.stream.Stream;

public enum HttpMethod {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE"),
    OPTIONS("OPTIONS"),
    HEAD("HEAD"),
    TRACE("TRACE"),
    PATCH("PATCH");

    private String method;

    HttpMethod(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }

    public static HttpMethod of(String method) {
        return Stream.of(values())
                .filter(httpMethod -> httpMethod.getMethod().equals(method))
                .findFirst().orElse(null);
    }
}
