package org.lazy.web;

public enum PathPartType {
    VARIABLE(true),
    LITERAL(false),
    MIXED(true);

    private boolean containVariable;

    PathPartType(boolean containVariable) {
        this.containVariable = containVariable;
    }

    public boolean containVariable() {
        return containVariable;
    }

    public static PathPartType of(String pathPart) {
        if (pathPart.startsWith("{") && pathPart.endsWith("}")) {
            return PathPartType.VARIABLE;
        } else if (pathPart.contains("{") && pathPart.contains("}")) {
            return PathPartType.MIXED;
        }
        return PathPartType.LITERAL;
    }
}
