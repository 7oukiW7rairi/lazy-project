package org.lazy.web;

import org.lazy.web.annotation.PathMapping;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.IntStream;

public class HandlerMapping {

    private static final String SPLIT_CHAR = "/";

    private Method[] methods;

    public HandlerMapping(Method... methods) {
        this.methods = methods;
    }

    public Method getHandler(HttpServletRequest req) throws WebException {
        return Arrays.stream(methods)
                .filter(method -> method.isAnnotationPresent(PathMapping.class))
                .filter(method -> method.getAnnotation(PathMapping.class).method().getMethod().equals(req.getMethod()))
                .filter(method -> isPathPartMatching(method.getAnnotation(PathMapping.class).path(), req.getPathInfo()))
                .min((m1, m2) -> comparePathPart(m1.getAnnotation(PathMapping.class).path(), m2.getAnnotation(PathMapping.class).path()))
                .orElseThrow(() -> new WebException("No servlet mapping found for this request " + req.getRequestURI()));
    }

    private boolean isPathPartMatching(String path, String pathInfo) {
        boolean isMatch = false;
        String[] pathParts = path.replaceFirst("^/","").split(SPLIT_CHAR);
        String[] reqParts = pathInfo != null ? pathInfo.replaceFirst("^/","").split(SPLIT_CHAR) : new String[]{""};
        if (reqParts.length >= pathParts.length) {
            isMatch = IntStream.range(0, reqParts.length)
                    .mapToObj(value -> new PathPartMatcher(reqParts[value],
                            value < pathParts.length ? pathParts[value] : pathParts[pathParts.length - 1]))
                    .allMatch(PathPartMatcher::match);
        }
        return isMatch;
    }

    private int comparePathPart(String path1, String path2) {
        return Integer.compare(path1.split(SPLIT_CHAR).length, path2.split(SPLIT_CHAR).length);
    }
}
