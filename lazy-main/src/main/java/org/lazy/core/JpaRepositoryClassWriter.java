package org.lazy.core;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.POP;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.V11;

public class JpaRepositoryClassWriter {

    static private final String CLASS_SUFFIX = "Impl";
    static private final String JPA_REPOSITORY_PACKAGE = "org/lazy/jpa/";
    static private final String ABSTRACT_REPOSITORY_CLASS_NAME = JPA_REPOSITORY_PACKAGE + "AbstractJpaRepository";
    static private final String JAVA_REFERENCE_DELIMITER = "L";
    static private final String JAVA_DESCRIPTOR_POSTFIX = ";";


    private ClassWriter classWriter;

    public JpaRepositoryClassWriter(String className, List<String> classParameters, List<JpaRepositoryMethod> methods) {
        this.classWriter = buildInterfaceImplClassWriter(className, classParameters, methods);
    }

    private ClassWriter buildInterfaceImplClassWriter(String className, List<String> classParameters, List<JpaRepositoryMethod> methods) {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        String classParam = "<" + classParameters.stream().map(JpaRepositoryMethod::convertToByteCodeFormat).reduce("", (result, item) -> result + item) + ">";
        String jvmClassName = className.replaceAll("\\.", "/");
        String classSignature = JAVA_REFERENCE_DELIMITER + ABSTRACT_REPOSITORY_CLASS_NAME + classParam + JAVA_DESCRIPTOR_POSTFIX +
                JAVA_REFERENCE_DELIMITER + jvmClassName + JAVA_DESCRIPTOR_POSTFIX;
        classWriter.visit(V11,
                ACC_PUBLIC,
                jvmClassName + CLASS_SUFFIX,
                classSignature,
                ABSTRACT_REPOSITORY_CLASS_NAME,
                new String[]{ jvmClassName });
        addClassAnnotation(classWriter);
        addDefaultConstructor(classWriter);
        methods.stream()
                .filter(method-> Objects.nonNull(method.getQuery())).
                forEach(jpaRepositoryMethod -> addQueryMethodImplementation(jpaRepositoryMethod, classWriter, jvmClassName));
        classWriter.visitEnd();
        return classWriter;

    }

    private void addClassAnnotation(ClassWriter classWriter) {
        classWriter.visitAnnotation( "Ljavax/transaction/Transactional;", true);
        classWriter.visitAnnotation( "Ljavax/inject/Singleton;", true);
    }

    public void addDefaultConstructor(ClassWriter classWriter) {
        String descriptor = "(Ljavax/persistence/EntityManager;)V";
        String name = "<init>";
        MethodVisitor constructor = classWriter.visitMethod(
                ACC_PUBLIC,
                name,
                descriptor,
                null,
                null);
        constructor.visitCode();
        constructor.visitVarInsn(ALOAD, 0);
        constructor.visitVarInsn(ALOAD, 1);
        constructor.visitMethodInsn(INVOKESPECIAL,
                ABSTRACT_REPOSITORY_CLASS_NAME,
                name,
                descriptor,
                false);

        constructor.visitInsn(RETURN);
        constructor.visitMaxs(2, 1);
        constructor.visitEnd();
    }

    private void addQueryMethodImplementation(JpaRepositoryMethod method, ClassWriter classWriter, String className) {
        String persistencePackage = "javax/persistence/";
        String entityManager = "EntityManager";
        MethodVisitor methodVisitor = classWriter.visitMethod(ACC_PUBLIC, method.getName(), method.getDesc(), method.getSignature(), null);
        methodVisitor.visitCode();
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitFieldInsn(GETFIELD,
                className + CLASS_SUFFIX,
                "entityManager",
                JAVA_REFERENCE_DELIMITER + persistencePackage + entityManager + JAVA_DESCRIPTOR_POSTFIX);
        methodVisitor.visitLdcInsn(method.getQuery());
        if (method.isUpdate()) {
            methodVisitor.visitMethodInsn(INVOKEINTERFACE,
                    persistencePackage + entityManager,
                    "createQuery",
                    "(Ljava/lang/String;)Ljavax/persistence/Query;",
                    true);
            String queryClassOwner = persistencePackage + "Query";
            setQueryParams(methodVisitor, method, queryClassOwner);
            methodVisitor.visitMethodInsn(INVOKEINTERFACE,
                    queryClassOwner,
                    "executeUpdate",
                    "()I",
                    true);
            // TODO only when it has a void return
            if (method.isVoid()) {
                methodVisitor.visitInsn(POP);
            }
            methodVisitor.visitInsn(RETURN);
        } else {
            String typedQueryClassOwner = persistencePackage + "TypedQuery";
            String queryType = getQueryType(method.getReturnType());
            methodVisitor.visitLdcInsn(Type.getType(queryType));
            methodVisitor.visitMethodInsn(INVOKEINTERFACE,
                    persistencePackage + entityManager,
                    "createQuery",
                    "(Ljava/lang/String;Ljava/lang/Class;)Ljavax/persistence/TypedQuery;",
                    true);
            setQueryParams(methodVisitor, method, typedQueryClassOwner);
            if (method.isReturningCollection()) {
                methodVisitor.visitMethodInsn(INVOKEINTERFACE,
                        typedQueryClassOwner,
                        "getResultList",
                        "()Ljava/util/List;",
                        true);
            } else {
                methodVisitor.visitMethodInsn(INVOKEINTERFACE,
                        typedQueryClassOwner,
                        "getSingleResult",
                        "()Ljava/lang/Object;",
                        true);
                methodVisitor.visitTypeInsn(CHECKCAST, cleanJvmClassName(method.getReturnType()));
            }
            methodVisitor.visitInsn(ARETURN);
        }
        // TODO maxStack in our case won't exceed 3 but it's better to do it dynamically
        methodVisitor.visitMaxs(3, method.getParameters().size() + 1);
        methodVisitor.visitEnd();
    }

    private void setQueryParams(MethodVisitor methodVisitor, JpaRepositoryMethod method, String queryClassName) {
        if (method.getQuery().contains("?")) {
            int opcodeIConst0 = 3;
            int paramSize = method.getParameters().size();
            List<Integer> paramNumbers = extractParamsNumberFromQuery(method.getQuery());
            paramNumbers.forEach(index -> {
                if (index <= paramSize) {
                    methodVisitor.visitInsn(opcodeIConst0 + index);
                    methodVisitor.visitVarInsn(ALOAD, index);
                    methodVisitor.visitMethodInsn(INVOKEINTERFACE,
                            queryClassName,
                            "setParameter",
                            "(ILjava/lang/Object;)Ljavax/persistence/TypedQuery;",
                            true);
                } else {// else throw an exception
                    throw new IllegalArgumentException("Malformed JPQL query " + method.getQuery());
                }
            });
        }
    }

    private List<Integer> extractParamsNumberFromQuery(String query) {
        return IntStream.range(0, query.length())
                .filter(i -> query.charAt(i) == '?' && i + 1 < query.length() && Character.isDigit(query.charAt(i + 1)))
                // TODO Throw an exception when the char at i + 1 is not a digit
                .mapToObj(i -> Integer.parseInt(String.valueOf(query.charAt(i + 1))))
                .collect(Collectors.toList());
    }

    public ClassWriter getClassWriter() {
        return classWriter;
    }

    public String getQueryType(String methodReturn) {
        // TODO more fine tuning
        return methodReturn.contains("<") ?  methodReturn.substring(methodReturn.indexOf("<") + 1, methodReturn.indexOf(">")) : methodReturn;
    }

    private String cleanJvmClassName(String methodReturnType) {
        return methodReturnType.replaceAll("^L", "").replaceAll(";$", "");
    }
}
