package org.lazy.core;

import org.lazy.common.BaseComponentDefinition;
import org.lazy.common.ComponentDefinition;
import org.lazy.common.ComponentProxy;
import org.lazy.common.ComponentType;
import org.lazy.common.ConstructorDefinition;
import org.lazy.jpa.LocalTransactionManager;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.persistence.EntityManager;
import java.util.*;

public class AnnotatedRepositoryElementTransformer extends AbstractAnnotatedElementTransformer<ComponentDefinition> {

    static private final String CLASS_SUFFIX = "Impl";

    public AnnotatedRepositoryElementTransformer(ProcessingEnvironment processingEnv) {
        super(processingEnv, Collections.emptySet());
    }

    @Override
    public ComponentDefinition transform(Element element) {
        String canonicalName = constructClassFullName(element);
        BaseComponentDefinition componentDefinition = new BaseComponentDefinition( canonicalName + CLASS_SUFFIX);
        componentDefinition.setAbstract(false);
        componentDefinition.setComponentProxy(ComponentProxy.TRANSACTIONAL);
        componentDefinition.setComponentSuperTypes(Collections.singletonList(canonicalName));
        componentDefinition.setDependencies(Arrays.asList(EntityManager.class.getName(), LocalTransactionManager.class.getName()));
        componentDefinition.setComponentType(ComponentType.REPOSITORY);
        componentDefinition.setConstructor(new ConstructorDefinition(Collections.singletonList(EntityManager.class.getName()), Collections.emptyList()));
        return componentDefinition;
    }
}
