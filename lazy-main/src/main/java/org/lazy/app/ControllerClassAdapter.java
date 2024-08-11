package org.lazy.app;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ASM8;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.RETURN;

public class ControllerClassAdapter extends ClassVisitor {

    private static final Logger logger = LoggerFactory.getLogger(ControllerClassAdapter.class);

    private static final String BASE_SERVLET = "org/lazy/web/BaseHttpServlet";
    private static final String CONSTRUCTOR_NAME = "<init>";
    private static final String CONSTRUCTOR_DESCRIPTOR = "()V";

    private boolean controller;
    private String className;
    private String[] classInterfaces;
    private int asmVersion;
    private List<ConstructorParam> constructorParams = new ArrayList<>();
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
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature,
            String[] exceptions) {
        if (controller && CONSTRUCTOR_NAME.equals(name)) {
            constructorParams = Stream.of(Type.getArgumentTypes(descriptor))
                    .map(type -> type.getDescriptor())
                    .map(ConstructorParam::new).collect(Collectors.toList());
            return new ConstructorScanner(constructorParams);
        } else {
            return cv.visitMethod(access, name, descriptor, signature, exceptions);
        }
    }

    @Override
    public void visitEnd() {
        if (controller) {
            cv.visit(asmVersion, ACC_PUBLIC, className, "L" + BASE_SERVLET + ";", BASE_SERVLET, classInterfaces);
            logger.info("ControlleAdapter parameters " + constructorParams.stream().map(ConstructorParam::getDescriptor).collect(Collectors.toList()));
            for (ClassField field : classFields) {
                FieldVisitor fieldVisitor = cv.visitField(field.getAccess(), field.getName(), field.getDescriptor(),
                        field.getSignature(), field.getValue());
                constructorParams.stream()
                        .filter(param -> field.getDescriptor().equals(param.getDescriptor())).findFirst()
                        .ifPresent(param -> {
                            logger.info("Controller adapt field " + field.getDescriptor());
                            fieldVisitor.visitAnnotation("Ljavax/inject/Inject;", true);
                            if (param.getNamedAnnotationValue() != null) {
                                AnnotationVisitor namedAnnotation = fieldVisitor.visitAnnotation("Ljavax/inject/Named;",
                                        true);
                                namedAnnotation.visit("value", param.getNamedAnnotationValue());
                            }
                        });
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
