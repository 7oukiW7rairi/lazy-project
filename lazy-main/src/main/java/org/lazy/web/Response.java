package org.lazy.web;


import com.google.common.base.Strings;

import java.util.*;

// TODO complete the basic response class with the rest of the needed implementation
public class Response {

    private Object body;
    private int status;
    private String view;
    private Map<String, String> headers;

    private Response(ResponseBuilder builder) {
        this.body = builder.body;
        this.status = builder.status;
        this.headers = builder.headers;
        this.view = builder.view;
    }

    public Object getBody() {
        return body;
    }

    public int getStatus() {
        return status;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getHeader(String name){
        return headers.get(name);
    }

    public boolean isView() {
        return !Strings.isNullOrEmpty(view);
    }

    public String getView() {
        return view;
    }

    public static ResponseBuilder body(Object body) {
        return new ResponseBuilder().body(body);
    }
    public static ResponseBuilder ok(Object body) {
       return new ResponseBuilder().body(body).status(200);
    }

    public static ResponseBuilder status(int status) {
        return new ResponseBuilder().status(status);
    }

    public static ResponseBuilder header(String name, String value) {
        return new ResponseBuilder().header(name, value);
    }

    public static Response view(String view) {
        return new ResponseBuilder(view).build();
    }

    public static Response build() {
        return new ResponseBuilder().build();
    }


    public static class ResponseBuilder {
        private Object body;
        private int status;
        private String view;
        private Map<String, String> headers = new HashMap<>();

        private ResponseBuilder() {
        }

        public ResponseBuilder(String view) {
            this.view = view;
        }

        public ResponseBuilder header(String name, String value) {
            this.headers.putIfAbsent(name, value);
            return this;
        }

        public ResponseBuilder body(Object body) {
            this.body = body;
            return this;
        }

        public ResponseBuilder status(int status) {
            this.status = status;
            return this;
        }

        public Response build() {
            return new Response(this);
        }

    }
}
