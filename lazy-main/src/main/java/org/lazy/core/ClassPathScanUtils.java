package org.lazy.core;

import org.lazy.jpa.integration.AnnotationClassVisitor;
import org.objectweb.asm.ClassReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

import static org.lazy.common.StreamUtils.enumerationAsStream;
import static org.lazy.common.StreamUtils.withoutCheckExceptions;
import static org.objectweb.asm.Opcodes.ASM7;

public class ClassPathScanUtils {

    private static final Logger logger = LoggerFactory.getLogger(ClassPathScanUtils.class);

    private static final String JVM_REFERENCE_CHAR = "L";
    private static final String JVM_ENDING_CHAR = ";";
    private static final String JVM_SEPARATOR_CHAR = "/";

    private ClassPathScanUtils() {
    }

    public static List<String> getClassesName(String basePackage, String superClass, List<String> interfaces, String annotation)  {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        List<String> classes = new ArrayList<>();
        if (basePackage != null) {
            String packagePath = basePackage.replaceAll("\\.", JVM_SEPARATOR_CHAR);
            try {
                List<ClassReader> classReaders = enumerationAsStream(classLoader.getResources(packagePath))
                        .flatMap(withoutCheckExceptions(url -> Files.walk(new File(url.getPath()).toPath())))
                        .filter(path -> path.toString().endsWith(".class"))
                        .map(withoutCheckExceptions(path -> new ClassReader(path.toUri().toURL().openStream())))
                        .filter(classReader -> superClass == null || superClass.equals(canonicalClassName(classReader.getSuperName())))
                        .filter(classReader -> interfaces == null || Arrays.stream(classReader.getInterfaces())
                                .map(ClassPathScanUtils::canonicalClassName).anyMatch(interfaces::contains))
                        .collect(Collectors.toList());
                if (annotation != null) {
                    for (ClassReader classReader : classReaders) {
                        AnnotationClassVisitor classVisitor = new AnnotationClassVisitor(ASM7, jvmClassName(annotation));
                        classReader.accept(classVisitor, 0);
                        if (classVisitor.isAnnotationPresent()) {
                            classes.add(canonicalClassName(classReader.getClassName()));
                        }
                    }
                } else {
                    classes.addAll(classReaders.stream().map(classReader -> canonicalClassName(classReader.getClassName())).collect(Collectors.toList()));
                }
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
        return classes;
    }

    private static String jvmClassName(String className) {
        return JVM_REFERENCE_CHAR + className.replaceAll("\\.", JVM_SEPARATOR_CHAR) + JVM_ENDING_CHAR;
    }

    private static String canonicalClassName(String jvmClassName) {
        return jvmClassName.replace(JVM_REFERENCE_CHAR, "").replace(JVM_ENDING_CHAR,"").replaceAll(JVM_SEPARATOR_CHAR, ".");
    }
}
