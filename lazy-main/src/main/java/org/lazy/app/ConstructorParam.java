package org.lazy.app;

public class ConstructorParam {

    private String name;
    private String descriptor;
    private String namedAnnotationValue;


    public ConstructorParam(String descriptor) {
        this.descriptor = descriptor;
    }
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescriptor() {
        return descriptor;
    }
    public void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
    }
    public String getNamedAnnotationValue() {
        return namedAnnotationValue;
    }
    public void setNamedAnnotationValue(String namedAnnotationValue) {
        this.namedAnnotationValue = namedAnnotationValue;
    }

    

}
