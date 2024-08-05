package org.lazy.core;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ComponentWithDependencies {

    private ComponentWithoutDependency dependency;
    private DummyInterface dummyComponent;

    @Inject
    public ComponentWithDependencies(ComponentWithoutDependency dependency, DummyInterface dummyComponent) {
        this.dependency = dependency;
        this.dummyComponent = dummyComponent;
    }
}
