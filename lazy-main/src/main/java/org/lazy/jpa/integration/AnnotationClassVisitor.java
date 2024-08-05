package org.lazy.jpa.integration;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;

public class AnnotationClassVisitor extends ClassVisitor {

    private final String annotation;
    private boolean annotationPresent;

    public AnnotationClassVisitor(int api, String annotation) {
        super(api);
        this.annotation = annotation;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        annotationPresent = annotation.equals(descriptor);
        return super.visitAnnotation(descriptor, visible);
    }

    public boolean isAnnotationPresent() {
        return annotationPresent;
    }
}
