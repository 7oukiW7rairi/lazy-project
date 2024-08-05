package org.lazy.web;

import java.util.*;
import java.util.regex.Pattern;

public class PathPartMatcher {

    private static final String DEFAULT_PATTERN = "[^/]+";

    private final PathPartType pathPartType;
    private final String pathPart;
    private final String reqPart;
    private final Optional<String> variable;
    private final Pattern pathPartPattern;

    public PathPartMatcher(String reqPart, String pathPart) {
        this.pathPart = pathPart;
        this.reqPart = reqPart;
        this.pathPartType = PathPartType.of(pathPart);
        this.variable = extractVariable(pathPart);
        this.pathPartPattern = compilePathPartPattern(pathPart);
    }

    private Optional<String> extractVariable(String pathPart) {
        return pathPartType.containVariable() ?
                Optional.of(pathPart.substring(pathPart.indexOf("{") + 1, pathPart.indexOf("}"))) : Optional.empty();
    }

    // TODO Improve the implementation to make the variable parts match multiple parts of the request
    private Pattern compilePathPartPattern(String part) {
        if (pathPartType == PathPartType.LITERAL) {
            return Pattern.compile(part);
        } else if (pathPartType == PathPartType.VARIABLE) {
            String[] strings = part.replace("{", "").replace("}", "").split(":");
            if (strings.length > 1) {
                return Pattern.compile(strings[1]);
            } else {
                return Pattern.compile(DEFAULT_PATTERN);
            }
        } else {
            throw new UnsupportedOperationException("Mixed request variable is not supported yet");
        }
    }

    public boolean match() {
        return pathPartPattern.matcher(reqPart).matches();
    }

    public PathPartType getPathPartType() {
        return pathPartType;
    }

    public String getPathPart() {
        return pathPart;
    }

    public String getReqPart() {
        return reqPart;
    }

    public Optional<String> getVariable() {
        return variable;
    }
}
