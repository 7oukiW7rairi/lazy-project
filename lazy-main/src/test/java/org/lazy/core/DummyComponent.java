package org.lazy.core;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;

@Singleton
@Transactional
public class DummyComponent implements DummyInterface{

    private ComponentFromConfig component;

    public ComponentFromConfig getComponent() {
        return component;
    }

    @Inject
    public void setComponent(ComponentFromConfig component) {
        this.component = component;
    }

    @Override
    public void doSomethingDummy() {

    }


    @Transactional(Transactional.TxType.NEVER)
    @Override
    public void doSomethingDummyNotTransactional() {

    }
}
