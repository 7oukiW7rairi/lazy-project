package org.lazy.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auto.service.AutoService;

import org.lazy.app.LazyApplication;
import org.lazy.common.CommonProperties;
import org.lazy.common.ComponentDefinition;
import org.lazy.common.Configuration;
import org.lazy.common.Prototype;
import org.lazy.jpa.Repository;
import org.lazy.web.annotation.Controller;
import org.objectweb.asm.ClassWriter;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.persistence.Entity;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;

@SupportedAnnotationTypes({"javax.inject.Singleton",
                "javax.inject.Named",
                "org.lazy.common.Configuration",
                "org.lazy.web.annotation.Controller",
                "org.lazy.jpa.Repository",
                "org.lazy.jpa.common.Prototype",
                "org.lazy.web.integration.LazyApplication",
                "javax.persistence.Entity"})
@SupportedSourceVersion(SourceVersion.RELEASE_11)
@AutoService(Processor.class)
public class AnnotationProcessor extends AbstractProcessor {

    private boolean isProcessed;

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {
        AnnotatedComponentProcessor annotatedComponentProcessor = new AnnotatedComponentProcessor(processingEnv, roundEnv.getElementsAnnotatedWithAny(
                Set.of(Controller.class, Singleton.class, Configuration.class, Repository.class, Prototype.class, LazyApplication.class, Entity.class)),
                roundEnv.getElementsAnnotatedWith(Named.class));
        Set<ComponentDefinition> componentDefinitions =  annotatedComponentProcessor.getComponentDefinitions();
        Map<String, ClassWriter> classWriterMap = annotatedComponentProcessor.getClassWriterMap();
        if (!isProcessed) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                String json = objectMapper.writeValueAsString(componentDefinitions);
                FileObject definitionsFile = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", CommonProperties.COMPONENT_DEFINITIONS.getName());
                Files.write(new File(definitionsFile.toUri().getPath()).toPath(), json.getBytes(), StandardOpenOption.CREATE);
                for (Map.Entry<String, ClassWriter> entry : classWriterMap.entrySet()) {
                    FileObject classFile = processingEnv.getFiler().createClassFile(entry.getKey());
                    File file = new File(classFile.toUri().getPath());
                    file.getParentFile().mkdirs();
                    Files.write(new File(classFile.toUri().getPath()).toPath(), entry.getValue().toByteArray(), StandardOpenOption.CREATE);
                }
                isProcessed = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }


}
