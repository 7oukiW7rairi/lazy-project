package org.lazy.web;

import java.util.stream.Stream;

public enum MediaType {
    APPLICATION_XML("application/xml"),
    APPLICATION_JSON("application/json"),
    MULTIPART_FORM_DATA("multipart/form-data"),
    TEXT_XML("text/xml"),
    TEXT_HTML("text/html");


    private String type;

    MediaType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static MediaType of(String type) {
        return Stream.of(values())
                .filter(mediaType -> type != null && type.contains(mediaType.getType()))
                .findFirst().orElse(APPLICATION_JSON);
    }
}
