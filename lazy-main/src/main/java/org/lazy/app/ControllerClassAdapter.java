package org.lazy.app;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ASM8;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.RETURN;

public class ControllerClassAdapter  extends ClassVisitor {

    private static final String BASE_SERVLET = "org/lazy/web/BaseHttpServlet";
    private static final String CONSTRUCTOR_NAME = "<init>";
    private static final String CONSTRUCTOR_DESCRIPTOR = "()V";

    private boolean controller;
    private String className;
    private String[] classInterfaces;
    private int asmVersion;
    private List<String> constructorParams;
    private List<ClassField> classFields = new ArrayList<>();


    public ControllerClassAdapter(ClassVisitor cv) {
        super(ASM8, cv);
        this.controller = false;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        if ("Lorg/lazy/web/annotation/Controller;".equals(descriptor)) {
            controller = true;
        }
        return cv.visitAnnotation(descriptor, visible);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        asmVersion = version;
        className = name;
        classInterfaces = interfaces;
        cv.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        classFields.add(new ClassField(access, name, descriptor, signature, value));
        return null;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        if (controller && CONSTRUCTOR_NAME.equals(name)) {
            constructorParams = Stream.of(descriptor.substring(descriptor.indexOf("(") + 1, descriptor.indexOf(")")).split(";"))
                    .filter(Predicate.not(String::isEmpty)).map(param -> param + ";").collect(Collectors.toList());
            return null;
        } else {
            return cv.visitMethod(access, name, descriptor, signature, exceptions);
        }
    }

    @Override
    public void visitEnd() {
        if (controller) {
            cv.visit(asmVersion, ACC_PUBLIC, className, "L" + BASE_SERVLET + ";", BASE_SERVLET, classInterfaces);
            for (ClassField field : classFields) {
                FieldVisitor fieldVisitor = cv.visitField(field.getAccess(), field.getName(), field.getDescriptor(), field.getSignature(), field.getValue());
                constructorParams.stream()
                        .filter(param -> field.getDescriptor().equals(param)).findFirst()
                        .ifPresent(param -> fieldVisitor.visitAnnotation("Ljavax/inject/Inject;", true));
                fieldVisitor.visitEnd();
            }
            MethodVisitor constructor = cv.visitMethod(
                    ACC_PUBLIC,
                    CONSTRUCTOR_NAME,
                    CONSTRUCTOR_DESCRIPTOR,
                    null,
                    null);
            constructor.visitCode();
            constructor.visitVarInsn(ALOAD, 0);
            constructor.visitMethodInsn(INVOKESPECIAL,
                    BASE_SERVLET,
                    CONSTRUCTOR_NAME,
                    CONSTRUCTOR_DESCRIPTOR,
                    false);
            constructor.visitInsn(RETURN);
            constructor.visitMaxs(1, 1);
            constructor.visitEnd();
        }
        cv.visitEnd();
    }

}
