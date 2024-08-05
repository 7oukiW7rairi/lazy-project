package org.lazy.core;

import org.lazy.common.ComponentDefinition;
import org.lazy.common.Configuration;
import org.lazy.jpa.Repository;
import org.objectweb.asm.ClassWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import java.util.*;
import java.util.stream.Collectors;

public class AnnotatedComponentProcessor {

    private static final Logger logger = LoggerFactory.getLogger(AnnotatedComponentProcessor.class);

    private static final List<AutoConfig> AUTO_CONFIGS = ServiceLoader.load(AutoConfig.class, AnnotationProcessor.class.getClassLoader()).stream()
            .map(ServiceLoader.Provider::get)
            .collect(Collectors.toList());

    private final Set<ComponentDefinition> definitions = new HashSet<>();
    private final Map<String, ClassWriter> classWriterMap = new HashMap<>();

    public AnnotatedComponentProcessor(ProcessingEnvironment processingEnv, Set<? extends Element> annotatedComponents) {
        AnnotatedComponentTransformer<Element, ComponentDefinition> annotatedElementTransformer = new AnnotatedElementTransformer(processingEnv);
        AnnotatedComponentTransformer<Element, ComponentDefinition> annotatedRepositoryTransformer = new AnnotatedRepositoryElementTransformer(processingEnv);
        AnnotatedComponentTransformer<Element, List<ComponentDefinition>> configElementTransformer = new AnnotatedConfigElementTransformer(processingEnv);
        for (Element element : annotatedComponents) {
            if (element.getAnnotation(Configuration.class) != null) {
                definitions.add(annotatedElementTransformer.transform(element));
                definitions.addAll(configElementTransformer.transform(element));
            } else if (element.getAnnotation(Repository.class) != null) {
                ComponentDefinition definition = annotatedRepositoryTransformer.transform(element);
                definitions.add(definition);
                classWriterMap.putIfAbsent(definition.getComponentClassName(), new JpaRepositoryClassWriterBuilder(definition.getComponentSuperTypes().get(0), element, processingEnv).build());
            } else {
                definitions.add(annotatedElementTransformer.transform(element));
            }
        }
        definitions.addAll(AUTO_CONFIGS.stream().flatMap(autoConfig -> autoConfig.getComponentDefinitions().stream()).collect(Collectors.toList()));
    }

    public Set<ComponentDefinition> getComponentDefinitions() {
        return definitions;
    }

    public Map<String, ClassWriter> getClassWriterMap() {
        return classWriterMap;
    }

    private String getDependencyName(String name, String qualifier) {
        return qualifier == null ? name : name + "@" + qualifier;
    }


}
