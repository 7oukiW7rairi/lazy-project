package org.lazy.core;

import java.util.*;
import java.util.stream.Collectors;

public class JpaRepositoryMethod {

    static private final String JAVA_REFERENCE_DELIMITER = "L";
    static private final String JAVA_DESCRIPTOR_POSTFIX = ";";

    private String name;
    private String returnType;
    private String query;
    private List<String> parameters;

    public JpaRepositoryMethod(String name, String returnType, String query, List<String> parameters) {
        this.name = name;
        this.returnType = returnType;
        this.query = query;
        this.parameters = parameters;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return createDesc(parameters.stream().map(this::withoutParameters).collect(Collectors.toList()), withoutParameters(returnType)) ;
    }

    public String getSignature() {
        return returnType.contains("<") || parameters.stream().anyMatch(s -> s.contains("<")) ? createDesc(parameters, returnType) : null;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }

    public String getReturnType() {
        return convertToByteCodeFormat(returnType);
    }

    public boolean isReturningCollection() {
        // TODO add support for more Collection
        return returnType.contains("java.util.List");
    }

    public boolean isUpdate() {
        return query.startsWith("update");
    }

    public boolean isVoid() {
        return "void".equals(returnType);
    }

    private String createDesc(List<String> params, String methodReturn) {
        return "(" + params.stream().map(JpaRepositoryMethod::convertToByteCodeFormat).reduce("", (result, item) -> result + item) + ")" + convertToByteCodeFormat(methodReturn);
    }

    private String withoutParameters(String name) {
        return name.contains("<") ? name.substring(0, name.indexOf("<")) : name;
    }

    public static String convertToByteCodeFormat(String canonicalName) {
        // TODO add support for primitive types
        String byteCodeName = "";
        if (canonicalName.contains("void")) {
            byteCodeName += "V";
        } else {
            if (canonicalName.contains("[]")) {
                byteCodeName += "[";
                canonicalName = canonicalName.substring(0, canonicalName.indexOf("["));
            }
            if (PrimitiveType.isPrimitive(canonicalName)) {
                byteCodeName += PrimitiveType.of(canonicalName).getByteCodeFormat();
            } else if (canonicalName.contains("<")) {
                byteCodeName += JAVA_REFERENCE_DELIMITER + canonicalName.substring(0, canonicalName.indexOf("<")).replaceAll("\\.", "/") +
                        Arrays.stream(canonicalName.substring(canonicalName.indexOf("<") + 1, canonicalName.indexOf(">")).split(","))
                                .map(stringPart -> JAVA_REFERENCE_DELIMITER + stringPart.replaceAll("\\.", "/") + JAVA_DESCRIPTOR_POSTFIX)
                                .collect(Collectors.joining("", "<", ">")) + JAVA_DESCRIPTOR_POSTFIX;
            } else {
                byteCodeName += JAVA_REFERENCE_DELIMITER + canonicalName.replaceAll("\\.", "/") + JAVA_DESCRIPTOR_POSTFIX;
            }
        }
        return byteCodeName;
    }


    @Override
    public String toString() {
        return "JpaRepositoryMethod{" +
                "name='" + name + '\'' +
                ", returnType='" + returnType + '\'' +
                ", query='" + query + '\'' +
                ", parameters=" + parameters +
                '}';
    }

    private enum PrimitiveType {

        INT("int", "I"),
        LONG("long", "J"),
        SHORT("short", "S"),
        CHAR("char", "C"),
        BYTE("byte", "B"),
        BOOLEAN("boolean", "Z"),
        FLOAT("float", "F"),
        DOUBLE("double", "D");

        private final String userFormat;
        private final String byteCodeFormat;

        PrimitiveType(String userFormat, String byteCodeFormat) {
            this.userFormat = userFormat;
            this.byteCodeFormat = byteCodeFormat;
        }

        public static boolean isPrimitive(String userFormat) {
            return Arrays.stream(values()).anyMatch(primitiveType -> primitiveType.getUserFormat().equals(userFormat));
        }

        public static PrimitiveType of(String userFormat) {
            return Arrays.stream(values())
                    .filter(primitiveType -> primitiveType.getUserFormat().equals(userFormat))
                    .findFirst().orElseThrow(() -> new IllegalArgumentException("Unknown primitive type " + userFormat));
        }

        public String getUserFormat() {
            return userFormat;
        }

        public String getByteCodeFormat() {
            return byteCodeFormat;
        }
    }
}
