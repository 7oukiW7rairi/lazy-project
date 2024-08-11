package org.lazy.app;

import static org.objectweb.asm.Opcodes.ASM8;

import java.util.List;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;

public class ConstructorScanner extends MethodVisitor {

    private List<ConstructorParam> constructorParams;

    public ConstructorScanner(List<ConstructorParam> constructorParams) {
        super(ASM8);
        this.constructorParams = constructorParams;
    }

    @Override
    public AnnotationVisitor visitParameterAnnotation(final int parameter, final String descriptor, final boolean visible) {
        if (descriptor.equals("Ljavax/inject/Named;")) {
            return new AnnotationVisitor(ASM8) {
                @Override
                public void visit(String name, Object value) {
                    if ("value".equals(name)) {
                        constructorParams.get(parameter).setNamedAnnotationValue((String) value);
                    }
                    super.visit(name, value);
                }
            };
        } else {
            return super.visitAnnotation(descriptor, visible);
        }
    }

}
